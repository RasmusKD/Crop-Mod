package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatistics;
import com.rasmus.cropmod.config.CropModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to track when blocks are successfully broken.
 */
@Mixin(MultiPlayerGameMode.class)
public class HarvestTrackerMixin {

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void cropmod$captureState(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // The state must be read BEFORE vanilla replaces the block, but the
        // harvest only counts if the break actually went through (see below).
        pendingState = null;
        if (!CropModConfig.get().modEnabled) {
            return;
        }
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        pendingState = world.getBlockState(pos);
    }

    @org.spongepowered.asm.mixin.Unique
    private BlockState pendingState;

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = pendingState;
        pendingState = null;
        if (state == null || !cir.getReturnValueZ()) {
            // Failed breaks (adventure mode, unbreakable, out of range) used
            // to count as harvests because the hook ran at HEAD and ignored
            // the outcome.
            return;
        }
        Block block = state.getBlock();

        // Check if it's a crop we track
        if (isSupportedCrop(block)) {
            // Only count if it was fully grown (if that setting is enabled, it would have
            // been blocked otherwise)
            if (!CropModConfig.get().onlyHarvestFullyGrown || isFullyGrown(state)) {
                HarvestStatistics.getInstance().recordHarvest(block);
            }
        }
    }

    private boolean isSupportedCrop(Block block) {
        return block == Blocks.WHEAT ||
                block == Blocks.CARROTS ||
                block == Blocks.POTATOES ||
                block == Blocks.BEETROOTS ||
                block == Blocks.NETHER_WART ||
                block == Blocks.COCOA;
    }

    private boolean isFullyGrown(BlockState state) {
        // One source of truth for maturity; a second copy of these age
        // checks lived here and could drift from the protection logic.
        return !com.rasmus.cropmod.CropProtection.isCropNotFullyGrown(state);
    }
}

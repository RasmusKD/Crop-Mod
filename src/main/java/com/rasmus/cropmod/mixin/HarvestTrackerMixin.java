package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatistics;
import com.rasmus.cropmod.config.CropModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
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
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CropModConfig.get().modEnabled) {
            return;
        }

        Level world = Minecraft.getInstance().level;

        if (world == null) {
            return;
        }

        BlockState state = world.getBlockState(pos);
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
        Block block = state.getBlock();

        if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        } else if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }

        return true;
    }
}

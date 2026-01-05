package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatistics;
import com.rasmus.cropmod.config.CropModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to track when blocks are successfully broken.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class HarvestTrackerMixin {

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!CropModConfig.get().modEnabled) {
            return;
        }

        World world = net.minecraft.client.MinecraftClient.getInstance().world;

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
            return cropBlock.isMature(state);
        } else if (block instanceof NetherWartBlock) {
            return state.get(NetherWartBlock.AGE) >= 3;
        } else if (block instanceof CocoaBlock) {
            return state.get(CocoaBlock.AGE) >= 2;
        }

        return true;
    }
}

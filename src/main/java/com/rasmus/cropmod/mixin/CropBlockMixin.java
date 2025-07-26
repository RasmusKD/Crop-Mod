package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.config.CropModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.HoeItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class CropBlockMixin {

    private static final Map<Block, Item> CROP_SEED_MAP = new HashMap<>();
    private static final Map<Block, String> CROP_CONFIG_KEYS = new HashMap<>();

    static {
        CROP_SEED_MAP.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        CROP_SEED_MAP.put(Blocks.CARROTS, Items.CARROT);
        CROP_SEED_MAP.put(Blocks.POTATOES, Items.POTATO);
        CROP_SEED_MAP.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        CROP_SEED_MAP.put(Blocks.NETHER_WART, Items.NETHER_WART);
        CROP_SEED_MAP.put(Blocks.COCOA, Items.COCOA_BEANS);

        CROP_CONFIG_KEYS.put(Blocks.WHEAT, "wheatEnabled");
        CROP_CONFIG_KEYS.put(Blocks.CARROTS, "carrotsEnabled");
        CROP_CONFIG_KEYS.put(Blocks.POTATOES, "potatoesEnabled");
        CROP_CONFIG_KEYS.put(Blocks.BEETROOTS, "beetrootsEnabled");
        CROP_CONFIG_KEYS.put(Blocks.NETHER_WART, "netherWartEnabled");
        CROP_CONFIG_KEYS.put(Blocks.COCOA, "cocoaEnabled");
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(boolean breaking, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.world == null || client.crosshairTarget == null ||
                client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = client.world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // First check if this crop is enabled at all
        if (!isCropEnabled(block)) {
            return; // Don't apply any CropMod features to disabled crops
        }

        // Hoe requirement logic
        if (CropModConfig.get().requireHoeToBreakCrops && !isHoldingHoe(player)) {
            ci.cancel();
            return;
        }

        // Camera snap logic
        if (CropModConfig.get().cameraSnapEnabled &&
                CropModConfig.get().cameraSnapMode == CropModConfig.CameraSnapMode.ALWAYS) {
            if (CropModConfig.get().cameraSnapDirectionMode == CropModConfig.CameraSnapDirectionMode.ALWAYS ||
                    isFacingSameRow(player, blockPos)) {
                snapCameraToNearest90Degrees(player);
            }
        }

        // Crop protection logic
        if (CropModConfig.get().cropProtectionEnabled && shouldCancelAttack(player, blockState)) {
            ci.cancel();
            return;
        }

        // Only harvest fully grown logic
        if (CropModConfig.get().onlyHarvestFullyGrown && isCropNotFullyGrown(blockState)) {
            ci.cancel();
            return;
        }

        // Camera snap on break
        if (breaking && CropModConfig.get().cameraSnapEnabled &&
                CropModConfig.get().cameraSnapMode == CropModConfig.CameraSnapMode.BREAK) {
            if (CropModConfig.get().cameraSnapDirectionMode == CropModConfig.CameraSnapDirectionMode.ALWAYS ||
                    isFacingSameRow(player, blockPos)) {
                snapCameraToNearest90Degrees(player);
            }
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.world == null || client.crosshairTarget == null ||
                client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = client.world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // First check if this crop is enabled at all
        if (!isCropEnabled(block)) {
            return; // Don't apply any CropMod features to disabled crops
        }

        // Hoe requirement logic
        if (CropModConfig.get().requireHoeToBreakCrops && !isHoldingHoe(player)) {
            cir.cancel();
            return;
        }

        // Camera snap on break
        if (CropModConfig.get().cameraSnapEnabled &&
                CropModConfig.get().cameraSnapMode == CropModConfig.CameraSnapMode.BREAK) {
            if (CropModConfig.get().cameraSnapDirectionMode == CropModConfig.CameraSnapDirectionMode.ALWAYS ||
                    isFacingSameRow(player, blockPos)) {
                snapCameraToNearest90Degrees(player);
            }
        }

        // Crop protection logic
        if (CropModConfig.get().cropProtectionEnabled && shouldCancelAttack(player, blockState)) {
            cir.cancel();
            return;
        }

        // Only harvest fully grown logic
        if (CropModConfig.get().onlyHarvestFullyGrown && isCropNotFullyGrown(blockState)) {
            cir.cancel();
            return;
        }
    }

    private boolean isHoldingHoe(PlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        return (mainHandStack.getItem() instanceof HoeItem) ||
                (offHandStack.getItem() instanceof HoeItem);
    }

    private boolean shouldCancelAttack(PlayerEntity player, BlockState blockState) {
        Block block = blockState.getBlock();
        Item correspondingSeed = CROP_SEED_MAP.get(block);

        if (correspondingSeed == null) {
            return false;
        }

        int seedCount = 0;
        for (ItemStack stack : player.getInventory()) {
            if (stack.getItem() == correspondingSeed) {
                seedCount += stack.getCount();
            }
            if (seedCount >= CropModConfig.get().itemThreshold) {
                return false;
            }
        }

        return true;
    }

    private boolean isCropEnabled(Block block) {
        CropModConfig config = CropModConfig.get();
        String configKey = CROP_CONFIG_KEYS.get(block);
        if (configKey == null) {
            return false; // Not a supported crop
        }

        switch (configKey) {
            case "wheatEnabled":
                return config.wheatEnabled;
            case "carrotsEnabled":
                return config.carrotsEnabled;
            case "potatoesEnabled":
                return config.potatoesEnabled;
            case "beetrootsEnabled":
                return config.beetrootsEnabled;
            case "netherWartEnabled":
                return config.netherWartEnabled;
            case "cocoaEnabled":
                return config.cocoaEnabled;
            default:
                return false;
        }
    }

    private boolean isCropNotFullyGrown(BlockState blockState) {
        Block block = blockState.getBlock();

        if (block instanceof CropBlock) {
            CropBlock cropBlock = (CropBlock) block;
            return !cropBlock.isMature(blockState);
        } else if (block instanceof NetherWartBlock) {
            return blockState.get(NetherWartBlock.AGE) < 3;
        } else if (block instanceof CocoaBlock) {
            return blockState.get(CocoaBlock.AGE) < 2;
        }

        return false;
    }

    private boolean isFacingSameRow(PlayerEntity player, BlockPos blockPos) {
        Direction playerFacing = player.getHorizontalFacing();
        BlockPos playerPos = player.getBlockPos();
        switch (playerFacing) {
            case NORTH:
            case SOUTH:
                return playerPos.getX() == blockPos.getX();
            case WEST:
            case EAST:
                return playerPos.getZ() == blockPos.getZ();
            default:
                return false;
        }
    }

    private void snapCameraToNearest90Degrees(PlayerEntity player) {
        float yaw = player.getYaw();
        float snappedYaw = Math.round(yaw / 90.0f) * 90.0f;
        player.setYaw(snappedYaw);
    }
}
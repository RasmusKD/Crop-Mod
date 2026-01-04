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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class CropBlockMixin {

    @Unique
    private static final Map<Block, Item> CROP_SEED_MAP = new HashMap<>();
    @Unique
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
        // Check if mod is enabled first
        if (!CropModConfig.get().modEnabled) {
            return;
        }

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

        // Only apply CropMod features to enabled crops
        if (isCropEnabled(block)) {
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

            // Require seeds in inventory logic
            if (CropModConfig.get().requireSeedsInInventory && shouldCancelAttack(player, blockState)) {
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
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        // Check if mod is enabled first
        if (!CropModConfig.get().modEnabled) {
            return;
        }

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

        // Only apply CropMod features to enabled crops
        if (isCropEnabled(block)) {
            // Hoe requirement logic
            if (CropModConfig.get().requireHoeToBreakCrops && !isHoldingHoe(player)) {
                showProtectionEffects(client, blockPos);
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

            // Require seeds in inventory logic
            if (CropModConfig.get().requireSeedsInInventory && shouldCancelAttack(player, blockState)) {
                showProtectionEffects(client, blockPos);
                cir.cancel();
                return;
            }

            // Only harvest fully grown logic
            if (CropModConfig.get().onlyHarvestFullyGrown && isCropNotFullyGrown(blockState)) {
                showProtectionEffects(client, blockPos);
                cir.cancel();
            }
        }
    }

    @Unique
    private boolean isHoldingHoe(PlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        return (mainHandStack.getItem() instanceof HoeItem) ||
                (offHandStack.getItem() instanceof HoeItem);
    }

    @Unique
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

    @Unique
    private boolean isCropEnabled(Block block) {
        CropModConfig config = CropModConfig.get();
        String configKey = CROP_CONFIG_KEYS.get(block);
        if (configKey == null) {
            return false; // Not a supported crop
        }

        return switch (configKey) {
            case "wheatEnabled" -> config.wheatEnabled;
            case "carrotsEnabled" -> config.carrotsEnabled;
            case "potatoesEnabled" -> config.potatoesEnabled;
            case "beetrootsEnabled" -> config.beetrootsEnabled;
            case "netherWartEnabled" -> config.netherWartEnabled;
            case "cocoaEnabled" -> config.cocoaEnabled;
            default -> false;
        };
    }

    @Unique
    private boolean isCropNotFullyGrown(BlockState blockState) {
        Block block = blockState.getBlock();

        if (block instanceof CropBlock cropBlock) {
            return !cropBlock.isMature(blockState);
        } else if (block instanceof NetherWartBlock) {
            return blockState.get(NetherWartBlock.AGE) < 3;
        } else if (block instanceof CocoaBlock) {
            return blockState.get(CocoaBlock.AGE) < 2;
        }

        return false;
    }

    @Unique
    private boolean isFacingSameRow(PlayerEntity player, BlockPos blockPos) {
        Direction playerFacing = player.getHorizontalFacing();
        BlockPos playerPos = player.getBlockPos();
        return switch (playerFacing) {
            case NORTH, SOUTH -> playerPos.getX() == blockPos.getX();
            case WEST, EAST -> playerPos.getZ() == blockPos.getZ();
            default -> false;
        };
    }

    @Unique
    private void snapCameraToNearest90Degrees(PlayerEntity player) {
        float yaw = player.getYaw();
        float snappedYaw = Math.round(yaw / 90.0f) * 90.0f;
        player.setYaw(snappedYaw);
    }

    @Unique
    private void showProtectionEffects(MinecraftClient client, BlockPos blockPos) {
        // Show particles if enabled
        if (CropModConfig.get().showProtectionParticles) {
            spawnProtectionParticles(client, blockPos);
        }

        // Play sound if enabled (independent of particles)
        if (CropModConfig.get().playProtectionSounds) {
            playProtectionSound(client, blockPos);
        }
    }

    @Unique
    private void spawnProtectionParticles(MinecraftClient client, BlockPos blockPos) {
        if (client.world == null || client.particleManager == null) return;

        // Create a simple, clean barrier effect that adapts to crop height
        double x = blockPos.getX();
        double y = blockPos.getY();
        double z = blockPos.getZ();

        // Get the actual height of the crop
        BlockState blockState = client.world.getBlockState(blockPos);
        double cropHeight = getCropHeight(blockState);
        boolean isCocoa = blockState.getBlock() instanceof CocoaBlock;

        // Four corner posts that match the crop height
        for (double h = 0; h <= cropHeight; h += 0.2) {
            // Northwest corner
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x, y + h, z, 0, 0, 0);
            // Northeast corner
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x + 1, y + h, z, 0, 0, 0);
            // Southwest corner
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x, y + h, z + 1, 0, 0, 0);
            // Southeast corner
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x + 1, y + h, z + 1, 0, 0, 0);
        }

        // Top edge particles at the crop's actual height
        double topHeight = y + cropHeight;

        // North edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x + w, topHeight, z, 0, 0, 0);
        }
        // South edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x + w, topHeight, z + 1, 0, 0, 0);
        }
        // West edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x, topHeight, z + w, 0, 0, 0);
        }
        // East edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleManager.addParticle(ParticleTypes.ENCHANT, x + 1, topHeight, z + w, 0, 0, 0);
        }

        // For cocoa, also add bottom edge particles (since it's a full block height)
        if (isCocoa) {
            double bottomHeight = y;

            // North edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleManager.addParticle(ParticleTypes.ENCHANT, x + w, bottomHeight, z, 0, 0, 0);
            }
            // South edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleManager.addParticle(ParticleTypes.ENCHANT, x + w, bottomHeight, z + 1, 0, 0, 0);
            }
            // West edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleManager.addParticle(ParticleTypes.ENCHANT, x, bottomHeight, z + w, 0, 0, 0);
            }
            // East edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleManager.addParticle(ParticleTypes.ENCHANT, x + 1, bottomHeight, z + w, 0, 0, 0);
            }
        }
    }

    @Unique
    private void playProtectionSound(MinecraftClient client, BlockPos blockPos) {
        if (client.player == null || client.world == null) return;

        client.world.playSound(
                client.player,
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5,
                SoundEvents.BLOCK_NOTE_BLOCK_BASS,
                net.minecraft.sound.SoundCategory.BLOCKS,
                0.3f,
                0.5f
        );
    }

    @Unique
    private double getCropHeight(BlockState blockState) {
        Block block = blockState.getBlock();

        if (block instanceof CropBlock cropBlock) {
            int age = cropBlock.getAge(blockState);
            int maxAge = cropBlock.getMaxAge();
            // Scale height from 0.2 to 1.0 based on growth
            return 0.2 + (0.8 * ((double) age / maxAge));
        } else if (block instanceof NetherWartBlock) {
            int age = blockState.get(NetherWartBlock.AGE);
            // Scale height from 0.3 to 0.9 for nether wart
            return 0.3 + (0.6 * ((double) age / 3));
        } else if (block instanceof CocoaBlock) {
            // Cocoa beans grow on the side of blocks, use a consistent smaller height
            // regardless of growth stage since they don't grow "up" like regular crops
            return 1;
        }

        // Default height for unknown crops
        return 1;
    }
}
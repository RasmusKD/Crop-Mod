package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.config.CropModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("null")
@Mixin(Minecraft.class)
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

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(boolean breaking, CallbackInfo ci) {
        // Check if mod is enabled first
        if (!CropModConfig.get().modEnabled) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if (player == null || client.level == null || client.hitResult == null ||
                client.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) client.hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = client.level.getBlockState(blockPos);
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

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        // Check if mod is enabled first
        if (!CropModConfig.get().modEnabled) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if (player == null || client.level == null || client.hitResult == null ||
                client.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) client.hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = client.level.getBlockState(blockPos);
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
    private boolean isHoldingHoe(Player player) {
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();

        return mainHandStack.is(ItemTags.HOES) || offHandStack.is(ItemTags.HOES);
    }

    @Unique
    private boolean shouldCancelAttack(Player player, BlockState blockState) {
        Block block = blockState.getBlock();
        Item correspondingSeed = CROP_SEED_MAP.get(block);

        if (correspondingSeed == null) {
            return false;
        }

        int seedCount = 0;
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.getItem() == correspondingSeed) {
                seedCount += stack.getCount();
            }
            if (seedCount >= CropModConfig.get().itemThreshold) {
                return false;
            }
        }

        // Also count seeds held in the offhand
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == correspondingSeed) {
            seedCount += offhand.getCount();
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
            return !cropBlock.isMaxAge(blockState);
        } else if (block instanceof NetherWartBlock) {
            return blockState.getValue(NetherWartBlock.AGE) < 3;
        } else if (block instanceof CocoaBlock) {
            return blockState.getValue(CocoaBlock.AGE) < 2;
        }

        return false;
    }

    @Unique
    private boolean isFacingSameRow(Player player, BlockPos blockPos) {
        Direction playerFacing = player.getDirection();
        BlockPos playerPos = player.blockPosition();
        return switch (playerFacing) {
            case NORTH, SOUTH -> playerPos.getX() == blockPos.getX();
            case WEST, EAST -> playerPos.getZ() == blockPos.getZ();
            default -> false;
        };
    }

    @Unique
    private void snapCameraToNearest90Degrees(Player player) {
        float yaw = player.getYRot();
        float snappedYaw = Math.round(yaw / 90.0f) * 90.0f;
        player.setYRot(snappedYaw);
    }

    @Unique
    private void showProtectionEffects(Minecraft client, BlockPos blockPos) {
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
    private void spawnProtectionParticles(Minecraft client, BlockPos blockPos) {
        if (client.level == null || client.particleEngine == null)
            return;

        // Create a simple, clean barrier effect that adapts to crop height
        double x = blockPos.getX();
        double y = blockPos.getY();
        double z = blockPos.getZ();

        // Get the actual height of the crop
        BlockState blockState = client.level.getBlockState(blockPos);
        double cropHeight = getCropHeight(blockState);
        boolean isCocoa = blockState.getBlock() instanceof CocoaBlock;

        // Four corner posts that match the crop height
        for (double h = 0; h <= cropHeight; h += 0.2) {
            // Northwest corner
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x, y + h, z, 0, 0, 0);
            // Northeast corner
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + 1, y + h, z, 0, 0, 0);
            // Southwest corner
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x, y + h, z + 1, 0, 0, 0);
            // Southeast corner
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + 1, y + h, z + 1, 0, 0, 0);
        }

        // Top edge particles at the crop's actual height
        double topHeight = y + cropHeight;

        // North edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + w, topHeight, z, 0, 0, 0);
        }
        // South edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + w, topHeight, z + 1, 0, 0, 0);
        }
        // West edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x, topHeight, z + w, 0, 0, 0);
        }
        // East edge
        for (double w = 0.2; w <= 0.8; w += 0.3) {
            client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + 1, topHeight, z + w, 0, 0, 0);
        }

        // For cocoa, also add bottom edge particles (since it's a full block height)
        if (isCocoa) {
            double bottomHeight = y;

            // North edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + w, bottomHeight, z, 0, 0, 0);
            }
            // South edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + w, bottomHeight, z + 1, 0, 0, 0);
            }
            // West edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleEngine.createParticle(ParticleTypes.ENCHANT, x, bottomHeight, z + w, 0, 0, 0);
            }
            // East edge (bottom)
            for (double w = 0.2; w <= 0.8; w += 0.3) {
                client.particleEngine.createParticle(ParticleTypes.ENCHANT, x + 1, bottomHeight, z + w, 0, 0, 0);
            }
        }
    }

    @Unique
    private void playProtectionSound(Minecraft client, BlockPos blockPos) {
        if (client.player == null || client.level == null)
            return;

        client.level.playSound(
                client.player,
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5,
                SoundEvents.NOTE_BLOCK_BASS,
                SoundSource.BLOCKS,
                0.3f,
                0.5f);
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
            int age = blockState.getValue(NetherWartBlock.AGE);
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

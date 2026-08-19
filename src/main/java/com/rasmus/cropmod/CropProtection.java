package com.rasmus.cropmod;

import com.rasmus.cropmod.config.CropModConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The protection decisions, shared by the input layer mixin (startAttack,
 * continueAttack, which also does camera snap and effects) and the safety
 * net on MultiPlayerGameMode, which catches breaks other mods trigger
 * without going through the mouse.
 */
public final class CropProtection {

    private static final Map<Block, Item> CROP_SEED_MAP = new HashMap<>();
    private static final Map<Block, String> CROP_CONFIG_KEYS = new HashMap<>();

    static {
        CROP_SEED_MAP.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        CROP_SEED_MAP.put(Blocks.CARROTS, Items.CARROT);
        CROP_SEED_MAP.put(Blocks.POTATOES, Items.POTATO);
        CROP_SEED_MAP.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        CROP_SEED_MAP.put(Blocks.NETHER_WART, Items.NETHER_WART);
        CROP_SEED_MAP.put(Blocks.COCOA, Items.COCOA_BEANS);
        CROP_SEED_MAP.put(Blocks.TORCHFLOWER_CROP, Items.TORCHFLOWER_SEEDS);
        CROP_SEED_MAP.put(Blocks.PITCHER_CROP, Items.PITCHER_POD);

        CROP_CONFIG_KEYS.put(Blocks.WHEAT, "wheatEnabled");
        CROP_CONFIG_KEYS.put(Blocks.CARROTS, "carrotsEnabled");
        CROP_CONFIG_KEYS.put(Blocks.POTATOES, "potatoesEnabled");
        CROP_CONFIG_KEYS.put(Blocks.BEETROOTS, "beetrootsEnabled");
        CROP_CONFIG_KEYS.put(Blocks.NETHER_WART, "netherWartEnabled");
        CROP_CONFIG_KEYS.put(Blocks.COCOA, "cocoaEnabled");
        CROP_CONFIG_KEYS.put(Blocks.TORCHFLOWER_CROP, "torchflowerEnabled");
        CROP_CONFIG_KEYS.put(Blocks.PITCHER_CROP, "pitcherPlantEnabled");
    }

    private CropProtection() {
    }

    /** Every reason to block a break, in one place. Used by the safety net. */
    public static boolean shouldBlockBreak(Player player, BlockPos pos) {
        CropModConfig config = CropModConfig.get();
        if (!config.modEnabled) {
            return false;
        }
        var level = Minecraft.getInstance().level;
        if (level == null || player == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (isProtectedGrowthPoint(pos, block)) {
            return true;
        }
        if (isCropEnabled(block)) {
            if (config.requireHoeToBreakCrops && !isHoldingHoe(player)) {
                return true;
            }
            if (config.requireSeedsInInventory && shouldCancelAttack(player, state)) {
                return true;
            }
            if (config.onlyHarvestFullyGrown && isCropNotFullyGrown(state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sugar cane, bamboo, kelp and cactus regrow from their bottom block;
     * cave vines (glow berries) and pale hanging moss hang from their top
     * block. Stems, berry bushes and budding amethyst are never the thing
     * you harvest, so they are protected outright.
     */
    public static boolean isProtectedGrowthPoint(BlockPos pos, Block block) {
        CropModConfig config = CropModConfig.get();
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        if (block == Blocks.SUGAR_CANE) {
            return config.sugarCaneEnabled
                    && level.getBlockState(pos.below()).getBlock() != Blocks.SUGAR_CANE;
        }
        if (block == Blocks.BAMBOO || block == Blocks.BAMBOO_SAPLING) {
            Block below = level.getBlockState(pos.below()).getBlock();
            return config.bambooEnabled
                    && below != Blocks.BAMBOO && below != Blocks.BAMBOO_SAPLING;
        }
        if (block == Blocks.KELP || block == Blocks.KELP_PLANT) {
            return config.kelpEnabled
                    && level.getBlockState(pos.below()).getBlock() != Blocks.KELP_PLANT;
        }
        if (block == Blocks.CAVE_VINES || block == Blocks.CAVE_VINES_PLANT) {
            Block above = level.getBlockState(pos.above()).getBlock();
            return config.glowBerriesEnabled
                    && above != Blocks.CAVE_VINES && above != Blocks.CAVE_VINES_PLANT;
        }
        if (block == Blocks.PALE_HANGING_MOSS) {
            return config.paleMossEnabled
                    && level.getBlockState(pos.above()).getBlock() != Blocks.PALE_HANGING_MOSS;
        }
        if (block == Blocks.CACTUS) {
            return config.cactusEnabled
                    && level.getBlockState(pos.below()).getBlock() != Blocks.CACTUS;
        }
        if (block == Blocks.MELON_STEM || block == Blocks.PUMPKIN_STEM
                || block == Blocks.ATTACHED_MELON_STEM || block == Blocks.ATTACHED_PUMPKIN_STEM) {
            return config.protectStems;
        }
        if (block == Blocks.SWEET_BERRY_BUSH) {
            return config.protectBerryBushes;
        }
        if (block == Blocks.BUDDING_AMETHYST) {
            // gone forever if broken, drops nothing even with silk touch
            return config.protectBuddingAmethyst;
        }
        return false;
    }

    public static boolean isCropEnabled(Block block) {
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
            case "torchflowerEnabled" -> config.torchflowerEnabled;
            case "pitcherPlantEnabled" -> config.pitcherPlantEnabled;
            default -> false;
        };
    }

    public static boolean isHoldingHoe(Player player) {
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();

        return mainHandStack.is(ItemTags.HOES) || offHandStack.is(ItemTags.HOES);
    }

    /** True when the player is below the seed threshold for this crop. */
    public static boolean shouldCancelAttack(Player player, BlockState blockState) {
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

    public static boolean isCropNotFullyGrown(BlockState blockState) {
        Block block = blockState.getBlock();

        if (block instanceof CropBlock cropBlock) {
            return !cropBlock.isMaxAge(blockState);
        } else if (block instanceof NetherWartBlock) {
            return blockState.getValue(NetherWartBlock.AGE) < 3;
        } else if (block instanceof CocoaBlock) {
            return blockState.getValue(CocoaBlock.AGE) < 2;
        } else if (block instanceof PitcherCropBlock) {
            return blockState.getValue(PitcherCropBlock.AGE) < PitcherCropBlock.MAX_AGE;
        }

        return false;
    }
}

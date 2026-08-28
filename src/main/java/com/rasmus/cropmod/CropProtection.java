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

    static {
        CROP_SEED_MAP.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        CROP_SEED_MAP.put(Blocks.CARROTS, Items.CARROT);
        CROP_SEED_MAP.put(Blocks.POTATOES, Items.POTATO);
        CROP_SEED_MAP.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        CROP_SEED_MAP.put(Blocks.NETHER_WART, Items.NETHER_WART);
        CROP_SEED_MAP.put(Blocks.COCOA, Items.COCOA_BEANS);
        CROP_SEED_MAP.put(Blocks.TORCHFLOWER_CROP, Items.TORCHFLOWER_SEEDS);
        CROP_SEED_MAP.put(Blocks.PITCHER_CROP, Items.PITCHER_POD);

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
        // Creative needs no seed protection, spectators cannot break blocks;
        // note GameType.isSurvival() also covers ADVENTURE, so the gate keys
        // on abilities and spectator state instead.
        if (player.isSpectator() || player.getAbilities().instabuild) {
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
            // Breaking IS a harvest path here - lit vines drop their berries,
            // and lower segments regrow from the remaining body - so only the
            // ceiling anchor is protected: losing it kills the whole column.
            // (3.3.0 protected the plant outright on the audit's advice that
            // right-click was the only harvest; that missed the berry drop
            // and blocked harvesting entirely.)
            Block above = level.getBlockState(pos.above()).getBlock();
            return config.glowBerriesEnabled
                    && above != Blocks.CAVE_VINES && above != Blocks.CAVE_VINES_PLANT;
        }
        if (block == Blocks.PALE_HANGING_MOSS) {
            // Collecting moss means breaking segments; protect the anchor only.
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
        if (block == Blocks.WHEAT)
            return config.wheatEnabled;
        if (block == Blocks.CARROTS)
            return config.carrotsEnabled;
        if (block == Blocks.POTATOES)
            return config.potatoesEnabled;
        if (block == Blocks.BEETROOTS)
            return config.beetrootsEnabled;
        if (block == Blocks.NETHER_WART)
            return config.netherWartEnabled;
        if (block == Blocks.COCOA)
            return config.cocoaEnabled;
        if (block == Blocks.TORCHFLOWER_CROP)
            return config.torchflowerEnabled;
        if (block == Blocks.PITCHER_CROP)
            return config.pitcherPlantEnabled;
        return false; // Not a supported crop
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

        int threshold = CropModConfig.get().itemThreshold;
        int seedCount = 0;
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.getItem() == correspondingSeed) {
                seedCount += stack.getCount();
            }
            if (seedCount >= threshold) {
                return false;
            }
        }

        // Also count seeds held in the offhand
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == correspondingSeed) {
            seedCount += offhand.getCount();
            if (seedCount >= threshold) {
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
            return blockState.getValue(NetherWartBlock.AGE) < NetherWartBlock.MAX_AGE;
        } else if (block instanceof CocoaBlock) {
            return blockState.getValue(CocoaBlock.AGE) < CocoaBlock.MAX_AGE;
        } else if (block instanceof PitcherCropBlock) {
            return blockState.getValue(PitcherCropBlock.AGE) < PitcherCropBlock.MAX_AGE;
        }

        return false;
    }
}

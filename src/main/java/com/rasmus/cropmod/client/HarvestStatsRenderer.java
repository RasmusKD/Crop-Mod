package com.rasmus.cropmod.client;

import com.rasmus.cropmod.config.CropModConfig;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Renders the harvest statistics HUD. Shared by the Gui (26.1) and Hud
 * (26.2) mixins, which only differ in their hidden-HUD check: 26.1 has the
 * public Options.hideGui field, 26.2 moved it to Hud.isHidden().
 */
public final class HarvestStatsRenderer {

    private static Field hideGuiField;

    private static final ItemStack ITEM_WHEAT = new ItemStack(Items.WHEAT);
    private static final ItemStack ITEM_CARROT = new ItemStack(Items.CARROT);
    private static final ItemStack ITEM_POTATO = new ItemStack(Items.POTATO);
    private static final ItemStack ITEM_BEETROOT = new ItemStack(Items.BEETROOT);
    private static final ItemStack ITEM_NETHER_WART = new ItemStack(Items.NETHER_WART);
    private static final ItemStack ITEM_COCOA_BEANS = new ItemStack(Items.COCOA_BEANS);
    private static final ItemStack ITEM_BARRIER = new ItemStack(Items.BARRIER);

    private HarvestStatsRenderer() {
    }

    /** 26.1 only: reads Options.hideGui by reflection, the field is gone in 26.2. */
    private static boolean hideGuiLookupFailed;

    public static boolean legacyHudHidden() {
        // Failure is cached (no per-frame exception construction) and fails
        // CLOSED: if we cannot read the flag, suppress the overlay rather
        // than drawing over an F1-hidden interface.
        if (hideGuiLookupFailed) {
            return true;
        }
        var options = Minecraft.getInstance().options;
        try {
            if (hideGuiField == null) {
                hideGuiField = options.getClass().getField("hideGui");
            }
            return hideGuiField.getBoolean(options);
        } catch (ReflectiveOperationException e) {
            hideGuiLookupFailed = true;
            return true;
        }
    }

    public static void render(GuiGraphicsExtractor context) {
        CropModConfig config = CropModConfig.get();

        if (!config.modEnabled || !config.showHarvestStats) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        HarvestStatistics stats = HarvestStatistics.getInstance();
        Map<Block, Integer> cropCounts = stats.getSessionCountsByCrop();

        // Filter out crops with 0 count and sort by count descending
        List<Map.Entry<Block, Integer>> sortedCrops = new ArrayList<>();
        for (Map.Entry<Block, Integer> entry : cropCounts.entrySet()) {
            if (entry.getValue() > 0) {
                sortedCrops.add(entry);
            }
        }

        if (sortedCrops.isEmpty()) {
            return;
        }

        // Sort by count descending
        sortedCrops.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Limit to max crops
        int maxCrops = Math.max(1, config.statsMaxCrops);
        if (sortedCrops.size() > maxCrops) {
            sortedCrops = sortedCrops.subList(0, maxCrops);
        }

        Font textRenderer = minecraft.font;
        float scale = Math.max(0.5f, Math.min(2.0f, config.statsScale));
        int screenWidth = context.guiWidth();
        int screenHeight = context.guiHeight();

        // Calculate max width of all entries (icon + text)
        int maxTextWidth = 0;
        int cropCount = sortedCrops.size();
        String[] displayTexts = new String[cropCount];
        for (int i = 0; i < cropCount; i++) {
            Map.Entry<Block, Integer> entry = sortedCrops.get(i);
            String text = buildDisplayText(entry.getValue(), entry.getKey(), stats, config.statsDisplayMode);
            displayTexts[i] = text;
            int textWidth = textRenderer.width(text);
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
        }
        int entryWidth = 18 + maxTextWidth; // 16px icon + 2px gap + text
        int padding = 5;

        // Calculate base position
        int baseX;
        int baseY;

        // Calculate scaled dimensions for positioning
        int scaledWidth = (int) (screenWidth / scale);
        int scaledHeight = (int) (screenHeight / scale);
        int hudHeight = cropCount * 18;

        // Use custom position if set (stored as percentage 0.0-1.0), otherwise use
        // preset position
        if (config.statsCustomX >= 0 && config.statsCustomY >= 0) {
            // Custom position from percentage
            int maxX = Math.max(1, scaledWidth - entryWidth);
            int maxY = Math.max(1, scaledHeight - hudHeight);
            baseX = (int) (config.statsCustomX * maxX);
            baseY = (int) (config.statsCustomY * maxY);
        } else {
            // Default to top-left
            baseX = padding;
            baseY = padding;
        }

        // Clamp to screen bounds - never go outside
        baseX = Math.max(0, Math.min(baseX, scaledWidth - entryWidth));
        baseY = Math.max(0, Math.min(baseY, scaledHeight - hudHeight));

        // Apply scaling
        context.pose().pushMatrix();
        context.pose().scale(scale, scale);

        int lineHeight = 18;
        int currentY = baseY;

        for (int i = 0; i < cropCount; i++) {
            Map.Entry<Block, Integer> entry = sortedCrops.get(i);
            Block cropBlock = entry.getKey();

            // Get the item to display
            ItemStack itemStack = getCropItemStack(cropBlock);

            // Draw background if enabled
            if (config.statsShowBackground) {
                context.fill(baseX - 2, currentY - 2, baseX + entryWidth + 2, currentY + 14, 0x80000000);
            }

            // Draw item icon (16x16)
            context.item(itemStack, baseX, currentY - 1);

            // Draw count text - white with full alpha
            context.text(textRenderer, displayTexts[i], baseX + 18, currentY + 3, 0xFFFFFFFF, true);

            currentY += lineHeight;
        }

        context.pose().popMatrix();
    }

    /**
     * Build display text based on the configured display mode.
     */
    private static String buildDisplayText(int count, Block cropBlock, HarvestStatistics stats,
            CropModConfig.StatsDisplayMode mode) {
        switch (mode) {
            case SESSION:
                return String.valueOf(count);
            case PER_HOUR:
                int perHour = stats.getHarvestsPerHour(cropBlock);
                return perHour + "/hr";
            case PER_MIN:
                int perMin = stats.getHarvestsPerMinute(cropBlock);
                return perMin + "/min";
            case SESSION_HOUR:
                int hourRate = stats.getHarvestsPerHour(cropBlock);
                return count + " (" + hourRate + "/hr)";
            case SESSION_MIN:
            default:
                int minRate = stats.getHarvestsPerMinute(cropBlock);
                return count + " (" + minRate + "/min)";
        }
    }

    /**
     * Get the item stack that represents a crop for display.
     */
    private static ItemStack getCropItemStack(Block cropBlock) {
        if (cropBlock == Blocks.WHEAT)
            return ITEM_WHEAT;
        if (cropBlock == Blocks.CARROTS)
            return ITEM_CARROT;
        if (cropBlock == Blocks.POTATOES)
            return ITEM_POTATO;
        if (cropBlock == Blocks.BEETROOTS)
            return ITEM_BEETROOT;
        if (cropBlock == Blocks.NETHER_WART)
            return ITEM_NETHER_WART;
        if (cropBlock == Blocks.COCOA)
            return ITEM_COCOA_BEANS;
        return ITEM_BARRIER; // visible the day TRACKED_CROPS grows past this table
    }
}

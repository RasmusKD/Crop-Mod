package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatistics;
import com.rasmus.cropmod.config.CropModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mixin to render harvest statistics directly on the HUD.
 * Shows per-crop counts with item icons.
 */
@Mixin(InGameHud.class)
public class HudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderHarvestStats(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        CropModConfig config = CropModConfig.get();

        if (!config.modEnabled || !config.showHarvestStats) {
            return;
        }

        if (client.player == null || client.options.hudHidden) {
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

        TextRenderer textRenderer = client.textRenderer;
        float scale = Math.max(0.5f, Math.min(2.0f, config.statsScale));
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Calculate max width of all entries (icon + text)
        int maxTextWidth = 0;
        for (Map.Entry<Block, Integer> entry : sortedCrops) {
            String text = buildDisplayText(entry.getValue(), entry.getKey(), stats, config.statsDisplayMode);
            int textWidth = textRenderer.getWidth(text);
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
        int hudHeight = sortedCrops.size() * 18;

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
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale, scale);

        int lineHeight = 18;
        int currentY = baseY;

        for (Map.Entry<Block, Integer> entry : sortedCrops) {
            Block cropBlock = entry.getKey();
            int count = entry.getValue();

            // Get the item to display
            ItemStack itemStack = new ItemStack(getCropItem(cropBlock));

            // Draw background if enabled
            if (config.statsShowBackground) {
                context.fill(baseX - 2, currentY - 2, baseX + entryWidth + 2, currentY + 14, 0x80000000);
            }

            // Draw item icon (16x16)
            context.drawItem(itemStack, baseX, currentY - 1);

            // Build text based on display mode
            String countText = buildDisplayText(count, cropBlock, stats, config.statsDisplayMode);

            // Draw count text - white with full alpha
            context.drawText(textRenderer, countText, baseX + 18, currentY + 3, 0xFFFFFFFF, true);

            currentY += lineHeight;
        }

        context.getMatrices().popMatrix();
    }

    /**
     * Build display text based on the configured display mode.
     */
    private String buildDisplayText(int count, Block cropBlock, HarvestStatistics stats,
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
     * Get the item that represents a crop for display.
     */
    private Item getCropItem(Block cropBlock) {
        if (cropBlock == Blocks.WHEAT)
            return Items.WHEAT;
        if (cropBlock == Blocks.CARROTS)
            return Items.CARROT;
        if (cropBlock == Blocks.POTATOES)
            return Items.POTATO;
        if (cropBlock == Blocks.BEETROOTS)
            return Items.BEETROOT;
        if (cropBlock == Blocks.NETHER_WART)
            return Items.NETHER_WART;
        if (cropBlock == Blocks.COCOA)
            return Items.COCOA_BEANS;
        return Items.BARRIER; // Fallback
    }
}

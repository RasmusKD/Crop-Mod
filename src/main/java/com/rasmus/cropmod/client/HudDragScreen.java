package com.rasmus.cropmod.client;

import com.rasmus.cropmod.config.CropModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Screen for dragging the HUD to a custom position.
 * Shows a single line preview matching the actual HUD scale.
 */
public class HudDragScreen extends Screen {
    private final CropModConfig config;
    private int hudX;
    private int hudY;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private int lastMouseX;
    private int lastMouseY;

    private static final ItemStack CARROT = new ItemStack(Items.CARROT);
    private static final String SAMPLE_TEXT = "100 (5/m)";

    // Cache preview dimensions
    private int previewWidth;
    private int previewHeight;

    public HudDragScreen() {
        super(Component.literal("Position HUD"));
        this.config = CropModConfig.get();
        this.hudX = -1;
        this.hudY = -1;
    }

    @Override
    protected void init() {
        // Calculate preview size once
        float scale = Math.max(0.5f, Math.min(2.0f, config.statsScale));
        int textWidth = this.font.width(SAMPLE_TEXT);
        previewWidth = (int) ((18 + textWidth + 6) * scale); // 16 icon + 2 gap + text + padding
        previewHeight = (int) (20 * scale);

        // Convert percentage to pixels
        if (config.statsCustomX >= 0 && config.statsCustomY >= 0) {
            int maxX = Math.max(1, this.width - previewWidth);
            int maxY = Math.max(1, this.height - previewHeight);
            hudX = (int) (config.statsCustomX * maxX);
            hudY = (int) (config.statsCustomY * maxY);
        } else {
            hudX = 10;
            hudY = 10;
        }

        // Clamp to screen bounds
        hudX = Math.max(0, Math.min(hudX, this.width - previewWidth));
        hudY = Math.max(0, Math.min(hudY, this.height - previewHeight));

        int buttonY = 45;

        addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
            int maxX = Math.max(1, this.width - previewWidth);
            int maxY = Math.max(1, this.height - previewHeight);
            config.statsCustomX = (float) hudX / (float) maxX;
            config.statsCustomY = (float) hudY / (float) maxY;
            // Clamp percentage to 0-1
            config.statsCustomX = Math.max(0f, Math.min(1f, config.statsCustomX));
            config.statsCustomY = Math.max(0f, Math.min(1f, config.statsCustomY));
            AutoConfig.getConfigHolder(CropModConfig.class).save();
            onClose();
        }).bounds(this.width / 2 - 75, buttonY, 70, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Reset"), button -> {
            config.statsCustomX = -1f;
            config.statsCustomY = -1f;
            AutoConfig.getConfigHolder(CropModConfig.class).save();
            onClose();
        }).bounds(this.width / 2 + 5, buttonY, 70, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        float scale = Math.max(0.5f, Math.min(2.0f, config.statsScale));

        // Semi-transparent background
        context.fill(0, 0, this.width, this.height, 0x80000000);

        // Handle dragging - clamp to bounds
        if (dragging) {
            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;
            hudX = Math.max(0, Math.min(newX, this.width - previewWidth));
            hudY = Math.max(0, Math.min(newY, this.height - previewHeight));
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        // Instructions
        context.centeredText(this.font,
                Component.literal("Drag to position (Scale: " + String.format("%.1f", scale) + "x)"),
                this.width / 2, 10, 0xFFFFFFFF);
        context.centeredText(this.font,
                Component.literal("X: " + hudX + " Y: " + hudY),
                this.width / 2, 25, 0xFF888888);

        // Apply scaling like the real HUD
        context.pose().pushMatrix();
        context.pose().scale(scale, scale);

        int scaledX = (int) (hudX / scale);
        int scaledY = (int) (hudY / scale);
        int scaledWidth = (int) (previewWidth / scale);
        int scaledHeight = (int) (previewHeight / scale);

        // Box background
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, 0xE0000000);

        // Border
        int boxColor = dragging ? 0xFFFFFF00 : 0xFF00FF00;
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + 1, boxColor);
        context.fill(scaledX, scaledY + scaledHeight - 1, scaledX + scaledWidth, scaledY + scaledHeight, boxColor);
        context.fill(scaledX, scaledY, scaledX + 1, scaledY + scaledHeight, boxColor);
        context.fill(scaledX + scaledWidth - 1, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, boxColor);

        // Single line: icon + text
        context.item(CARROT, scaledX + 1, scaledY + 1);
        context.text(this.font, SAMPLE_TEXT, scaledX + 18, scaledY + 5, 0xFFFFFFFF, true);

        context.pose().popMatrix();

        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    private boolean isOverHud(int mouseX, int mouseY) {
        return mouseX >= hudX && mouseX <= hudX + previewWidth &&
                mouseY >= hudY && mouseY <= hudY + previewHeight;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.minecraft != null && this.minecraft.mouseHandler != null) {
            boolean leftDown = org.lwjgl.glfw.GLFW.glfwGetMouseButton(
                    this.minecraft.getWindow().handle(),
                    org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

            if (leftDown && !dragging) {
                if (isOverHud(lastMouseX, lastMouseY)) {
                    dragging = true;
                    dragOffsetX = lastMouseX - hudX;
                    dragOffsetY = lastMouseY - hudY;
                }
            } else if (!leftDown) {
                dragging = false;
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(null);
    }
}

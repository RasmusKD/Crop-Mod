package com.rasmus.cropmod.client;

import com.rasmus.cropmod.config.CropModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

/**
 * Drag screen for the stats HUD position. The math runs in the renderer's
 * own coordinate space (scaled units, fraction against scaledWidth minus
 * the unscaled entry width), so the saved fraction reproduces the previewed
 * pixel at every scale - the old version divided by a different denominator
 * and drifted at any scale other than 1.0. Dragging uses the Screen mouse
 * callbacks instead of polling raw GLFW at 20Hz.
 */
public class HudDragScreen extends Screen {

    private static final ItemStack CARROT = new ItemStack(Items.CARROT);
    private static final String SAMPLE_TEXT = "100 (5/m)";

    private final CropModConfig config;
    /** The screen to return to (the Cloth config screen keeps unsaved edits). */
    private final @Nullable Screen parent;

    private float scale;
    /** Preview geometry in the renderer's scaled units. */
    private int entryWidth;
    private int entryHeight;
    private int hudX;
    private int hudY;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public HudDragScreen(@Nullable Screen parent) {
        super(Component.literal("Position HUD"));
        this.config = CropModConfig.get();
        this.parent = parent;
    }

    private int scaledWidth() {
        return (int) (this.width / scale);
    }

    private int scaledHeight() {
        return (int) (this.height / scale);
    }

    @Override
    protected void init() {
        scale = Math.max(0.5f, Math.min(2.0f, config.statsScale));
        entryWidth = 18 + this.font.width(SAMPLE_TEXT); // renderer: 16px icon + 2px gap + text
        entryHeight = 18;

        int maxX = Math.max(1, scaledWidth() - entryWidth);
        int maxY = Math.max(1, scaledHeight() - entryHeight);
        if (config.statsCustomX >= 0 && config.statsCustomY >= 0) {
            hudX = (int) (config.statsCustomX * maxX);
            hudY = (int) (config.statsCustomY * maxY);
        } else {
            hudX = 5;
            hudY = 5;
        }
        hudX = Math.max(0, Math.min(hudX, maxX));
        hudY = Math.max(0, Math.min(hudY, maxY));

        int buttonY = this.height - 40;
        addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
            config.statsCustomX = Math.max(0f, Math.min(1f,
                    (float) hudX / Math.max(1, scaledWidth() - entryWidth)));
            config.statsCustomY = Math.max(0f, Math.min(1f,
                    (float) hudY / Math.max(1, scaledHeight() - entryHeight)));
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
        context.fill(0, 0, this.width, this.height, 0x80000000);

        context.centeredText(this.font,
                Component.literal("Drag to position (Scale: " + String.format("%.1f", scale) + "x)"),
                this.width / 2, 10, 0xFFFFFFFF);
        context.centeredText(this.font,
                Component.literal("The HUD grows down and right from this corner as crops accumulate"),
                this.width / 2, 25, 0xFF888888);

        context.pose().pushMatrix();
        context.pose().scale(scale, scale);

        context.fill(hudX - 2, hudY - 2, hudX + entryWidth + 2, hudY + entryHeight - 2, 0xE0000000);
        int boxColor = dragging ? 0xFFFFFF00 : 0xFF00FF00;
        context.fill(hudX - 2, hudY - 2, hudX + entryWidth + 2, hudY - 1, boxColor);
        context.fill(hudX - 2, hudY + entryHeight - 3, hudX + entryWidth + 2, hudY + entryHeight - 2, boxColor);
        context.fill(hudX - 2, hudY - 2, hudX - 1, hudY + entryHeight - 2, boxColor);
        context.fill(hudX + entryWidth + 1, hudY - 2, hudX + entryWidth + 2, hudY + entryHeight - 2, boxColor);

        context.item(CARROT, hudX, hudY - 1);
        context.text(this.font, SAMPLE_TEXT, hudX + 18, hudY + 3, 0xFFFFFFFF, true);

        context.pose().popMatrix();

        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    private boolean overPreview(double mouseX, double mouseY) {
        int mx = (int) (mouseX / scale);
        int my = (int) (mouseY / scale);
        return mx >= hudX - 2 && mx <= hudX + entryWidth + 2
                && my >= hudY - 2 && my <= hudY + entryHeight - 2;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (overPreview(event.x(), event.y())) {
            dragging = true;
            dragOffsetX = (int) (event.x() / scale) - hudX;
            dragOffsetY = (int) (event.y() / scale) - hudY;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            hudX = Math.max(0, Math.min((int) (event.x() / scale) - dragOffsetX,
                    scaledWidth() - entryWidth));
            hudY = Math.max(0, Math.min((int) (event.y() / scale) - dragOffsetY,
                    scaledHeight() - entryHeight));
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

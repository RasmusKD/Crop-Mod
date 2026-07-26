package com.rasmus.cropmod.config;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

/**
 * A config entry that displays a clickable button.
 */
@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class ButtonListEntry extends TooltipListEntry<Void> {

    private final Button buttonWidget;
    private final List<Button> widgets;
    private final Runnable onClick;

    public ButtonListEntry(Component fieldName, Component buttonText, Runnable onClick) {
        super(fieldName, null, false);
        this.onClick = onClick;
        this.buttonWidget = Button.builder(buttonText, button -> {
            if (this.onClick != null) {
                this.onClick.run();
            }
        }).bounds(0, 0, 150, 20).build();
        this.widgets = Lists.newArrayList(buttonWidget);
    }

    @Override
    public boolean isEdited() {
        return false;
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public Optional<Void> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int index, int y, int x, int entryWidth,
            int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);

        // Draw field name on the left
        Component displayedFieldName = getDisplayedFieldName();
        context.text(Minecraft.getInstance().font,
                displayedFieldName, x, y + 6, getPreferredTextColor(), true);

        // Position button on the right
        this.buttonWidget.setX(x + entryWidth - 150);
        this.buttonWidget.setY(y);
        this.buttonWidget.extractRenderState(context, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }
}

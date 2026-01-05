package com.rasmus.cropmod.config;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * A config entry that displays a clickable button.
 */
@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class ButtonListEntry extends TooltipListEntry<Void> {

    private final ButtonWidget buttonWidget;
    private final List<ButtonWidget> widgets;
    private final Runnable onClick;

    public ButtonListEntry(Text fieldName, Text buttonText, Runnable onClick) {
        super(fieldName, null, false);
        this.onClick = onClick;
        this.buttonWidget = ButtonWidget.builder(buttonText, button -> {
            if (this.onClick != null) {
                this.onClick.run();
            }
        }).dimensions(0, 0, 150, 20).build();
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
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
            int mouseY, boolean isHovered, float delta) {
        super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);

        // Draw field name on the left
        Text displayedFieldName = getDisplayedFieldName();
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer,
                displayedFieldName, x, y + 6, getPreferredTextColor());

        // Position button on the right
        this.buttonWidget.setX(x + entryWidth - 150);
        this.buttonWidget.setY(y);
        this.buttonWidget.render(context, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }

    @Override
    public List<? extends Selectable> narratables() {
        return widgets;
    }
}

package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatsRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.1: the HUD class is Gui and the hidden flag is Options.hideGui. The
 * target is a string because 26.2 also has a Gui class with a different
 * extractRenderState signature; the plugin only applies this on 26.1.
 */
@Mixin(targets = "net.minecraft.client.gui.Gui")
public class GuiStatsLegacyMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderHarvestStats(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (HarvestStatsRenderer.legacyHudHidden()) {
            return;
        }
        HarvestStatsRenderer.render(context);
    }
}

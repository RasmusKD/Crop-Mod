package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatsRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2: the HUD class is Hud and the hidden flag lives on it. The plugin
 * only applies this mixin on 26.2+.
 */
@Mixin(Hud.class)
public class HudStatsModernMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderHarvestStats(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (((Hud) (Object) this).isHidden()) {
            return;
        }
        HarvestStatsRenderer.render(context);
    }
}

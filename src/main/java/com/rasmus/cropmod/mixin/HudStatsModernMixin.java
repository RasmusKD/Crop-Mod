package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.client.HarvestStatsRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2: the HUD class is Hud and the hidden flag lives on it. The plugin
 * only applies this mixin on 26.2+.
 */
@Mixin(targets = "net.minecraft.client.gui.Hud")
public abstract class HudStatsModernMixin {

    // String target and a shadow instead of a hard class reference: Hud does
    // not exist on 26.1, and mixin resolves declared targets before the
    // plugin's shouldApplyMixin can veto - a hard reference was a latent
    // apply failure on the version this mixin is switched off for.
    @org.spongepowered.asm.mixin.Shadow
    public abstract boolean isHidden();

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderHarvestStats(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (this.isHidden()) {
            return;
        }
        HarvestStatsRenderer.render(context);
    }
}

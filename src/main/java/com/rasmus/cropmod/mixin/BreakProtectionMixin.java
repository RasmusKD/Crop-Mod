package com.rasmus.cropmod.mixin;

import com.rasmus.cropmod.CropProtection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Safety net one layer below the input mixin: catches block breaks that
 * never went through startAttack, like other mods calling the game mode
 * directly. Cancels silently, the input layer handles feedback.
 */
@Mixin(MultiPlayerGameMode.class)
public class BreakProtectionMixin {

    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void guardStartDestroy(BlockPos pos, Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        if (CropProtection.shouldBlockBreak(Minecraft.getInstance().player, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void guardContinueDestroy(BlockPos pos, Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        if (CropProtection.shouldBlockBreak(Minecraft.getInstance().player, pos)) {
            cir.setReturnValue(false);
        }
    }
}

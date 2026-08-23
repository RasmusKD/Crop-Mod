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
            // Returning false at HEAD also skips vanilla's own abort of a
            // previous in-progress break, so perform it: stopDestroyBlock is
            // the one call that sends ABORT_DESTROY_BLOCK, clears
            // isDestroying and clears the crack overlay (no-op when idle).
            cropmod$self().stopDestroyBlock();
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void guardContinueDestroy(BlockPos pos, Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        if (CropProtection.shouldBlockBreak(Minecraft.getInstance().player, pos)) {
            cropmod$self().stopDestroyBlock();
            cir.setReturnValue(false);
        }
    }

    /**
     * The chokepoint every break funnels through (creative start, survival
     * instant-break, creative continue, survival completion, and any mod
     * calling the game mode directly). Guarding only the entry points left
     * exactly the direct-call case this class exists for.
     */
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void guardDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CropProtection.shouldBlockBreak(Minecraft.getInstance().player, pos)) {
            cropmod$self().stopDestroyBlock();
            cir.setReturnValue(false);
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private MultiPlayerGameMode cropmod$self() {
        return (MultiPlayerGameMode) (Object) this;
    }
}

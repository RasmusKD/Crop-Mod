package com.rasmus.cropmod.client;

import com.rasmus.cropmod.config.CropModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CropModClient implements ClientModInitializer {
    private static KeyBinding toggleModKeyBinding;
    private static KeyBinding toggleCameraSnapKeyBinding;
    private static KeyBinding toggleStatsKeyBinding;

    // Create a custom category for CropMod keybindings
    private static final KeyBinding.Category CROPMOD_CATEGORY = KeyBinding.Category.create(
            Identifier.of("cropmod", "category"));

    @Override
    public void onInitializeClient() {
        // HUD rendering is now handled by HudMixin

        // Register key bindings
        toggleModKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.cropmod.toggleMod",
                        InputUtil.Type.KEYSYM,
                        66, // B key
                        CROPMOD_CATEGORY));

        toggleCameraSnapKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.cropmod.toggleCameraSnap",
                        InputUtil.Type.KEYSYM,
                        79, // O key
                        CROPMOD_CATEGORY));

        toggleStatsKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.cropmod.toggleStats",
                        InputUtil.Type.KEYSYM,
                        72, // H key
                        CROPMOD_CATEGORY));

        // Register tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleModKeyBinding.wasPressed()) {
                CropModConfig config = CropModConfig.get();

                // Simply toggle the master switch - all settings are preserved
                config.modEnabled = !config.modEnabled;
                AutoConfig.getConfigHolder(CropModConfig.class).save();

                String message = config.modEnabled ? "CropMod enabled" : "CropMod disabled";

                if (client.player != null) {
                    client.player.sendMessage(Text.literal(message), false);
                }
            }

            while (toggleCameraSnapKeyBinding.wasPressed()) {
                CropModConfig config = CropModConfig.get();
                config.cameraSnapEnabled = !config.cameraSnapEnabled;
                AutoConfig.getConfigHolder(CropModConfig.class).save();

                String message = config.cameraSnapEnabled ? "Camera snap enabled" : "Camera snap disabled";

                if (client.player != null) {
                    client.player.sendMessage(Text.literal(message), false);
                }
            }

            while (toggleStatsKeyBinding.wasPressed()) {
                CropModConfig config = CropModConfig.get();

                // If shift is held, reset stats
                if (client.options.sneakKey.isPressed()) {
                    HarvestStatistics.getInstance().reset();
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§6Harvest statistics reset!"), false);
                    }
                }
                // If control/sprint is held, open drag screen
                else if (client.options.sprintKey.isPressed()) {
                    client.setScreen(new HudDragScreen());
                } else {
                    config.showHarvestStats = !config.showHarvestStats;
                    AutoConfig.getConfigHolder(CropModConfig.class).save();

                    String message = config.showHarvestStats ? "Harvest stats enabled" : "Harvest stats disabled";

                    if (client.player != null) {
                        client.player.sendMessage(Text.literal(message), false);
                    }
                }
            }
        });
    }
}

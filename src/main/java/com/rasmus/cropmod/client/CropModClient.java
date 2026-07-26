package com.rasmus.cropmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.rasmus.cropmod.config.CropModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CropModClient implements ClientModInitializer {
    private static KeyMapping toggleModKeyBinding;
    private static KeyMapping toggleCameraSnapKeyBinding;
    private static KeyMapping toggleStatsKeyBinding;

    // Create a custom category for CropMod keybindings
    private static final KeyMapping.Category CROPMOD_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("cropmod", "category"));

    @Override
    public void onInitializeClient() {
        // HUD rendering is now handled by HudMixin

        // Register key bindings
        toggleModKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.cropmod.toggleMod",
                        InputConstants.Type.KEYSYM,
                        66, // B key
                        CROPMOD_CATEGORY));

        toggleCameraSnapKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.cropmod.toggleCameraSnap",
                        InputConstants.Type.KEYSYM,
                        79, // O key
                        CROPMOD_CATEGORY));

        toggleStatsKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.cropmod.toggleStats",
                        InputConstants.Type.KEYSYM,
                        72, // H key
                        CROPMOD_CATEGORY));

        // Register tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleModKeyBinding.consumeClick()) {
                CropModConfig config = CropModConfig.get();

                // Simply toggle the master switch - all settings are preserved
                config.modEnabled = !config.modEnabled;
                AutoConfig.getConfigHolder(CropModConfig.class).save();

                String message = config.modEnabled ? "CropMod enabled" : "CropMod disabled";

                if (client.player != null) {
                    client.player.sendSystemMessage(Component.literal(message));
                }
            }

            while (toggleCameraSnapKeyBinding.consumeClick()) {
                CropModConfig config = CropModConfig.get();
                config.cameraSnapEnabled = !config.cameraSnapEnabled;
                AutoConfig.getConfigHolder(CropModConfig.class).save();

                String message = config.cameraSnapEnabled ? "Camera snap enabled" : "Camera snap disabled";

                if (client.player != null) {
                    client.player.sendSystemMessage(Component.literal(message));
                }
            }

            while (toggleStatsKeyBinding.consumeClick()) {
                CropModConfig config = CropModConfig.get();

                // If shift is held, reset stats
                if (client.options.keyShift.isDown()) {
                    HarvestStatistics.getInstance().reset();
                    if (client.player != null) {
                        client.player.sendSystemMessage(Component.literal("§6Harvest statistics reset!"));
                    }
                }
                // If control/sprint is held, open drag screen
                else if (client.options.keySprint.isDown()) {
                    client.setScreen(new HudDragScreen());
                } else {
                    config.showHarvestStats = !config.showHarvestStats;
                    AutoConfig.getConfigHolder(CropModConfig.class).save();

                    String message = config.showHarvestStats ? "Harvest stats enabled" : "Harvest stats disabled";

                    if (client.player != null) {
                        client.player.sendSystemMessage(Component.literal(message));
                    }
                }
            }
        });
    }
}

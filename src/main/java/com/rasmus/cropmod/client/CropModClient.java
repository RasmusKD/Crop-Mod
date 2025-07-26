package com.rasmus.cropmod.client;

import com.rasmus.cropmod.config.CropModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class CropModClient implements ClientModInitializer {
    private static KeyBinding toggleProtectionKeyBinding;
    private static KeyBinding toggleCameraSnapKeyBinding;

    @Override
    public void onInitializeClient() {
        // Don't register config here - it's already registered in the main mod initializer

        // Register key bindings
        toggleProtectionKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.cropmod.toggleProtection",
                        InputUtil.Type.KEYSYM,
                        80, // P key
                        "key.category.cropmod"
                )
        );

        toggleCameraSnapKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.cropmod.toggleCameraSnap",
                        InputUtil.Type.KEYSYM,
                        79, // O key
                        "key.category.cropmod"
                )
        );

        // Register tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleProtectionKeyBinding.wasPressed()) {
                CropModConfig config = CropModConfig.get();
                config.cropProtectionEnabled = !config.cropProtectionEnabled;
                AutoConfig.getConfigHolder(CropModConfig.class).save();

                String message = config.cropProtectionEnabled ?
                        "Crop protection enabled" : "Crop protection disabled";

                if (client.player != null) {
                    client.player.sendMessage(Text.literal(message), false);
                }
            }

            while (toggleCameraSnapKeyBinding.wasPressed()) {
                CropModConfig config = CropModConfig.get();
                config.cameraSnapEnabled = !config.cameraSnapEnabled;
                AutoConfig.getConfigHolder(CropModConfig.class).save();

                String message = config.cameraSnapEnabled ?
                        "Camera snap enabled" : "Camera snap disabled";

                if (client.player != null) {
                    client.player.sendMessage(Text.literal(message), false);
                }
            }
        });
    }
}
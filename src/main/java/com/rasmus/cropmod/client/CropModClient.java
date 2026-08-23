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

    // 26.1 exposes the current screen as the public field Minecraft.screen,
    // 26.2 as the method Minecraft.screen(). A direct reference to either is
    // a NoSuchFieldError/NoSuchMethodError on the other version, so the
    // lookup happens once by name (same pattern as Rare Fish Finder 2.5.3).
    private static java.lang.reflect.Method screenMethod;
    private static java.lang.reflect.Field screenField;
    private static boolean screenLookupFailed;

    public static net.minecraft.client.gui.screens.Screen currentScreen(net.minecraft.client.Minecraft client) {
        if (screenLookupFailed) {
            return null;
        }
        try {
            if (screenMethod == null && screenField == null) {
                try {
                    screenMethod = net.minecraft.client.Minecraft.class.getMethod("screen");
                } catch (NoSuchMethodException e) {
                    screenField = net.minecraft.client.Minecraft.class.getField("screen");
                }
            }
            Object result = screenMethod != null ? screenMethod.invoke(client) : screenField.get(client);
            return (net.minecraft.client.gui.screens.Screen) result;
        } catch (ReflectiveOperationException e) {
            screenLookupFailed = true;
            return null;
        }
    }

    private static boolean modifierDown(net.minecraft.client.Minecraft client, int left, int right) {
        return InputConstants.isKeyDown(client.getWindow(), left)
                || InputConstants.isKeyDown(client.getWindow(), right);
    }

    private static boolean shiftDown(net.minecraft.client.Minecraft client) {
        return modifierDown(client, 340, 344); // GLFW LEFT/RIGHT SHIFT
    }

    private static boolean ctrlDown(net.minecraft.client.Minecraft client) {
        return modifierDown(client, 341, 345); // GLFW LEFT/RIGHT CONTROL
    }
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

        // Harvest counts confirm here a few ticks after the predicted break,
        // so server-rejected breaks never reach the statistics.
        ClientTickEvents.END_CLIENT_TICK.register(PendingHarvests::tick);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.register(
                (handler, sender, client) -> {
                    PendingHarvests.clear();
                    HarvestStatistics.getInstance().reset();
                });

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
                if (shiftDown(client)) {
                    HarvestStatistics.getInstance().reset();
                    if (client.player != null) {
                        client.player.sendSystemMessage(Component.literal("§6Harvest statistics reset!"));
                    }
                }
                // If control/sprint is held, open drag screen
                else if (ctrlDown(client)) {
                    client.setScreenAndShow(new HudDragScreen(currentScreen(client)));
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

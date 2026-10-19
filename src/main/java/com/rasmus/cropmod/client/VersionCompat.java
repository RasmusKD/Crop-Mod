package com.rasmus.cropmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Members that differ between 26.1/26.2 and 26.3. The 26.3 jar replaces this class. */
public final class VersionCompat {
    // 26.1 exposes the current screen as the public field Minecraft.screen,
    // 26.2 as the method Minecraft.screen(). A direct reference to either is
    // a NoSuchFieldError/NoSuchMethodError on the other version, so the
    // lookup happens once by name (same pattern as Rare Fish Finder 2.5.3).
    private static java.lang.reflect.Method screenMethod;
    private static java.lang.reflect.Field screenField;
    private static boolean screenLookupFailed;

    private VersionCompat() {
    }

    public static Screen currentScreen(Minecraft client) {
        if (screenLookupFailed) {
            return null;
        }
        try {
            if (screenMethod == null && screenField == null) {
                try {
                    screenMethod = Minecraft.class.getMethod("screen");
                } catch (NoSuchMethodException e) {
                    screenField = Minecraft.class.getField("screen");
                }
            }
            Object result = screenMethod != null ? screenMethod.invoke(client) : screenField.get(client);
            return (Screen) result;
        } catch (ReflectiveOperationException e) {
            screenLookupFailed = true;
            return null;
        }
    }

    public static boolean isKeyDown(Minecraft client, int key) {
        return InputConstants.isKeyDown(client.getWindow(), key);
    }
}

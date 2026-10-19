package com.rasmus.cropmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** 26.3 versions of the members that differ from 26.1/26.2; replaces the class of the same name. */
public final class VersionCompat {
    private VersionCompat() {
    }

    public static Screen currentScreen(Minecraft client) {
        return client.gui.screen();
    }

    public static boolean isKeyDown(Minecraft client, int key) {
        return InputConstants.isKeyDown(key);
    }
}

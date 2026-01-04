package com.rasmus.cropmod.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "cropmod")
public class CropModConfig implements ConfigData {

    // Master toggle for the entire mod
    @ConfigEntry.Gui.Tooltip
    public boolean modEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public boolean requireSeedsInInventory = false;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 640)
    @ConfigEntry.Gui.Tooltip
    public int itemThreshold = 67;

    @ConfigEntry.Gui.Tooltip
    public boolean onlyHarvestFullyGrown = true;

    @ConfigEntry.Gui.Tooltip
    public boolean requireHoeToBreakCrops = false;

    @ConfigEntry.Gui.Tooltip
    public boolean showProtectionParticles = false; // Changed to false by default

    @ConfigEntry.Gui.Tooltip
    public boolean playProtectionSounds = false; // Changed to false by default

    // Camera snap settings moved to bottom
    @ConfigEntry.Gui.Tooltip
    public boolean cameraSnapEnabled = false;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    public CameraSnapMode cameraSnapMode = CameraSnapMode.BREAK;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    public CameraSnapDirectionMode cameraSnapDirectionMode = CameraSnapDirectionMode.SAME_ROW;

    // Individual crop settings (no longer nested)
    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    public boolean wheatEnabled = true;

    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    public boolean carrotsEnabled = true;

    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    public boolean potatoesEnabled = true;

    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    public boolean beetrootsEnabled = true;

    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    public boolean netherWartEnabled = true;

    @ConfigEntry.Category("crops")
    @ConfigEntry.Gui.Tooltip
    public boolean cocoaEnabled = true;

    public enum CameraSnapMode {
        ALWAYS,
        BREAK
    }

    public enum CameraSnapDirectionMode {
        ALWAYS,
        SAME_ROW
    }

    public static void register() {
        AutoConfig.register(CropModConfig.class, GsonConfigSerializer::new);
    }

    public static CropModConfig get() {
        return AutoConfig.getConfigHolder(CropModConfig.class).getConfig();
    }
}
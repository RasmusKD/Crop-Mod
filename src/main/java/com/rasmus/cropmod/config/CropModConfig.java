package com.rasmus.cropmod.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "cropmod")
public class CropModConfig implements ConfigData {

    // LIVE PREVIEW: These transient fields hold real-time values when config screen
    // is open
    @ConfigEntry.Gui.Excluded
    public transient StatsDisplayMode previewDisplayMode = null;
    @ConfigEntry.Gui.Excluded
    public transient StatsPosition previewPosition = null;
    @ConfigEntry.Gui.Excluded
    public transient Float previewScale = null;
    @ConfigEntry.Gui.Excluded
    public transient Boolean previewShowBackground = null;
    @ConfigEntry.Gui.Excluded
    public transient Integer previewMaxCrops = null;

    // Getters that return preview value if set, otherwise saved value
    public StatsDisplayMode getDisplayMode() {
        return previewDisplayMode != null ? previewDisplayMode : statsDisplayMode;
    }

    public StatsPosition getPosition() {
        return previewPosition != null ? previewPosition : statsPosition;
    }

    public float getScale() {
        return previewScale != null ? previewScale : statsScale;
    }

    public boolean getShowBackground() {
        return previewShowBackground != null ? previewShowBackground : statsShowBackground;
    }

    public int getMaxCrops() {
        return previewMaxCrops != null ? previewMaxCrops : statsMaxCrops;
    }

    // Clear preview values (called when screen closes)
    public void clearPreview() {
        previewDisplayMode = null;
        previewPosition = null;
        previewScale = null;
        previewShowBackground = null;
        previewMaxCrops = null;
    }

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

    // Harvest Statistics settings
    @ConfigEntry.Gui.Tooltip
    public boolean showHarvestStats = false;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    public StatsDisplayMode statsDisplayMode = StatsDisplayMode.SESSION_MIN;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    public StatsPosition statsPosition = StatsPosition.TOP_LEFT;

    @ConfigEntry.Gui.Tooltip
    public float statsScale = 1.0f;

    @ConfigEntry.Gui.Tooltip
    public boolean statsShowBackground = false;

    @ConfigEntry.Gui.Tooltip
    public int statsMaxCrops = 3;

    // Custom position as percentage (0.0-1.0), -1 for preset position
    @ConfigEntry.Gui.Excluded
    public float statsCustomX = -1f;
    @ConfigEntry.Gui.Excluded
    public float statsCustomY = -1f;

    public enum CameraSnapMode {
        ALWAYS,
        BREAK
    }

    public enum CameraSnapDirectionMode {
        ALWAYS,
        SAME_ROW
    }

    public enum StatsDisplayMode {
        SESSION,
        PER_HOUR,
        PER_MIN,
        SESSION_HOUR,
        SESSION_MIN
    }

    public enum StatsPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public static void register() {
        AutoConfig.register(CropModConfig.class, GsonConfigSerializer::new);
    }

    public static CropModConfig get() {
        return AutoConfig.getConfigHolder(CropModConfig.class).getConfig();
    }
}
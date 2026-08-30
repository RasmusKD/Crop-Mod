package com.rasmus.cropmod.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

@SuppressWarnings("null")
public class CropModModMenuIntegration implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
                return parent -> {
                        CropModConfig config = CropModConfig.get();

                        ConfigBuilder builder = ConfigBuilder.create()
                                        .setParentScreen(parent)
                                        .setTitle(Component.translatable("title.cropmod.config"));

                        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                        // General category
                        ConfigCategory general = builder
                                        .getOrCreateCategory(Component.translatable("category.cropmod.general"));

                        general.addEntry(
                                        entryBuilder.startBooleanToggle(Component.translatable("option.cropmod.modEnabled"),
                                                        config.modEnabled)
                                                        .setDefaultValue(true)
                                                        .setTooltip(Component.translatable(
                                                                        "option.cropmod.modEnabled.tooltip"))
                                                        .setSaveConsumer(val -> config.modEnabled = val)
                                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.requireSeedsInInventory"),
                                                        config.requireSeedsInInventory)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.requireSeedsInInventory.tooltip"))
                                        .setSaveConsumer(val -> config.requireSeedsInInventory = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startIntSlider(Component.translatable("option.cropmod.itemThreshold"),
                                                        config.itemThreshold, 0, 640)
                                        .setDefaultValue(67)
                                        .setTooltip(Component.translatable("option.cropmod.itemThreshold.tooltip"))
                                        .setSaveConsumer(val -> config.itemThreshold = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.onlyHarvestFullyGrown"),
                                                        config.onlyHarvestFullyGrown)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.onlyHarvestFullyGrown.tooltip"))
                                        .setSaveConsumer(val -> config.onlyHarvestFullyGrown = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.requireHoeToBreakCrops"),
                                                        config.requireHoeToBreakCrops)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.requireHoeToBreakCrops.tooltip"))
                                        .setSaveConsumer(val -> config.requireHoeToBreakCrops = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.showProtectionParticles"),
                                                        config.showProtectionParticles)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.showProtectionParticles.tooltip"))
                                        .setSaveConsumer(val -> config.showProtectionParticles = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.playProtectionSounds"),
                                                        config.playProtectionSounds)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.playProtectionSounds.tooltip"))
                                        .setSaveConsumer(val -> config.playProtectionSounds = val)
                                        .build());

                        // Camera snap category
                        ConfigCategory cameraSnap = builder
                                        .getOrCreateCategory(Component.translatable("category.cropmod.cameraSnap"));

                        cameraSnap.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.cameraSnapEnabled"),
                                                        config.cameraSnapEnabled)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.cameraSnapEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.cameraSnapEnabled = val)
                                        .build());

                        cameraSnap.addEntry(entryBuilder
                                        .startEnumSelector(Component.translatable("option.cropmod.cameraSnapMode"),
                                                        CropModConfig.CameraSnapMode.class, config.cameraSnapMode)
                                        .setDefaultValue(CropModConfig.CameraSnapMode.BREAK)
                                        .setTooltip(Component.translatable("option.cropmod.cameraSnapMode.tooltip"))
                                        .setSaveConsumer(val -> config.cameraSnapMode = val)
                                        .build());

                        cameraSnap.addEntry(entryBuilder
                                        .startEnumSelector(Component.translatable("option.cropmod.cameraSnapDirectionMode"),
                                                        CropModConfig.CameraSnapDirectionMode.class,
                                                        config.cameraSnapDirectionMode)
                                        .setDefaultValue(CropModConfig.CameraSnapDirectionMode.SAME_ROW)
                                        .setTooltip(Component.translatable("option.cropmod.cameraSnapDirectionMode.tooltip"))
                                        .setSaveConsumer(val -> config.cameraSnapDirectionMode = val)
                                        .build());

                        // Crops category
                        ConfigCategory crops = builder.getOrCreateCategory(Component.translatable("category.cropmod.crops"));

                        crops.addEntry(entryBuilder
                                        .startTextDescription(Component.translatable("section.cropmod.seedCrops"))
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.wheatEnabled"),
                                                        config.wheatEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.wheatEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.wheatEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.carrotsEnabled"),
                                                        config.carrotsEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.carrotsEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.carrotsEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.potatoesEnabled"),
                                                        config.potatoesEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.potatoesEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.potatoesEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.beetrootsEnabled"),
                                                        config.beetrootsEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.beetrootsEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.beetrootsEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.netherWartEnabled"),
                                                        config.netherWartEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.netherWartEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.netherWartEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.cocoaEnabled"),
                                                        config.cocoaEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.cocoaEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.cocoaEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.torchflowerEnabled"),
                                                        config.torchflowerEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.torchflowerEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.torchflowerEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.pitcherPlantEnabled"),
                                                        config.pitcherPlantEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.pitcherPlantEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.pitcherPlantEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startTextDescription(Component.translatable("section.cropmod.growthPoints"))
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.sugarCaneEnabled"),
                                                        config.sugarCaneEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.sugarCaneEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.sugarCaneEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.bambooEnabled"),
                                                        config.bambooEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.bambooEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.bambooEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.kelpEnabled"),
                                                        config.kelpEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.kelpEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.kelpEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.cactusEnabled"),
                                                        config.cactusEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.cactusEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.cactusEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.glowBerriesEnabled"),
                                                        config.glowBerriesEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.glowBerriesEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.glowBerriesEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.paleMossEnabled"),
                                                        config.paleMossEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.paleMossEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.paleMossEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startTextDescription(Component.translatable("section.cropmod.neverBreak"))
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.protectStems"),
                                                        config.protectStems)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.protectStems.tooltip"))
                                        .setSaveConsumer(val -> config.protectStems = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.protectBerryBushes"),
                                                        config.protectBerryBushes)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.protectBerryBushes.tooltip"))
                                        .setSaveConsumer(val -> config.protectBerryBushes = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.protectBuddingAmethyst"),
                                                        config.protectBuddingAmethyst)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.protectBuddingAmethyst.tooltip"))
                                        .setSaveConsumer(val -> config.protectBuddingAmethyst = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.protectSuspiciousBlocks"),
                                                        config.protectSuspiciousBlocks)
                                        .setDefaultValue(true)
                                        .setTooltip(Component.translatable("option.cropmod.protectSuspiciousBlocks.tooltip"))
                                        .setSaveConsumer(val -> config.protectSuspiciousBlocks = val)
                                        .build());

                        // Harvest Statistics category - with LIVE PREVIEW
                        ConfigCategory stats = builder.getOrCreateCategory(Component.translatable("category.cropmod.stats"));

                        stats.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.showHarvestStats"),
                                                        config.showHarvestStats)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.showHarvestStats.tooltip"))
                                        .setSaveConsumer(val -> config.showHarvestStats = val)
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startEnumSelector(Component.translatable("option.cropmod.statsDisplayMode"),
                                                        CropModConfig.StatsDisplayMode.class, config.statsDisplayMode)
                                        .setDefaultValue(CropModConfig.StatsDisplayMode.SESSION_MIN)
                                        .setTooltip(Component.translatable("option.cropmod.statsDisplayMode.tooltip"))
                                        .setSaveConsumer(val -> {
                                                config.statsDisplayMode = val;
                                        })
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startFloatField(Component.translatable("option.cropmod.statsScale"),
                                                        config.statsScale)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.5f)
                                        .setMax(2.0f)
                                        .setTooltip(Component.translatable("option.cropmod.statsScale.tooltip"))
                                        .setSaveConsumer(val -> config.statsScale = val)
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startBooleanToggle(Component.translatable("option.cropmod.statsShowBackground"),
                                                        config.statsShowBackground)
                                        .setDefaultValue(false)
                                        .setTooltip(Component.translatable("option.cropmod.statsShowBackground.tooltip"))
                                        .setSaveConsumer(val -> config.statsShowBackground = val)
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startIntSlider(Component.translatable("option.cropmod.statsMaxCrops"),
                                                        config.statsMaxCrops, 1, 10)
                                        .setDefaultValue(3)
                                        .setTooltip(Component.translatable("option.cropmod.statsMaxCrops.tooltip"))
                                        .setSaveConsumer(val -> config.statsMaxCrops = val)
                                        .build());
                        // Position HUD button
                        stats.addEntry(new ButtonListEntry(
                                        Component.literal("Position HUD"),
                                        Component.literal("§eOpen"),
                                        () -> net.minecraft.client.Minecraft.getInstance()
                                                        .setScreenAndShow(new com.rasmus.cropmod.client.HudDragScreen(
                                                        com.rasmus.cropmod.client.CropModClient.currentScreen(
                                                                        net.minecraft.client.Minecraft.getInstance())))));

                        // Reset Session Stats button
                        stats.addEntry(new ButtonListEntry(
                                        Component.literal("Reset Session Stats"),
                                        Component.literal("§cReset"),
                                        () -> com.rasmus.cropmod.client.HarvestStatistics.getInstance()
                                                        .reset()));

                        // Set transparent background so HUD is visible while configuring
                        builder.setTransparentBackground(true);

                        // Auto-save when screen is closed (not just when clicking Save)
                        builder.setDoesConfirmSave(false);

                        builder.setSavingRunnable(() -> {
                                me.shedaniel.autoconfig.AutoConfig.getConfigHolder(CropModConfig.class).save();
                        });

                        return builder.build();
                };
        }
}

package com.rasmus.cropmod.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

@SuppressWarnings("null")
public class CropModModMenuIntegration implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
                return parent -> {
                        CropModConfig config = CropModConfig.get();

                        ConfigBuilder builder = ConfigBuilder.create()
                                        .setParentScreen(parent)
                                        .setTitle(Text.translatable("title.cropmod.config"));

                        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                        // General category
                        ConfigCategory general = builder
                                        .getOrCreateCategory(Text.translatable("category.cropmod.general"));

                        general.addEntry(
                                        entryBuilder.startBooleanToggle(Text.translatable("option.cropmod.modEnabled"),
                                                        config.modEnabled)
                                                        .setDefaultValue(true)
                                                        .setTooltip(Text.translatable(
                                                                        "option.cropmod.modEnabled.tooltip"))
                                                        .setSaveConsumer(val -> config.modEnabled = val)
                                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.requireSeedsInInventory"),
                                                        config.requireSeedsInInventory)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.requireSeedsInInventory.tooltip"))
                                        .setSaveConsumer(val -> config.requireSeedsInInventory = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startIntSlider(Text.translatable("option.cropmod.itemThreshold"),
                                                        config.itemThreshold, 0, 640)
                                        .setDefaultValue(67)
                                        .setTooltip(Text.translatable("option.cropmod.itemThreshold.tooltip"))
                                        .setSaveConsumer(val -> config.itemThreshold = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.onlyHarvestFullyGrown"),
                                                        config.onlyHarvestFullyGrown)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.onlyHarvestFullyGrown.tooltip"))
                                        .setSaveConsumer(val -> config.onlyHarvestFullyGrown = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.requireHoeToBreakCrops"),
                                                        config.requireHoeToBreakCrops)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.requireHoeToBreakCrops.tooltip"))
                                        .setSaveConsumer(val -> config.requireHoeToBreakCrops = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.showProtectionParticles"),
                                                        config.showProtectionParticles)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.showProtectionParticles.tooltip"))
                                        .setSaveConsumer(val -> config.showProtectionParticles = val)
                                        .build());

                        general.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.playProtectionSounds"),
                                                        config.playProtectionSounds)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.playProtectionSounds.tooltip"))
                                        .setSaveConsumer(val -> config.playProtectionSounds = val)
                                        .build());

                        // Camera snap category
                        ConfigCategory cameraSnap = builder
                                        .getOrCreateCategory(Text.translatable("category.cropmod.cameraSnap"));

                        cameraSnap.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.cameraSnapEnabled"),
                                                        config.cameraSnapEnabled)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.cameraSnapEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.cameraSnapEnabled = val)
                                        .build());

                        cameraSnap.addEntry(entryBuilder
                                        .startEnumSelector(Text.translatable("option.cropmod.cameraSnapMode"),
                                                        CropModConfig.CameraSnapMode.class, config.cameraSnapMode)
                                        .setDefaultValue(CropModConfig.CameraSnapMode.BREAK)
                                        .setTooltip(Text.translatable("option.cropmod.cameraSnapMode.tooltip"))
                                        .setSaveConsumer(val -> config.cameraSnapMode = val)
                                        .build());

                        cameraSnap.addEntry(entryBuilder
                                        .startEnumSelector(Text.translatable("option.cropmod.cameraSnapDirectionMode"),
                                                        CropModConfig.CameraSnapDirectionMode.class,
                                                        config.cameraSnapDirectionMode)
                                        .setDefaultValue(CropModConfig.CameraSnapDirectionMode.SAME_ROW)
                                        .setTooltip(Text.translatable("option.cropmod.cameraSnapDirectionMode.tooltip"))
                                        .setSaveConsumer(val -> config.cameraSnapDirectionMode = val)
                                        .build());

                        // Crops category
                        ConfigCategory crops = builder.getOrCreateCategory(Text.translatable("category.cropmod.crops"));

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.wheatEnabled"),
                                                        config.wheatEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.wheatEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.wheatEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.carrotsEnabled"),
                                                        config.carrotsEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.carrotsEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.carrotsEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.potatoesEnabled"),
                                                        config.potatoesEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.potatoesEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.potatoesEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.beetrootsEnabled"),
                                                        config.beetrootsEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.beetrootsEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.beetrootsEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.netherWartEnabled"),
                                                        config.netherWartEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.netherWartEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.netherWartEnabled = val)
                                        .build());

                        crops.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.cocoaEnabled"),
                                                        config.cocoaEnabled)
                                        .setDefaultValue(true)
                                        .setTooltip(Text.translatable("option.cropmod.cocoaEnabled.tooltip"))
                                        .setSaveConsumer(val -> config.cocoaEnabled = val)
                                        .build());

                        // Harvest Statistics category - with LIVE PREVIEW
                        // Changes apply immediately so you can see them in the HUD
                        ConfigCategory stats = builder.getOrCreateCategory(Text.translatable("category.cropmod.stats"));

                        stats.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.showHarvestStats"),
                                                        config.showHarvestStats)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.showHarvestStats.tooltip"))
                                        .setSaveConsumer(val -> config.showHarvestStats = val)
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startEnumSelector(Text.translatable("option.cropmod.statsDisplayMode"),
                                                        CropModConfig.StatsDisplayMode.class, config.statsDisplayMode)
                                        .setDefaultValue(CropModConfig.StatsDisplayMode.SESSION_MIN)
                                        .setTooltip(Text.translatable("option.cropmod.statsDisplayMode.tooltip"))
                                        .setSaveConsumer(val -> {
                                                config.statsDisplayMode = val;
                                        })
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startFloatField(Text.translatable("option.cropmod.statsScale"),
                                                        config.statsScale)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.5f)
                                        .setMax(2.0f)
                                        .setTooltip(Text.translatable("option.cropmod.statsScale.tooltip"))
                                        .setSaveConsumer(val -> config.statsScale = val)
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startBooleanToggle(Text.translatable("option.cropmod.statsShowBackground"),
                                                        config.statsShowBackground)
                                        .setDefaultValue(false)
                                        .setTooltip(Text.translatable("option.cropmod.statsShowBackground.tooltip"))
                                        .setSaveConsumer(val -> config.statsShowBackground = val)
                                        .build());

                        stats.addEntry(entryBuilder
                                        .startIntSlider(Text.translatable("option.cropmod.statsMaxCrops"),
                                                        config.statsMaxCrops, 1, 10)
                                        .setDefaultValue(3)
                                        .setTooltip(Text.translatable("option.cropmod.statsMaxCrops.tooltip"))
                                        .setSaveConsumer(val -> {
                                                config.statsMaxCrops = val;
                                                config.previewMaxCrops = val;
                                        })
                                        .build());
                        // Position HUD button
                        stats.addEntry(new ButtonListEntry(
                                        Text.literal("Position HUD"),
                                        Text.literal("§eOpen"),
                                        () -> net.minecraft.client.MinecraftClient.getInstance()
                                                        .setScreen(new com.rasmus.cropmod.client.HudDragScreen())));

                        // Reset Session Stats button
                        stats.addEntry(new ButtonListEntry(
                                        Text.literal("Reset Session Stats"),
                                        Text.literal("§cReset"),
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

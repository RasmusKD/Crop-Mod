package com.rasmus.cropmod.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

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
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.cropmod.general"));

            general.addEntry(
                    entryBuilder.startBooleanToggle(Text.translatable("option.cropmod.modEnabled"), config.modEnabled)
                            .setDefaultValue(true)
                            .setTooltip(Text.translatable("option.cropmod.modEnabled.tooltip"))
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
                    .startIntSlider(Text.translatable("option.cropmod.itemThreshold"), config.itemThreshold, 0, 640)
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
            ConfigCategory cameraSnap = builder.getOrCreateCategory(Text.translatable("category.cropmod.cameraSnap"));

            cameraSnap.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.cameraSnapEnabled"), config.cameraSnapEnabled)
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
                            CropModConfig.CameraSnapDirectionMode.class, config.cameraSnapDirectionMode)
                    .setDefaultValue(CropModConfig.CameraSnapDirectionMode.SAME_ROW)
                    .setTooltip(Text.translatable("option.cropmod.cameraSnapDirectionMode.tooltip"))
                    .setSaveConsumer(val -> config.cameraSnapDirectionMode = val)
                    .build());

            // Crops category
            ConfigCategory crops = builder.getOrCreateCategory(Text.translatable("category.cropmod.crops"));

            crops.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.wheatEnabled"), config.wheatEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.cropmod.wheatEnabled.tooltip"))
                    .setSaveConsumer(val -> config.wheatEnabled = val)
                    .build());

            crops.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.carrotsEnabled"), config.carrotsEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.cropmod.carrotsEnabled.tooltip"))
                    .setSaveConsumer(val -> config.carrotsEnabled = val)
                    .build());

            crops.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.potatoesEnabled"), config.potatoesEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.cropmod.potatoesEnabled.tooltip"))
                    .setSaveConsumer(val -> config.potatoesEnabled = val)
                    .build());

            crops.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.beetrootsEnabled"), config.beetrootsEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.cropmod.beetrootsEnabled.tooltip"))
                    .setSaveConsumer(val -> config.beetrootsEnabled = val)
                    .build());

            crops.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.netherWartEnabled"), config.netherWartEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.cropmod.netherWartEnabled.tooltip"))
                    .setSaveConsumer(val -> config.netherWartEnabled = val)
                    .build());

            crops.addEntry(entryBuilder
                    .startBooleanToggle(Text.translatable("option.cropmod.cocoaEnabled"), config.cocoaEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.cropmod.cocoaEnabled.tooltip"))
                    .setSaveConsumer(val -> config.cocoaEnabled = val)
                    .build());

            builder.setSavingRunnable(() -> {
                me.shedaniel.autoconfig.AutoConfig.getConfigHolder(CropModConfig.class).save();
            });

            return builder.build();
        };
    }
}

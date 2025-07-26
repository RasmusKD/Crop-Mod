package com.rasmus.cropmod;

import com.rasmus.cropmod.config.CropModConfig;
import net.fabricmc.api.ModInitializer;

public class Cropmod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Register config
        CropModConfig.register();
    }
}
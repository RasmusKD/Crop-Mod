package com.rasmus.cropmod;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * 26.1 calls the HUD class Gui, 26.2 renamed it Hud but kept a different
 * Gui class around, so the choice is keyed off the Minecraft version
 * string instead of class presence.
 */
public class CropModMixinPlugin implements IMixinConfigPlugin {

    private static boolean isLegacyGui() {
        // A real version predicate, not a display-string prefix: "26.10"
        // startsWith "26.1" and would silently bind the legacy mixin to
        // whatever Gui looks like by then.
        return FabricLoader.getInstance().getModContainer("minecraft")
                .map(c -> {
                    try {
                        return net.fabricmc.loader.api.metadata.version.VersionPredicate.parse("<26.2")
                                .test(c.getMetadata().getVersion());
                    } catch (net.fabricmc.loader.api.VersionParsingException e) {
                        return false;
                    }
                }).orElse(false);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("GuiStatsLegacyMixin")) {
            return isLegacyGui();
        }
        if (mixinClassName.endsWith("HudStatsModernMixin")) {
            return !isLegacyGui();
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }
}

# One jar, two Minecraft versions (26.1.x and 26.2)

The jar runs on both because runtime uses mojmap names directly. Renamed
members crash hard, so every difference is handled explicitly:

| What | 26.1 | 26.2 | How we handle it |
|---|---|---|---|
| HUD class | `Gui` | `Hud` (a different `Gui` still exists!) | Mixin pair gated by `CropModMixinPlugin` on the version string, never by class presence |
| HUD hidden check | `Options.hideGui` field | `Hud.isHidden()` | Reflection in `HarvestStatsRenderer.legacyHudHidden()` / direct call in the modern mixin |
| Open a screen | `Minecraft.setScreen` | gone, `Hud.setScreen` | `Minecraft.setScreenAndShow`, identical in both |

Everything else the mod touches (extractRenderState on the HUD class,
Blocks fields incl. CAVE_VINES/PALE_HANGING_MOSS, startAttack,
continueAttack, destroyBlock) is identical in both versions.

Build targets 26.2. The legacy mixin uses a string target so the compiler
never validates it against the 26.2 Gui.

## Before every release

Verify with javap against both deobf jars in
`~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/{26.1.2,26.2}/`
and run the full-jar reference audit. One unaudited class is how
rare-fish-finder 2.5.2 crashed on 26.2.

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

## 26.3 is a separate jar

`./gradlew build` produces `Cropmod-X.Y.Z+26.1.jar` (26.1.x and 26.2). `./gradlew build -PmcTarget=26.3`
produces `Cropmod-X.Y.Z+26.3.jar` (pins in `gradle-26.3.properties`). Files under `src/mc26_3/java` and
`src/mc26_3/resources` replace the same-named file of `src/main`; `src/mc26_3/remove.txt` lists main
files the 26.3 jar drops. `verifyMixinClasses` (part of `check`) fails when a mixins.json entry has no
class in that variant. Check each jar against the Minecraft versions it claims without starting the game:
`python C:\Users\Rasmus\.claude\skills\minecraft-mods\scripts\linkcheck.py <jar> <mapped minecraft jar>`
(plugin-gated twins report as missing on the other version; that is expected).

| What | 26.1/26.2 | 26.3 | Fix |
|---|---|---|---|
| Current screen | `Minecraft.screen` field (26.1) / `screen()` (26.2) | `Minecraft.gui.screen()` | `VersionCompat.currentScreen`, replaced in `src/mc26_3` |
| `InputConstants.isKeyDown` | `(Window, int)` | `(int)` | `VersionCompat.isKeyDown`, replaced in `src/mc26_3` |
| `InputConstants.Type.KEYSYM` | exists | now `KEYBOARD` | `new KeyMapping(String, int, Category)`, exists on all three |
| `Gui` HUD class | `Gui` (26.1) / `Hud` (26.2) | `Hud` | legacy twin dropped from the 26.3 jar |
| Key codes (`InputConstants.KEY_*`, `KeyMapping` int argument) | GLFW keycodes (`KEY_B` 66, `KEY_LSHIFT` 340) | SDL scancodes (`KEY_B` 5, `KEY_LSHIFT` 225) | always write `InputConstants.KEY_*`, never a number; each jar inlines its own value |

Full 26.3 table: `C:\Users\Rasmus\Desktop\Ny mappe (3)\mc-26.3-splits.md`.

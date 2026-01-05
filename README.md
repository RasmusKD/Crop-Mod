# CropMod

A Fabric mod for smarter crop farming. Protect your seeds, track your harvests, and farm efficiently.

## Why CropMod?

Built for **mcMMO** players. At Herbalism 1000, mcMMO auto-replants crops using seeds from your inventory. CropMod prevents breaking crops when you're running low on seeds—so you never brick your farm mid-harvest.

## Features

### Crop Protection
- Blocks harvesting when seed count drops below threshold (default: 67)
- Works with mcMMO's auto-replant
- Visual particles show when protection kicks in

### Harvest Control
- Only break fully grown crops
- Require hoe in hand to harvest
- Per-crop toggles for all supported crops

### Camera Snap
- Auto-align camera to 90° when farming
- Two modes: always on, or only when breaking crops
- Row detection for precision alignment

### Harvest Statistics HUD
- Live tracking of crops harvested per session
- Per-minute rate calculation
- Draggable position (Ctrl+H)
- Configurable scale and display mode
- Supports 17 crop types

### Supported Crops
Wheat, Carrots, Potatoes, Beetroots, Nether Wart, Cocoa, Pumpkin, Melon, Sweet Berries, Sugar Cane, Bamboo, Kelp, Cactus, Chorus, Glow Berries, Torchflower, Pitcher Plant

### Keybinds
- **P** — Toggle crop protection
- **O** — Toggle camera snap  
- **H** — Toggle stats HUD
- **Shift+H** — Reset session stats
- **Ctrl+H** — Position HUD

## Configuration

All settings accessible via ModMenu. Changes apply immediately.

## Dependencies

- Fabric API
- Cloth Config
- ModMenu (optional)

---

MIT License
# CropMod

A Minecraft Fabric mod designed to enhance crop farming with intelligent protection and quality-of-life features.

## Background

This mod was created specifically for players using the popular **mcMMO** server plugin. In mcMMO, reaching Herbalism level 1000 grants a 100% crop replant chance, which automatically consumes seeds from your inventory. CropMod prevents accidental crop destruction when you don't have enough seeds left, using a configurable buffer system as a failsafe.

Beyond mcMMO compatibility, CropMod adds general-purpose farming improvements that make crop management more efficient and user-friendly.

## Features

### 🛡️ **Crop Protection**
- **Smart Seed Management**: Prevents breaking crops when you don't have enough seeds in your inventory
- **Configurable Threshold**: Set a seed buffer (default: 67) to ensure you never run out completely
- **mcMMO Compatible**: Works perfectly with mcMMO's auto-replant feature

### 🌾 **Harvest Control**
- **Only Harvest Fully Grown**: Prevents accidentally breaking immature crops
- **Individual Crop Settings**: Enable/disable features for specific crop types:
  - Wheat
  - Carrots
  - Potatoes
  - Beetroots
  - Nether Wart
  - Cocoa Beans

### ⚒️ **Hoe Requirement** *(Optional)*
- **Tool-Based Harvesting**: Require holding a hoe to break crops
- **Perfect for mcMMO**: Ensures you only harvest when intentionally farming
- **Flexible Detection**: Works with any hoe in main hand or offhand

### 📷 **Camera Snap** *(Optional)*
- **Precision Farming**: Automatically snaps camera to nearest 90° angle
- **Two Modes**:
  - **Always**: Snap when looking at crops
  - **Break**: Snap only when harvesting
- **Direction Options**:
  - **Always**: Snap regardless of position
  - **Same Row**: Only snap when aligned with crop rows

### ✨ **Protection Particles** *(Optional)*
- **Visual Feedback**: Shows magical barrier particles when crop harvesting is blocked
- **Height-Adaptive**: Barrier height automatically scales with crop growth stage
- **Elegant Design**: Clean enchantment particles form corner posts and connecting edges
- **Smart Scaling**: Young crops get small barriers, mature crops get full-height barriers

### ⌨️ **Keybinds**
- **P Key**: Toggle crop protection on/off
- **O Key**: Toggle camera snap on/off
- Real-time feedback with in-game messages

### ⚙️ **Configuration**
- **ModMenu Integration**: Easy-to-use configuration screen
- **Per-Crop Control**: Enable/disable features for individual crop types
- **Customizable Settings**: Adjust all thresholds and behaviors to your preference
- **Live Updates**: Changes apply immediately without restart

## Default Settings

- **Crop Protection**: ✅ Enabled
- **Camera Snap**: ❌ Disabled
- **Hoe Requirement**: ❌ Disabled
- **Protection Particles**: ❌ Disabled
- **Only Harvest Fully Grown**: ✅ Enabled
- **Seed Threshold**: 67 seeds
- **All Crop Types**: ✅ Enabled

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download CropMod
4. Place both mods in your `mods` folder
5. Launch Minecraft!

## Dependencies

- **Fabric Loader**
- **Fabric API**: Required
- **Cloth Config**: Required (for configuration GUI)
- **ModMenu**: Recommended (for easy config access)

## Perfect For

- **mcMMO Players**: Protect your seed investment while auto-replanting
- **Farm Builders**: Avoid accidentally destroying immature crops
- **Precision Farmers**: Use camera snap for perfectly aligned crop rows
- **Visual Farmers**: Enable protection particles for clear feedback when harvesting is blocked
- **Selective Harvesting**: Choose exactly which crops to apply features to

---

*Made for farmers who want smarter, safer crop management in Minecraft.*
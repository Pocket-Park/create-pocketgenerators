# Pocket Generators

**A Create addon that turns rotational power into resources.**

Pocket Generators adds **duplicators**: kinetic blocks that copy the item placed in their filter as long as they receive enough rotation. Feed them a shaft, pick an ore or a block, and they will steadily push copies onto your belts or into your chests.

The item in the filter is never consumed. It only tells the duplicator what to produce.

## How it works

1. Place a duplicator and power it with rotation (shaft, motor, water wheel, anything Create).
2. **Right-click with an item** to set the filter (the item to duplicate).
3. The duplicator generates copies over time and pushes them into the belt or inventory on its output face.
4. **Right-click with an empty hand** to collect the output. **Shift + right-click with an empty hand** to take the filter back.
5. **Wrench** the block to change its output face (cycles through all 6 directions).
6. *(Brass tier only)* **Hold right-click on the top face** to open Create's value dial and choose the batch size, from 1 to 64 items per cycle. Stress cost scales with the batch size.

The faster the input rotation, the faster the production. Each tier requires a minimum speed to run.

## Tiers

| Tier | Minimum speed | What it can duplicate |
|---|---|---|
| **Basic** | 16 RPM | Cobblestone, coal, copper (ores and blocks) |
| **Andesite** | 32 RPM | Everything from Basic, plus iron, redstone and lapis |
| **Brass** | 64 RPM | Everything from Andesite, plus gold. Adjustable batch size |
| **Netherite** | 128 RPM | Nether resources only: quartz, glowstone, gilded blackstone, ancient debris, netherite |
| **End** | 256 RPM | End resources only: end stone, purpur, chorus fruit, shulker shells |

Basic, Andesite and Brass form a normal progression: each tier can duplicate everything the previous ones can.

Netherite and End are two **separate branches** built from a Brass duplicator. A Netherite duplicator cannot copy End items and vice versa.

**Diamonds and emeralds cannot be duplicated**, on purpose. Some resources should still be earned.

## Crafting

| Tier | Recipe |
|---|---|
| Basic | 3x3 crafting table: cobblestone, iron nuggets, shaft, redstone |
| Andesite | 4x4 Mechanical Crafter: andesite alloy, iron, shaft, plus a Basic duplicator |
| Brass | 5x5 Mechanical Crafter: brass, precision mechanisms, iron, plus an Andesite duplicator |
| Netherite | 5x5 Mechanical Crafter: netherite, netherite scrap, precision mechanisms, plus a Brass duplicator |
| End | 5x5 Mechanical Crafter: shulker shells, ender pearls, chorus fruit, plus a Brass duplicator |

Full recipes are visible in game with **JEI**, which also shows which items each tier can duplicate.

## Configuration

Minimum speed, stress impact and production speed of every tier can be tuned in `config/pocketgenerators-common.toml`. Ideal for modpacks that want a different balance.

Modpack makers can also add or remove duplicable items with a datapack: each duplicable item is a simple JSON recipe of type `pocketgenerators:resource_generating`. No code required.

## Requirements

- Minecraft **1.21.1**
- **NeoForge** 21.1+
- **Create** 6.0+ (required)
- **JEI** (optional, adds a recipe category for duplicators)

## Modpacks

Feel free to include Pocket Generators in your modpacks. The mod is released under the **MIT** license.

## Feedback

The mod is still in early development. Bug reports and balance suggestions are very welcome on the [issue tracker](https://github.com/Pocket-Park/create-pocketgenerators/issues).

# Matching Deposit (Fabric, Minecraft 26.2)

A small client-side companion mod for Lightweight Inventory Sorting. It adds a **D** button beside the existing sort button on vanilla chest-like container screens.

## Behavior

- Deposits player-inventory stacks only when the open container already has the same item **and components** (for example, the same named/enchantable variant).
- Uses the menu's normal quick-move action, so Minecraft handles stack sizes and insertion restrictions.
- Supports vanilla chest menus (chests, trapped chests, barrels, ender chests) and shulker boxes.
- Leaves unrelated items alone.
- Does nothing if the cursor is currently holding an item.

## Build

Requires JDK 25 and a Gradle installation (or open this folder as a Gradle project in IntelliJ IDEA). Run:

```sh
gradle build
```

The jar will be in `build/libs/`. Put that jar in the Minecraft instance's `mods` folder alongside Fabric Loader and the existing inventory-sorting mod.

## Notes

This is an independent companion mod; it does not replace or modify the uploaded Lightweight Inventory Sorting jar. The source was prepared against the Mojang-mapped Minecraft 26.2 API, but it has **not been compiled or tested in-game in this environment** because the available runtime is Java 21 and network access for fetching the Minecraft/Gradle build dependencies is unavailable. If the build reports a mapping/API mismatch, send me the build error and I can adjust it.

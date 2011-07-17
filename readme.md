# DimensionDoor

Based on the idea that the best plugin do one job and do it very well, DimensionDoor was born. DimensionDoor is a lightweight but powerful solution to managing multiple worlds, with full permissions support (or gracefully falls back if not available) and is simple to configure.

## Features:

- Lightweight: only manages worlds.
- Create and remove worlds without restarting the server.
- Create worlds using either a number or a word for your seed.
- World specific chat.
- Supports skylands and nether worlds.
- Use any Environment type that Bukkit supports
- Teleport to any world.
- Change the spawn location of each world
- Players respawn in the world they died in.
- Set world specific settings.
- Automatically manages worlds which are loaded unexpectedly by other plugins.
- Automatically loads all managed worlds on server start.
- Uses Bukkit persistence for data storage; you choose what is best for you!
- Supports permissions if available (falls back to OP only without)
- Simple. Nothing to setup, just put the .jar in your plugin directory.

## Requirements

- Bukkit Persistence needs to be configured in bukkit.yml
- If using MySQL for Persistence, you need a MySQL database
- If using Permissions any version above 2.5 will do (does work with GroupManager)

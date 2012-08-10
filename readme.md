DimensionDoor: multiworld management made easy
====================================

DimensionDoor is a plugin for the Minecraft wrapper [Bukkit](http://bukkit.org/) that allows adminstrators and other trusted users to create and manage worlds. Based on the idea that the best plugin do one job and do it very well, DimensionDoor was born. It  is a lightweight but powerful solution to managing multiple worlds, with full permissions support and is simple to configure.

## Features

- Simple and easy to configure.
* Custom chunk generator support.
* Lightweight: only manages worlds.
* Create and remove worlds without restarting the server.
* Create worlds using either a number or a word for your seed.
* World specific chat.
* Use any Environment type that Bukkit supports
* Teleport to any world.
* Change the spawn location of each world
* Players respawn in the world they died in.
* Set world specific settings.
* Automatically manages worlds which are loaded unexpectedly by other plugins.
* Automatically loads all managed worlds on server start.
- Supports built in Bukkit permissions, operators have all commands by default.

## License

DimensionDoor is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

DimensionDoor is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

## Documentation

If you are a server administrator, many of the features specific to DimensionDoor are documented [on the wiki](https://github.com/grandwazir/DimensionDoor/wiki). If you are looking to change the messages used in DimensionDoor or localise the plugin into your own language you will want to look at [this page](https://github.com/grandwazir/BukkitUtilities/wiki/Localisation) instead.

If you are a developer you may find the [JavaDocs](http://grandwazir.github.com/DimensionDoor/apidocs/index.html) and a [Maven website](http://grandwazir.github.com/DimensionDoor/) useful to you as well.

## Installation

Before installing, you need to make sure you are running at least the latest [recommended build](http://dl.bukkit.org/latest-rb/craftbukkit.jar) for Bukkit. Support is only given for problems when using a recommended build. This does not mean that the plugin will not work on other versions of Bukkit, the likelihood is it will, but it is not supported.

### Getting the latest version

The best way to install DimensionDoor is to use the [symbolic link](http://repository.james.richardson.name/symbolic/DimensionDoor.jar) to the latest version. This link always points to the latest version of DimensionDoor, so is safe to use in scripts or update plugins. A [feature changelog](https://github.com/grandwazir/DimensionDoor/wiki/changelog) is also available.

### Getting older versions

Alternatively [older versions](http://repository.james.richardson.name/releases/name/richardson/james/bukkit/ban-hammer/) are available as well, however they are not supported. If you are forced to use an older version for whatever reason, please let me know why by [opening a issue](https://github.com/grandwazir/DimensionDoor/issues/new) on GitHub.

### Building from source

You can also build DimensionDoor from the source if you would prefer to do so. This is useful for those who wish to modify DimensionDoor before using it. Note it is no longer necessary to do this to alter messages in the plugin. Instead you should read the documentation on how to localise the plugin instead. This assumes that you have Maven and git installed on your computer.

    git clone git://github.com/grandwazir/DimensionDoor.git
    cd DimensionDoor
    mvn install

## Reporting issues

If you are a server administrator and you are requesting support in installing or using the plugin you should [make a post](http://dev.bukkit.org/server-mods/dimensiondoor/forum/create-thread/) in the forum on BukkitDev. If you want to make a bug report or feature request please do so using the [issue tracking](https://github.com/grandwazir/DimensionDoor/issues) on GitHub.







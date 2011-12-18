/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CreateCommand.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor.creation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import name.richardson.james.dimensiondoor.util.Command;

public class CreateCommand extends Command {

  public CreateCommand() {
    name = "create";
    description = "create (or import) a new world.";
    usage = "/dd create [world] [environment] <s:seed> <g:generatorPlugin:generatorID>";
    permission = this.registerCommandPermission();
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final String worldName = (String) arguments.get("worldName");
    final Environment environment = (Environment) arguments.get("environment");
    final Long seed = (Long) arguments.get("seed");

    sender.sendMessage(String.format(ChatColor.YELLOW + "Creating %s (this may take a while)", worldName));

    if (arguments.containsKey("generatorPlugin")) {
      final String generatorPlugin = (String) arguments.get("generatorPlugin");
      final String generatorID = (String) arguments.get("generatorID");
      WorldHandler.createWorld(worldName, environment, seed, generatorPlugin, generatorID);
    } else {
      WorldHandler.createWorld(worldName, environment, seed);
    }

    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been created.", worldName));
    logger.info(String.format("%s has created a new world called %s", sender.getName(), worldName));

  }

  @Override
  protected Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> map = new HashMap<String, Object>();

    try {
      map.put("worldName", arguments.remove(0));
    } catch (final IndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("You must specify a world name.");
    }

    map.put("environment", WorldHandler.getDefaults().get("environment"));
    map.put("seed", System.currentTimeMillis());

    for (String argument : arguments) {
      if (argument.startsWith("s:")) {
        final int seed = argument.replaceFirst("s:", "").hashCode();
        map.put("seed", new Long(seed));
      } else if (argument.startsWith("g:")) {
        argument = argument.replaceFirst("g:", "");
        final String[] args = argument.split(":");
        map.put("generatorPlugin", args[0]);
        if (args.length == 2) {
          map.put("generatorID", args[1]);
        }
      }
    }
    return map;
  }

}

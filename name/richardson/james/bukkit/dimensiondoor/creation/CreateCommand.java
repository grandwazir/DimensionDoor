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

package name.richardson.james.bukkit.dimensiondoor.creation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class CreateCommand extends PlayerCommand {

  public static final String NAME = "create";
  public static final String DESCRIPTION = "Create a new world.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to create new worlds.";
  public static final String USAGE = "<name> [e:environment] [s:seed] [g:plugin:id]";
  
  public static final Permission PERMISSION = new Permission("dimensiondoor.create", PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private DimensionDoor plugin;
  
  public CreateCommand(DimensionDoor plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.plugin = plugin;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandUsageException {
    final String worldName = (String) arguments.get("worldName");
    final Environment environment = (Environment) arguments.get("environment");
    final Long seed = (Long) arguments.get("seed");

    sender.sendMessage(String.format(ChatColor.YELLOW + "Creating %s (this may take a while)", worldName));

    if (arguments.containsKey("generatorPlugin")) {
      final String generatorPlugin = (String) arguments.get("generatorPlugin");
      final String generatorID = (String) arguments.get("generatorID");
      try {
        plugin.createWorld(worldName, environment, seed, generatorPlugin, generatorID);
      } catch (IllegalArgumentException exception) {
        throw new CommandUsageException(exception.getMessage(), CreateCommand.USAGE);
      }
    } else {
      plugin.createWorld(worldName, environment, seed);
    }

    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been created.", worldName));
    logger.info(String.format("%s has created a new world called %s", sender.getName(), worldName));

  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> map = new HashMap<String, Object>();

    map.put("environment", plugin.getDefaults().get("environment"));
    map.put("seed", System.currentTimeMillis());
    
    // get our required arguments
    try {
      final String worldName = arguments.remove(0);
      map.put("worldName", worldName);
      if (plugin.getWorld(worldName) != null) throw new CommandArgumentException(worldName + " already exists!", "You must specify a name that is not in use.");
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a world name!", "It must be a name not used by another world.");
    }

    if (arguments.isEmpty()) return map;
    
    for (String argument : arguments) {
      if (argument.startsWith("e:")) {
        try {
          map.put("environment", Environment.valueOf(argument.replaceFirst("e:", "")));
        } catch (final IllegalArgumentException exception) {
          throw new CommandArgumentException("The environment specified does not exist!", "Valid types are: " + this.buildEnvironmentList() + ".");
        }
      } else if (argument.startsWith("s:")) {
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
  
  private String buildEnvironmentList() {
    StringBuilder message = new StringBuilder();
    for (Environment environment : Environment.values()) {
      message.append(environment.name());
      message.append(", ");
    }
    message.delete(message.length() - 2, message.length());
    return message.toString();
  }

}

/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * LoadCommand.java is part of DimensionDoor.
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
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class LoadCommand extends PlayerCommand {

  public static final String NAME = "load";
  public static final String DESCRIPTION = "Load a world mananged by DimensionDoor.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to load existing worlds.";
  public static final String USAGE = "<name>";
  public static final Permission PERMISSION = new Permission("dimensiondoor.load", PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final DimensionDoor plugin;
  
  public LoadCommand(DimensionDoor plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.plugin = plugin;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final WorldRecord record = (WorldRecord) arguments.get("record");
    plugin.loadWorld(record);
    logger.info(String.format("%s has loaded the world %s", sender.getName(), record.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been loaded.", record.getName()));
  }

  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      final String worldName = arguments.get(0);
      final WorldRecord record = WorldRecord.findByName(plugin.getDatabaseHandler(), worldName);
      if (plugin.isWorldLoaded(worldName)) {
        throw new CommandArgumentException(String.format("%s is already loaded!", worldName), "You may not load a world twice.");
      } else if (record == null) {
        throw new CommandArgumentException(String.format("%s is not managed by DimensionDoor!", worldName), "To import an existing world use /dd create.");
      } else {
        map.put("record", record);
      }
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a world name!", "Use /dd list for a list of worlds to load.");
    }
    return map;
  }

}

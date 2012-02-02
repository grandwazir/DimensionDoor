/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * RemoveCommand.java is part of DimensionDoor.
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

public class RemoveCommand extends PlayerCommand {

  public static final String NAME = "remove";
  public static final String DESCRIPTION = "Unload and remove a world from DimensionDoor.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to remove worlds.";
  public static final String USAGE = "<name>";
  public static final Permission PERMISSION = new Permission("dimensiondoor.remove", RemoveCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final DimensionDoor plugin;

  public RemoveCommand(final DimensionDoor plugin) {
    super(plugin, RemoveCommand.NAME, RemoveCommand.DESCRIPTION, RemoveCommand.USAGE, RemoveCommand.PERMISSION_DESCRIPTION, RemoveCommand.PERMISSION);
    this.plugin = plugin;
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, Object> arguments) {
    final WorldRecord record = (WorldRecord) arguments.get("record");
    final String worldName = record.getName();
    this.plugin.removeWorld(record);
    this.logger.info(String.format("%s has removed the WorldRecord for %s", sender.getName(), worldName));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been removed.", worldName));
    sender.sendMessage(ChatColor.YELLOW + "You will still need to remove the world directory.");
  }

  @Override
  public Map<String, Object> parseArguments(final List<String> arguments) throws CommandArgumentException {
    final Map<String, Object> map = new HashMap<String, Object>();
    try {
      final String worldName = arguments.get(0);
      final WorldRecord record = WorldRecord.findByName(this.plugin.getDatabaseHandler(), worldName);
      if (record == null)
        throw new CommandArgumentException(String.format("%s is not managed by DimensionDoor!", worldName), "Use /dd list for a list of worlds.");
      else {
        map.put("record", record);
      }
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a world name!", "Use /dd list for a list of worlds.");
    }
    return map;
  }

}

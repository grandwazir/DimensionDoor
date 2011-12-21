/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ListCommand.java is part of DimensionDoor.
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

package name.richardson.james.bukkit.dimensiondoor.management;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ListCommand extends PlayerCommand {

  public static final String NAME = "list";
  public static final String DESCRIPTION = "List all the available worlds.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to list all available worlds.";
  public static final String USAGE = "";
  public static final Permission PERMISSION = new Permission("dimensiondoor.list", PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final DimensionDoor plugin;
  
  public ListCommand(DimensionDoor plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.plugin = plugin;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    List<? extends Object> records = plugin.getDatabaseHandler().list(WorldRecord.class);
    final String message = buildWorldList(records);
    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "Currently managing %d worlds:", records.size()));
    sender.sendMessage(message);
  }

  private String buildWorldList(final List<? extends Object> records) {
    final StringBuilder message = new StringBuilder();
    for (final Object record : records) {
      final WorldRecord worldRecord = (WorldRecord) record;
      final String name = worldRecord.getName();
      if (plugin.isWorldLoaded(name)) {
        message.append(ChatColor.GREEN + name + ", ");
      } else {
        message.append(ChatColor.RED + name + ", ");
      }
    }
    message.delete(message.length() - 2, message.length());
    return message.toString();
  }

}

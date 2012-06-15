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
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class ListCommand extends PluginCommand {

  private final DimensionDoor plugin;

  public ListCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  private String buildWorldList(final List<? extends Object> records) {
    final StringBuilder message = new StringBuilder();
    for (final Object record : records) {
      final WorldRecord worldRecord = (WorldRecord) record;
      final String name = worldRecord.getName();
      if (this.plugin.isWorldLoaded(name)) {
        message.append(ChatColor.GREEN + name + ", ");
      } else {
        message.append(ChatColor.RED + name + ", ");
      }
    }
    message.delete(message.length() - 2, message.length());
    return message.toString();
  }

  
  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final List<? extends Object> records = this.plugin.getDatabaseHandler().list(WorldRecord.class);
    final String message = this.buildWorldList(records);
    sender.sendMessage(plugin.getSimpleFormattedMessage("list-header", records.size()));
    sender.sendMessage(message);
  }
  

  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    // TODO Auto-generated method stub
    
  }

}

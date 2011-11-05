/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ListCommand.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.dimensiondoor.commands;

import java.util.List;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command {

  public ListCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "list";
    description = "list all the worlds managed by DimensionDoor";
    usage = "/dd list";
    permission = plugin.getName() + "." + name;

  }

  @Override
  public void execute(final CommandSender sender, final List<String> arguments) {
    final List<WorldRecord> worlds = WorldRecord.findAll();
    final String message = buildWorldList(WorldRecord.findAll());

    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "Currently managing %d worlds:", worlds.size()));
    sender.sendMessage(message.toString());
  }

  private String buildWorldList(final List<WorldRecord> worlds) {
    final StringBuilder message = new StringBuilder();

    for (final WorldRecord world : worlds) {
      final String worldName = world.getName();

      if (plugin.isWorldLoaded(worldName)) {
        message.append(ChatColor.GREEN + worldName + ", ");
      } else {
        message.append(ChatColor.RED + worldName + ", ");
      }
    }

    message.delete(message.length() - 2, message.length());
    return message.toString();
  }
}

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

package name.richardson.james.dimensiondoor.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import name.richardson.james.dimensiondoor.WorldRecord;

public class ListCommand extends Command {

  public ListCommand() {
    super();
    name = "list";
    description = "list managed worlds.";
    usage = "/dd list";
    permission = this.registerCommandPermission();
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final List<WorldRecord> worlds = WorldRecordHandler.getWorldRecordList();
    final String message = buildWorldList(worlds);
    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "Currently managing %d worlds:", worlds.size()));
    sender.sendMessage(message);
  }

  private String buildWorldList(final List<WorldRecord> records) {
    final StringBuilder message = new StringBuilder();
    for (final WorldRecord record : records) {
      final String name = record.getName();
      if (WorldHandler.isWorldLoaded(name)) {
        message.append(ChatColor.GREEN + name + ", ");
      } else {
        message.append(ChatColor.RED + name + ", ");
      }
    }
    message.delete(message.length() - 2, message.length());
    return message.toString();
  }

  protected Map<String, Object> parseArguments(List<String> arguments) {
    return new HashMap<String, Object>();
  }

}

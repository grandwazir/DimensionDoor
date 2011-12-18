/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * LoadCommand.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor.creation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import name.richardson.james.dimensiondoor.database.WorldRecord;
import name.richardson.james.dimensiondoor.database.WorldRecordHandler;
import name.richardson.james.dimensiondoor.util.Command;

public class LoadCommand extends Command {

  public LoadCommand() {
    name = "load";
    description = "load a managed world into memory.";
    usage = "/dd load [world]";
    permission = this.registerCommandPermission();
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final String worldName = (String) arguments.get("world");
    WorldRecord record = WorldRecordHandler.getWorldRecord(worldName);
    WorldHandler.loadWorld(record);
    logger.info(String.format("%s has loaded the world %s", sender.getName(), worldName));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been loaded.", worldName));
  }

  protected Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      final String worldName = arguments.get(0);
      if (WorldHandler.isWorldLoaded(worldName)) {
        throw new IllegalArgumentException(String.format("%s is already loaded.", worldName));
      } else {
        map.put("world", worldName);
      }
    } catch (final IndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("You must specify a world.");
    }
    return map;
  }

}

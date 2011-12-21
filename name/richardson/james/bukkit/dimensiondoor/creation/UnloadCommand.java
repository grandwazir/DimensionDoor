/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * UnloadCommand.java is part of DimensionDoor.
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
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class UnloadCommand extends Command {

  public UnloadCommand() {
    name = "unload";
    description = "unload a world from memory.";
    usage = "/dd unload [world]";
    permission = this.registerCommandPermission();
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final World world = (World) arguments.get("world");
    WorldHandler.unloadWorld(world);
    logger.info(String.format("%s has unloaded the world %s", sender.getName(), world.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been unloaded.", world.getName()));
  }

  @Override
  protected Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      final String worldName = arguments.get(0);
      final World world = WorldHandler.getWorld(worldName);
      if (world == null) {
        throw new IllegalArgumentException(String.format("%s not loaded.", worldName));
      } else {
        map.put("world", world);
      }
    } catch (final IndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("You must specify a world.");
    }
    return map;
  }

}

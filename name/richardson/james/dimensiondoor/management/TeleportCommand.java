/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * TeleportCommand.java is part of DimensionDoor.
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

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends Command {

  public TeleportCommand() {
    name = "teleport";
    description = "teleport to another world.";
    usage = "/dd teleport [world]";
    permission = this.registerCommandPermission();
    isPlayerOnly = true;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final Player player = (Player) sender;
    final World world = (World) arguments.get("world");
    player.teleport(world.getSpawnLocation());
  }

  @Override
  protected Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      final String worldName = arguments.remove(0);
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

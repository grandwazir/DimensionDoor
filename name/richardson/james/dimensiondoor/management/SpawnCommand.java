/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * SpawnCommand.java is part of DimensionDoor.
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

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import name.richardson.james.dimensiondoor.util.Command;

public class SpawnCommand extends Command {

  public SpawnCommand() {
    super();
    name = "spawn";
    description = "set the spawn point of a world.";
    usage = "/dd spawn";
    permission = this.registerCommandPermission();
    isPlayerOnly = true;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final Player player = (Player) sender;
    final World world = player.getWorld();
    final Integer x = (int) player.getLocation().getX();
    final Integer y = (int) player.getLocation().getY();
    final Integer z = (int) player.getLocation().getZ();
    world.setSpawnLocation(x, y, z);
    logger.info(String.format("%s has set a new spawn location for %s", sender.getName(), world.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "New spawn location set for %s", world.getName()));
  }

  @Override
  protected Map<String, Object> parseArguments(List<String> arguments) {
    return null;
  }

}

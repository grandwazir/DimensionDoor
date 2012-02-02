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

package name.richardson.james.bukkit.dimensiondoor.management;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class SpawnCommand extends PlayerCommand {

  public static final String NAME = "spawn";
  public static final String DESCRIPTION = "Set the spawn point of the world.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to set the spawn points of worlds.";
  public static final String USAGE = "";
  public static final Permission PERMISSION = new Permission("dimensiondoor.spawn", SpawnCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);

  public SpawnCommand(final DimensionDoor plugin) {
    super(plugin, SpawnCommand.NAME, SpawnCommand.DESCRIPTION, SpawnCommand.USAGE, SpawnCommand.PERMISSION_DESCRIPTION, SpawnCommand.PERMISSION);
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, Object> arguments) throws CommandUsageException {
    if (sender instanceof ConsoleCommandSender) throw new CommandUsageException("You may not use this command from the console.");
    final Player player = (Player) sender;
    final World world = player.getWorld();
    final Integer x = (int) player.getLocation().getX();
    final Integer y = (int) player.getLocation().getY();
    final Integer z = (int) player.getLocation().getZ();
    world.setSpawnLocation(x, y, z);
    this.logger.info(String.format("%s has set a new spawn location for %s", sender.getName(), world.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "New spawn location set for %s", world.getName()));
  }

}

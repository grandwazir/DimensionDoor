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

package name.richardson.james.bukkit.dimensiondoor.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class TeleportCommand extends PlayerCommand {

  public static final String NAME = "teleport";
  public static final String DESCRIPTION = "Teleport to another world.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to teleport to other worlds.";
  public static final String USAGE = "<name>";
  public static final Permission PERMISSION = new Permission("dimensiondoor.teleport", PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final DimensionDoor plugin;
  
  public TeleportCommand(DimensionDoor plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.plugin = plugin;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final Player player = (Player) sender;
    final World world = (World) arguments.get("world");
    player.teleport(world.getSpawnLocation());
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      final String worldName = arguments.get(0);
      final World world = plugin.getWorld(worldName);
      if (world == null) {
        throw new CommandArgumentException(String.format("%s not loaded!", worldName), "Use /dd list for a list of worlds.");
      } else {
        map.put("world", world);
      }
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a world!", "Use /dd list for a list of worlds.");
    }
    return map;
  }

}

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

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class TeleportCommand extends PluginCommand {

  private final DimensionDoor plugin;
  private String worldName;

  public TeleportCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
    this.registerPermissions();
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final Player player = (Player) sender;
    final World world = this.plugin.getServer().getWorld(worldName);
    if (world == null) {
      throw new CommandUsageException(this.getSimpleFormattedMessage("world-not-loaded", worldName));
    }
    player.teleport(world.getSpawnLocation());
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {

    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("list-worlds-hint"));
    } else {
      this.worldName = arguments[0];
    }

  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }
  
}

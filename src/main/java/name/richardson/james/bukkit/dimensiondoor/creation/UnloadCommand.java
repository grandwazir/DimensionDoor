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

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.World;
import name.richardson.james.bukkit.dimensiondoor.WorldManager;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class UnloadCommand extends PluginCommand {

  private final WorldManager manager;

  private String worldName;

  public UnloadCommand(final DimensionDoor plugin) {
    super(plugin);
    this.manager = plugin.getWorldManager();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final World world = manager.getWorld(worldName);
    if (world == null) {
      throw new CommandArgumentException(this.getSimpleFormattedMessage("world-is-not-managed", this.worldName), this.getMessage("load-world-hint"));
    } else {
      world.unload();
      sender.sendMessage(this.getSimpleFormattedMessage("world-unloaded", this.worldName));
    }  
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {

    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("load-world-hint"));
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

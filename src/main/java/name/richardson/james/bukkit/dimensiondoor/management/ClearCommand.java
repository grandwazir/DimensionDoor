/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ClearCommand.java is part of DimensionDoor.
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

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class ClearCommand extends PluginCommand {

  private final DimensionDoor plugin;
  private String worldName;

  public ClearCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
  }


  public void execute(CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException, CommandPermissionException, CommandUsageException {
    final World world = this.plugin.getWorld(worldName);
    if (world == null) {
      throw new CommandArgumentException(this.getSimpleFormattedMessage("world-is-not-managed", this.worldName), this.getMessage("load-world-hint"));
    }
    int count = 0;
    for (Entity entity : world.getEntities()) {
      if (entity instanceof Monster || entity instanceof Animals) {
        entity.remove();
        count++;
      }
    }
    Object[] arguments = {count,world.getName()};
    sender.sendMessage(this.getSimpleFormattedMessage("clear-report", arguments));
  }
  

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {

    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("load-world-hint"));
    } else {
      this.worldName = arguments[0];
    }

  }

}

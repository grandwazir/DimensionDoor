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

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class ClearCommand extends AbstractCommand {

  private String worldName;

  public ClearCommand(final DimensionDoor plugin) {
    super(plugin, false);
  }

  public void execute(CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException, CommandPermissionException, CommandUsageException {
    final World world = Bukkit.getServer().getWorld(worldName);
    if (world == null) {
      throw new CommandUsageException(this.getLocalisation().getMessage(DimensionDoor.class, "world-is-not-managed", this.worldName));
    }
    int count = 0;
    for (Entity entity : world.getEntities()) {
      if (entity instanceof Monster || entity instanceof Animals) {
        entity.remove();
        count++;
      }
    }
    Object[] arguments = { count, world.getName() };
    sender.sendMessage(this.getLocalisation().getMessage(this, "clear-report", arguments));
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(DimensionDoor.class, "must-specify-a-world-name"), null);
    } else {
      this.worldName = arguments[0];
    }
  }
  

}

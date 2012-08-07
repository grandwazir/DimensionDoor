/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * LoadCommand.java is part of DimensionDoor.
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

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.World;
import name.richardson.james.bukkit.dimensiondoor.WorldManager;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class LoadCommand extends AbstractCommand {

  private final WorldManager worldManager;

  private String worldName;

  public LoadCommand(final DimensionDoor plugin) {
    super(plugin, false);
    this.worldManager = plugin.getWorldManager();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final World world = this.worldManager.getWorld(worldName);
    if (world == null) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(DimensionDoor.class, "world-is-not-managed", this.worldName), this.getLocalisation().getMessage(this, "load-existing-world"));
    } else if (world.isLoaded()) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "world-is-already-loaded", this.worldName), this.getLocalisation().getMessage(this, "may-not-load-world-twice"));
    } else {
      try {
        world.load();
      } catch (final Exception exception) {
        final String message = this.getLocalisation().getMessage(this, "unable-to-load-world", this.worldName);
        this.getLogger().warning(this, "unable-to-load-world", this.worldName);
        throw new CommandUsageException(message);
      }
      sender.sendMessage(this.getLocalisation().getMessage(this, "world-loaded", this.worldName));
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(DimensionDoor.class, "must-specify-a-world-name"), null);
    } else {
      this.worldName = arguments[0];
    }
  }

  
}

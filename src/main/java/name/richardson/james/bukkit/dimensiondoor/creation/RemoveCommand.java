/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * RemoveCommand.java is part of DimensionDoor.
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
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.internals.Logger;

public class RemoveCommand extends PluginCommand {

  private static Logger logger = new Logger(RemoveCommand.class);
  
  private final DimensionDoor plugin;
  
  private String worldName;

  public RemoveCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
  }
  
  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("load-world-hint"));
    } else {
      this.worldName = arguments[0];
    }
    
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final WorldRecord record = WorldRecord.findByName(this.plugin.getDatabaseHandler(), worldName);
    if (record == null) {
      throw new CommandArgumentException(this.getSimpleFormattedMessage("world-is-not-managed", this.worldName), this.getMessage("load-world-hint"));
    }
    this.plugin.removeWorld(record);
    RemoveCommand.logger.info(String.format("%s has removed the WorldRecord for %s", sender.getName(), worldName));
    sender.sendMessage(this.getSimpleFormattedMessage("world-removed", worldName));
    sender.sendMessage(this.getMessage("remove-data-also"));
  }

}

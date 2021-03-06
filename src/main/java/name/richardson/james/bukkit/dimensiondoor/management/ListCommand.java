/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ListCommand.java is part of DimensionDoor.
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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.World;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class ListCommand extends AbstractCommand {

  private final DimensionDoor plugin;

  public ListCommand(final DimensionDoor plugin) {
    super(plugin, false);
    this.plugin = plugin;
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final String message = this.buildWorldList();
    sender.sendMessage(this.getLocalisation().getMessage(this, "header", plugin.getWorldManager().configuredWorldCount()));
    sender.sendMessage(message);
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    // TODO Auto-generated method stub

  }

  private String buildWorldList() {
    final StringBuilder message = new StringBuilder();
    for (final World world : this.plugin.getWorldManager().getWorlds().values()) {
      final String name = world.getName();
      if (world.isLoaded()) {
        message.append(ChatColor.GREEN + name + ", ");
      } else {
        message.append(ChatColor.RED + name + ", ");
      }
    }
    message.delete(message.length() - 2, message.length());
    return message.toString();
  }
  
}

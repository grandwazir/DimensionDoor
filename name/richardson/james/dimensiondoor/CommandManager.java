/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CommandManager.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import name.richardson.james.dimensiondoor.util.Command;

public class CommandManager implements CommandExecutor {

  private final HashMap<String, Command> commands = new HashMap<String, Command>();

  public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
    if (args.length != 0) {
      if (args[0].equalsIgnoreCase("help")) {
        if (commands.containsKey(args[0])) {
          final Command c = commands.get(args[0]);
          sender.sendMessage(ChatColor.RED + c.getUsage());
          sender.sendMessage(ChatColor.YELLOW + "Description: " + c.getDescription());
        } else {
          sender.sendMessage(ChatColor.RED + "/dd help <command>");
          sender.sendMessage(ChatColor.YELLOW + "You must specify a valid command.");
        }
      } else if (commands.containsKey(args[0])) {
        commands.get(args[0]).onCommand(sender, command, label, args);
      }
    }
    return true;
  }

  public void registerCommand(final String commandName, final Command command) {
    commands.put(commandName, command);
  }

}

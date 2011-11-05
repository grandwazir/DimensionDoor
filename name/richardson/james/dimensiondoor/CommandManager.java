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
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.dimensiondoor;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

  private final HashMap<String, CommandExecutor> commands = new HashMap<String, CommandExecutor>();

  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (args.length != 0) {
      if (commands.containsKey(args[0])) {
        commands.get(args[0]).onCommand(sender, command, label, args);
        return true;
      }
    }
    sender.sendMessage(ChatColor.RED + "Invalid command!");
    sender.sendMessage(ChatColor.YELLOW + "/dd [create|info|list|load|modify|template|remove|unload]");
    sender.sendMessage(ChatColor.YELLOW + "/dd [teleport|spawn]");
    return true;
  }

  public void registerCommand(final String command, final CommandExecutor executor) {
    commands.put(command, executor);
  }

}

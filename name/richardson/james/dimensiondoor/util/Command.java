/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * Command.java is part of jChat.
 * 
 * jChat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * jChat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * jChat. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import name.richardson.james.dimensiondoor.DimensionDoor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public abstract class Command implements CommandExecutor {

  protected String description;

  protected final Logger logger;
  protected String name;
  protected String permission;
  protected final DimensionDoor plugin;
  protected String usage;

  public Command(final DimensionDoor plugin) {
    this.plugin = plugin;
    this.logger = new Logger(this.getClass());
  }

  public abstract void execute(CommandSender sender, Map<String, Object> arguments);

  @Override
  public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
    if (!this.authorisePlayer(sender)) {
      sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
      return true;
    }

    try {
      final LinkedList<String> arguments = new LinkedList<String>();
      arguments.addAll(Arrays.asList(args));
      final Map<String, Object> parsedArguments = this.parseArguments(arguments);
      this.execute(sender, parsedArguments);
    } catch (final IllegalArgumentException e) {
      sender.sendMessage(ChatColor.RED + this.usage);
      sender.sendMessage(ChatColor.YELLOW + e.getMessage());
    } catch (final IllegalStateException e) {
      sender.sendMessage(ChatColor.RED + this.usage);
      sender.sendMessage(ChatColor.YELLOW + e.getMessage());
    }
    return true;
  }

  /**
   * Check to see if a player has permission to use this command.
   * 
   * A console user is permitted to use all commands by default.
   * 
   * @param sender
   * The player/console that is attempting to use the command
   * @return true if the player has permission; false otherwise.
   */
  protected boolean authorisePlayer(final CommandSender sender) {
    if (sender instanceof ConsoleCommandSender)
      return true;
    else if (sender instanceof Player) {
      final Player player = (Player) sender;
      if (player.hasPermission(this.permission) || player.hasPermission("jchat.*")) { return true; }
    }
    return false;
  }

  protected abstract Map<String, Object> parseArguments(List<String> arguments);

  protected void registerPermission(final String name, final String description, final PermissionDefault defaultValue) {
    final Permission permission = new Permission(name, description, defaultValue);
    this.plugin.getServer().getPluginManager().addPermission(permission);
  }

}

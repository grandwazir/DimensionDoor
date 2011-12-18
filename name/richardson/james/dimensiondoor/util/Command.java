/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * Command.java is part of DimensionDoor.
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

package name.richardson.james.dimensiondoor.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.dimensiondoor.DimensionDoor;

public abstract class Command implements CommandExecutor {

  protected static final Logger logger = new Logger(Command.class);

  protected String name;
  protected String description;
  protected String usage;
  protected Permission permission;
  protected Boolean isPlayerOnly = false;

  public abstract void execute(CommandSender sender, Map<String, Object> arguments);

  public String getDescription() {
    return new String(description);
  }

  public String getName() {
    return new String(name);
  }

  public Permission getPermission() {
    return permission;
  }

  public String getUsage() {
    return new String(usage);
  }

  @Override
  public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
    if (sender.hasPermission(this.permission) && (!(sender instanceof ConsoleCommandSender))) {
      sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
      return true;
    }

    if (this.isPlayerOnly && (sender instanceof ConsoleCommandSender)) {
      sender.sendMessage(ChatColor.RED + "You may not use this command from the console.");
      return true;
    }

    try {
      final LinkedList<String> arguments = new LinkedList<String>();
      arguments.addAll(Arrays.asList(args));
      arguments.remove(0);
      final Map<String, Object> parsedArguments = this.parseArguments(arguments);
      this.execute(sender, parsedArguments);
    } catch (final IllegalStateException e) {
      sender.sendMessage(ChatColor.RED + e.getMessage());
    } catch (final IllegalArgumentException e) {
      sender.sendMessage(ChatColor.RED + this.usage);
      sender.sendMessage(ChatColor.YELLOW + e.getMessage());
    }
    return true;
  }

  protected abstract Map<String, Object> parseArguments(List<String> arguments);

  protected Permission registerCommandPermission() {
    Permission permission = new Permission("dimensiondoor." + name, "Allow users to " + description, PermissionDefault.OP);
    permission.addParent(DimensionDoor.getInstance().getRootPermission(), true);
    return registerPermission(permission);
  }

  protected Permission registerPermission(Permission permission) {
    DimensionDoor.getInstance().getServer().getPluginManager().addPermission(permission);
    return permission;
  }

}

/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * InfoCommand.java is part of DimensionDoor.
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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class InfoCommand extends PluginCommand {

  private final DimensionDoor plugin;

  private String worldName;

  public InfoCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
    this.registerPermissions();
  }

  public void execute(CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException, CommandPermissionException, name.richardson.james.bukkit.utilities.command.CommandUsageException {
    WorldRecord record = WorldRecord.findByName(this.plugin.getDatabaseHandler(), worldName);

    if (record == null) {
      throw new CommandArgumentException(this.getSimpleFormattedMessage("world-is-not-managed", this.worldName), this.getMessage("load-existing-world"));
    }

    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "World information for %s:", record.getName()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- seed: %d", record.getSeed()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- environment: %s", record.getEnvironment().toString()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- difficulty: %s", record.getDifficulty().name()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- game mode: %s", record.getGamemode().name()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- isolated chat: %b", record.isIsolatedChat()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- pvp: %b", record.isPvp()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- spawn animals: %b", record.isSpawnAnimals()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- spawn monsters: %b", record.isSpawnMonsters()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- keep spawn in memory: %b", record.isKeepSpawnInMemory()));
    if (record.getGeneratorPlugin() != null) {
      sender.sendMessage(String.format(ChatColor.YELLOW + "- generator plugin: %s", record.getGeneratorPlugin()));
      sender.sendMessage(String.format(ChatColor.YELLOW + "- generator id: %s", record.getGeneratorID()));
    }

  }

  public void parseArguments(String[] arguments, CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {

    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("load-world-hint"));
    } else {
      this.worldName = arguments[0];
    }

  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("infocommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }
  
}

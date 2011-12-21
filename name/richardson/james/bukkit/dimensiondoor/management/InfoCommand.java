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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class InfoCommand extends PlayerCommand {

  public static final String NAME = "info";
  public static final String DESCRIPTION = "Get information about a world.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to get information about worlds.";
  public static final String USAGE = "[name]";
  public static final Permission PERMISSION = new Permission("dimensiondoor.info", PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final DimensionDoor plugin;
  
  public InfoCommand(DimensionDoor plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.plugin = plugin;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandUsageException {
    final WorldRecord record;

    if (arguments.isEmpty()) {
      if (sender instanceof Player) {
        final Player player = (Player) sender;
        final World world = player.getWorld();
        record = WorldRecord.findByWorld(plugin.getDatabaseHandler(), world);
      } else {
        throw new CommandUsageException("You must specify a world.");
      }
    } else {
      record = (WorldRecord) arguments.get("record");
    }

    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "World information for %s:", record.getName()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- seed: %d", record.getSeed()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- environment: %s", record.getEnvironment().toString()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- difficulty: %s", record.getDifficulty().name()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- game-mode: %s", record.getGamemode().name()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- isolated-chat: %b", record.isIsolatedChat()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- pvp: %b", record.isPvp()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- spawn-animals: %b", record.isSpawnAnimals()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- spawn-monsters: %b", record.isSpawnMonsters()));
    if (record.getGeneratorPlugin() != null) {
      sender.sendMessage(String.format(ChatColor.YELLOW + "- generator-plugin: %s", record.getGeneratorPlugin()));
      sender.sendMessage(String.format(ChatColor.YELLOW + "- generator-id: %s", record.getGeneratorID()));
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> map = new HashMap<String, Object>();
    if (!arguments.isEmpty()) {
      final String worldName = arguments.get(0);
      final WorldRecord record = WorldRecord.findByName(plugin.getDatabaseHandler(), worldName);
      if (record == null) {
        throw new CommandArgumentException(String.format("%s is not managed by DimensionDoor!", worldName), "Use /dd list for a list of worlds.");
      } else {
        map.put("record", record);
      }
    }
    return map;
  }

}

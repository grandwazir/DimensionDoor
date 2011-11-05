/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * InfoCommand.java is part of DimensionDoor.
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
package name.richardson.james.dimensiondoor.commands;

import java.util.HashMap;
import java.util.List;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends Command {

  public InfoCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "info";
    description = "shows specific details about a world";
    usage = "/dd info <world>";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws WorldIsNotManagedException, NotEnoughArgumentsException, WorldIsNotLoadedException {
    WorldRecord worldRecord = null;

    try {
      worldRecord = WorldRecord.findFirst(args.get(0));
    } catch (final IndexOutOfBoundsException e) {
      if (sender instanceof Player) {
        final Player player = (Player) sender;
        worldRecord = WorldRecord.findFirst(player.getWorld());
      } else {
        throw new NotEnoughArgumentsException(name, usage);
      }
    }

    final HashMap<String, Boolean> attributes = worldRecord.getAttributes();
    final HashMap<String, String> generatorAttributes = worldRecord.getGeneratorAttributes();
    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "World information for %s:", worldRecord.getName()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- seed: %s", plugin.getWorldSeed(worldRecord.getName())));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- environment: %s", worldRecord.getEnvironment().toString()));
    for (final String key : attributes.keySet())
      sender.sendMessage(String.format(ChatColor.YELLOW + "- %s: %s", key, Boolean.toString(attributes.get(key))));
    for (final String key : generatorAttributes.keySet()) {
      if (generatorAttributes.get(key) != null)
        sender.sendMessage(String.format(ChatColor.YELLOW + "- %s: %s", key, generatorAttributes.get(key)));
    }
    sender.sendMessage(String.format(ChatColor.YELLOW + "- %s: %s", "difficulty", worldRecord.getDifficulty().name()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- %s: %s", "gamemode", worldRecord.getGamemode().name()));
  }
}

/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * UnloadCommand.java is part of DimensionDoor.
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

import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotEmptyException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class UnloadCommand extends Command {

  public UnloadCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "unload";
    description = "unload a specific world from memory";
    usage = "/dd unload [world]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws NotEnoughArgumentsException, WorldIsNotLoadedException,
      WorldIsNotEmptyException {
    if (args.size() < 1)
      throw new NotEnoughArgumentsException(name, usage);
    final World world = plugin.getWorld(args.get(0));

    plugin.unloadWorld(world);
    DimensionDoor.log(Level.INFO, String.format("%s has unloaded the world %s", getSenderName(sender), world.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been unloaded.", world.getName()));
  }

}

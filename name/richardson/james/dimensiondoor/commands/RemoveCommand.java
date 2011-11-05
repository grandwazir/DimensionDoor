/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * RemoveCommand.java is part of DimensionDoor.
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
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class RemoveCommand extends Command {

  public RemoveCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "remove";
    description = "remove a specfic world so it is no longer managed by DimensionDoor";
    usage = "/dd remove [world]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws NotEnoughArgumentsException, WorldIsNotEmptyException,
      WorldIsNotLoadedException, WorldIsNotManagedException {
    if (args.size() < 1)
      throw new NotEnoughArgumentsException(name, usage);
    final World world = plugin.getWorld(args.get(0));
    final WorldRecord worldRecord = WorldRecord.findFirst(world.getName());

    plugin.unloadWorld(world);
    worldRecord.delete();
    DimensionDoor.log(Level.INFO, String.format("%s has removed the WorldRecord for %s", getSenderName(sender), args.get(0)));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been removed.", args.get(0)));
    sender.sendMessage(ChatColor.YELLOW + "You will still need to remove the world directory.");
  }

}

/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * TeleportCommand.java is part of DimensionDoor.
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

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.CommandIsPlayerOnlyException;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends Command {

  public TeleportCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "teleport";
    description = "teleport yourself to a different world";
    usage = "/dd teleport [world]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws NotEnoughArgumentsException, CommandIsPlayerOnlyException,
      WorldIsNotLoadedException {
    if (args.size() < 1)
      throw new NotEnoughArgumentsException(name, usage);
    if (sender instanceof ConsoleCommandSender)
      throw new CommandIsPlayerOnlyException();
    final Player player = (Player) sender;
    final World targetWorld = plugin.getWorld(args.get(0));

    player.teleport(targetWorld.getSpawnLocation());
  }

}

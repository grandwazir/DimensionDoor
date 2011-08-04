
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

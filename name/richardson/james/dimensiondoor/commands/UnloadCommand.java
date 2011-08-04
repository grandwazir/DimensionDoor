
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

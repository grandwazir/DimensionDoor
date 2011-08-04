
package name.richardson.james.dimensiondoor.commands;

import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.CustomChunkGeneratorNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.PluginNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsAlreadyLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LoadCommand extends Command {

  public LoadCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "load";
    description = "load a managed world into memory";
    usage = "/dd load [world]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws WorldIsNotManagedException, NotEnoughArgumentsException,
      WorldIsAlreadyLoadedException, WorldIsNotLoadedException, PluginNotFoundException, CustomChunkGeneratorNotFoundException {
    if (args.size() < 1)
      throw new NotEnoughArgumentsException(name, usage);
    final WorldRecord worldRecord = WorldRecord.findFirst(args.get(0));

    plugin.loadWorld(worldRecord);
    DimensionDoor.log(Level.INFO, String.format("%s has loaded the world %s", getSenderName(sender), args.get(0)));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been loaded.", args.get(0)));
  }

}

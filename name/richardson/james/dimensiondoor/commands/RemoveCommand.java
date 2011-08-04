
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

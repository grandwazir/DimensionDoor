
package name.richardson.james.dimensiondoor.commands;

import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TemplateCommand extends Command {

  public TemplateCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "template";
    description = "copy all the settings from one world to another";
    usage = "/dd template [sourceWorld] [targetWorld]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws NotEnoughArgumentsException, WorldIsNotManagedException {
    if (args.size() < 2)
      throw new NotEnoughArgumentsException(name, usage);
    final WorldRecord sourceWorld = WorldRecord.findFirst(args.get(0));
    final WorldRecord targetWorld = WorldRecord.findFirst(args.get(1));

    targetWorld.setAttributes(sourceWorld.getAttributes());
    DimensionDoor.log(Level.INFO, String.format("%s has copied the attributes from %s to %s", getSenderName(sender), args.get(0), args.get(0)));
    sender.sendMessage(String.format(ChatColor.GREEN + "Settings copied from %s to %s", args.get(0), args.get(1)));
  }

}

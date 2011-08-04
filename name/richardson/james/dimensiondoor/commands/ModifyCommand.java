
package name.richardson.james.dimensiondoor.commands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.InvalidAttributeException;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ModifyCommand extends Command {

  public ModifyCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "modify";
    description = "modify an attribute on a specific world";
    usage = "/dd modify [world] [attribute] [value]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws WorldIsNotManagedException, NotEnoughArgumentsException,
      InvalidAttributeException, WorldIsNotLoadedException {
    if (args.size() < 3)
      throw new NotEnoughArgumentsException(name, usage);
    final WorldRecord worldRecord = WorldRecord.findFirst(args.get(0));
    final String attributeName = args.get(1);
    final boolean attributeValue = Boolean.valueOf(args.get(2));
    final HashMap<String, Boolean> attributes = worldRecord.getAttributes();

    if (attributes.containsKey(attributeName)) {
      attributes.put(attributeName, attributeValue);
      worldRecord.setAttributes(attributes);
      DimensionDoor.log(Level.INFO, String.format("%s has changed %s to %s for %s", getSenderName(sender), attributeName, Boolean.toString(attributeValue),
          args.get(0)));
      plugin.applyWorldAttributes(worldRecord);
      sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attributeName, Boolean.toString(attributeValue), args.get(0)));
    } else {
      throw new InvalidAttributeException(attributeName);
    }
  }

}

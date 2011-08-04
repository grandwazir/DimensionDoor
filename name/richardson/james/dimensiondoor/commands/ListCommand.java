
package name.richardson.james.dimensiondoor.commands;

import java.util.List;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command {

  public ListCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "list";
    description = "list all the worlds managed by DimensionDoor";
    usage = "/dd list";
    permission = plugin.getName() + "." + name;

  }

  @Override
  public void execute(final CommandSender sender, final List<String> arguments) {
    final List<WorldRecord> worlds = WorldRecord.findAll();
    final String message = buildWorldList(WorldRecord.findAll());

    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "Currently managing %d worlds:", worlds.size()));
    sender.sendMessage(message.toString());
  }

  private String buildWorldList(final List<WorldRecord> worlds) {
    final StringBuilder message = new StringBuilder();

    for (final WorldRecord world : worlds) {
      final String worldName = world.getName();

      if (plugin.isWorldLoaded(worldName)) {
        message.append(ChatColor.GREEN + worldName + ", ");
      } else {
        message.append(ChatColor.RED + worldName + ", ");
      }
    }

    message.delete(message.length() - 2, message.length());
    return message.toString();
  }
}

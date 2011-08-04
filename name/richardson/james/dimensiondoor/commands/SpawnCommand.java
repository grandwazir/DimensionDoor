
package name.richardson.james.dimensiondoor.commands;

import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.CommandIsPlayerOnlyException;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends Command {

  public SpawnCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "spawn";
    description = "set the spawn point of the world to your current location";
    usage = "/dd spawn";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws CommandIsPlayerOnlyException {
    if (sender instanceof ConsoleCommandSender)
      throw new CommandIsPlayerOnlyException();
    final Player player = (Player) sender;
    final World world = player.getWorld();
    final Integer x = (int) player.getLocation().getX();
    final Integer y = (int) player.getLocation().getY();
    final Integer z = (int) player.getLocation().getZ();

    world.setSpawnLocation(x, y, z);
    DimensionDoor.log(Level.INFO, String.format("%s has set a new spawn location for %s", getSenderName(sender), world.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "New spawn location set for %s", world.getName()));
  }

}

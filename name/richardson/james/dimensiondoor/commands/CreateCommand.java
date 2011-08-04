
package name.richardson.james.dimensiondoor.commands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.CustomChunkGeneratorNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.InvalidEnvironmentException;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.PluginNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsAlreadyLoadedException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CreateCommand extends Command {

  public CreateCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "create";
    description = "create (or import) a new world";
    usage = "/dd create [world] [environment] <s:seed> <g:generatorPlugin:generatorID>";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws NotEnoughArgumentsException, InvalidEnvironmentException, WorldIsAlreadyLoadedException, PluginNotFoundException, CustomChunkGeneratorNotFoundException {
    if (args.size() < 2)
      throw new NotEnoughArgumentsException(name, usage);
    final HashMap<String, String> optionalArguments = getOptionalArguments(args);

    sender.sendMessage(String.format(ChatColor.YELLOW + "Creating %s (this may take a while)", args.get(0)));
    
    if (optionalArguments.containsKey("seed") && !optionalArguments.containsKey("generatorPlugin"))
      plugin.createWorld(args.get(0), args.get(1).toUpperCase(), optionalArguments.get("seed"));
    else if (optionalArguments.containsKey("seed") && optionalArguments.containsKey("generatorPlugin"))
      plugin.createWorld(args.get(0), args.get(1).toUpperCase(), optionalArguments.get("seed"), optionalArguments.get("generatorPlugin"), optionalArguments.get("generatorID"));
    else if (optionalArguments.containsKey("generatorPlugin"))
      plugin.createWorld(args.get(0), args.get(1).toUpperCase(), optionalArguments.get("generatorPlugin"), null);
    else 
      plugin.createWorld(args.get(0), args.get(1).toUpperCase());
    sender.sendMessage(String.format(ChatColor.GREEN + "has been created.", args.get(0)));
    DimensionDoor.log(Level.INFO, String.format("%s has created a new world called %s", getSenderName(sender), args.get(0)));

  }

  private HashMap<String, String> getOptionalArguments(final List<String> args) {
    final HashMap<String, String> m = new HashMap<String, String>();

    for (String argument : args) {
      if (argument.startsWith("s:")) {
        m.put("seed", argument.replaceFirst("s:", ""));
      } else if (argument.startsWith("g:")) {
        argument = argument.replaceFirst("g:", "");
        final String[] arguments = argument.split(":");
        m.put("generatorPlugin", arguments[0]);
        if (arguments.length == 2) {
          m.put("generatorID", arguments[1]);
        }
      }
    }
    DimensionDoor.log(Level.INFO, m.toString());
    return m;
    
  }

}

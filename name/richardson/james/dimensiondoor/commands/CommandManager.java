
package name.richardson.james.dimensiondoor.commands;

import java.util.Arrays;
import java.util.List;

import name.richardson.james.dimensiondoor.exceptions.InvalidEnvironment;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArguments;
import name.richardson.james.dimensiondoor.exceptions.WorldIsAlreadyLoaded;
import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.PlayerNotAuthorised;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
  private final DimensionDoor plugin;

  public CommandManager(DimensionDoor plugin) {
    this.plugin = plugin;
  }
  
  public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
    String playerName = getSenderName(sender);
    
     try {
       if (command.getName().equalsIgnoreCase("dd")) { 
         if (args.length > 0) {
           String subCommand = args[0];
           playerHasPermission(playerName, "dimensiondoor." + subCommand);
           if (subCommand.matches("list")) {
             return listWorlds(sender, args);
           } else if (subCommand.matches("create")) {
             return createWorld(sender, args);
           } else {
             return false;
           }
         } else {
           return false;
         }
       } else {
         return false;
       }
     } catch (PlayerNotAuthorised e) {
       sender.sendMessage(ChatColor.RED + "You do not have permission to do that");
     } catch (NotEnoughArguments e) {
       sender.sendMessage(ChatColor.RED + "Not enough arguments!");
       sender.sendMessage(ChatColor.YELLOW + e.getUsage());
     } catch (WorldIsAlreadyLoaded e) {
       sender.sendMessage(ChatColor.RED + "World is already loaded!");
     } catch (InvalidEnvironment e) {
       sender.sendMessage(ChatColor.RED + "Invalid environment type!");
     } 
     return true;
  }
  
  public boolean createWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, InvalidEnvironment, WorldIsAlreadyLoaded {
    if (arguments.length < 3) throw new NotEnoughArguments("dd create", "/dd create [name] [environment] <seed>");
    final String worldName = arguments[1];
    final String environment = arguments[2];
    
    if (plugin.isWorldLoaded(worldName)) throw new WorldIsAlreadyLoaded();
    
    if (arguments.length == 4) {
      plugin.createWorld(worldName, environment, arguments[3]);
    } else {
      plugin.createWorld(worldName, environment, null);
    }
     
    return true;
  }
  
  public boolean listWorlds(CommandSender sender, String[] arguments) {
    final List<WorldRecord> worlds = WorldRecord.findAll();
    StringBuilder message = new StringBuilder();
    
    for (WorldRecord world : worlds) {
      final String worldName = world.getName();
      if (plugin.isWorldLoaded(worldName)) {
        message.append(ChatColor.GREEN + worldName + ", ");
      } else {
        message.append(ChatColor.RED + worldName + ", ");
      }
    }
    
    message.deleteCharAt(message.length() - 2);
    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "Currently managing %s worlds:", Integer.toString(worlds.size())));
    sender.sendMessage(message.toString());
    return true;
  }
  
  private String getSenderName(CommandSender sender) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      String senderName = player.getName();
      return senderName;
    } else {
      return "console";
    }
  }
  
  private Player getPlayerFromName(final String playerName) {
    final List<Player> possiblePlayers = plugin.getServer().matchPlayer(playerName);
    return possiblePlayers.get(0);
  }
  
  private boolean playerHasPermission(final String playerName, final String node) throws PlayerNotAuthorised {
    if (playerName == "console") { 
      return true;
    } else {
      Player player = getPlayerFromName(playerName);
      if (player.hasPermission(node)) {
        return true;
      } else if (plugin.externalPermissions != null) {
        if (plugin.externalPermissions.has(player, node)) {
          return true;
        }
      }   
    } 
    throw new PlayerNotAuthorised();
  }

}


package name.richardson.james.dimensiondoor.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.CommandIsPlayerOnlyException;
import name.richardson.james.dimensiondoor.exceptions.CustomChunkGeneratorNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.InvalidAttributeException;
import name.richardson.james.dimensiondoor.exceptions.InvalidEnvironmentException;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.PlayerNotAuthorisedException;
import name.richardson.james.dimensiondoor.exceptions.PluginNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsAlreadyLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotEmptyException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class Command implements CommandExecutor {

  protected String description;
  protected String name;
  protected String permission;
  protected DimensionDoor plugin;
  protected String usage;

  public Command(final DimensionDoor plugin) {
    super();
    this.plugin = plugin;
  }

  public abstract void execute(CommandSender sender, List<String> args) throws CommandIsPlayerOnlyException, PlayerNotAuthorisedException,
      NotEnoughArgumentsException, WorldIsNotManagedException, WorldIsAlreadyLoadedException, InvalidAttributeException, InvalidEnvironmentException,
      WorldIsNotLoadedException, WorldIsNotEmptyException, PluginNotFoundException, CustomChunkGeneratorNotFoundException;

  public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
    try {
      final List<String> arguments = new LinkedList<String>(Arrays.asList(args));
      arguments.remove(0);
      authorisePlayer(sender, permission);
      execute(sender, arguments);
    } catch (final CommandIsPlayerOnlyException e) {
      sender.sendMessage(ChatColor.RED + "You can not use this command from the console!");
    } catch (final PlayerNotAuthorisedException e) {
      sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
    } catch (final NotEnoughArgumentsException e) {
      sender.sendMessage(ChatColor.RED + "Not enough arguments!");
      sender.sendMessage(ChatColor.YELLOW + e.getUsage());
    } catch (final WorldIsNotManagedException e) {
      sender.sendMessage(ChatColor.RED + "That world is not managed by DimensionDoor!");
    } catch (final WorldIsAlreadyLoadedException e) {
      sender.sendMessage(ChatColor.RED + "World is already loaded!");
    } catch (final InvalidAttributeException e) {
      sender.sendMessage(ChatColor.RED + "Invalid attribute!");
      sender.sendMessage(ChatColor.YELLOW + "Valid attributes: pvp, spawnMonsters/Animals, isolatedChat");
    } catch (final InvalidEnvironmentException e) {
      sender.sendMessage(ChatColor.RED + "Invalid environment type!");
      sender.sendMessage(ChatColor.YELLOW + "Valid types: NORMAL, NETHER, SKYLANDS");
    } catch (final WorldIsNotLoadedException e) {
      sender.sendMessage(ChatColor.RED + "World is not loaded!");
    } catch (final WorldIsNotEmptyException e) {
      sender.sendMessage(ChatColor.RED + "Can not unload worlds which contain players!");
    } catch (PluginNotFoundException e) {
      sender.sendMessage(ChatColor.RED + "Generator plugin not found!");
    } catch (CustomChunkGeneratorNotFoundException e) {
      sender.sendMessage(ChatColor.RED + "Generator plugin does not support that generator!");
    }
    return true;
  }

  protected String getSenderName(final CommandSender sender) {
    if (sender instanceof ConsoleCommandSender) {
      return "console";
    } else {
      final Player player = (Player) sender;
      return player.getName();
    }
  }
  
  protected void authorisePlayer(CommandSender sender, String node) throws PlayerNotAuthorisedException {
    node = node.toLowerCase();
    
    if (sender instanceof ConsoleCommandSender) {
      return;
    } else {
      final Player player = (Player) sender;
      if (player.hasPermission(node) || player.hasPermission("dimensiondoor.*")) { 
        return; 
      }

      if (plugin.externalPermissions != null) {
        if (plugin.externalPermissions.has(player, node))
          return;
      }
    }

    throw new PlayerNotAuthorisedException();
  }

}

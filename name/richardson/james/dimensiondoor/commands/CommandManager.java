
package name.richardson.james.dimensiondoor.commands;


import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.exceptions.CommandIsPlayerOnly;
import name.richardson.james.dimensiondoor.exceptions.InvalidAttribute;
import name.richardson.james.dimensiondoor.exceptions.InvalidEnvironment;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArguments;
import name.richardson.james.dimensiondoor.exceptions.WorldIsAlreadyLoaded;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotEmpty;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoaded;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManaged;
import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.PlayerNotAuthorised;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
           } else if (subCommand.matches("load")) {
             return loadWorld(sender, args);
           } else if (subCommand.matches("unload")) {
             return unloadWorld(sender, args);
           } else if (subCommand.matches("remove")) {
             return removeWorld(sender, args);
           } else if (subCommand.matches("info")) {
             return worldInfo(sender, args);
           } else if (subCommand.matches("modify")) {
             return modifyWorld(sender, args);
           } else if (subCommand.matches("template")) {
             return cloneWorld(sender, args);
           } else if (subCommand.matches("teleport")) {
             return teleportToWorld(sender, args);
           } else if (subCommand.matches("spawn")) {
             return setWorldSpawn(sender, args);
           }
         }
       }
       sender.sendMessage(ChatColor.RED + "Invalid command!");
       sender.sendMessage(ChatColor.YELLOW + "/dd [create|info|list|load|modify|template|remove|unload]");
       sender.sendMessage(ChatColor.YELLOW + "/dd [teleport|spawn]");
       return true;
     } catch (PlayerNotAuthorised e) {
       sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
     } catch (NotEnoughArguments e) {
       sender.sendMessage(ChatColor.RED + "Not enough arguments!");
       sender.sendMessage(ChatColor.YELLOW + e.getUsage());
     } catch (WorldIsNotManaged e) {
       sender.sendMessage(ChatColor.RED + "That world is not managed by DimensionDoor!");
     } catch (WorldIsAlreadyLoaded e) {
       sender.sendMessage(ChatColor.RED + "World is already loaded!");
     } catch (InvalidAttribute e) {
       sender.sendMessage(ChatColor.RED + "Invalid attribute!");
       sender.sendMessage(ChatColor.YELLOW + "Valid attributes: pvp, spawnMonsters/Animals, isolatedChat");
     } catch (InvalidEnvironment e) {
       sender.sendMessage(ChatColor.RED + "Invalid environment type!");
       sender.sendMessage(ChatColor.YELLOW + "Valid types: NORMAL, NETHER, SKYLANDS");
     } catch (WorldIsNotLoaded e) {
       sender.sendMessage(ChatColor.RED + "World is not loaded!");
     } catch (WorldIsNotEmpty e) {
       sender.sendMessage(ChatColor.RED + "Can not unload worlds which contain players!");
     }catch (CommandIsPlayerOnly e) {
       sender.sendMessage(ChatColor.RED + "You can not use this command from the console!");
     }
     return true;
  }
  
  public boolean createWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, InvalidEnvironment, WorldIsAlreadyLoaded {
    if (arguments.length < 3) throw new NotEnoughArguments("dd create", "/dd create [name] [environment] <seed>");
    final String worldName = arguments[1];
    final String environment = arguments[2].toUpperCase();
    
    if (plugin.isWorldLoaded(worldName)) throw new WorldIsAlreadyLoaded();
    
    sender.sendMessage(String.format(ChatColor.YELLOW + "Creating %s (this may take a while)", worldName));
    DimensionDoor.log(Level.INFO, String.format("%s has created a new world called %s", getSenderName(sender), worldName));
    if (arguments.length == 4) {
      plugin.createWorld(worldName, environment, arguments[3]);
    } else {
      plugin.createWorld(worldName, environment, null);
    }
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been created.", worldName));
     
    return true;
  }
  
  public boolean cloneWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, WorldIsNotManaged, InvalidAttribute {
    if (arguments.length < 3) throw new NotEnoughArguments("dd template", "/dd template [sourceWorld] [targetWorld]");
    
    try {
      final WorldRecord sourceWorldRecord = WorldRecord.findFirst(arguments[1]);
      final WorldRecord targetWorldRecord = WorldRecord.findFirst(arguments[2]);
      targetWorldRecord.setAttributes(sourceWorldRecord.getAttributes());
      DimensionDoor.log(Level.INFO, String.format("%s has copied the attributes from %s to %s", getSenderName(sender), arguments[1], arguments[2]));
      sender.sendMessage(String.format(ChatColor.GREEN + "Settings copied from %s to %s", sourceWorldRecord.getName(), targetWorldRecord.getName()));
    } catch (IndexOutOfBoundsException e) {
      throw new WorldIsNotManaged();
    }
     
    return true;
  }
  
  public boolean modifyWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, WorldIsNotManaged, InvalidAttribute {
    if (arguments.length < 4) throw new NotEnoughArguments("dd modify", "/dd modify [name] [attribute] [value]");
    final String worldName = arguments[1];
    final String attributeName = arguments[2];
    final boolean attributeValue = Boolean.valueOf(arguments[3]);
    
    try {
      final WorldRecord world = WorldRecord.findFirst(worldName);
      HashMap<String, Boolean> attributes = world.getAttributes();
    
      if (attributes.containsKey(attributeName)) {
        attributes.put(attributeName, attributeValue);
        world.setAttributes(attributes);
        world.applyAttributes();
        DimensionDoor.log(Level.INFO, String.format("%s has changed %s to %s for %s", getSenderName(sender), attributeName, Boolean.toString(attributeValue), worldName));
        sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attributeName, Boolean.toString(attributeValue), worldName));
      } else {
        throw new InvalidAttribute(attributeName);
      }
    } catch (IndexOutOfBoundsException e) {
      throw new WorldIsNotManaged();
    }
    
    return true;
  }
  
  public boolean worldInfo(CommandSender sender, String[] arguments) throws NotEnoughArguments, WorldIsNotManaged {
    if (arguments.length != 2) throw new NotEnoughArguments("dd info", "/dd info [name]");
    final String worldName = arguments[1];
    
    if (!plugin.isWorldManaged(worldName)) throw new WorldIsNotManaged();
    
    final WorldRecord world = WorldRecord.findFirst(worldName);
    HashMap<String, Boolean> attributes = world.getAttributes();
    
    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "World information for %s:", world.getName()));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- seed: %s", plugin.getWorldSeed(worldName)));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- environment: %s", world.getEnvironment().toString()));
    for (String key : attributes.keySet())
      sender.sendMessage(String.format(ChatColor.YELLOW + "- %s: %s", key, Boolean.toString(attributes.get(key))));
    return true;
  }
  
  public boolean loadWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, InvalidEnvironment, WorldIsAlreadyLoaded, WorldIsNotManaged {
    if (arguments.length != 2) throw new NotEnoughArguments("dd load", "/dd load [name]");
    
    final String worldName = arguments[1];
    if (plugin.isWorldLoaded(worldName)) throw new WorldIsAlreadyLoaded();
    if (!plugin.isWorldManaged(worldName)) throw new WorldIsNotManaged();
    
    final WorldRecord world = WorldRecord.findFirst(worldName);
    plugin.createWorld(world);
    DimensionDoor.log(Level.INFO, String.format("%s has loaded the world %s", getSenderName(sender), worldName));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been loaded.", worldName));
    return true;
  }
  
  public boolean unloadWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, WorldIsNotLoaded, WorldIsNotEmpty {
    if (arguments.length != 2) throw new NotEnoughArguments("dd unload", "/dd unload [name]");
    
    final String worldName = arguments[1];
    if (plugin.isWorldLoaded(worldName)) {
      plugin.unloadWorld(worldName);
    } else {
      throw new WorldIsNotLoaded();
    }
    
    DimensionDoor.log(Level.INFO, String.format("%s has unloaded the world %s", getSenderName(sender), worldName));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been unloaded.", worldName));
    return true;
  }
  
  public boolean setWorldSpawn(CommandSender sender, String[] arguments) throws CommandIsPlayerOnly {
    if (sender instanceof ConsoleCommandSender) throw new CommandIsPlayerOnly();
    final String playerName = getSenderName(sender);
    final Player player = getPlayerFromName(playerName);
    final String worldName = player.getWorld().getName();
    final World world = player.getWorld();
    final Integer x = (int)player.getLocation().getX();
    final Integer y = (int)player.getLocation().getY();
    final Integer z = (int)player.getLocation().getZ();
    
    world.setSpawnLocation(x, y, z);
    DimensionDoor.log(Level.INFO, String.format("%s has set a new spawn location for %s", getSenderName(sender), worldName));
    sender.sendMessage(String.format(ChatColor.GREEN + "New spawn location set for %s", worldName));
    return true;
  }
  
  public boolean teleportToWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, WorldIsNotLoaded, CommandIsPlayerOnly {
    if (sender instanceof ConsoleCommandSender) throw new CommandIsPlayerOnly();
    if (arguments.length != 2) throw new NotEnoughArguments("dd teleport", "/dd teleport [name]");
    final String worldName = arguments[1];
    
    if (!plugin.isWorldLoaded(worldName)) throw new WorldIsNotLoaded();
    final String playerName = getSenderName(sender);
    final Player player = getPlayerFromName(playerName);
    final World targetWorld = plugin.getWorld(worldName);
    
    player.teleport(targetWorld.getSpawnLocation());
    return true;
  }
  
  public boolean removeWorld(CommandSender sender, String[] arguments) throws NotEnoughArguments, WorldIsNotManaged, WorldIsNotEmpty {
    if (arguments.length != 2) throw new NotEnoughArguments("dd remove", "/dd remove [name]");
    final String worldName = arguments[1];
    
    if (!plugin.isWorldManaged(worldName)) throw new WorldIsNotManaged();
    final WorldRecord world = WorldRecord.findFirst(worldName);
    
    plugin.unloadWorld(worldName);
    world.delete();
    DimensionDoor.log(Level.INFO, String.format("%s has removed the WorldRecord for %s", getSenderName(sender), worldName));
    sender.sendMessage(String.format(ChatColor.GREEN + "%s has been removed.", worldName));
    sender.sendMessage(ChatColor.YELLOW + "You will still need to remove the world directory.");
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

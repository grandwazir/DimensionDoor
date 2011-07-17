package name.richardson.james.dimensiondoor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.dimensiondoor.DimensionDoorWorldListener;
import name.richardson.james.dimensiondoor.DimensionDoorPlugin;
import name.richardson.james.dimensiondoor.DimensionDoorWorld;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class DimensionDoorPlugin extends JavaPlugin {
	
	static Logger log = Logger.getLogger("Minecraft");
	static PermissionHandler CurrentPermissions = null;
	static final List<String> commands = Arrays.asList("create", "teleport", "unload", "remove", "modify", "info", "list", "load", "spawn");
	PluginDescriptionFile info = null;
	
	// Listeners
	private final DimensionDoorWorldListener WorldListener = new DimensionDoorWorldListener(this);
	private final DimensionDoorPlayerListener PlayerListener = new DimensionDoorPlayerListener(this);
	
	public void onEnable(){
		info = this.getDescription();
		final DimensionDoorPlugin plugin = this;
		DimensionDoorWorld.setPlugin(plugin);
		DimensionDoorWorld.setDefaultAttributes();
		log.info(String.format("[DimensionDoor] %s is enabled!", info.getFullName()));
		
		// setup environment
		setupDatabase();
		setupPermissions();
		
		// register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.WORLD_LOAD, WorldListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.WORLD_INIT, WorldListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, PlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, PlayerListener, Event.Priority.Highest, this);
		
		// register existing worlds
		for (World world : plugin.getServer().getWorlds()) {
			if (DimensionDoorWorld.isManaged(world.getName())) {
				DimensionDoorWorld.find(world.getName()).applyAttributes();
			} else {
				log.warning(String.format("[DimensionDoor] - No configuration found for %s", world.getName()));
				DimensionDoorWorld.manageWorld(world);
				DimensionDoorWorld.find(world.getName()).applyAttributes();
			}
		}
		
		// load managed worlds if they are not already loaded
		for (DimensionDoorWorld world : DimensionDoorWorld.findAll())
			if (!world.isLoaded())
				world.loadWorld();
		
		log.info(String.format("[DimensionDoor] %d worlds configured!", plugin.getServer().getWorlds().size()));
	}
	
	public void onDisable(){
		log.info(String.format("[DimensionDoor] %s is disabled!", info.getFullName()));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equalsIgnoreCase("dd")) {
			// check of the command is valid
			if (args.length == 0) return false;
			String command = args[0];
			if (!commands.contains(command)) return false;
			if (!playerHasPermission(sender, "dd." + command)) return true;
			// execute the right command
			if (command.equalsIgnoreCase("create")) return createWorld(sender, args);
			if (command.equalsIgnoreCase("teleport")) return teleportToWorld(sender, args);
			if (command.equalsIgnoreCase("unload")) return unloadWorld(sender, args); 
			if (command.equalsIgnoreCase("load")) return loadWorld(sender, args);
			if (command.equalsIgnoreCase("remove")) return removeWorld(sender, args);
			if (command.equalsIgnoreCase("modify")) return modifyWorld(sender, args);
			if (command.equalsIgnoreCase("info")) return infoWorld(sender, args);
			if (command.equalsIgnoreCase("list")) return listWorlds(sender, args);
			if (command.equalsIgnoreCase("spawn")) return setWorldSpawn(sender, args);
		}
		return false; 
	}
	
	// Commands
	
	private boolean createWorld(CommandSender sender, String[] args) {
		// check we have enough arguments
		if (args.length < 3) { sender.sendMessage(ChatColor.RED + "/dd create [world] [type] <seed>"); return true; }
		// check the world is not already loaded
		if (DimensionDoorWorld.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED + "A world is already loaded with that name!"); return true; }
		// check if we are already managing this world, if so load it instead of creating it
		if (DimensionDoorWorld.isManaged(args[1])) {
			sender.sendMessage(ChatColor.GREEN + "Loading " + args[1]);
			DimensionDoorWorld.find(args[1]).loadWorld();
			sender.sendMessage(ChatColor.GREEN + "Loading complete!");
			return true; 
		}
		
		Long worldSeed = null;
		if (args.length == 4) {
		    try {
		        worldSeed = Long.parseLong(args[3]);
		    } catch (NumberFormatException e) {
		        worldSeed = (long)args[3].hashCode();
		    }
		}
		
		// check the type is valid
		if (!DimensionDoorWorld.isEnvironmentValid(args[2])) { sender.sendMessage(ChatColor.RED + args[2].toUpperCase() + " is not a valid environment type."); return true; }
		// actually create the world
		sender.sendMessage(ChatColor.GREEN + "Creating " + args[1] + " (this may take a while)");
		DimensionDoorWorld.createWorld(args[1], args[2], worldSeed, DimensionDoorWorld.defaultAttributes);
		sender.sendMessage(ChatColor.GREEN + "Creation complete!");
		log.info(String.format("[DimensionDoor] %s created a new world called %s", getName(sender), args[1]));
		return true;
	}
	
	private boolean teleportToWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "/dd teleport [world]"); return true; }
		// check to see if destination world is loaded
		if (!DimensionDoorWorld.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is not loaded!"); return true; }
		// check player is not console
		if (getName(sender).equals("console")) { sender.sendMessage(ChatColor.RED + "Console can not use this command"); return true; }
		// teleport the player
		World destinationWorld = DimensionDoorWorld.getWorld(args[1]);
		Player player = getPlayerFromName(getName(sender));
		player.teleport(destinationWorld.getSpawnLocation());
		return true;
	}
	
	private boolean loadWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "/dd load [world]"); return true; }
		// check to see if destination world is loaded
		if (DimensionDoorWorld.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is already loaded!"); return true; }
		if (!DimensionDoorWorld.isManaged(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is not managed by DimensionDoor!"); return true; }
		DimensionDoorWorld world = DimensionDoorWorld.find(args[1]);
		world.loadWorld();
		log.info(String.format("[DimensionDoor] %s loaded %s", getName(sender), args[1]));
		sender.sendMessage(ChatColor.GREEN + args[1] + " loaded");
		return true;
	}
	
	private boolean unloadWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "/dd unload [world]"); return true; }
		// check to see if destination world is loaded
		if (!DimensionDoorWorld.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is not loaded!"); return true; }
		DimensionDoorWorld world = DimensionDoorWorld.find(args[1]);
		world.unloadWorld();
		log.info(String.format("[DimensionDoor] %s unloaded %s", getName(sender), args[1]));
		sender.sendMessage(ChatColor.GREEN + "World unloaded");
		return true;
	}
	
	private boolean removeWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "/dd remove [world]"); return true; }
		// check to see if destination world is loaded
		if (!DimensionDoorWorld.isManaged(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is not managed by DimensionDoor!"); return true; }
		DimensionDoorWorld world = DimensionDoorWorld.find(args[1]);
		if (DimensionDoorWorld.isLoaded(args[1])) world.unloadWorld();
		world.removeWorld();
		log.info(String.format("[DimensionDoor] %s removed %s", getName(sender), args[1]));
		sender.sendMessage(ChatColor.GREEN + "World unloaded");
		return true;
	}
	
	private boolean modifyWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 4) { sender.sendMessage(ChatColor.RED + "/dd modify [world] [attribute] [value]"); return true; }
		// check to see if destination world is loaded
		if (!DimensionDoorWorld.isManaged(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is not managed by DimensionDoor!"); return true; }
		DimensionDoorWorld world = DimensionDoorWorld.find(args[1]);
		boolean newValue = Boolean.parseBoolean(args[3]);
		HashMap<String, Boolean> attributes = world.getAttributes();
		
		// check to see if the attribute is valid
		if (!attributes.containsKey(args[2])) {
			sender.sendMessage(ChatColor.RED + "Unknown attribute: " + args[2]);
			sender.sendMessage(ChatColor.YELLOW + "Valid attributes: pvp, spawnAnimals, spawnMonsters, isolatedChat");
			return true;
		}
		
		// save world and apply attributes
		attributes.put(args[2], newValue);
		world.setAttributes(attributes);
		world.applyAttributes();
		log.info(String.format("[DimensionDoor] %s changed %s to %s on %s", getName(sender), args[2], Boolean.toString(newValue), world.getName()));
		sender.sendMessage(ChatColor.GREEN + args[2] + " on " + args[1] + " changed to " + Boolean.toString(newValue));
		return true;
	}
	
	private boolean infoWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "/dd info [world]"); return true; }
		if (!DimensionDoorWorld.isManaged(args[1])) { sender.sendMessage(ChatColor.RED + args[1] + " is not managed by DimensionDoor!"); return true; }
		DimensionDoorWorld world = DimensionDoorWorld.find(args[1]);
		// check to see if destination world is loaded
		if (!DimensionDoorWorld.isLoaded(args[1]))
			sender.sendMessage(ChatColor.RED + args[1] + " is not loaded!");
		else
			sender.sendMessage(ChatColor.GREEN + args[1] + " is loaded!");
		sender.sendMessage(ChatColor.YELLOW + " - seed: " + Long.toString(getServer().getWorld(world.getName()).getSeed()));
		sender.sendMessage(ChatColor.YELLOW + " - environment: " + world.getEnvironment().name());
		sender.sendMessage(ChatColor.YELLOW + " - pvp: " + Boolean.toString(world.isPvp()));
		sender.sendMessage(ChatColor.YELLOW + " - spawnAnimals: " + Boolean.toString(world.isSpawnAnimals()));
		sender.sendMessage(ChatColor.YELLOW + " - spawnMonsters: " + Boolean.toString(world.isSpawnMonsters()));
		sender.sendMessage(ChatColor.YELLOW + " - isolatedChat: " + Boolean.toString(world.isIsolatedChat()));
		return true;
	}
	
	private boolean listWorlds(CommandSender sender, String[] args) {
		// get all the worlds
		List<DimensionDoorWorld> worlds = DimensionDoorWorld.findAll();
		StringBuilder message = new StringBuilder();
		// Build the message
		for (DimensionDoorWorld world : worlds) {
			if (world.isLoaded()) { 
				message.append(ChatColor.GREEN + world.getName() + ", ");
			} else {
				message.append(ChatColor.RED + world.getName() + ", ");
			}
		}
		// get rid of the trailing comma
		message.deleteCharAt(message.length() - 2);
		sender.sendMessage(message.toString());
		return true;
	}
	
	private boolean setWorldSpawn(CommandSender sender, String[] args) {
		// check we are not doing this from console
		if (getName(sender).equalsIgnoreCase("console")) { sender.sendMessage(ChatColor.RED + "You can not use this command from the console!"); return true; }
		// get parameters
		Player player = getPlayerFromName(getName(sender));
		World world = player.getWorld();
		// set the new location
		final Integer x = (int)player.getLocation().getX();
		final Integer y = (int)player.getLocation().getY();
		final Integer z = (int)player.getLocation().getZ();
		world.setSpawnLocation(x, y, z);
		sender.sendMessage(ChatColor.GREEN + "New spawn location set for " + world.getName());
		log.info(String.format("[DimensionDoor] %s set a new spawn location on %s", player.getName(), world.getName()));
		return true;
	}
	
	// Utilities
		
	private boolean playerHasPermission(CommandSender sender, String node) {
		String playerName = this.getName(sender);
		if (CurrentPermissions != null) {
			// skip the check if the user is the console
			if (playerName.equals("console")) return true;
			if (CurrentPermissions.has(this.getPlayerFromName(playerName), node))
				return true;
		} else if (sender.isOp()) {
			return true;
		}
		sender.sendMessage(ChatColor.RED + " You do not have permission to do that.");	
		return false;
	}
	
	public String getName(CommandSender sender) {
		 if (sender instanceof Player) {
			 Player player = (Player)sender;
		     String senderName = player.getName();
		     return senderName;
	     } else {
	        return "console";
	     }
	 }
	
	private Player getPlayerFromName(String playerName) {
		List<Player> possiblePlayers = getServer().matchPlayer(playerName);
		return possiblePlayers.get(0);
	}
	
	private void setupPermissions() {
		// if we have already hooked permissions don't do it again
		if (CurrentPermissions != null)
			return;
		 // attempt to hook the plugin
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permissionsPlugin != null) {
			CurrentPermissions = ((Permissions) permissionsPlugin).getHandler();
			log.info(String.format("[DimensionDoor] - Permissions found (%s)", ((Permissions)permissionsPlugin).getDescription().getFullName()));
		} else {
			log.info("[DimensionDoor] - Permission system not detected, defaulting to OP");
		}
	}

	private void setupDatabase() {
		try {
            getDatabase().find(DimensionDoorWorld.class).findRowCount();
            getDatabase().find(DimensionDoorWorld.class).findList();
        } catch (PersistenceException ex) {
        	if (ex.getMessage().contains("isolated_chat")) {
        		log.warning("[DimensionDoor] - Database schema out of date!");
        	    log.info("[DimensionDoor] -- Updating to version 1.3.0");
        		getDatabase().createSqlUpdate("ALTER TABLE dd_worlds ADD isolated_chat tinyint(1) not null DEFAULT 0").execute();
        	} else {
        		log.warning("[DimensionDoor] - No database found, creating table.");
        		installDDL();
        	}
        }
	}
	
	@Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(DimensionDoorWorld.class);
        return list;
    }
	
	
}

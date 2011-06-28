package name.richardson.james.dimensiondoor;

import java.util.ArrayList;
import java.util.Arrays;
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
	static final List<String> commands = Arrays.asList("create", "teleport");
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
		
		// register existing worlds
		for (World world : plugin.getServer().getWorlds()) {
			if (DimensionDoorWorld.isManaged(world.getName())) {
				DimensionDoorWorld.find(world.getName()).applyAttributes();
			} else {
				log.warning(String.format("[DimensionDoor] - No configuration found for %s", world.getName()));
				DimensionDoorWorld.manageWorld(world.getName());
				DimensionDoorWorld.find(world.getName()).applyAttributes();
			}
		}
		
		// register event
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.WORLD_LOAD, WorldListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.WORLD_UNLOAD, WorldListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, PlayerListener, Event.Priority.Normal, this);
		
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
			if (!playerHasPermission(sender, "dd." + command)) return false;
			// execute the right command
			if (command.equalsIgnoreCase("create")) return createWorld(sender, args);
			if (command.equalsIgnoreCase("teleport")) return teleportToWorld(sender, args);       
		}
		return false; 
	}
	
	// Commands
	
	private boolean createWorld(CommandSender sender, String[] args) {
		// check we have enough arguments
		if (args.length != 3) { sender.sendMessage(ChatColor.RED + "/dd create [world] [type]"); return true; }
		// check the world is not already loaded
		if (DimensionDoorWorld.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED + "A world is already loaded with that name!"); return true; }
		// check if we are already managing this world, if so load it instead of creating it
		if (DimensionDoorWorld.isManaged(args[1])) {
			sender.sendMessage(ChatColor.GREEN + "Loading " + args[1]);
			DimensionDoorWorld.find(args[1]).loadWorld();
			sender.sendMessage(ChatColor.GREEN + "Loading complete!");
			return true; 
		}
		// check the type is valid
		if (!DimensionDoorWorld.isEnvironmentValid(args[2])) { sender.sendMessage(ChatColor.RED + args[2].toUpperCase() + " is not a valid environment type."); return true; }
		// actually create the world
		sender.sendMessage(ChatColor.GREEN + "Creating world (this may take a while)");
		DimensionDoorWorld.createWorld(args[1], args[2], DimensionDoorWorld.defaultAttributes);
		sender.sendMessage(ChatColor.GREEN + "Creation complete!");
		return true;
	}
	
	private boolean teleportToWorld(CommandSender sender, String[] args) {
		// check if we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "/dd teleport [world]"); return true; }
		// check to see if destination world is loaded
		if (!DimensionDoorWorld.isLoaded(args[1])) { sender.sendMessage(ChatColor.RED + "That world is not loaded!"); return true; }
		// check player is not console
		if (getName(sender).equals("console")) { sender.sendMessage(ChatColor.RED + "Console can not use this command"); return true; }
		// teleport the player
		World destinationWorld = DimensionDoorWorld.getWorld(args[1]);
		Player player = getPlayerFromName(getName(sender));
		player.teleport(destinationWorld.getSpawnLocation());
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
        } catch (PersistenceException ex) {
        	log.warning("[DimensionDoor] - No database found, creating table.");
        	installDDL();
        }
	}
	
	@Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(DimensionDoorWorld.class);
        return list;
    }
	
	
}

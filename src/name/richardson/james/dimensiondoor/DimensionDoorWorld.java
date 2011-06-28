package name.richardson.james.dimensiondoor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.World;
import org.bukkit.World.Environment;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.dimensiondoor.DimensionDoorPlugin;

@Entity()
@Table(name = "dd_worlds")

public class DimensionDoorWorld {
	
	public static HashMap<String, Boolean> defaultAttributes = new HashMap<String, Boolean>(3);
	private static DimensionDoorPlugin plugin;
	private final static Logger log = Logger.getLogger("Minecraft");
	
	@Id
    private String name;
	
	@NotNull
	private World.Environment environment;
	
	@NotNull
    private boolean pvp;
    
    @NotNull
    private boolean spawnAnimals;

    @NotNull
    private boolean spawnMonsters;

	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	public String getName() {
		return name;
	}
	
	public void setEnvironment(World.Environment environment) {
		this.environment = environment;
	}
	
	public Environment getEnvironment() {
		return environment;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

	public boolean isPvp() {
		return pvp;
	}

	public void setSpawnAnimals(boolean spawnAnimals) {
		this.spawnAnimals = spawnAnimals;
	}

	public boolean isSpawnAnimals() {
		return spawnAnimals;
	}

	public void setSpawnMonsters(boolean spawnMonsters) {
		this.spawnMonsters = spawnMonsters;
	}

	public boolean isSpawnMonsters() {
		return spawnMonsters;
	}
	
	public void applyAttributes() {
		World world = plugin.getServer().getWorld(name);
		world.setPVP(pvp);
		world.setSpawnFlags(spawnAnimals, spawnMonsters);
		log.info(String.format("[DimensionDoor] - Applying configuration for %s", world.getName()));
	}
	
	static public void createWorld(String worldName, String environment, HashMap<String, Boolean> attributes) {
		DimensionDoorWorld createdWorld = new DimensionDoorWorld();
		World.Environment environmentType = Enum.valueOf(World.Environment.class, environment.toUpperCase());
		HashMap<String, Boolean> combinedAttributes = mergeDefaultAttributes(attributes);
		createdWorld.setName(worldName);
		createdWorld.setEnvironment(environmentType);
		createdWorld.setAttributes(combinedAttributes);
		// Save and create the world
		plugin.getDatabase().save(createdWorld);
		log.info(String.format("[DimensionDoor] Creating new world '%s' (%s)", worldName, environmentType.name()));
		plugin.getServer().createWorld(worldName, environmentType);
	}
	
	
	static public DimensionDoorWorld find(String worldName) {
		List<DimensionDoorWorld> worlds = plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", worldName).findList();
		return worlds.get(0);
	}
	
	static public DimensionDoorWorld find(World world) {
		List<DimensionDoorWorld> worlds = plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", world.getName()).findList();
		return worlds.get(0);
	}
	
	static public boolean isEnvironmentValid(String environment) {
		for (Environment type : World.Environment.values())
			if (type.name().equalsIgnoreCase(environment)) return true;
		return false;
	}
	
	static public boolean isManaged(String worldName) {
		if (plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", worldName).findRowCount() == 1)
			return true;
		return false;
	}
	
	static public boolean isManaged(World world) {
		if (plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", world.getName()).findRowCount() == 1)
			return true;
		return false;
	}
	
	static public void manageWorld(String worldName) {
		DimensionDoorWorld managedWorld = new DimensionDoorWorld();
		World world = getWorld(worldName);
		HashMap<String, Boolean> attributes = getAttributes(world);
		managedWorld.setEnvironment(world.getEnvironment());
		managedWorld.setName(world.getName());
		managedWorld.setAttributes(attributes);
		plugin.getDatabase().save(managedWorld);
		log.info(String.format("[DimensionDoor] -- Creating default configuation: %s", managedWorld.getName()));
	}
	
	static public void manageWorld(World world) {
		DimensionDoorWorld managedWorld = new DimensionDoorWorld();
		HashMap<String, Boolean> attributes = getAttributes(world);
		managedWorld.setEnvironment(world.getEnvironment());
		managedWorld.setName(world.getName());
		managedWorld.setAttributes(attributes);
		plugin.getDatabase().save(managedWorld);
		log.info(String.format("[DimensionDoor] -- Creating default configuation: %s", managedWorld.getName()));
	}
	
	static private HashMap<String, Boolean> getAttributes(World world) {
		HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
		attributes.put("pvp", world.getPVP());
		attributes.put("spawnMonsters", world.getAllowMonsters());
		attributes.put("spawnAnimals", world.getAllowAnimals());
		return attributes;
	}
	
	public void loadWorld() {
		if (!isLoaded(this.getName()))
			plugin.getServer().createWorld(this.getName(), this.getEnvironment());
		else
			log.warning(String.format("Attempted to load %s but it was loaded", this.getName()));
	}
	
	static private HashMap<String, Boolean> mergeDefaultAttributes(HashMap<String, Boolean> attributes) {
		Set<String> keys = attributes.keySet();
		for (String key : keys) {
			if (!attributes.containsKey(key))
				attributes.put(key, defaultAttributes.get(key));
		}
		return attributes;	
	}
	
	private void setAttributes(HashMap<String, Boolean> attributes) {
		this.setPvp(attributes.get("pvp"));
		this.setSpawnMonsters(attributes.get("spawnMonsters"));
		this.setSpawnAnimals(attributes.get("spawnAnimals"));
	}
	
	public static void setDefaultAttributes() {
		defaultAttributes.put("pvp", false);
		defaultAttributes.put("spawnAnimals", true);
		defaultAttributes.put("spawnMonsters", true);
	}
	
	static public void setPlugin(DimensionDoorPlugin plugin) {
		DimensionDoorWorld.plugin = plugin;
	}
	
	public void removeWorld() {
		if (isLoaded(this.getName()))
			unloadWorld();
		plugin.getDatabase().delete(this);
	}
	
	public void unloadWorld() {
		if (isLoaded(this.getName()))
			plugin.getServer().unloadWorld(this.getName(), true);
		else
			log.warning(String.format("Attempted to unload %s but it was not loaded", this.getName()));
	}
	
	static public World getWorld(String worldName) {
		return plugin.getServer().getWorld(worldName.toLowerCase());
	}
	
	static public World getMainWorld() {
		return plugin.getServer().getWorlds().get(0);
	}
	
	
	static boolean isLoaded (String worldName) {
		if (plugin.getServer().getWorld(worldName.toLowerCase()) != null)
			return true;
		return false;
	}
}

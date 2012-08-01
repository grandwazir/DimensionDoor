package name.richardson.james.bukkit.dimensiondoor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.localisation.Localised;

public class World extends Localised implements ConfigurationSerializable, Serializable, Listener {

  static {

  }
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8551768503578434301L;

  /**s
   * Deserialize a world into a new object.
   *
   * @param map the map containing the data
   * @return a new world object
   */
  public static World deserialize(Map<String, Object> map) {
    DimensionDoor plugin = (DimensionDoor) Bukkit.getServer().getPluginManager().getPlugin("DimensionDoor");
    final World world = new World(plugin, (String) map.get("world-name"));
    world.allowAnimals = ((Boolean) map.get("allow-animals"));
    world.allowMonsters = ((Boolean) map.get("allow-monsters"));
    world.difficulty = (Difficulty.valueOf((String) map.get("difficulty")));
    world.enabled = ((Boolean) map.get("enabled"));
    world.environment = (Environment.valueOf((String) map.get("environment")));
    world.gameMode = (GameMode.valueOf((String) map.get("game-mode")));
    world.generateStructures = ((Boolean) map.get("generate-structures"));
    world.generatorID = ((String) map.get("generator-id"));
    world.generatorPluginName = ((String) map.get("generator-plugin-name"));
    world.isolatedChat = ((Boolean) map.get("isolated-chat"));
    world.pvp = ((Boolean) map.get("pvp"));
    world.seed = ((Long) map.get("seed"));
    world.worldType = (WorldType.valueOf((String) map.get("world-type")));
    return world;
  }

  /** The logger. */
  private final Logger logger = new Logger(this.getClass());

  /** The loaded world. */
  private org.bukkit.World world;

  /** The difficulty of the world. */
  private Difficulty difficulty = Difficulty.NORMAL;

  /** If we should keep the spawn of this world in memory. */
  private boolean keepSpawnInMemory = true;

  /** If we should allow PVP in this world. */
  private boolean pvp = false;

  /** If we should allow monsters to spawn in this world. */
  private boolean allowMonsters = true;

  /** If we should allow animals to spawn in this world. */
  private boolean allowAnimals = true;

  /** The type of the world. */
  private WorldType worldType = WorldType.NORMAL;

  /** The game mode of this world. */
  private GameMode gameMode = GameMode.SURVIVAL;

  /** The plugin. */
  private DimensionDoor plugin;

  /** The permission required to enter this world. */
  private Permission permission;

  /** The name of the world. */
  private final String worldName;

  /** The type of environment of this world. */
  private Environment environment;

  /** If we are generating structures in this world. */
  private boolean generateStructures = true;

  /** The seed of this world. */
  private long seed = 0;

  /** The name of the generator plugin. */
  private String generatorPluginName;

  /** The name of the generator id. */
  private String generatorID;

  /** Get the unique ID for this world. */
  private UUID worldUUID;

  /** If chat on this world is isolated. */
  private boolean isolatedChat = false;

  /** Is the world enabled (automatically load on startup). */
  private boolean enabled = true;

  /** Do we listen for events or not?. */
  private boolean listening = true;

  /**
   * Instantiates a new world and base the attributes of the world provided.
   *
   * @param plugin the plugin
   * @param world the world
   */
  public World(DimensionDoor plugin, org.bukkit.World world) {
    super(plugin);
    this.logger.debug("Initalising world object.");
    this.logger.debug(String.format("Using attributes from %s as a base.", world.getName()));
    this.plugin = plugin;
    this.world = world;
    this.worldName = this.world.getName();
    this.allowAnimals = this.world.getAllowAnimals();
    this.allowMonsters = this.world.getAllowMonsters();
    this.environment = this.world.getEnvironment();
    this.gameMode = this.plugin.getServer().getDefaultGameMode();
    this.pvp = this.world.getPVP();
    this.seed = this.world.getSeed();
    this.worldType = this.world.getWorldType();
    this.worldUUID = this.world.getUID();
    this.setPermission();
    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Instantiates a new world with the default attributes.
   *
   * @param plugin the plugin
   * @param worldName the world name
   */
  public World(DimensionDoor plugin, String worldName) {
    super(plugin);
    this.plugin = plugin;
    this.worldName = worldName;
    this.checkIfWorldIsLoaded();
    this.setPermission();
    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Apply world attributes.
   */
  public void applyAttributes() {
    this.logger.debug(String.format("Applying saved attributes for %s.", this.worldName));
    world.setDifficulty(difficulty);
    world.setKeepSpawnInMemory(keepSpawnInMemory);
    world.setPVP(pvp);
    world.setSpawnFlags(allowMonsters, allowAnimals);
    this.applyGameMode();
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public Environment getEnvironment() {
    return environment;
  }

  /**
   * Gets the generator id.
   *
   * @return the generator id
   */
  public String getGeneratorID() {
    return generatorID;
  }

  public String getGeneratorPluginName() {
    return generatorPluginName;
  }

  /**
   * Gets the name of the world.
   *
   * @return the name
   */
  public String getName() {
    return this.worldName;
  }

  public long getSeed() {
    return seed;
  }
  
  /**
   * Gets the UUID.
   *
   * @return the UUID
   */
  public UUID getUUID() {
    return this.worldUUID;
  }

  public WorldType getWorldType() {
    return worldType;
  }

  /**
   * Checks if is chat isolated.
   *
   * @return true, if is chat isolated
   */
  public boolean isChatIsolated() {
    return isolatedChat;
  }

  /**
   * Checks if is enabled.
   *
   * @return true, if is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  public boolean isGeneratingStructures() {
    return generateStructures;
  }

  public boolean isLoaded() {
    return (this.world != null);
  }

  /**
   * Checks if is main world.
   *
   * @return true, if is main world
   */
  public boolean isMainWorld() {
    return (plugin.getServer().getWorlds().get(0) == world);
  }

  public boolean isPVP() {
    return pvp;
  }

  public boolean isSpawningAnimals() {
    return allowAnimals;
  }

  public boolean isSpawningMonsters() {
    return allowMonsters;
  }

  public boolean isSpawnKeptInMemory() {
    return keepSpawnInMemory;
  }

  /**
   * Attempt to load the world.
   */
  public void load() {
    this.logger.debug(String.format("Loading %s into memory.", this.worldName));
    if (world == null) {
      this.getWorldCreator().createWorld();
    } 
  }

  /**
   * When a player has sent a chat message, check if this world is isolated and if it is prevent the message leaving the world.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerChat(PlayerChatEvent event) {
    if (!listening) return;
    if (this.isolatedChat == true && event.getPlayer().getWorld() == this.world) {
      this.logger.debug(String.format("Isolating chat message by %s within %s.", event.getPlayer().getName(), this.worldName));
      event.getRecipients().clear();
      event.getRecipients().addAll(this.world.getPlayers());
    }
  }

  /**
   * When a player teleports check they are allowed to enter the world.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
    if (!listening) return;
    if (event.getTo().getWorld() == this.world) {
      if (event.getPlayer().hasPermission(permission) || this.isMainWorld()) {
        this.logger.debug(String.format("Allowing %s to enter %s", event.getPlayer().getName(), this.worldName));
        this.applyGameMode(event.getPlayer());
      } else {
        this.logger.debug(String.format("Preventing %s from entering %s", event.getPlayer().getName(), this.worldName));
        event.getPlayer().sendMessage("not-allowed-to-enter-world");
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerTeleportEvent(PlayerRespawnEvent event) {
    if (this.environment == Environment.THE_END) return;
    if (event.getPlayer().getWorld() == this.world) {
      if (event.getPlayer().getWorld() != event.getRespawnLocation().getWorld()) {
        event.setRespawnLocation(this.world.getSpawnLocation());
      }
    }
  }
  
  /**
   * When a world is loaded, set a reference to the world if it isn't present and apply attributes.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onWorldLoaded(WorldLoadEvent event) {
    if (!listening) return;
    if (!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
    this.logger.debug(String.format("%s has loaded.", this.worldName));
    if (world == null) this.world = event.getWorld();
    this.applyAttributes();
  }

  /**
   * When a world is unloaded, remove the reference to the world.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onWorldUnloaded(WorldUnloadEvent event) {
    if (!listening) return;
    if (!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
    this.logger.debug(String.format("%s has unloaded, setting world to null.", this.worldName));
    this.world = null;
  }

  /**
   * Serialize the world and attributes.
   *
   * @return the map
   */
  public Map<String, Object> serialize() {
    this.logger.debug(String.format("Serializing %s.", worldName));
    final Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("world-name", worldName);
    map.put("enabled", enabled);
    map.put("allow-animals", allowAnimals);
    map.put("allow-monsters", allowMonsters);
    map.put("difficulty", difficulty.toString());
    map.put("environment", environment.toString());
    map.put("game-mode", gameMode.toString());
    map.put("generate-structures", generateStructures);
    map.put("generator-id", generatorID);
    map.put("generator-plugin-name", generatorPluginName);
    map.put("isolated-chat", isolatedChat);
    map.put("pvp", pvp);
    map.put("seed", seed);
    map.put("world-type", worldType.toString());
    return map;
  }

  /**
   * Sets if animals should be allowed to spawn.
   *
   * @param allowAnimals the new value
   */
  public void setAllowAnimals(boolean allowAnimals) {
    this.logger.debug(String.format("Setting allowAnimals to %b for %s.", allowAnimals, this.worldName));
    this.allowAnimals = allowAnimals;
    if (world != null) world.setSpawnFlags(allowMonsters, allowAnimals);
  }

  /**
   * Sets if monsters should be allowed to spawn.
   *
   * @param allowMonsters the new value
   */
  public void setAllowMonsters(boolean allowMonsters) {
    this.logger.debug(String.format("Setting allowMonsters to %b for %s.", allowMonsters, this.worldName));
    this.allowMonsters = allowMonsters;
    if (world != null) world.setSpawnFlags(allowMonsters, allowAnimals);
  }

  /**
   * Sets the difficulty of the world.
   *
   * @param difficulty the new value.
   */
  public void setDifficulty(Difficulty difficulty) {
    if (difficulty == null) throw new IllegalArgumentException("Difficulty can not be null!");
    this.logger.debug(String.format("Setting difficulty to %s for %s.", difficulty.name(), this.worldName));
    this.difficulty = difficulty;
    if (world != null) world.setDifficulty(difficulty);
  }

  /**
   * Sets if this world should be automatically loaded when the plugin starts.
   *
   * @param enabled the new value
   */
  public void setEnabled(boolean enabled) {
    this.logger.debug(String.format("Setting enabled to %s for %s.", enabled, this.worldName));
    this.enabled = enabled;
  }

  /**
   * Sets the environment of the world.
   *
   * @param environment the new value
   */
  public void setEnvironment(Environment environment) {
    if (environment == null) throw new IllegalArgumentException("Environment can not be null!");
    this.logger.debug(String.format("Setting environment to %s for %s.", environment, this.worldName));
    if (world == null) this.environment = environment;
  }

  /**
   * Sets the game mode of the world.
   *
   * @param gameMode the new value
   */
  public void setGameMode(GameMode gameMode) {
    if (environment == null) throw new IllegalArgumentException("GameMode can not be null!");
    this.logger.debug(String.format("Setting gameMode to %s for %s.", gameMode.name(), this.worldName));
    this.gameMode = gameMode;
    this.applyGameMode();
  }

  /**
   * Sets if this world should generate structures.
   *
   * @param generateStructures the new value
   */
  public void setGenerateStructures(boolean generateStructures) {
    this.logger.debug(String.format("Setting generateStructures to %s for %s.", generateStructures, this.worldName));
    this.generateStructures = generateStructures;
  }

  public GameMode getGameMode() {
    return gameMode;
  }
  
  /**
   * Sets the generator id.
   *
   * @param generatorID the new value
   */
  public void setGeneratorID(String generatorID) {
    this.logger.debug(String.format("Setting generatorID to %s for %s.", generatorID, this.worldName));
    this.generatorID = generatorID;
  }

  /**
   * Sets the generator plugin name.
   *
   * @param generatorPlugin the new value
   */
  public void setGeneratorPluginName(Plugin generatorPlugin) {
    if (generatorPlugin == null) throw new IllegalArgumentException("GeneratorPlugin can not be null!");
    this.logger.debug(String.format("Setting generatorPluginName to %s for %s.", generatorPlugin.getName(), this.worldName));
    this.generatorPluginName = generatorPlugin.getName();
  }

  /**
   * Sets the generator plugin name.
   *
   * @param generatorPluginName the new value
   */
  public void setGeneratorPluginName(String generatorPluginName) {
    if (generatorPluginName == null) throw new IllegalArgumentException("GeneratorPluginName can not be null!");
    this.logger.debug(String.format("Setting generatorPluginName to %s for %s.", generatorPluginName, this.worldName));
    this.generatorPluginName = generatorPluginName;
  }
  

  /**
   * Sets if chat in this world should be isolated.
   *
   * @param isolatedChat the new value
   */
  public void setIsolatedChat(boolean isolatedChat) {
    this.logger.debug(String.format("Setting isolatedChat to %b for %s.", isolatedChat, this.worldName));
    this.isolatedChat = isolatedChat;
  }
  

  /**
   * Sets if we should keep the spawn of the world in memory.
   *
   * @param keepSpawnInMemory the new value
   */
  public void setKeepSpawnInMemory(boolean keepSpawnInMemory) {
    this.logger.debug(String.format("Setting keepSpawnInMemory to %b for %s.", keepSpawnInMemory, this.worldName));
    this.keepSpawnInMemory = keepSpawnInMemory;
    if (world != null) world.setKeepSpawnInMemory(keepSpawnInMemory);
  }

  /**
   * Sets if PvP should be allowed in this world.
   *
   * @param pvp the new value
   */
  public void setPVP(boolean pvp) {
    this.logger.debug(String.format("Setting pvp to %b for %s.", pvp, this.worldName));
    this.pvp = pvp;
    if (world != null) world.setPVP(pvp);
  }

  /**
   * Sets the seed of the world.
   *
   * @param seed the new value
   */
  public void setSeed(long seed) {
    // if (seed != 0) throw new IllegalStateException("You may not change the seed of a world.");
    this.logger.debug(String.format("Setting seed to %d for %s.", seed, this.worldName));
    this.seed = seed;
  }
  
  /**
   * Sets a reference to a loaded world.
   *
   * @param world the new value
   */
  public void setWorld(org.bukkit.World world) {
    if (world != null) throw new IllegalStateException("You may not change an existing world reference.");
    this.logger.debug(String.format("Setting world reference for %s.", this.worldName));
    this.world = world;
  }
  
  /**
   * Sets the type of world.
   *
   * @param worldType the new value
   */
  public void setWorldType(WorldType worldType) {
    if (world != null) throw new IllegalStateException("You may not change the type of a world which is loaded.");
    this.logger.debug(String.format("Setting worldType to %s for %s.", worldType.name(), this.worldName));
    this.worldType = worldType;
  }
  
  public String toString() {
    return this.serialize().toString();
  }

  /**
   * Unload a world and teleport players out if they are preventing the unload.
   */
  public void unload() {
    if (world == null) throw new IllegalStateException("You may not unload a world which is not loaded.");
    if (isMainWorld()) throw new IllegalStateException("You may not unload the main world.");
    this.logger.debug(String.format("Unloading %s.", this.worldName));
    this.removePlayers();
    this.plugin.getServer().unloadWorld(worldName, true);
  }

  /**
   * Unregister events.
   */
  public void unregisterEvents() {
    this.logger.debug(String.format("Unregistering events for %s.", this.worldName));
    this.listening = false;
  }

  /**
   * Apply the world's game mode to all players in the world.
   *
   * @return the int
   */
  private int applyGameMode() {
    int i = 0;
    for (Player player : world.getPlayers()) {
      player.setGameMode(gameMode);
      i++;
    }
    this.logger.debug(String.format("Applied %s GameMode to %d players in %s.", this.gameMode.name(), i, this.worldName));
    return i;
  }

  /**
   * Apply the world's game mode to a specific player.
   *
   * @param player the player
   */
  private void applyGameMode(Player player) {
    if (difficulty == null) throw new IllegalArgumentException("Player can not be null!");
    player.setGameMode(gameMode);
  }

  /**
   * Check if world is loaded.
   */
  private void checkIfWorldIsLoaded() {
    for (org.bukkit.World world : this.plugin.getServer().getWorlds()) {
      if (world.getName().equalsIgnoreCase(worldName)) this.world = world;
    }
  }

  /**
   * Gets the custom chunk generator.
   *
   * @return the custom chunk generator
   */
  private ChunkGenerator getCustomChunkGenerator() {
    if (generatorPluginName != null) {
      Plugin generatorPlugin = this.plugin.getServer().getPluginManager().getPlugin(generatorPluginName);
      if (generatorPlugin != null) {
        return generatorPlugin.getDefaultWorldGenerator(this.worldName, this.generatorID);
      } else {
        throw new IllegalStateException("Unable to load world generation plugin!");
      }
    }
    return null;
  }
  
  /**
   * Gets the main world spawn.
   *
   * @return the main world spawn
   */
  private Location getMainWorldSpawn() {
    return plugin.getServer().getWorlds().get(0).getSpawnLocation();
  }

  private Permission getRootPermission() {
    final String permission = plugin.getName().toLowerCase() + "." + this.getMessage("permission-name") + ".*";
    return Bukkit.getServer().getPluginManager().getPermission(permission);
  }

  /**
   * Gets the world creator.
   *
   * @return the world creator
   */
  private WorldCreator getWorldCreator() {
    WorldCreator worldCreator = new WorldCreator(worldName);
    worldCreator.environment(environment);
    worldCreator.generateStructures(generateStructures);
    worldCreator.seed(seed);
    worldCreator.type(worldType);
    ChunkGenerator chunkGenerator = this.getCustomChunkGenerator();
    if (chunkGenerator != null) worldCreator.generator(chunkGenerator);
    return worldCreator;
  }

  /**
   * Removes players from this world by teleporting them to the main world.
   *
   * @return the int
   */
  private int removePlayers() {
    int i = 0;
    for (Player player : world.getPlayers()) {
      player.teleport(this.getMainWorldSpawn(), TeleportCause.PLUGIN);
      i++;
    }
    this.logger.debug(String.format("Removed %d players from %s.", i, this.worldName));
    return i;
  }

  private Permission setPermission() {
   if (permission != null) return permission;
   final String prefix = plugin.getName().toLowerCase() + "." + getMessage("permission-name") + ".";
   // create the base permission
   final Permission base = new Permission(prefix + this.getName().toLowerCase().replaceAll(" ", "_"), this.getMessage("permission-description"), PermissionDefault.OP);
   base.addParent(this.getRootPermission(), true);
   plugin.addPermission(base);
   this.permission = base;
   return base;
  }
  
  

}

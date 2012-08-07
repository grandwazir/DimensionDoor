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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

public class World implements ConfigurationSerializable, Serializable, Listener {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8551768503578434301L;

  /**
   * Deserialize a world into a new object.
   * 
   * @param map the map containing the data
   * @return a new world object
   */
  public static World deserialize(final Map<String, Object> map) {
    final DimensionDoor plugin = (DimensionDoor) Bukkit.getServer().getPluginManager().getPlugin("DimensionDoor");
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
    world.seed = (Long) map.get("seed");
    world.worldType = (WorldType.valueOf((String) map.get("world-type")));
    return world;
  }

  /** If we should allow animals to spawn in this world. */
  private boolean allowAnimals = true;

  /** If we should allow monsters to spawn in this world. */
  private boolean allowMonsters = true;

  /** The difficulty of the world. */
  private Difficulty difficulty = Difficulty.NORMAL;

  /** Is the world enabled (automatically load on startup). */
  private boolean enabled = true;

  /** The type of environment of this world. */
  private Environment environment;

  /** The game mode of this world. */
  private GameMode gameMode = GameMode.SURVIVAL;

  /** If we are generating structures in this world. */
  private boolean generateStructures = true;

  /** The name of the generator id. */
  private String generatorID;

  /** The name of the generator plugin. */
  private String generatorPluginName;

  /** If chat on this world is isolated. */
  private boolean isolatedChat = false;

  /** If we should keep the spawn of this world in memory. */
  private boolean keepSpawnInMemory = true;

  /** Do we listen for events or not?. */
  private boolean listening = true;

  private static Permission rootPermission;
  
  /** The permission required to enter this world. */
  private Permission permission;

  /** The plugin. */
  private final DimensionDoor plugin;

  /** If we should allow PVP in this world. */
  private boolean pvp = false;

  /** The seed of this world. */
  private long seed = 0;

  /** The loaded world. */
  private org.bukkit.World world;

  /** The name of the world. */
  private final String worldName;

  /** The type of the world. */
  private WorldType worldType = WorldType.NORMAL;

  /** Get the unique ID for this world. */
  private UUID worldUUID;

  /**
   * Instantiates a new world and base the attributes of the world provided.
   * 
   * @param plugin the plugin
   * @param world the world
   */
  public World(final DimensionDoor plugin, final org.bukkit.World world) {
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
  public World(final DimensionDoor plugin, final String worldName) {
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

    this.world.setDifficulty(this.difficulty);
    this.world.setKeepSpawnInMemory(this.keepSpawnInMemory);
    this.world.setPVP(this.pvp);
    this.world.setSpawnFlags(this.allowMonsters, this.allowAnimals);
    this.applyGameMode();
  }

  public Difficulty getDifficulty() {
    return this.difficulty;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public Environment getEnvironment() {
    return this.environment;
  }

  public GameMode getGameMode() {
    return this.gameMode;
  }

  /**
   * Gets the generator id.
   * 
   * @return the generator id
   */
  public String getGeneratorID() {
    return this.generatorID;
  }

  public String getGeneratorPluginName() {
    return this.generatorPluginName;
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
    return this.seed;
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
    return this.worldType;
  }

  /**
   * Checks if is chat isolated.
   * 
   * @return true, if is chat isolated
   */
  public boolean isChatIsolated() {
    return this.isolatedChat;
  }

  /**
   * Checks if is enabled.
   * 
   * @return true, if is enabled
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean isGeneratingStructures() {
    return this.generateStructures;
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
    return (this.plugin.getServer().getWorlds().get(0) == this.world);
  }

  public boolean isPVP() {
    return this.pvp;
  }

  public boolean isSpawningAnimals() {
    return this.allowAnimals;
  }

  public boolean isSpawningMonsters() {
    return this.allowMonsters;
  }

  public boolean isSpawnKeptInMemory() {
    return this.keepSpawnInMemory;
  }

  /**
   * Attempt to load the world.
   */
  public void load() {

    if (this.world == null) {
      this.getWorldCreator().createWorld();
    }
  }

  /**
   * When a player has sent a chat message, check if this world is isolated and
   * if it is prevent the message leaving the world.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerChat(final AsyncPlayerChatEvent event) {
    if (!this.listening) {
      return;
    }
    if ((this.isolatedChat == true) && (event.getPlayer().getWorld() == this.world)) {
      event.getRecipients().clear();
      event.getRecipients().addAll(this.world.getPlayers());
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerTeleportEvent(final PlayerRespawnEvent event) {
    if (this.environment == Environment.THE_END) {
      return;
    }
    if (event.getPlayer().getWorld() == this.world) {
      if (event.getPlayer().getWorld() != event.getRespawnLocation().getWorld()) {
        event.setRespawnLocation(this.world.getSpawnLocation());
      }
    }
  }

  /**
   * When a player teleports check they are allowed to enter the world.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {
    if (!this.listening) {
      return;
    }
    if (event.getTo().getWorld() == this.world && event.getFrom().getWorld() != this.world) {
      if (event.getPlayer().hasPermission(this.permission) || this.isMainWorld()) {
        this.applyGameMode(event.getPlayer());
      } else {
        event.getPlayer().sendMessage(this.plugin.getLocalisation().getMessage(this, "not-allowed-to-enter-world"));
        event.setCancelled(true);
      }
    }
  }

  /**
   * When a world is loaded, set a reference to the world if it isn't present
   * and apply attributes.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onWorldLoaded(final WorldLoadEvent event) {
    if (!this.listening) {
      return;
    }
    if (!event.getWorld().getName().equalsIgnoreCase(this.worldName)) {
      return;
    }

    if (this.world == null) {
      this.world = event.getWorld();
    }
    this.applyAttributes();
  }

  /**
   * When a world is unloaded, remove the reference to the world.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onWorldUnloaded(final WorldUnloadEvent event) {
    if (!this.listening) {
      return;
    }
    if (!event.getWorld().getName().equalsIgnoreCase(this.worldName)) {
      return;
    }

    this.world = null;
  }

  /**
   * Serialize the world and attributes.
   * 
   * @return the map
   */
  public Map<String, Object> serialize() {

    final Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("world-name", this.worldName);
    map.put("enabled", this.enabled);
    map.put("allow-animals", this.allowAnimals);
    map.put("allow-monsters", this.allowMonsters);
    map.put("difficulty", this.difficulty.toString());
    map.put("environment", this.environment.toString());
    map.put("game-mode", this.gameMode.toString());
    map.put("generate-structures", this.generateStructures);
    map.put("generator-id", this.generatorID);
    map.put("generator-plugin-name", this.generatorPluginName);
    map.put("isolated-chat", this.isolatedChat);
    map.put("pvp", this.pvp);
    map.put("seed", this.seed);
    map.put("world-type", this.worldType.toString());
    return map;
  }

  /**
   * Sets if animals should be allowed to spawn.
   * 
   * @param allowAnimals the new value
   */
  public void setAllowAnimals(final boolean allowAnimals) {

    this.allowAnimals = allowAnimals;
    if (this.world != null) {
      this.world.setSpawnFlags(this.allowMonsters, allowAnimals);
    }
  }

  /**
   * Sets if monsters should be allowed to spawn.
   * 
   * @param allowMonsters the new value
   */
  public void setAllowMonsters(final boolean allowMonsters) {

    this.allowMonsters = allowMonsters;
    if (this.world != null) {
      this.world.setSpawnFlags(allowMonsters, this.allowAnimals);
    }
  }

  /**
   * Sets the difficulty of the world.
   * 
   * @param difficulty the new value.
   */
  public void setDifficulty(final Difficulty difficulty) {
    if (difficulty == null) {
      throw new IllegalArgumentException("Difficulty can not be null!");
    }

    this.difficulty = difficulty;
    if (this.world != null) {
      this.world.setDifficulty(difficulty);
    }
  }

  /**
   * Sets if this world should be automatically loaded when the plugin starts.
   * 
   * @param enabled the new value
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Sets the environment of the world.
   * 
   * @param environment the new value
   */
  public void setEnvironment(final Environment environment) {
    if (environment == null) {
      throw new IllegalArgumentException("Environment can not be null!");
    }
    if (this.world == null) {
      this.environment = environment;
    }
  }

  /**
   * Sets the game mode of the world.
   * 
   * @param gameMode the new value
   */
  public void setGameMode(final GameMode gameMode) {
    if (this.environment == null) {
      throw new IllegalArgumentException("GameMode can not be null!");
    }
    this.gameMode = gameMode;
    this.applyGameMode();
  }

  /**
   * Sets if this world should generate structures.
   * 
   * @param generateStructures the new value
   */
  public void setGenerateStructures(final boolean generateStructures) {
    this.generateStructures = generateStructures;
  }

  /**
   * Sets the generator id.
   * 
   * @param generatorID the new value
   */
  public void setGeneratorID(final String generatorID) {
    this.generatorID = generatorID;
  }

  /**
   * Sets the generator plugin name.
   * 
   * @param generatorPlugin the new value
   */
  public void setGeneratorPluginName(final Plugin generatorPlugin) {
    if (generatorPlugin == null) {
      throw new IllegalArgumentException("GeneratorPlugin can not be null!");
    }
    this.generatorPluginName = generatorPlugin.getName();
  }

  /**
   * Sets the generator plugin name.
   * 
   * @param generatorPluginName the new value
   */
  public void setGeneratorPluginName(final String generatorPluginName) {
    if (generatorPluginName == null) {
      throw new IllegalArgumentException("GeneratorPluginName can not be null!");
    }
    this.generatorPluginName = generatorPluginName;
  }

  /**
   * Sets if chat in this world should be isolated.
   * 
   * @param isolatedChat the new value
   */
  public void setIsolatedChat(final boolean isolatedChat) {
    this.isolatedChat = isolatedChat;
  }

  /**
   * Sets if we should keep the spawn of the world in memory.
   * 
   * @param keepSpawnInMemory the new value
   */
  public void setKeepSpawnInMemory(final boolean keepSpawnInMemory) {
    this.keepSpawnInMemory = keepSpawnInMemory;
    if (this.world != null) {
      this.world.setKeepSpawnInMemory(keepSpawnInMemory);
    }
  }

  /**
   * Sets if PvP should be allowed in this world.
   * 
   * @param pvp the new value
   */
  public void setPVP(final boolean pvp) {
    this.pvp = pvp;
    if (this.world != null) {
      this.world.setPVP(pvp);
    }
  }

  /**
   * Sets the seed of the world.
   * 
   * @param seed the new value
   */
  public void setSeed(final long seed) {
    this.seed = seed;
  }

  /**
   * Sets a reference to a loaded world.
   * 
   * @param world the new value
   */
  public void setWorld(final org.bukkit.World world) {
    if (world != null) {
      throw new IllegalStateException("You may not change an existing world reference.");
    }
    this.world = world;
  }

  /**
   * Sets the type of world.
   * 
   * @param worldType the new value
   */
  public void setWorldType(final WorldType worldType) {
    if (this.world != null) {
      throw new IllegalStateException("You may not change the type of a world which is loaded.");
    }
    this.worldType = worldType;
  }

  @Override
  public String toString() {
    return this.serialize().toString();
  }

  /**
   * Unload a world and teleport players out if they are preventing the unload.
   */
  public void unload() {
    if (this.world == null) {
      throw new IllegalStateException("You may not unload a world which is not loaded.");
    }
    if (this.isMainWorld()) {
      throw new IllegalStateException("You may not unload the main world.");
    }
    this.removePlayers();
    this.plugin.getServer().unloadWorld(this.worldName, true);
  }

  /**
   * Unregister events.
   */
  public void unregisterEvents() {
    this.listening = false;
  }

  /**
   * Apply the world's game mode to all players in the world.
   * 
   * @return the int
   */
  private int applyGameMode() {
    int i = 0;
    for (final Player player : this.world.getPlayers()) {
      player.setGameMode(this.gameMode);
      i++;
    }
    return i;
  }

  /**
   * Apply the world's game mode to a specific player.
   * 
   * @param player the player
   */
  private void applyGameMode(final Player player) {
    if (this.difficulty == null) {
      throw new IllegalArgumentException("Player can not be null!");
    }
    player.setGameMode(this.gameMode);
  }

  /**
   * Check if world is loaded.
   */
  private void checkIfWorldIsLoaded() {
    for (final org.bukkit.World world : this.plugin.getServer().getWorlds()) {
      if (world.getName().equalsIgnoreCase(this.worldName)) {
        this.world = world;
      }
    }
  }

  /**
   * Gets the custom chunk generator.
   * 
   * @return the custom chunk generator
   */
  private ChunkGenerator getCustomChunkGenerator() {
    if (this.generatorPluginName != null) {
      final Plugin generatorPlugin = this.plugin.getServer().getPluginManager().getPlugin(this.generatorPluginName);
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
    return this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
  }

  private Permission getRootPermission() {
    if (World.rootPermission == null) this.setRootPermission();
    return World.rootPermission;
  }

  /**
   * Gets the world creator.
   * 
   * @return the world creator
   */
  private WorldCreator getWorldCreator() {
    final WorldCreator worldCreator = new WorldCreator(this.worldName);
    worldCreator.environment(this.environment);
    worldCreator.generateStructures(this.generateStructures);
    worldCreator.seed(this.seed);
    worldCreator.type(this.worldType);
    final ChunkGenerator chunkGenerator = this.getCustomChunkGenerator();
    if (chunkGenerator != null) {
      worldCreator.generator(chunkGenerator);
    }
    return worldCreator;
  }

  /**
   * Removes players from this world by teleporting them to the main world.
   * 
   * @return the int
   */
  private int removePlayers() {
    int i = 0;
    for (final Player player : this.world.getPlayers()) {
      player.teleport(this.getMainWorldSpawn(), TeleportCause.PLUGIN);
      i++;
    }
    return i;
  }

  private void setRootPermission() {
    final String prefix = plugin.getPermissionManager().getRootPermission().getName().replace("*", "");
    final Permission permission = new Permission(
        prefix + this.plugin.getLocalisation().getMessage(this, "wildcard-permission-name") + ".*",
        this.plugin.getLocalisation().getMessage(this, "wildcard-permission-description"),
        PermissionDefault.OP
    );
    plugin.getPermissionManager().addPermission(permission, true);
    World.rootPermission = permission;
  }
  
  private void setPermission() {
    final String prefix = this.getRootPermission().getName().replace("*", "");
    final Permission permission = new Permission(
        prefix + this.getName().toLowerCase().replaceAll(" ", "_"),
        this.plugin.getLocalisation().getMessage(this, "permission-description"),
        PermissionDefault.OP
    );
    permission.addParent(this.getRootPermission(), true);
    plugin.getPermissionManager().addPermission(permission, false);
  }

}

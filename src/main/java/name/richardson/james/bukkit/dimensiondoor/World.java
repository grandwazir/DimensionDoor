package name.richardson.james.bukkit.dimensiondoor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import name.richardson.james.bukkit.utilities.localisation.Localised;

public class World extends Localised implements Serializable, Listener {

  private static final long serialVersionUID = 8551768503578434301L;

  public static World deserialize(final Map<String, Object> map) {
    DimensionDoor plugin = (DimensionDoor) Bukkit.getServer().getPluginManager().getPlugin("DimensionDoor");
    final World world = new World(plugin, (String) map.get("world-name"));
    world.setAllowAnimals((Boolean) map.get("allow-animals"));
    world.setAllowMonsters((Boolean) map.get("allow-monsters"));
    world.setDifficulty((Difficulty) map.get("difficulty"));
    world.setEnvironment((Environment) map.get("environment"));
    world.setGameMode((GameMode) map.get("game-mode"));
    world.setGenerateStructures((Boolean) map.get("generate-structures"));
    world.setGeneratorID((String) map.get("generator-id"));
    world.setGeneratorPluginName((String) map.get("generator-plugin-name"));
    world.setPVP((Boolean) map.get("pvp"));
    world.setSeed((Long) map.get("seed"));
    return world;
  }
  
  /** The loaded world */
  private org.bukkit.World world;
  
  /** The difficulty of the world */
  private Difficulty difficulty = Difficulty.NORMAL;
  
  /** If we should keep the spawn of this world in memory */
  private boolean keepSpawnInMemory = true;
  
  /** If we should allow PVP in this world */
  private boolean pvp = false;
  
  /** If we should allow monsters to spawn in this world */
  private boolean allowMonsters = true;
  
  /** If we should allow animals to spawn in this world */
  private boolean allowAnimals = true;
  
  /** The type of the world */
  private WorldType worldType = WorldType.NORMAL;

  /** The game mode of this world */
  private GameMode gameMode = GameMode.SURVIVAL;
  
  /** The plugin */
  private DimensionDoor plugin;
  
  /** The permission required to enter this world */
  private Permission permission;

  /** The name of the world */
  private final String worldName;

  /** The type of environment of this world */
  private Environment environment;

  /** If we are generating structures in this world */
  private boolean generateStructures = true;
  
  /** The seed of this world */
  private long seed;
  
  /** The name of the generator plugin */
  private String generatorPluginName; 
  
  /** The name of the generator id */
  private String generatorID;
  
  /** Get the unique ID for this world */
  private UUID worldUUID;
  
  /** If chat on this world is isolated */
  private boolean isolatedChat = true;
  
  public World(DimensionDoor plugin, org.bukkit.World world) {
    super(plugin);
    this.plugin = plugin; 
    this.world = world;
    this.worldName = world.getName();
    this.allowAnimals = world.getAllowAnimals();
    this.allowMonsters = world.getAllowMonsters();
    this.environment = world.getEnvironment();
    this.gameMode = this.plugin.getServer().getDefaultGameMode();
    this.pvp = world.getPVP();
    this.seed = world.getSeed();
    this.worldType = world.getWorldType();
    this.worldUUID = world.getUID();
    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  public World(DimensionDoor plugin, String worldName) {
    super(plugin);
    this.plugin = plugin; 
    this.worldName = worldName;
    this.checkIfWorldIsLoaded();
    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  private void checkIfWorldIsLoaded() {
    for (org.bukkit.World world : this.plugin.getServer().getWorlds()) {
      if (world.getName().equalsIgnoreCase(worldName)) this.world = world;
    }
  }
  
  public void applyAttributes() {
    world.setDifficulty(difficulty);
    world.setKeepSpawnInMemory(keepSpawnInMemory);
    world.setPVP(pvp);
    world.setSpawnFlags(allowMonsters, allowAnimals);
    this.applyGameMode();
  }
  
  public UUID getUUID() {
    return this.worldUUID;
  }
  
  public String getName() {
    return this.worldName;
  }
  
  public String getGeneratorID() {
    return generatorID;
  }
  
  public boolean isMainWorld() {
    return (plugin.getServer().getWorlds().get(0) == world);
  }
  
  public void load() {
    if (world != null) throw new IllegalStateException("You may not load a world which is already loaded.");
    this.getWorldCreator().createWorld();
  }
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
  public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
    if (event.getTo().getWorld() == this.world) {
      if (event.getPlayer().hasPermission(permission)) {
        this.applyGameMode(event.getPlayer());
      } else {
        this.getMessage("not-allowed-to-enter-world");
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onWorldLoaded(WorldLoadEvent event) {
    if (!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
    if (world == null) this.world = event.getWorld();
    this.applyAttributes();
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onWorldUnloaded(WorldUnloadEvent event) {
    if (!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
    this.world = null;
  }
  
  public Map<String, Object> serialize() {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("world-name", worldName);
    map.put("allow-animals", allowAnimals);
    map.put("allow-monsters", allowMonsters);
    map.put("difficulty", difficulty);
    map.put("environment", environment);
    map.put("game-mode", gameMode);
    map.put("generate-structures", generateStructures);
    map.put("generator-id", generatorID);
    map.put("generator-plugin-name", generatorPluginName);
    map.put("pvp", pvp);
    map.put("seed", seed);
    return map;
  }
  
  public void setAllowAnimals(boolean allowAnimals) {
    this.allowAnimals = allowAnimals;
    world.setSpawnFlags(allowMonsters, allowAnimals);
  }
  
  public void setAllowMonsters(boolean allowMonsters) {
    this.allowMonsters = allowMonsters;
    world.setSpawnFlags(allowMonsters, allowAnimals);
  }
  
  public void setDifficulty(Difficulty difficulty) {
    if (difficulty == null) throw new IllegalArgumentException("Difficulty can not be null!");
    this.difficulty = difficulty;
    world.setDifficulty(difficulty);
  }
  
  public void setEnvironment(Environment environment) {
    if (environment == null) throw new IllegalArgumentException("Environment can not be null!");
    this.environment = environment;
  }
  
  public void setGameMode(GameMode gameMode) {
    this.gameMode = gameMode;
    this.applyGameMode();
  }
  
  public void setGenerateStructures(boolean generateStructures) {
    this.setGenerateStructures(generateStructures);
  }
  
  public void setGeneratorID(String generatorID) {
    this.generatorID = generatorID;
  }
  
  public void setGeneratorPluginName(Plugin generatorPlugin) {
    this.generatorPluginName = generatorPlugin.getName();
  }
  
  public void setGeneratorPluginName(String generatorPluginName) {
    this.generatorPluginName = generatorPluginName;
  }
  
  public void setKeepSpawnInMemory(boolean keepSpawnInMemory) {
    this.keepSpawnInMemory = keepSpawnInMemory;
    world.setKeepSpawnInMemory(keepSpawnInMemory);
  }
  
  public void setPVP(boolean pvp) {
    this.pvp = pvp;
    world.setPVP(pvp);
  }
  
  public void setSeed(long seed) {
    if (seed != 0) throw new IllegalStateException("You may not change the seed of a world.");
    this.seed = seed;
  }
  
  public void setWorldType(WorldType worldType) {
    if (world != null) throw new IllegalStateException("You may not change the type of a world which is loaded.");
    this.worldType = worldType;
  }
  
  public void setWorld(org.bukkit.World world) {
    if (world != null) throw new IllegalStateException("You may not change an existing world reference.");
    this.world = world;
  }
  
  public void unload() {
    if (world == null) throw new IllegalStateException("You may not unload a world which is not loaded.");
    if (isMainWorld()) throw new IllegalStateException("You may not unload the main world.");
    this.removePlayers();
    this.plugin.getServer().unloadWorld(world, true);
  }
  
  private void applyGameMode() {
    for (Player player : world.getPlayers()) {
      player.setGameMode(gameMode);
    }
  }
  
  private void applyGameMode(Player player) {
    if (difficulty == null) throw new IllegalArgumentException("Player can not be null!");
    player.setGameMode(gameMode);
  }

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
  
  private Location getMainWorldSpawn() {
    return plugin.getServer().getWorlds().get(0).getSpawnLocation();
  }

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

  private int removePlayers() {
    int i = 0;
    for (Player player : world.getPlayers()) {
      player.teleport(this.getMainWorldSpawn(), TeleportCause.PLUGIN);
      i++;
    }
    return i;
  }

  public boolean isChatIsolated() {
    return isolatedChat;
  }

  public void setIsolatedChat(boolean isolatedChat) {
    this.isolatedChat = isolatedChat;
  }
  
}

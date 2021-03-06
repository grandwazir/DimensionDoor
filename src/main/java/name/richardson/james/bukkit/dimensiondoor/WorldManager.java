package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.PlayerInventory;

import name.richardson.james.bukkit.utilities.listener.LoggableListener;

public class WorldManager extends LoggableListener {
  
  /** The storage backing the WorldManager. */
  private final WorldConfiguration storage;
  
  /** A collection of configured worlds. */
  private final Map<String, World> worlds;

  /** The plugin. */
  private final DimensionDoor plugin;
  
  /**
   * Instantiates a new world manager.
   *
   * @param plugin the plugin
   * @throws IOException Signals that an I/O exception has occurred loading the configuration
   */
  public WorldManager(DimensionDoor plugin) throws IOException {
    super(plugin);
    this.plugin = plugin;
    this.storage = new WorldConfiguration(plugin);
    this.worlds = this.storage.getWorlds();
    this.checkForMainWorlds();
    int i = 0;
    for (World world : this.worlds.values()) {    
      if (world.isEnabled()) {
        if (!world.isLoaded()) {
          world.load();
        } else {
          world.applyAttributes();
        }
      }
      i++;
    }
    this.getLogger().debug(this, "configured-worlds",i, this.worlds.size());
  }
  
  private void checkForMainWorlds() {
    boolean saveRequired = false;
    for (org.bukkit.World world : this.plugin.getServer().getWorlds()) {
      if (!this.worlds.containsKey(world.getName())) {
        this.worlds.put(world.getName(), new World(this.plugin, world));
        saveRequired = true;
      }
    }
    if (saveRequired) this.save();
  }

  /**
   * When a world is initialised, check that it has been configured, if not create one.
   *
   * @param event the event
   */
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) 
  public void onWorldInit(WorldInitEvent event) {
    this.getLogger().debug(this, "new-world", event.getWorld().getName());
    if (worlds.containsKey(event.getWorld().getName())) return;
    this.getLogger().warning(this, "unexpected-world", event.getWorld().getName());
    this.worlds.put(event.getWorld().getName(), new World(this.plugin, event.getWorld()));
  }
  
  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true) 
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (plugin.isClearingCreativeInventories()) {
      GameMode origin = this.worlds.get(event.getFrom().getWorld().getName()).getGameMode();
      GameMode destination = this.worlds.get(event.getTo().getWorld().getName()).getGameMode();
      if (origin.equals(GameMode.CREATIVE) && !destination.equals(GameMode.CREATIVE)) {
    	  final PlayerInventory inventory = event.getPlayer().getInventory();
    	  inventory.setArmorContents(null);
    	  inventory.clear();
      }
    }
    final String texturePack = this.worlds.get(event.getTo().getWorld().getName()).getTexturePack();
    if (texturePack != null && (event.getFrom().getWorld() != event.getTo().getWorld())) {
      final SwitchTexturePackTask task = new SwitchTexturePackTask(event.getPlayer(), texturePack);
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 10L);
    }
  }
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true) 
  public void onPlayerJoin(PlayerJoinEvent event) {
    final String texturePack = this.worlds.get(event.getPlayer().getWorld().getName()).getTexturePack();
    if (texturePack != null) {
      final SwitchTexturePackTask task = new SwitchTexturePackTask(event.getPlayer(), texturePack);
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 10L);
    }
  }
  
  public void addWorld(World world) {
    this.worlds.put(world.getName(), world);
  }
  
  /**
   * Gets a configured world.
   *
   * @param name the name
   * @return the world
   */
  public World getWorld(String name) {
    return worlds.get(name);
  }
  
  /**
   * Save all worlds to the backing storage.
   */
  public void save() {
    storage.setWorlds(worlds);
  }
  
  /**
   * Get the number of configured worlds.
   *
   * @return the int
   */
  public int configuredWorldCount() {
    return this.worlds.size();
  }

  public void removeWorld(World world) {
    if (world.isLoaded()) {
      world.unload();
      world.unregisterEvents();
    }
    this.worlds.remove(world.getName());
    this.save();
  }

  public Map<String, World> getWorlds() {
    return Collections.unmodifiableMap(this.worlds);
  }
  
}

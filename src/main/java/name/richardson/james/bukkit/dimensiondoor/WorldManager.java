package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import name.richardson.james.bukkit.utilities.internals.Logger;

public class WorldManager implements Listener {

  /** The logger for this class. */
  private final Logger logger = new Logger(this.getClass());
  
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
    this.logger.debug("Initalising world manager.");
    this.storage = new WorldConfiguration(plugin);
    this.plugin = plugin;
    this.worlds = this.storage.getWorlds();
    int i = 0;
    for (World world : this.worlds.values()) {
      if (world.isEnabled()) world.load();
      i++;
    }
    this.logger.debug(String.format("Enabled %d of %d configured worlds.", i, this.worlds.size()));
  }
  
  /**
   * When a world is initialised, check that it has been configured, if not create one.
   *
   * @param event the event
   */
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) 
  public void onWorldInit(WorldInitEvent event) {
    this.logger.debug(String.format("A world called %s has initalised.", event.getWorld().getName()));
    if (worlds.containsKey(event.getWorld().getName())) return;
    this.logger.debug(String.format("%s has not been configured by DimensionDoor.", event.getWorld().getName()));
    this.worlds.put(event.getWorld().getName(), new World(this.plugin, event.getWorld()));
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
   * Removes a configured world.
   *
   * @param name the name
   */
  public void removeWorld(String name) {
    World world = this.getWorld(name);
    if (world != null) {
      world.unload();
      world.unregisterEvents();
      this.worlds.remove(name);
      this.save();
    }
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

}

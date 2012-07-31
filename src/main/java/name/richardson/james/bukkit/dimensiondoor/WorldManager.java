package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldManager implements Listener {

  private final WorldConfiguration storage;
  
  private final Map<String, World> worlds;

  private final DimensionDoor plugin;
  
  public WorldManager(DimensionDoor plugin) throws IOException {
    this.storage = new WorldConfiguration(plugin);
    this.plugin = plugin;
    this.worlds = this.storage.getWorlds();
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) 
  public void onWorldInit(WorldInitEvent event) {
    if (worlds.containsKey(event.getWorld().getName())) return;
    this.worlds.put(event.getWorld().getName(), new World(this.plugin, event.getWorld()));
  }
  
  public World getWorld(String name) {
    return worlds.get(name);
  }
  
  public void save() {
    storage.setWorlds(worlds);
  }
  
  public int configuredWorldCount() {
    return this.worlds.size();
  }

}

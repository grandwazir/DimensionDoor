package name.richardson.james.dimensiondoor.listeners;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ItemSpawnEvent;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

public class DimensionDoorEntityListener extends EntityListener {

  private final DimensionDoor plugin;
  public HashMap<String, GameMode> worldGameModes = new HashMap<String, GameMode>();

  public DimensionDoorEntityListener(final DimensionDoor plugin) {
    this.plugin = plugin;
  }
  
  public void onItemSpawn(final ItemSpawnEvent event) {
    Item item = (Item) event.getEntity();
    GameMode gamemode = worldGameModes.get(item.getWorld().getName());
    if (gamemode == GameMode.CREATIVE) {
      event.setCancelled(true);
    }
  }
  
  public void updateCache() {
    WorldRecord.findAll();
    for (WorldRecord record : WorldRecord.findAll()) {
      worldGameModes.put(record.getName(), record.getGamemode());
    }
  }
  
}

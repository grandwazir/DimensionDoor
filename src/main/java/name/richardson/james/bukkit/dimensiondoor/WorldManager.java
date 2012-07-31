package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.world.WorldInitEvent;

public class WorldManager implements Listener {

  private final WorldConfiguration storage;
  
  private final Set<String> isolatedWorlds = new HashSet<String>(8);
  
  private final Map<String, World> worlds;

  private final DimensionDoor plugin;
  
  public WorldManager(DimensionDoor plugin) throws IOException {
    this.storage = new WorldConfiguration(plugin);
    this.plugin = plugin;
    this.worlds = this.storage.getWorlds();
  }
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
  public void onPlayerChat(PlayerChatEvent event) {
    if (this.isolatedWorlds.isEmpty()) return;
    final List<Player> players = Arrays.asList(this.plugin.getServer().getOnlinePlayers());
    final String originWorld = event.getPlayer().getWorld().getName();
    for (Player player : players) {
      String playerWorld = player.getWorld().getName();
      if (this.isolatedWorlds.contains(originWorld) && !playerWorld.equalsIgnoreCase(originWorld)) {
        // TODO: Test if this actually works. Would be nice if it does, I am guessing it does not.
        event.getRecipients().remove(player);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) 
  public void onWorldInit(WorldInitEvent event) {
    if (worlds.containsKey(event.getWorld().getName())) return;
    this.worlds.put(event.getWorld().getName(), new World(this.plugin, event.getWorld()));
  }
  
  public World getWorld(String name) {
    return worlds.get(name);
  }
  
  public void refresh() {
    this.isolatedWorlds.clear();
    for (World world : this.worlds.values()) {
      if (world.isChatIsolated()) this.isolatedWorlds.add(world.getName());
    }
  }
  
  public void save() {
    storage.setWorlds(worlds);
  }
  
  public int configuredWorldCount() {
    return this.worlds.size();
  }

}

package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import name.richardson.james.bukkit.utilities.persistence.YAMLStorage;

public class WorldConfiguration extends YAMLStorage {

  public static final String FILE_NAME = "worlds.yml";
  
  private final ConfigurationSection section;
  
  public WorldConfiguration(JavaPlugin plugin) throws IOException {
    super(plugin, FILE_NAME);
    section = this.configuration.getConfigurationSection("worlds");
  }
  
  public void setDefaults() throws IOException {
    super.setDefaults();
    if (!configuration.isConfigurationSection("worlds")) {
      this.configuration.createSection("worlds");
      this.save();
    }
  }

  public Map<String, World> getWorlds() {
    Map<String, World> worlds = new HashMap<String, World>(8);
    if (this.section.getKeys(false) != null) {
      for (String key : this.section.getKeys(false)) {
        final World world = (World) this.section.get(key);
        worlds.put(world.getName(), world);
      }
    }
    return worlds;
  }
  
  public void setWorlds(Map<String, World> worlds) {
    for (World world : worlds.values()) {
      this.section.set(world.getName(), world);
    }
    try {
      this.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}

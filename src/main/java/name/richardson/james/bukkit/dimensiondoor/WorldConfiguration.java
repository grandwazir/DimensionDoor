package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import name.richardson.james.bukkit.utilities.persistence.AbstractYAMLStorage;
import name.richardson.james.bukkit.utilities.plugin.Plugin;

public class WorldConfiguration extends AbstractYAMLStorage {

  public static final String FILE_NAME = "worlds.yml";
  
  private final ConfigurationSection section;
  
  public WorldConfiguration(Plugin plugin) throws IOException {
    super(plugin, FILE_NAME);
    this.setDefaultWorlds();
    section = this.getConfiguration().getConfigurationSection("worlds");
  }
  
  private void setDefaultWorlds() throws IOException {
    if (!this.getConfiguration().isConfigurationSection("worlds")) {
      this.getConfiguration().createSection("worlds");
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
    this.save();
  }
  
}

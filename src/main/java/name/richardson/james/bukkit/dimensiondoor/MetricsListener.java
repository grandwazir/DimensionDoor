package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldInitEvent;

import name.richardson.james.bukkit.utilities.metrics.Metrics;
import name.richardson.james.bukkit.utilities.metrics.Metrics.Graph;
import name.richardson.james.bukkit.utilities.metrics.Metrics.Plotter;

public final class MetricsListener {

  private int normal = 0;
  
  private int custom = 0;
  
  private int end = 0;
  
  private int nether = 0;
  
  private int normalType = 0;
  
  private int largeBiomesType = 0;
  
  private int flatType = 0;
  
  private int customType = 0;
  
  private final Metrics metrics;

  private final WorldManager manager;

  public MetricsListener(DimensionDoor plugin) throws IOException {
    this.metrics = new Metrics(plugin);
    this.manager = plugin.getWorldManager();
    this.refreshStatistics();
    this.setupUsageStatistics();
    this.metrics.start();
  }

  private void refreshStatistics() {
    // reset statistics
    this.normal = 0;
    this.nether = 0;
    this.end = 0;
    this.custom = 0;
    this.normalType = 0;
    this.flatType = 0;
    this.customType = 0;
    this.largeBiomesType = 0;
    for (World world : this.manager.getWorlds().values()) {
      // set statistic for world environment
      switch (world.getEnvironment()) {
      case NORMAL:
        if (world.getGeneratorPluginName() == null) {
          this.normal++;
        } else {
          this.custom++;
        }
        break;
      case NETHER:
        this.nether++;
        break;
      case THE_END:
        this.end++;
        break;
      }
      // set statistics for world types
      switch (world.getWorldType()) {
      case NORMAL:
        if (world.getGeneratorPluginName() == null) {
          this.normalType++;
        } else {
          this.customType++;
        }
        break;
      case FLAT:
        this.flatType++;
        break;
      case LARGE_BIOMES:
        this.largeBiomesType++;
        break;
      }
    }
  }

  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onWorldInit(WorldInitEvent event) {
    this.refreshStatistics();
  }
  
  private void setupUsageStatistics() {
    // Create a graph to show the total amount of kits issued.
    Graph graph = this.metrics.createGraph("World Environment Statistics");
    graph.addPlotter(new Plotter("Custom") {
      @Override
      public int getValue() {
        int i = nether;
        return i;
      }
    });
    graph.addPlotter(new Plotter("Nether") {
      @Override
      public int getValue() {
        int i = custom;
        return i;
      }
    });
    graph.addPlotter(new Plotter("Normal") {
      @Override
      public int getValue() {
        int i = normal;
        return i;
      }
    });
    graph.addPlotter(new Plotter("The End") {
      @Override
      public int getValue() {
        int i = end;
        return i;
      }
    });
    // Create a graph to show the total amount of kits issued.
    Graph graph1 = this.metrics.createGraph("World Type Statistics");
    graph1.addPlotter(new Plotter("Custom") {
      @Override
      public int getValue() {
        int i = customType;
        return i;
      }
    });
    graph1.addPlotter(new Plotter("Flat") {
      @Override
      public int getValue() {
        int i = flatType;
        return i;
      }
    });
    graph1.addPlotter(new Plotter("Large Biomes") {
      @Override
      public int getValue() {
        int i = largeBiomesType;
        return i;
      }
    });
    graph1.addPlotter(new Plotter("Normal") {
      @Override
      public int getValue() {
        int i = normalType;
        return i;
      }
    });
  }
  
}

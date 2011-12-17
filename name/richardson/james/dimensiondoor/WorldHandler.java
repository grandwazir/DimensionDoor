package name.richardson.james.dimensiondoor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import name.richardson.james.dimensiondoor.database.WorldRecord;
import name.richardson.james.dimensiondoor.database.WorldRecordHandler;
import name.richardson.james.dimensiondoor.util.Handler;


public class WorldHandler extends Handler {

  protected final static WorldRecordHandler handler = new WorldRecordHandler(WorldHandler.class);
  
  private final Map<String, Object> defaults = new HashMap<String, Object>();
  private final Server server;
  
  public WorldHandler(Class<?> parentClass) {
    super(parentClass);
    this.server = DimensionDoor.getInstance().getServer();
    this.setDefaults();
  }

  public World createWorld(String worldName) {
    WorldCreator newWorld = getDefaultWorld(worldName);
    return server.createWorld(newWorld);
  }
  
  public World createWorld(String worldName, Environment environment) {
    WorldCreator newWorld = getDefaultWorld(worldName);
    newWorld.environment(environment);
    return server.createWorld(newWorld);
  }
  
  public World createWorld(String worldName, Environment environment, Long seed) {
    WorldCreator newWorld = getDefaultWorld(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    return server.createWorld(newWorld);
  }
  
  public World createWorld(String worldName, Environment environment, Long seed, String generatorPlugin, String generatorID) {
    WorldCreator newWorld = getDefaultWorld(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    World world = server.createWorld(newWorld);
    WorldRecord record = handler.getWorldRecord(world);
    record.setGeneratorPlugin(generatorPlugin);
    record.setGeneratorID(generatorID);
    handler.saveWorldRecord(record);
    return world;
  }
  
  public void deleteWorld(World world) {
    unloadWorld(world);
    handler.deleteWorldRecord(world);
  }
  
  public World loadWorld(WorldRecord record) {
    WorldCreator newWorld = getDefaultWorld(record.getName());
    newWorld.environment(record.getEnvironment());
    return server.createWorld(newWorld);
  }
  
  
  public void unloadWorld(World world) {
    server.unloadWorld(world, true);
  }
  
  public boolean isWorldLoaded(String worldName) {
    World world = server.getWorld(worldName);
    if (world == null) {
      return false;
    } else {
      return true;
    }
  }
  
  protected void applyWorldAttributes(World world) {
    WorldRecord record = handler.getWorldRecord(world);
    world.setDifficulty(record.getDifficulty());
    world.setPVP(record.isPvp());
    world.setSpawnFlags(record.isSpawnMonsters(), record.isSpawnAnimals());
  }
  
  private WorldCreator getDefaultWorld(String worldName) {
    WorldCreator world = new WorldCreator(worldName);
    world.environment((Environment) defaults.get("environment"));
    return world;
  }
  
  private void setDefaults() {
    World world = server.getWorlds().get(0);
    defaults.clear();
    defaults.put("pvp", world.getPVP());
    defaults.put("spawn-monsters", world.getAllowMonsters());
    defaults.put("spawn-animals", world.getAllowAnimals());
    defaults.put("difficulty", world.getDifficulty());
    defaults.put("environment", world.getEnvironment());
    defaults.put("game-mode", server.getDefaultGameMode());
  }
  
  public Map<String, Object> getDefaults() {
    return Collections.unmodifiableMap(defaults);
  }
  
}

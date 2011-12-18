
package name.richardson.james.dimensiondoor.creation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.database.WorldRecord;
import name.richardson.james.dimensiondoor.database.WorldRecordHandler;
import name.richardson.james.dimensiondoor.util.Logger;

public class WorldHandler {

  private static final Logger logger = new Logger(WorldHandler.class);

  private static final Set<World> isolatedChatWorlds = new HashSet<World>();
  private static final Set<World> creativeWorlds = new HashSet<World>();
  private static final Map<String, Object> defaults = new HashMap<String, Object>();

  public static void applyWorldAttributes(World world) {
    logger.debug(String.format("Applying world attributes for %s.", world.getName()));
    WorldRecord record = WorldRecordHandler.getWorldRecord(world);
    world.setDifficulty(record.getDifficulty());
    world.setPVP(record.isPvp());
    world.setSpawnFlags(record.isSpawnMonsters(), record.isSpawnAnimals());
    if (record.isIsolatedChat()) isolatedChatWorlds.add(world);
    if (record.getGamemode().equals(GameMode.CREATIVE)) creativeWorlds.add(world);
    WorldHandler.setPlayerGameModes(world, record.getGamemode());
  }

  public static World createWorld(String worldName, Environment environment, Long seed) {
    final Server server = DimensionDoor.getInstance().getServer();
    final WorldCreator newWorld = new WorldCreator(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    logger.debug(String.format("Creating new world called %s.", worldName));
    logger.debug(String.format("name: %s, environment: %s, seed: %s.", worldName, environment.toString(), seed.toString()));
    return server.createWorld(newWorld);
  }

  public static World createWorld(String worldName, Environment environment, Long seed, String generatorPlugin, String generatorID) {
    final Server server = DimensionDoor.getInstance().getServer();
    final WorldCreator newWorld = new WorldCreator(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    logger.debug(String.format("Creating new world called %s.", worldName));
    logger.debug(String.format("name: %s, environment: %s, seed: %s.", worldName, environment.toString(), seed.toString()));
    logger.debug(String.format("Setting generator specifics for %s.", worldName));
    logger.debug(String.format("generatorPlugin: %s, generatorID: %s", generatorPlugin, generatorID));
    ChunkGenerator chunkGenerator = WorldHandler.getCustomChunkGenerator(generatorPlugin, generatorID, worldName);
    newWorld.generator(chunkGenerator);
    World world = server.createWorld(newWorld);
    WorldRecord record = WorldRecordHandler.getWorldRecord(world);
    record.setGeneratorPlugin(generatorPlugin);
    record.setGeneratorID(generatorID);
    WorldRecordHandler.saveWorldRecord(record);
    return world;
  }

  public static Set<World> getCreativeWorlds() {
    return Collections.unmodifiableSet(creativeWorlds);
  }

  public static Map<String, Object> getDefaults() {
    if (defaults.isEmpty()) setDefaults();
    return Collections.unmodifiableMap(defaults);
  }

  public static Set<World> getIsolatedWorlds() {
    return Collections.unmodifiableSet(isolatedChatWorlds);
  }

  public static World getWorld(String worldName) {
    final Server server = DimensionDoor.getInstance().getServer();
    return server.getWorld(worldName);
  }

  public static boolean isWorldLoaded(String worldName) {
    final Server server = DimensionDoor.getInstance().getServer();
    World world = server.getWorld(worldName);
    if (world == null) {
      return false;
    } else {
      return true;
    }
  }

  public static World loadWorld(WorldRecord record) {
    final Server server = DimensionDoor.getInstance().getServer();
    if (isWorldLoaded(record.getName())) return server.getWorld(record.getName());
    logger.debug(String.format("Loading world called %s.", record.getName()));
    WorldCreator newWorld = new WorldCreator(record.getName());
    newWorld.environment(record.getEnvironment());
    if (record.getGeneratorPlugin() != null) {
      ChunkGenerator chunkGenerator = WorldHandler.getCustomChunkGenerator(record.getGeneratorPlugin(), record.getGeneratorID(), record.getName());
      newWorld.generator(chunkGenerator);
    }
    return server.createWorld(newWorld);
  }

  public static void setPlayerGameModes(World world, GameMode gameMode) {
    logger.debug(String.format("Applying %s gamemode to all players in %s.", gameMode.toString(), world.getName()));
    for (Player player : world.getPlayers()) {
      if (player.isOnline()) player.setGameMode(gameMode);
    }
  }

  public static void unloadWorld(World world) {
    final Server server = DimensionDoor.getInstance().getServer();
    logger.debug(String.format("Unloading world called %s.", world.getName()));
    if (!world.getPlayers().isEmpty()) {
      throw new IllegalStateException("You can not unload a world which contains players.");
    } else {
      server.unloadWorld(world, true);
    }
  }

  private static ChunkGenerator getCustomChunkGenerator(String generator, final String generatorID, final String worldName) {
    final PluginManager pluginManager = DimensionDoor.getInstance().getServer().getPluginManager();
    if (pluginManager.isPluginEnabled(generator)) {
      final Plugin plugin = pluginManager.getPlugin(generator);
      ChunkGenerator chunkGenerator = plugin.getDefaultWorldGenerator(worldName, generatorID);
      if (chunkGenerator == null) {
        throw new IllegalArgumentException(String.format("%s does not support that generator!", generator));
      } else {
        return chunkGenerator;
      }
    } else {
      throw new IllegalArgumentException(String.format("%s is not enabled!", generator));
    }
  }

  private static void setDefaults() {
    final Server server = DimensionDoor.getInstance().getServer();
    World world = server.getWorlds().get(0);
    defaults.clear();
    defaults.put("pvp", world.getPVP());
    defaults.put("spawn-monsters", world.getAllowMonsters());
    defaults.put("spawn-animals", world.getAllowAnimals());
    defaults.put("difficulty", world.getDifficulty());
    defaults.put("environment", world.getEnvironment());
    defaults.put("game-mode", server.getDefaultGameMode());
  }

  protected static void onWorldUnload(World world) {
    isolatedChatWorlds.remove(world);
    creativeWorlds.remove(world);
  }

  public void deleteWorld(World world) {
    unloadWorld(world);
    logger.debug(String.format("Deleting world called %s.", world.getName()));
    WorldRecordHandler.deleteWorldRecord(world);
  }

}

/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * DimensionDoor.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.util.Logger;
import name.richardson.james.bukkit.util.Plugin;
import name.richardson.james.bukkit.util.command.CommandManager;
import name.richardson.james.dimensiondoor.creation.CreateCommand;
import name.richardson.james.dimensiondoor.creation.WorldListener;

public class DimensionDoor extends Plugin {
  
  private final Set<World> isolatedChatWorlds = new HashSet<World>();
  private final Set<World> creativeWorlds = new HashSet<World>();
  private final Map<String, Object> defaults = new HashMap<String, Object>();
  
  private Server server;
  private PluginManager pluginManager;
  private DimensionDoorConfiguration configuration;
  private WorldListener worldListener;
  private DatabaseHandler database;

  public void applyWorldAttributes(World world) {
    logger.debug(String.format("Applying world attributes for %s.", world.getName()));
    WorldRecord record = WorldRecord.findByWorld(this.database, world);
    world.setDifficulty(record.getDifficulty());
    world.setPVP(record.isPvp());
    world.setSpawnFlags(record.isSpawnMonsters(), record.isSpawnAnimals());
    if (record.isIsolatedChat()) isolatedChatWorlds.add(world);
    if (record.getGamemode().equals(GameMode.CREATIVE)) creativeWorlds.add(world);
    setPlayerGameModes(world, record.getGamemode());
  }

  public World createWorld(String worldName, Environment environment, Long seed) {
    final WorldCreator newWorld = new WorldCreator(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    logger.debug(String.format("Creating new world called %s.", worldName));
    logger.debug(String.format("name: %s, environment: %s, seed: %s.", worldName, environment.toString(), seed.toString()));
    return server.createWorld(newWorld);
  }
  
  public World createWorld(String worldName, Environment environment, Long seed, String generatorPlugin, String generatorID) {
    final WorldCreator newWorld = new WorldCreator(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    logger.debug(String.format("Creating new world called %s.", worldName));
    logger.debug(String.format("name: %s, environment: %s, seed: %s.", worldName, environment.toString(), seed.toString()));
    logger.debug(String.format("Setting generator specifics for %s.", worldName));
    logger.debug(String.format("generatorPlugin: %s, generatorID: %s", generatorPlugin, generatorID));
    ChunkGenerator chunkGenerator = getCustomChunkGenerator(generatorPlugin, generatorID, worldName);
    newWorld.generator(chunkGenerator);
    World world = server.createWorld(newWorld);
    WorldRecord record = WorldRecord.findByWorld(this.database, world);
    record.setGeneratorPlugin(generatorPlugin);
    record.setGeneratorID(generatorID);
    database.save(record);
    return world;
  }

  public void deleteWorld(World world) {
    unloadWorld(world);
    logger.debug(String.format("Deleting world called %s.", world.getName()));
    database.delete(world);
  }

  public Set<World> getCreativeWorlds() {
    return Collections.unmodifiableSet(creativeWorlds);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    return DatabaseHandler.getDatabaseClasses();
  }

  public Map<String, Object> getDefaults() {
    if (defaults.isEmpty()) setDefaults();
    return Collections.unmodifiableMap(defaults);
  }

  public Set<World> getIsolatedWorlds() {
    return Collections.unmodifiableSet(isolatedChatWorlds);
  }

  public World getWorld(String worldName) {
    return server.getWorld(worldName);
  }

  public boolean isWorldLoaded(String worldName) {
    World world = server.getWorld(worldName);
    if (world == null) {
      return false;
    } else {
      return true;
    }
  }

  public void addWorld(World world) {
    logger.debug(String.format("Creating world record for %s.", world.getName()));
    WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    record.setEnvironment(world.getEnvironment());
    record.setSeed(world.getSeed());
    record.setDifficulty(world.getDifficulty());
    record.setGamemode((GameMode) defaults.get("game-mode"));
    database.save(record);
  }
  
  public World loadWorld(WorldRecord record) {
    if (isWorldLoaded(record.getName())) return server.getWorld(record.getName());
    logger.debug(String.format("Loading world called %s.", record.getName()));
    WorldCreator newWorld = new WorldCreator(record.getName());
    newWorld.environment(record.getEnvironment());
    if (record.getGeneratorPlugin() != null) {
      ChunkGenerator chunkGenerator = getCustomChunkGenerator(record.getGeneratorPlugin(), record.getGeneratorID(), record.getName());
      newWorld.generator(chunkGenerator);
    }
    return server.createWorld(newWorld);
  }

  public void onDisable() {
    logger.info(String.format("%s is disabled!", this.getDescription().getName()));
  }

  public void onEnable() {
    this.pluginManager = this.getServer().getPluginManager();
    this.server = this.getServer();
    
    try {
      this.loadConfiguration();
      this.setupDatabase();
      this.setDefaults();
      this.setPermission();
      // load the worlds
      this.registerListeners();
      this.registerMainWorlds();
      this.registerAuxiliaryWorlds();
      logger.info(String.format("%d worlds loaded and configured.", this.getServer().getWorlds().size()));
      this.registerCommands();
    } catch (IOException exception) {
      logger.severe("Unable to load configuration!");
      exception.printStackTrace();
    } finally {
      if (!this.getServer().getPluginManager().isPluginEnabled(this)) return;
    }
    logger.info(String.format("%s is enabled.", this.getDescription().getFullName()));
  }

  public void onWorldUnload(World world) {
    isolatedChatWorlds.remove(world);
    creativeWorlds.remove(world);
  }

  public void setPlayerGameModes(World world, GameMode gameMode) {
    logger.debug(String.format("Applying %s gamemode to all players in %s.", gameMode.toString(), world.getName()));
    for (Player player : world.getPlayers()) {
      if (player.isOnline()) player.setGameMode(gameMode);
    }
  }

  public void unloadWorld(World world) {
    logger.debug(String.format("Unloading world called %s.", world.getName()));
    if (!world.getPlayers().isEmpty()) {
      throw new IllegalStateException("You can not unload a world which contains players.");
    } else {
      server.unloadWorld(world, true);
    }
  }

  private ChunkGenerator getCustomChunkGenerator(String generator, final String generatorID, final String worldName) {
    if (pluginManager.isPluginEnabled(generator)) {
      final org.bukkit.plugin.Plugin plugin = pluginManager.getPlugin(generator);
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

  private void loadConfiguration() throws IOException {
    DimensionDoorConfiguration configuration = new DimensionDoorConfiguration(this);
    if (configuration.isDebugging()) Logger.enableDebugging(this.getDescription().getName().toLowerCase());
  }

  private void registerAuxiliaryWorlds() {
    for (Object entity : this.database.list(WorldRecord.class)) {
      final WorldRecord record = (WorldRecord) entity;
      try {
        loadWorld(record);
      } catch (IllegalArgumentException exception) {
        logger.warning(String.format("Unable to load %s: %s", record.getName(), exception.getMessage()));
      }
    }
  }

  private void registerCommands() {
    CommandManager cm = new CommandManager(this.getDescription());
    getCommand("dd").setExecutor(cm);
    cm.registerCommand("create", new CreateCommand(this));
    /*
    cm.registerCommand("info", new InfoCommand());
    cm.registerCommand("list", new ListCommand());
    cm.registerCommand("load", new LoadCommand());
    cm.registerCommand("modify", new ModifyCommand());
    cm.registerCommand("remove", new RemoveCommand());
    cm.registerCommand("spawn", new SpawnCommand());
    cm.registerCommand("teleport", new TeleportCommand());
    cm.registerCommand("unload", new UnloadCommand());
    */
  }

  private void registerListeners() {
    worldListener = new WorldListener(this);
    pluginManager.registerEvent(Event.Type.WORLD_LOAD, worldListener, Event.Priority.Monitor, this);
    pluginManager.registerEvent(Event.Type.WORLD_UNLOAD, worldListener, Event.Priority.Monitor, this);
    pluginManager.registerEvent(Event.Type.WORLD_INIT, worldListener, Event.Priority.Monitor, this);
    /*
    pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
    pluginManager.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.High, this);
    pluginManager.registerEvent(Event.Type.PLAYER_CHANGED_WORLD, playerListener, Event.Priority.High, this);
    if (configuration.isPreventContainerBlocks()) {
      pluginManager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
    }
    if (configuration.isPreventItemSpawning()) {
      pluginManager.registerEvent(Event.Type.ITEM_SPAWN, entityListener, Event.Priority.High, this);
    }
    */
  }

  private void registerMainWorlds() {
    for (World world : this.getServer().getWorlds()) {
      final WorldRecord record = WorldRecord.findByWorld(getDatabaseHandler(), world);
      if (record == null) {
        this.addWorld(world);
      }
      applyWorldAttributes(world);
    }
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

  private void setupDatabase() {
    try {
      this.getDatabase().find(WorldRecord.class).findRowCount();
      this.database = new DatabaseHandler(this.getDatabase());
    } catch (final PersistenceException ex) {
      logger.warning("No database schema found. Generating a new one.");
      this.installDDL();
    }
  }

  public DatabaseHandler getDatabaseHandler() {
    return database;
  }

}

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

package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;
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

import name.richardson.james.bukkit.dimensiondoor.creation.CreateCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.WorldListener;
import name.richardson.james.bukkit.util.Logger;
import name.richardson.james.bukkit.util.Plugin;
import name.richardson.james.bukkit.util.command.CommandManager;

public class DimensionDoor extends Plugin {

  private final Set<World> isolatedChatWorlds = new HashSet<World>();
  private final Set<World> creativeWorlds = new HashSet<World>();
  private final Map<String, Object> defaults = new HashMap<String, Object>();

  private Server server;
  private PluginManager pluginManager;
  private DimensionDoorConfiguration configuration;
  private WorldListener worldListener;
  private DatabaseHandler database;

  public void addWorld(final World world) {
    this.logger.debug(String.format("Creating world record for %s.", world.getName()));
    final WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    record.setEnvironment(world.getEnvironment());
    record.setSeed(world.getSeed());
    record.setDifficulty(world.getDifficulty());
    record.setGamemode((GameMode) this.defaults.get("game-mode"));
    this.database.save(record);
  }

  public void applyWorldAttributes(final World world) {
    this.logger.debug(String.format("Applying world attributes for %s.", world.getName()));
    final WorldRecord record = WorldRecord.findByWorld(this.database, world);
    world.setDifficulty(record.getDifficulty());
    world.setPVP(record.isPvp());
    world.setSpawnFlags(record.isSpawnMonsters(), record.isSpawnAnimals());
    if (record.isIsolatedChat()) {
      this.isolatedChatWorlds.add(world);
    }
    if (record.getGamemode().equals(GameMode.CREATIVE)) {
      this.creativeWorlds.add(world);
    }
    this.setPlayerGameModes(world, record.getGamemode());
  }

  public World createWorld(final String worldName, final Environment environment, final Long seed) {
    final WorldCreator newWorld = new WorldCreator(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    this.logger.debug(String.format("Creating new world called %s.", worldName));
    this.logger.debug(String.format("name: %s, environment: %s, seed: %s.", worldName, environment.toString(), seed.toString()));
    return this.server.createWorld(newWorld);
  }

  public World createWorld(final String worldName, final Environment environment, final Long seed, final String generatorPlugin, final String generatorID) {
    final WorldCreator newWorld = new WorldCreator(worldName);
    newWorld.environment(environment);
    newWorld.seed(seed);
    this.logger.debug(String.format("Creating new world called %s.", worldName));
    this.logger.debug(String.format("name: %s, environment: %s, seed: %s.", worldName, environment.toString(), seed.toString()));
    this.logger.debug(String.format("Setting generator specifics for %s.", worldName));
    this.logger.debug(String.format("generatorPlugin: %s, generatorID: %s", generatorPlugin, generatorID));
    final ChunkGenerator chunkGenerator = this.getCustomChunkGenerator(generatorPlugin, generatorID, worldName);
    newWorld.generator(chunkGenerator);
    final World world = this.server.createWorld(newWorld);
    final WorldRecord record = WorldRecord.findByWorld(this.database, world);
    record.setGeneratorPlugin(generatorPlugin);
    record.setGeneratorID(generatorID);
    this.database.save(record);
    return world;
  }

  public void deleteWorld(final World world) {
    this.unloadWorld(world);
    this.logger.debug(String.format("Deleting world called %s.", world.getName()));
    this.database.delete(world);
  }

  public Set<World> getCreativeWorlds() {
    return Collections.unmodifiableSet(this.creativeWorlds);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    return DatabaseHandler.getDatabaseClasses();
  }

  public DatabaseHandler getDatabaseHandler() {
    return this.database;
  }

  public Map<String, Object> getDefaults() {
    if (this.defaults.isEmpty()) {
      this.setDefaults();
    }
    return Collections.unmodifiableMap(this.defaults);
  }

  public Set<World> getIsolatedWorlds() {
    return Collections.unmodifiableSet(this.isolatedChatWorlds);
  }

  public DimensionDoorConfiguration getPluginConfiguration() {
    return this.configuration;
  }

  public World getWorld(final String worldName) {
    return this.server.getWorld(worldName);
  }

  public boolean isWorldLoaded(final String worldName) {
    final World world = this.server.getWorld(worldName);
    if (world == null)
      return false;
    else
      return true;
  }

  public World loadWorld(final WorldRecord record) {
    if (this.isWorldLoaded(record.getName())) return this.server.getWorld(record.getName());
    this.logger.debug(String.format("Loading world called %s.", record.getName()));
    final WorldCreator newWorld = new WorldCreator(record.getName());
    newWorld.environment(record.getEnvironment());
    if (record.getGeneratorPlugin() != null) {
      final ChunkGenerator chunkGenerator = this.getCustomChunkGenerator(record.getGeneratorPlugin(), record.getGeneratorID(), record.getName());
      newWorld.generator(chunkGenerator);
    }
    return this.server.createWorld(newWorld);
  }

  @Override
  public void onDisable() {
    this.logger.info(String.format("%s is disabled!", this.getDescription().getName()));
  }

  @Override
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
      this.logger.info(String.format("%d worlds loaded and configured.", this.getServer().getWorlds().size()));
      this.registerCommands();
    } catch (final IOException exception) {
      this.logger.severe("Unable to load configuration!");
      exception.printStackTrace();
    } finally {
      if (!this.getServer().getPluginManager().isPluginEnabled(this)) return;
    }
    this.logger.info(String.format("%s is enabled.", this.getDescription().getFullName()));
  }

  public void onWorldUnload(final World world) {
    this.isolatedChatWorlds.remove(world);
    this.creativeWorlds.remove(world);
  }

  public void removeWorld(final WorldRecord record) {
    this.logger.debug(String.format("Removing world record for %s.", record.getName()));
    final World world = this.getWorld(record.getName());
    if (world != null) {
      this.unloadWorld(world);
    }
    this.database.delete(record);
  }

  public void setPlayerGameModes(final World world, final GameMode gameMode) {
    this.logger.debug(String.format("Applying %s gamemode to all players in %s.", gameMode.toString(), world.getName()));
    for (final Player player : world.getPlayers()) {
      if (player.isOnline()) {
        player.setGameMode(gameMode);
      }
    }
  }

  public void unloadWorld(final World world) {
    this.logger.debug(String.format("Unloading world called %s.", world.getName()));
    if (!world.getPlayers().isEmpty())
      throw new IllegalStateException("You can not unload a world which contains players.");
    else {
      this.server.unloadWorld(world, true);
    }
  }

  private ChunkGenerator getCustomChunkGenerator(final String generator, final String generatorID, final String worldName) {
    if (this.pluginManager.isPluginEnabled(generator)) {
      final org.bukkit.plugin.Plugin plugin = this.pluginManager.getPlugin(generator);
      final ChunkGenerator chunkGenerator = plugin.getDefaultWorldGenerator(worldName, generatorID);
      if (chunkGenerator == null)
        throw new IllegalArgumentException(String.format("%s does not support that generator!", generator));
      else
        return chunkGenerator;
    } else
      throw new IllegalArgumentException(String.format("%s is not enabled!", generator));
  }

  private void loadConfiguration() throws IOException {
    final DimensionDoorConfiguration configuration = new DimensionDoorConfiguration(this);
    if (configuration.isDebugging()) {
      Logger.enableDebugging(this.getDescription().getName().toLowerCase());
    }
  }

  private void registerAuxiliaryWorlds() {
    for (final Object entity : this.database.list(WorldRecord.class)) {
      final WorldRecord record = (WorldRecord) entity;
      try {
        this.loadWorld(record);
      } catch (final IllegalArgumentException exception) {
        this.logger.warning(String.format("Unable to load %s: %s", record.getName(), exception.getMessage()));
      }
    }
  }

  private void registerCommands() {
    final CommandManager cm = new CommandManager(this.getDescription());
    this.getCommand("dd").setExecutor(cm);
    cm.registerCommand("create", new CreateCommand(this));
    /*
     * cm.registerCommand("info", new InfoCommand());
     * cm.registerCommand("list", new ListCommand());
     * cm.registerCommand("load", new LoadCommand());
     * cm.registerCommand("modify", new ModifyCommand());
     * cm.registerCommand("remove", new RemoveCommand());
     * cm.registerCommand("spawn", new SpawnCommand());
     * cm.registerCommand("teleport", new TeleportCommand());
     * cm.registerCommand("unload", new UnloadCommand());
     */
  }

  private void registerListeners() {
    this.worldListener = new WorldListener(this);
    this.pluginManager.registerEvent(Event.Type.WORLD_LOAD, this.worldListener, Event.Priority.Monitor, this);
    this.pluginManager.registerEvent(Event.Type.WORLD_UNLOAD, this.worldListener, Event.Priority.Monitor, this);
    this.pluginManager.registerEvent(Event.Type.WORLD_INIT, this.worldListener, Event.Priority.Monitor, this);
    /*
     * pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
     * Event.Priority.Normal, this);
     * pluginManager.registerEvent(Event.Type.PLAYER_CHAT, playerListener,
     * Event.Priority.High, this);
     * pluginManager.registerEvent(Event.Type.PLAYER_CHANGED_WORLD,
     * playerListener, Event.Priority.High, this);
     * if (configuration.isPreventContainerBlocks()) {
     * pluginManager.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
     * Event.Priority.High, this);
     * }
     * if (configuration.isPreventItemSpawning()) {
     * pluginManager.registerEvent(Event.Type.ITEM_SPAWN, entityListener,
     * Event.Priority.High, this);
     * }
     */
  }

  private void registerMainWorlds() {
    for (final World world : this.getServer().getWorlds()) {
      final WorldRecord record = WorldRecord.findByWorld(this.getDatabaseHandler(), world);
      if (record == null) {
        this.addWorld(world);
      }
      this.applyWorldAttributes(world);
    }
  }

  private void setDefaults() {
    final World world = this.server.getWorlds().get(0);
    this.defaults.clear();
    this.defaults.put("pvp", world.getPVP());
    this.defaults.put("spawn-monsters", world.getAllowMonsters());
    this.defaults.put("spawn-animals", world.getAllowAnimals());
    this.defaults.put("difficulty", world.getDifficulty());
    this.defaults.put("environment", world.getEnvironment());
    this.defaults.put("game-mode", this.server.getDefaultGameMode());
  }

  private void setupDatabase() {
    try {
      this.getDatabase().find(WorldRecord.class).findRowCount();
      this.database = new DatabaseHandler(this.getDatabase());
    } catch (final PersistenceException ex) {
      this.logger.warning("No database schema found. Generating a new one.");
      this.installDDL();
    }
  }

}

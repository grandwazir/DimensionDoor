/* 
Copyright 2011 James Richardson.

This file is part of DimensionDoor.

DimensionDoor is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DimensionDoor is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.richardson.james.dimensiondoor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.dimensiondoor.commands.CreateCommand;
import name.richardson.james.dimensiondoor.commands.InfoCommand;
import name.richardson.james.dimensiondoor.commands.ListCommand;
import name.richardson.james.dimensiondoor.commands.LoadCommand;
import name.richardson.james.dimensiondoor.commands.ModifyCommand;
import name.richardson.james.dimensiondoor.commands.RemoveCommand;
import name.richardson.james.dimensiondoor.commands.SpawnCommand;
import name.richardson.james.dimensiondoor.commands.TeleportCommand;
import name.richardson.james.dimensiondoor.commands.TemplateCommand;
import name.richardson.james.dimensiondoor.commands.UnloadCommand;
import name.richardson.james.dimensiondoor.exceptions.CustomChunkGeneratorNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.InvalidEnvironmentException;
import name.richardson.james.dimensiondoor.exceptions.PluginNotFoundException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsAlreadyLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotEmptyException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.listeners.DimensionDoorPlayerListener;
import name.richardson.james.dimensiondoor.listeners.DimensionDoorWorldListener;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.Transaction;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class DimensionDoor extends JavaPlugin {

  public static DimensionDoor instance;
  static Logger logger = Logger.getLogger("Minecraft");

  public PermissionHandler externalPermissions = null;
  public HashMap<String, Boolean> isolatedChatAttributes = new HashMap<String, Boolean>();
  private CommandManager cm;
  private PluginDescriptionFile desc;
  private final DimensionDoorPlayerListener playerListener;
  private PluginManager pm;
  private final DimensionDoorWorldListener worldListener;

  public DimensionDoor() {
    DimensionDoor.instance = this;
    worldListener = new DimensionDoorWorldListener(this);
    playerListener = new DimensionDoorPlayerListener(this);
  }

  public static DimensionDoor getInstance() {
    return instance;
  }

  public static void log(final Level level, final String msg) {
    logger.log(level, "[" + instance.getName() + "] " + msg);
  }

  public void applyWorldAttributes(final WorldRecord worldRecord) {
    try {
      final World world = getWorld(worldRecord.getName());
      world.setPVP(worldRecord.isPvp());
      world.setSpawnFlags(worldRecord.isSpawnMonsters(), worldRecord.isSpawnAnimals());
      isolatedChatAttributes.put(world.getName(), worldRecord.isIsolatedChat());
      DimensionDoor.log(Level.INFO, String.format("Applying world configuration: %s", world.getName()));
    } catch (final WorldIsNotLoadedException e) {
      DimensionDoor.log(Level.WARNING, String.format("Attempted to apply configuration to unloaded world: %s", worldRecord.getName()));
    }
  }

  public void createWorld(final String worldName, final String environmentName, final String seedString) throws InvalidEnvironmentException,
      WorldIsAlreadyLoadedException {
    if (!isWorldLoaded(worldName)) {
      final World.Environment environment;
      long worldSeed = 0;

      try {
        environment = Environment.valueOf(environmentName);
      } catch (final IllegalArgumentException e) {
        throw new InvalidEnvironmentException(environmentName);
      }

      try {
        worldSeed = Long.parseLong(seedString);
      } catch (final NumberFormatException e) {
        worldSeed = seedString.hashCode();
      } finally {
        getServer().createWorld(worldName, environment, worldSeed);
      }

      getServer().createWorld(worldName, environment);
    } else {
      throw new WorldIsAlreadyLoadedException();
    }
  }

  public void createWorld(final String worldName, final String environmentName, final String seedString, final String generatorName, final String generatorID)
      throws InvalidEnvironmentException, WorldIsAlreadyLoadedException, PluginNotFoundException, CustomChunkGeneratorNotFoundException {
    if (!isWorldLoaded(worldName)) {
      final World.Environment environment;
      final ChunkGenerator generator = getCustomChunkGenerator(generatorName, generatorID, worldName);
      long worldSeed = System.currentTimeMillis();

      try {
        environment = Environment.valueOf(environmentName);
      } catch (final IllegalArgumentException e) {
        throw new InvalidEnvironmentException(environmentName);
      }

      try {
        worldSeed = Long.parseLong(seedString);
      } catch (final NumberFormatException e) {
        worldSeed = seedString.hashCode();
      }

      try {
        final World world = getServer().createWorld(worldName, environment, worldSeed, generator);
        registerCustomWorld(world, generatorName, generatorID);
      } catch (final WorldIsNotManagedException e) {
        DimensionDoor.log(Level.SEVERE, String.format("Unable to register custom world: %s", worldName));
      }

    } else {
      throw new WorldIsAlreadyLoadedException();
    }
  }

  public void createWorld(final WorldRecord world) {
    getServer().createWorld(world.getName(), world.getEnvironment());
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(WorldRecord.class);
    return list;
  }

  public HashMap<String, Boolean> getDefaultAttributes() {
    final HashMap<String, Boolean> m = new HashMap<String, Boolean>();
    m.put("pvp", getMainWorld().getPVP());
    m.put("spawnAnimals", getMainWorld().getAllowAnimals());
    m.put("spawnMonsters", getMainWorld().getAllowMonsters());
    m.put("isolatedChat", false);
    return m;
  }

  public String getName() {
    return desc.getName();
  }

  public World getWorld(final String worldName) throws WorldIsNotLoadedException {
    final World world = getServer().getWorld(worldName);
    if (world == null)
      throw new WorldIsNotLoadedException();
    return world;
  }

  public HashMap<String, Boolean> getWorldAttributes(final World world) {
    final HashMap<String, Boolean> m = new HashMap<String, Boolean>();
    m.put("pvp", world.getPVP());
    m.put("spawnMonsters", world.getAllowMonsters());
    m.put("spawnAnimals", world.getAllowAnimals());
    return m;
  }

  public List<World> getWorlds() {
    return getServer().getWorlds();
  }

  public String getWorldSeed(final String worldName) throws WorldIsNotLoadedException {
    if (isWorldLoaded(worldName)) {
      return Long.toString(getWorld(worldName).getSeed());
    } else {
      return "Unable to retrieve for unloaded worlds";
    }
  }

  public boolean isWorldLoaded(final String worldName) {
    if (getServer().getWorld(worldName) != null) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isWorldManaged(final String worldName) {
    if (WorldRecord.count(worldName) == 1) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isWorldManaged(final World world) {
    if (WorldRecord.count(world) == 1) {
      return true;
    } else {
      return false;
    }
  }

  public void loadWorld(final WorldRecord worldRecord) throws WorldIsAlreadyLoadedException, PluginNotFoundException, CustomChunkGeneratorNotFoundException {
    if (isWorldLoaded(worldRecord.getName()))
      throw new WorldIsAlreadyLoadedException();
    
    if (worldRecord.getGeneratorPlugin() != null) {
      final ChunkGenerator generator = getCustomChunkGenerator(worldRecord.getGeneratorPlugin(), worldRecord.getGeneratorID(), worldRecord.getName());
      PluginDescriptionFile generatorDesc = getPluginDescription(worldRecord.getGeneratorPlugin());
      getServer().createWorld(worldRecord.getName(), worldRecord.getEnvironment(), generator);
      log(Level.INFO, String.format("Using custom world generator: %s",generatorDesc.getFullName()));
    } else {
      getServer().createWorld(worldRecord.getName(), worldRecord.getEnvironment());
    }
  }

  public void onDisable() {
    log(Level.INFO, String.format("%s is disabled!", desc.getName()));
  }

  public void onEnable() {
    pm = getServer().getPluginManager();
    desc = getDescription();
    cm = new CommandManager();

    // get external permissions if available
    connectPermissions();

    
    try {
      setupDatabase();
    } catch (SQLException e) {
      log(Level.SEVERE, "Unable to establish database!");
      pm.disablePlugin(this);
    }

    // check to see if we are disabled
    if (!pm.isPluginEnabled(this)) return;
    
    // register the main worlds
    log(Level.INFO, "Registering and loading worlds...");

    for (final World world : getWorlds()) {
      if (!isWorldManaged(world.getName())) {
        registerWorld(world);
      }
    }

    // load and apply attributes to all managed worlds
    for (final WorldRecord worldRecord : WorldRecord.findAll()) {
      try {
        loadWorld(worldRecord);
      } catch (final WorldIsAlreadyLoadedException e) {
        // we can safely ignore this
      } catch (PluginNotFoundException e) {
        log(Level.WARNING, String.format("Unable to load %s as %s is not available", worldRecord.getName(), worldRecord.getGeneratorPlugin()));
      } catch (CustomChunkGeneratorNotFoundException e) {
        log(Level.WARNING, String.format("Unable to load %s as custom chunk generator is not available from %s", worldRecord.getName(), worldRecord.getGeneratorPlugin()));
      } finally {
        applyWorldAttributes(worldRecord);
      }
    }

    // register events
    pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.WORLD_INIT, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Highest, this);

    // register commands
    getCommand("dd").setExecutor(cm);
    cm.registerCommand("create", new CreateCommand(this));
    cm.registerCommand("info", new InfoCommand(this));
    cm.registerCommand("list", new ListCommand(this));
    cm.registerCommand("load", new LoadCommand(this));
    cm.registerCommand("modify", new ModifyCommand(this));
    cm.registerCommand("remove", new RemoveCommand(this));
    cm.registerCommand("spawn", new SpawnCommand(this));
    cm.registerCommand("teleport", new TeleportCommand(this));
    cm.registerCommand("template", new TemplateCommand(this));
    cm.registerCommand("unload", new UnloadCommand(this));

    log(Level.INFO, String.format("%d worlds configured!", getWorlds().size()));
    log(Level.INFO, String.format("%s is enabled!", desc.getFullName()));
  }

  public void registerCustomWorld(final World world, final String generatorPlugin, final String generatorID) throws WorldIsNotManagedException {
    final WorldRecord worldRecord = WorldRecord.findFirst(world);
    final HashMap<String, String> attributes = worldRecord.getGeneratorAttributes();
    attributes.put("generatorPlugin", generatorPlugin);
    attributes.put("generatorID", generatorID);
    worldRecord.setGeneratorAttributes(attributes);
  }

  public void registerWorld(final World world) {
    final String worldName = world.getName();

    DimensionDoor.log(Level.INFO, String.format("Creating world configuration: %s", worldName));
    WorldRecord.create(world);
  }

  public void unloadWorld(final String worldName) throws WorldIsNotEmptyException, WorldIsNotLoadedException {
    final World world = getWorld(worldName);

    if (world.getPlayers().size() == 0) {
      getServer().unloadWorld(getWorld(worldName), true);
    } else {
      throw new WorldIsNotEmptyException();
    }
  }

  public void unloadWorld(final World world) throws WorldIsNotEmptyException, WorldIsNotLoadedException {
    if (world.getPlayers().size() == 0) {
      getServer().unloadWorld(world.getName(), true);
    } else {
      throw new WorldIsNotEmptyException();
    }
  }

  private void connectPermissions() {
    final Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
    if (permissionsPlugin != null) {
      externalPermissions = ((Permissions) permissionsPlugin).getHandler();
      log(Level.INFO, String.format("External permissions system found (%s)", ((Permissions) permissionsPlugin).getDescription().getFullName()));
    }
  }

  // Utilities

  private ChunkGenerator getCustomChunkGenerator(final String generator, final String generatorID, final String worldName) throws PluginNotFoundException,
      CustomChunkGeneratorNotFoundException {
    final Plugin generatorPlugin = pm.getPlugin(generator);
    ChunkGenerator customChunkGenerator = null;
    
    if (generatorPlugin != null) {
      customChunkGenerator = generatorPlugin.getDefaultWorldGenerator(worldName, generatorID);
      
      if (customChunkGenerator == null)
        throw new CustomChunkGeneratorNotFoundException();
    } else {
      throw new PluginNotFoundException();
    }

    return customChunkGenerator;
  }

  public World getMainWorld() {
    return getServer().getWorlds().get(0);
  }
  
  private void setupDatabase() throws SQLException {
    try {
      getDatabase().find(WorldRecord.class).findList();
    } catch (final PersistenceException ex) {
      if (ex.getMessage().contains("table") || ex.getMessage().contains("'minecraft_game.dd_worlds' doesn't exist") ) {
        log(Level.WARNING, "No database found, creating schema.");
        installDDL();
      } else if (ex.getMessage().contains("column")) {
        log(Level.WARNING, "Database schema is out of date");
        upgradeDatabase(getDatabaseVersion());
      }
    } finally {
      WorldRecord.setup(this);
    }
  }
  
  private double getDatabaseVersion() throws SQLException {
    final Transaction transaction = getDatabase().createTransaction();
    Connection connection = transaction.getConnection();
    
    double version = 1.0;
   
    try {
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      Statement statement = connection.createStatement();
      statement.execute("SELECT isolated_chat FROM dd_worlds");
      statement.execute("SELECT generator_plugin FROM dd_worlds");
      connection.commit();
    } catch (SQLException e) {
      if (e.getMessage().contains("no such column: isolated_chat") || e.getMessage().contains("Unknown column 'isolated_chat'"))
        return version = 1.2;
      else if (e.getMessage().contains("no such column: generator_plugin") || e.getMessage().contains("Unknown column 'generator_plugin'")) 
        return version = 1.5;
    } finally {
      connection.close();
    }
       
    return version;
    
  }
  
  private void upgradeDatabase(double version) throws SQLException {
    
    final Transaction transaction = getDatabase().createTransaction();
    Connection connection = transaction.getConnection();
   
    log(Level.INFO, "Schema is currently based on v" + Double.toString(version));
    try {
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      Statement statement = connection.createStatement();
      
      if (version <= 1.2) {
        log(Level.INFO, "Updating schema to v1.3");
        statement.execute("ALTER TABLE dd_worlds ADD isolated_chat tinyint(1) DEFAULT 0 NOT NULL");
      } 
      
      if (version <= 1.5) {
        log(Level.INFO, "Updating schema to v1.6");
        statement.execute("ALTER TABLE dd_worlds ADD generator_plugin varchar(255)");
        statement.execute("ALTER TABLE dd_worlds ADD generator_id varchar(255)");
      }
      connection.commit();
    } catch (SQLException e) {
      log(Level.WARNING, "Error when upgrading database!");
      e.printStackTrace();
    } finally {
      connection.close();
    }
  }
  
  private PluginDescriptionFile getPluginDescription(String plugin) {
    return pm.getPlugin(plugin).getDescription();
  }
  
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.dimensiondoor.commands.CommandManager;
import name.richardson.james.dimensiondoor.exceptions.InvalidEnvironment;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotEmpty;
import name.richardson.james.dimensiondoor.listeners.DimensionDoorPlayerListener;
import name.richardson.james.dimensiondoor.listeners.DimensionDoorWorldListener;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class DimensionDoor extends JavaPlugin {

  private static DimensionDoor instance;

  static Logger logger = Logger.getLogger("Minecraft");
  private final DimensionDoorPlayerListener playerListener;
  private final DimensionDoorWorldListener worldListener;
  private CommandManager cm;
  public PermissionHandler externalPermissions = null;
  private PluginDescriptionFile desc;
  private PluginManager pm;

  public DimensionDoor() {
    DimensionDoor.instance = this;
    cm = new CommandManager(this);
    worldListener = new DimensionDoorWorldListener(this);
    playerListener = new DimensionDoorPlayerListener(this);
  }

  public HashMap<String, Boolean> getDefaultAttributes() {
    HashMap<String, Boolean> m = new HashMap<String, Boolean>();
    m.put("pvp", getMainWorld().getPVP());
    m.put("spawnAnimals", getMainWorld().getAllowAnimals());
    m.put("spawnMonsters", getMainWorld().getAllowMonsters());
    m.put("isolatedChat", false);
    return m;
  }

  public static void log(final Level level, final String msg) {
    logger.log(level, "[" + instance.getName() + "] " + msg);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(WorldRecord.class);
    return list;
  }

  public String getName() {
    return desc.getName();
  }

  public String getName(final CommandSender sender) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      final String senderName = player.getName();
      return senderName;
    } else {
      return "console";
    }
  }

  public String getWorldSeed(String worldName) {
    if (isWorldLoaded(worldName)) {
      return Long.toString(getWorld(worldName).getSeed());
    } else {
      return "Unable to retrieve for unloaded worlds";
    }
  }

  public void onDisable() {
    log(Level.INFO, String.format("%s is disabled!", desc.getName()));
  }

  public void onEnable() {
    cm = new CommandManager(this);
    pm = getServer().getPluginManager();
    desc = getDescription();

    // setup environment
    connectPermissions();
    setupDatabase();

    // register events
    pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.WORLD_INIT, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Highest, this);

    // register commands
    getCommand("dd").setExecutor(cm);

    log(Level.INFO, "Registering and loading worlds...");

    // load the main worlds
    for (final World world : getWorlds()) {
      if (!isWorldManaged(world)) {
        registerWorld(world);
      }
      applyWorldAttributes(WorldRecord.findFirst(world.getName()));
    }

    // load and apply attributes to managed worlds
    for (final WorldRecord world : WorldRecord.findAll()) {
      loadWorld(world);
    }

    log(Level.INFO, String.format("%d worlds configured!", getWorlds().size()));
    log(Level.INFO, String.format("%s is enabled!", desc.getFullName()));
  }

  public void registerWorld(World world) {
    final String worldName = world.getName();
    
    DimensionDoor.log(Level.INFO, String.format("Creating world configuration: %s", worldName));
    WorldRecord.create(world);
  }
  
  public boolean isWorldLoaded(String worldName) {
    if (getServer().getWorld(worldName) != null) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isWorldManaged(String worldName) {
    if (WorldRecord.count(worldName) == 1) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isWorldManaged(World world) {
    if (WorldRecord.count(world) == 1) {
      return true;
    } else {
      return false;
    }
  }

  public DimensionDoor getInstance() {
    return instance;
  }

  public List<World> getWorlds() {
    return getServer().getWorlds();
  }

  public void createWorld(WorldRecord world) {
    getServer().createWorld(world.getName(), world.getEnvironment());
  }

  public void applyWorldAttributes(WorldRecord worldRecord) {
    final World world = getWorld(worldRecord.getName());

    world.setPVP(worldRecord.isPvp());
    world.setSpawnFlags(worldRecord.isSpawnMonsters(), worldRecord.isSpawnAnimals());
    DimensionDoor.log(Level.INFO, String.format("Applying world configuration: %s", world.getName()));
  }

  public void createWorld(String worldName, String environmentName, String seedString) throws InvalidEnvironment {
    final World.Environment environment;
    long worldSeed = 0;

    // check the environment is valid
    try {
      environment = Environment.valueOf(environmentName);
    } catch (IllegalArgumentException e) {
      throw new InvalidEnvironment(environmentName);
    }

    // convert the seed if necessary
    if (seedString == null) {
      getServer().createWorld(worldName, environment);
    } else {
      try {
        worldSeed = Long.parseLong(seedString);
      } catch (NumberFormatException e) {
        worldSeed = (long) seedString.hashCode();
      } finally {
        getServer().createWorld(worldName, environment, worldSeed);
      }
    }
  }

  public void loadWorld(WorldRecord worldRecord) {
    getServer().createWorld(worldRecord.getName(), worldRecord.getEnvironment());
  }

  public void unloadWorld(String worldName) throws WorldIsNotEmpty {
    final World world = getWorld(worldName);
    
    if (world.getPlayers().size() == 0) {
      getServer().unloadWorld(getWorld(worldName), true);
    } else {
      throw new WorldIsNotEmpty();
    }
  }

  private World getMainWorld() {
    return getServer().getWorlds().get(0);
  }

  public World getWorld(String worldName) {
    return getServer().getWorld(worldName);
  }

  public HashMap<String, Boolean> getWorldAttributes(final World world) {
    final HashMap<String, Boolean> m = new HashMap<String, Boolean>();
    m.put("pvp", world.getPVP());
    m.put("spawnMonsters", world.getAllowMonsters());
    m.put("spawnAnimals", world.getAllowAnimals());
    return m;
  }

  // Utilities

  private void setupDatabase() {
    WorldRecord.setup(this);
    try {
      getDatabase().find(WorldRecord.class).findRowCount();
      getDatabase().find(WorldRecord.class).findList();
    } catch (final PersistenceException ex) {
      if (ex.getMessage().contains("isolated_chat")) {
        log(Level.WARNING, "Database schema out of date!");
        log(Level.INFO, "- Updating to version 1.3.0");
        getDatabase().createSqlUpdate("ALTER TABLE dd_worlds ADD isolated_chat tinyint(1) not null DEFAULT 0").execute();
      } else {
        log(Level.WARNING, "No database found, creating schema.");
        installDDL();
      }
    }
  }

  private void connectPermissions() {
    final Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
    if (permissionsPlugin != null) {
      externalPermissions = ((Permissions) permissionsPlugin).getHandler();
      log(Level.INFO, String.format("External permissions system found (%s)", ((Permissions) permissionsPlugin).getDescription().getFullName()));
    }
  }

}

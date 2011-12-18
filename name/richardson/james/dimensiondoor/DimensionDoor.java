/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * DimensionDoor.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor;

import java.io.IOException;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import name.richardson.james.dimensiondoor.creation.CreateCommand;
import name.richardson.james.dimensiondoor.creation.LoadCommand;
import name.richardson.james.dimensiondoor.creation.RemoveCommand;
import name.richardson.james.dimensiondoor.creation.UnloadCommand;
import name.richardson.james.dimensiondoor.creation.WorldHandler;
import name.richardson.james.dimensiondoor.creation.WorldListener;
import name.richardson.james.dimensiondoor.database.Database;
import name.richardson.james.dimensiondoor.database.WorldRecord;
import name.richardson.james.dimensiondoor.database.WorldRecordHandler;
import name.richardson.james.dimensiondoor.management.BlockListener;
import name.richardson.james.dimensiondoor.management.EntityListener;
import name.richardson.james.dimensiondoor.management.InfoCommand;
import name.richardson.james.dimensiondoor.management.ListCommand;
import name.richardson.james.dimensiondoor.management.ModifyCommand;
import name.richardson.james.dimensiondoor.management.PlayerListener;
import name.richardson.james.dimensiondoor.management.SpawnCommand;
import name.richardson.james.dimensiondoor.management.TeleportCommand;
import name.richardson.james.dimensiondoor.util.Logger;

public class DimensionDoor extends JavaPlugin {

  private static DimensionDoor instance;
  private static final Logger logger = new Logger(DimensionDoor.class);

  private CommandManager cm;
  private PluginDescriptionFile description;
  private PlayerListener playerListener;
  private final WorldListener worldListener;
  private final EntityListener entityListener;
  private final BlockListener blockListener;
  private PluginManager pm;

  public DimensionDoor() {
    DimensionDoor.instance = this;
    worldListener = new WorldListener();
    entityListener = new EntityListener();
    blockListener = new BlockListener();
  }

  public static DimensionDoor getInstance() {
    return instance;
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    return Database.getDatabaseClasses();
  }

  public Permission getPermission(String permission) {
    return this.getServer().getPluginManager().getPermission(permission);
  }

  public void onDisable() {
    logger.info(String.format("%s is disabled!", description.getName()));
  }

  public void onEnable() {
    pm = getServer().getPluginManager();
    description = getDescription();
    cm = new CommandManager();

    try {
      this.loadConfiguration();
      this.setupDatabase();
      new Database(this);
      this.registerListeners();
      // load the worlds
      this.registerMainWorlds();
      this.registerAuxiliaryWorlds();
      logger.info(String.format("%d worlds loaded and configured.", this.getServer().getWorlds().size()));
      this.registerCommands();
    } catch (IOException exception) {
      logger.severe("Unable to load configuration!");
      exception.printStackTrace();
    } finally {
      if (!pm.isPluginEnabled(this)) return;
    }

    logger.info(String.format("%s is enabled.", description.getFullName()));
  }

  private void loadConfiguration() throws IOException {
    DimensionDoorConfiguration configuration = new DimensionDoorConfiguration();
    if (configuration.isDebugging()) {
      Logger.enableDebugging();
      configuration.logValues();
    }
  }

  private void registerAuxiliaryWorlds() {
    for (WorldRecord record : WorldRecordHandler.getWorldRecordList()) {
      try {
        WorldHandler.loadWorld(record);
      } catch (IllegalArgumentException exception) {
        logger.warning(String.format("Unable to load %s: %s", record.getName(), exception.getMessage()));
      }
    }
  }

  private void registerCommands() {
    getCommand("dd").setExecutor(cm);
    cm.registerCommand("create", new CreateCommand());
    cm.registerCommand("info", new InfoCommand());
    cm.registerCommand("list", new ListCommand());
    cm.registerCommand("load", new LoadCommand());
    cm.registerCommand("modify", new ModifyCommand());
    cm.registerCommand("remove", new RemoveCommand());
    cm.registerCommand("spawn", new SpawnCommand());
    cm.registerCommand("teleport", new TeleportCommand());
    cm.registerCommand("unload", new UnloadCommand());
  }

  private void registerListeners() {
    playerListener = new PlayerListener();
    pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.WORLD_UNLOAD, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.WORLD_INIT, worldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.High, this);
    pm.registerEvent(Event.Type.PLAYER_CHANGED_WORLD, playerListener, Event.Priority.High, this);
    if (DimensionDoorConfiguration.getInstance().isPreventContainerBlocks()) {
      pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
    }
    if (DimensionDoorConfiguration.getInstance().isPreventItemSpawning()) {
      pm.registerEvent(Event.Type.ITEM_SPAWN, entityListener, Event.Priority.High, this);
    }
  }

  private void registerMainWorlds() {
    for (World world : this.getServer().getWorlds()) {
      if (!WorldRecordHandler.isWorldManaged(world)) {
        WorldRecordHandler.createWorldRecord(world);
      }
      WorldHandler.applyWorldAttributes(world);
    }
  }

  private void setupDatabase() {
    try {
      this.getDatabase().find(WorldRecord.class).findRowCount();
    } catch (final PersistenceException ex) {
      logger.warning("No database schema found. Generating a new one.");
      this.installDDL();
    }
  }

}

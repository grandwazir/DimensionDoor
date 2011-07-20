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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.dimensiondoor.listeners.DimensionDoorPlayerListener;
import name.richardson.james.dimensiondoor.listeners.DimensionDoorWorldListener;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.World;
import org.bukkit.command.Command;
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
  static final List<String> commands = Arrays.asList("create", "teleport", "unload", "remove", "modify", "info", "list", "load", "spawn");
  static PermissionHandler CurrentPermissions = null;
  static Logger logger = Logger.getLogger("Minecraft");
  private final DimensionDoorPlayerListener PlayerListener = new DimensionDoorPlayerListener(this);

  // Listeners
  private final DimensionDoorWorldListener WorldListener = new DimensionDoorWorldListener(this);
  PluginDescriptionFile info = null;

  public DimensionDoor() {
    DimensionDoor.instance = this;
  }

  public static void log(final Level level, final String msg) {
    logger.log(level, "[" + instance.getName() + "]" + msg);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(WorldRecord.class);
    return list;
  }

  public String getName() {
    return info.getName();
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

  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
    return true;
  }

  // Commands

  public void onDisable() {
    log(Level.INFO, String.format("[DimensionDoor] %s is disabled!", info.getName()));
  }

  public void onEnable() {
    info = getDescription();
    final DimensionDoor plugin = this;
    WorldRecord.setPlugin(plugin);
    WorldRecord.setDefaultAttributes();
    log(Level.INFO, String.format("[DimensionDoor] %s is enabled!", info.getFullName()));

    // setup environment
    setupDatabase();
    setupPermissions();

    // register events
    final PluginManager pm = getServer().getPluginManager();
    pm.registerEvent(Event.Type.WORLD_LOAD, WorldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.WORLD_INIT, WorldListener, Event.Priority.Monitor, this);
    pm.registerEvent(Event.Type.PLAYER_RESPAWN, PlayerListener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.PLAYER_CHAT, PlayerListener, Event.Priority.Highest, this);

    // register existing worlds
    for (final World world : plugin.getServer().getWorlds()) {
      if (WorldRecord.isManaged(world.getName())) {
        WorldRecord.find(world.getName()).applyAttributes();
      } else {
        log(Level.WARNING, String.format("[DimensionDoor] - No configuration found for %s", world.getName()));
        WorldRecord.manageWorld(world);
        WorldRecord.find(world.getName()).applyAttributes();
      }
    }

    // load managed worlds if they are not already loaded
    for (final WorldRecord world : WorldRecord.findAll())
      if (!world.isLoaded())
        world.loadWorld();

    log(Level.INFO, String.format("[DimensionDoor] %d worlds configured!", plugin.getServer().getWorlds().size()));
  }

  private Player getPlayerFromName(final String playerName) {
    final List<Player> possiblePlayers = getServer().matchPlayer(playerName);
    return possiblePlayers.get(0);
  }

  /*  
  private boolean playerHasPermission(final CommandSender sender, final String node) {
    final String playerName = this.getName(sender);
    if (CurrentPermissions != null) {
      // skip the check if the user is the console
      if (playerName.equals("console"))
        return true;
      if (CurrentPermissions.has(getPlayerFromName(playerName), node))
        return true;
    } else if (sender.isOp()) { return true; }
    sender.sendMessage(ChatColor.RED + " You do not have permission to do that.");
    return false;
  }
  */

  // Utilities

  private void setupDatabase() {
    try {
      getDatabase().find(WorldRecord.class).findRowCount();
      getDatabase().find(WorldRecord.class).findList();
    } catch (final PersistenceException ex) {
      if (ex.getMessage().contains("isolated_chat")) {
        log(Level.WARNING, "[DimensionDoor] - Database schema out of date!");
        log(Level.INFO, "[DimensionDoor] -- Updating to version 1.3.0");
        getDatabase().createSqlUpdate("ALTER TABLE dd_worlds ADD isolated_chat tinyint(1) not null DEFAULT 0").execute();
      } else {
        log(Level.WARNING, "[DimensionDoor] - No database found, creating table.");
        installDDL();
      }
    }
  }

  private void setupPermissions() {
    // if we have already hooked permissions don't do it again
    if (CurrentPermissions != null)
      return;
    // attempt to hook the plugin
    final Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
    if (permissionsPlugin != null) {
      CurrentPermissions = ((Permissions) permissionsPlugin).getHandler();
      log(Level.INFO, String.format("[DimensionDoor] - Permissions found (%s)", ((Permissions) permissionsPlugin).getDescription().getFullName()));
    } else {
      log(Level.INFO, "[DimensionDoor] - Permission system not detected, defaulting to OP");
    }
  }

}

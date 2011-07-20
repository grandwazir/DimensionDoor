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

package name.richardson.james.dimensiondoor.persistent;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import name.richardson.james.dimensiondoor.DimensionDoor;

import org.bukkit.World;
import org.bukkit.World.Environment;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "dd_worlds")
public class WorldRecord {

  public static HashMap<String, Boolean> chatAttributes = new HashMap<String, Boolean>();
  public static HashMap<String, Boolean> defaultAttributes = new HashMap<String, Boolean>();
  private final static Logger log = Logger.getLogger("Minecraft");
  private static DimensionDoor plugin;

  @NotNull
  private World.Environment environment;

  @NotNull
  private boolean isolatedChat;

  @Id
  private String name;

  @NotNull
  private boolean pvp;

  @NotNull
  private boolean spawnAnimals;

  @NotNull
  private boolean spawnMonsters;

  static public void createWorld(final String worldName, final String environment, final Long worldSeed, final HashMap<String, Boolean> attributes) {
    final WorldRecord createdWorld = new WorldRecord();
    final World.Environment environmentType = Enum.valueOf(World.Environment.class, environment.toUpperCase());
    final HashMap<String, Boolean> combinedAttributes = mergeDefaultAttributes(attributes);
    createdWorld.setName(worldName);
    createdWorld.setEnvironment(environmentType);
    createdWorld.setAttributes(combinedAttributes);
    log.info(String.format("[DimensionDoor] Creating new world '%s' (%s)", worldName, environmentType.name()));
    if (worldSeed == null) {
      plugin.getServer().createWorld(worldName, environmentType);
    } else {
      plugin.getServer().createWorld(worldName, environmentType, worldSeed);
    }
  }

  static public WorldRecord find(final String worldName) {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).where().ieq("name", worldName).findList();
    return worlds.get(0);
  }

  static public WorldRecord find(final World world) {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).where().ieq("name", world.getName()).findList();
    return worlds.get(0);
  }

  static public List<WorldRecord> findAll() {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).findList();
    return worlds;
  }

  static public HashMap<String, Boolean> getAttributes(final World world) {
    final HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    attributes.put("pvp", world.getPVP());
    attributes.put("spawnMonsters", world.getAllowMonsters());
    attributes.put("spawnAnimals", world.getAllowAnimals());
    return attributes;
  }

  static public World getMainWorld() {
    return plugin.getServer().getWorlds().get(0);
  }

  static public World getWorld(final String worldName) {
    return plugin.getServer().getWorld(worldName.toLowerCase());
  }

  static public boolean isEnvironmentValid(final String environment) {
    for (final Environment type : World.Environment.values())
      if (type.name().equalsIgnoreCase(environment))
        return true;
    return false;
  }

  public static boolean isLoaded(final String worldName) {
    if (plugin.getServer().getWorld(worldName.toLowerCase()) != null)
      return true;
    return false;
  }

  static public boolean isManaged(final String worldName) {
    if (plugin.getDatabase().find(WorldRecord.class).where().ieq("name", worldName).findRowCount() == 1)
      return true;
    return false;
  }

  static public boolean isManaged(final World world) {
    if (plugin.getDatabase().find(WorldRecord.class).where().ieq("name", world.getName()).findRowCount() == 1)
      return true;
    return false;
  }

  static public void manageWorld(final String worldName) {
    final WorldRecord managedWorld = new WorldRecord();
    final World world = getWorld(worldName);
    final HashMap<String, Boolean> attributes = getAttributes(world);
    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setIsolatedChat(false);
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
    log.info(String.format("[DimensionDoor] - Creating default configuation: %s", managedWorld.getName()));
  }

  static public void manageWorld(final World world) {
    final WorldRecord managedWorld = new WorldRecord();
    final HashMap<String, Boolean> attributes = getAttributes(world);
    attributes.put("isolatedChat", false);
    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
    log.info(String.format("[DimensionDoor] - Creating default configuation: %s", managedWorld.getName()));
  }

  public static void setDefaultAttributes() {
    final boolean pvp = plugin.getServer().getWorlds().get(0).getPVP();
    final boolean allowAnimals = plugin.getServer().getWorlds().get(0).getAllowAnimals();
    final boolean allowMonsters = plugin.getServer().getWorlds().get(0).getAllowMonsters();
    defaultAttributes.put("pvp", pvp);
    defaultAttributes.put("spawnAnimals", allowAnimals);
    defaultAttributes.put("spawnMonsters", allowMonsters);
    defaultAttributes.put("isolatedChat", false);
  }

  static public void setPlugin(final DimensionDoor plugin) {
    WorldRecord.plugin = plugin;
  }

  static private HashMap<String, Boolean> mergeDefaultAttributes(final HashMap<String, Boolean> attributes) {
    final Set<String> keys = attributes.keySet();
    for (final String key : keys) {
      if (!attributes.containsKey(key))
        attributes.put(key, defaultAttributes.get(key));
    }
    return attributes;
  }

  public void applyAttributes() {
    final World world = plugin.getServer().getWorld(name);
    world.setPVP(pvp);
    world.setSpawnFlags(spawnMonsters, spawnAnimals);
    chatAttributes.put(world.getName(), isIsolatedChat());
    log.info(String.format("[DimensionDoor] - Applying configuration for %s", world.getName()));
  }

  public HashMap<String, Boolean> getAttributes() {
    final HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    attributes.put("pvp", isPvp());
    attributes.put("spawnMonsters", isSpawnMonsters());
    attributes.put("spawnAnimals", isSpawnAnimals());
    attributes.put("isolatedChat", isIsolatedChat());
    return attributes;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public String getName() {
    return name;
  }

  public boolean isIsolatedChat() {
    return isolatedChat;
  }

  public boolean isLoaded() {
    if (plugin.getServer().getWorld(getName()) != null)
      return true;
    return false;
  }

  public boolean isPvp() {
    return pvp;
  }

  public boolean isSpawnAnimals() {
    return spawnAnimals;
  }

  public boolean isSpawnMonsters() {
    return spawnMonsters;
  }

  public void loadWorld() {
    if (!isLoaded(getName())) {
      plugin.getServer().createWorld(getName(), getEnvironment());
    } else {
      log.warning(String.format("[DimensionDoor] Attempted to load %s but it was loaded", getName()));
    }
  }

  public void removeWorld() {
    if (isLoaded(getName()))
      unloadWorld();
    plugin.getDatabase().delete(this);
  }

  public void setAttributes(final HashMap<String, Boolean> attributes) {
    setPvp(attributes.get("pvp"));
    setSpawnMonsters(attributes.get("spawnMonsters"));
    setSpawnAnimals(attributes.get("spawnAnimals"));
    setIsolatedChat(attributes.get("isolatedChat"));
    plugin.getDatabase().save(this);
  }

  public void setEnvironment(final World.Environment environment) {
    this.environment = environment;
  }

  public void setIsolatedChat(final boolean isolatedChat) {
    this.isolatedChat = isolatedChat;
  }

  public void setName(final String name) {
    this.name = name.toLowerCase();
  }

  public void setPvp(final boolean pvp) {
    this.pvp = pvp;
  }

  public void setSpawnAnimals(final boolean spawnAnimals) {
    this.spawnAnimals = spawnAnimals;
  }

  public void setSpawnMonsters(final boolean spawnMonsters) {
    this.spawnMonsters = spawnMonsters;
  }

  public void unloadWorld() {
    if (isLoaded(getName())) {
      plugin.getServer().unloadWorld(getName(), true);
    } else {
      log.warning(String.format("[DimensionDoor] Attempted to unload %s but it was not loaded", getName()));
    }
  }
}

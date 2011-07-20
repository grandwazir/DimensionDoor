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

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.World;
import org.bukkit.World.Environment;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.dimensiondoor.DimensionDoorPlugin;

@Entity()
@Table(name = "dd_worlds")
public class DimensionDoorWorld {

  public static HashMap<String, Boolean> defaultAttributes = new HashMap<String, Boolean>();
  public static HashMap<String, Boolean> chatAttributes = new HashMap<String, Boolean>();
  private static DimensionDoorPlugin plugin;
  private final static Logger log = Logger.getLogger("Minecraft");

  @Id
  private String name;

  @NotNull
  private World.Environment environment;

  @NotNull
  private boolean pvp;

  @NotNull
  private boolean spawnAnimals;

  @NotNull
  private boolean spawnMonsters;

  @NotNull
  private boolean isolatedChat;

  static public void createWorld(String worldName, String environment, Long worldSeed, HashMap<String, Boolean> attributes) {
    DimensionDoorWorld createdWorld = new DimensionDoorWorld();
    World.Environment environmentType = Enum.valueOf(World.Environment.class, environment.toUpperCase());
    HashMap<String, Boolean> combinedAttributes = mergeDefaultAttributes(attributes);
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

  static public DimensionDoorWorld find(String worldName) {
    List<DimensionDoorWorld> worlds = plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", worldName).findList();
    return worlds.get(0);
  }

  static public DimensionDoorWorld find(World world) {
    List<DimensionDoorWorld> worlds = plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", world.getName()).findList();
    return worlds.get(0);
  }

  static public List<DimensionDoorWorld> findAll() {
    List<DimensionDoorWorld> worlds = plugin.getDatabase().find(DimensionDoorWorld.class).findList();
    return worlds;
  }

  static public HashMap<String, Boolean> getAttributes(World world) {
    HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    attributes.put("pvp", world.getPVP());
    attributes.put("spawnMonsters", world.getAllowMonsters());
    attributes.put("spawnAnimals", world.getAllowAnimals());
    return attributes;
  }

  static public World getMainWorld() {
    return plugin.getServer().getWorlds().get(0);
  }

  static public World getWorld(String worldName) {
    return plugin.getServer().getWorld(worldName.toLowerCase());
  }

  static public boolean isEnvironmentValid(String environment) {
    for (Environment type : World.Environment.values())
      if (type.name().equalsIgnoreCase(environment))
        return true;
    return false;
  }

  static public boolean isManaged(String worldName) {
    if (plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", worldName).findRowCount() == 1)
      return true;
    return false;
  }

  static public boolean isManaged(World world) {
    if (plugin.getDatabase().find(DimensionDoorWorld.class).where().ieq("name", world.getName()).findRowCount() == 1)
      return true;
    return false;
  }

  static public void manageWorld(String worldName) {
    DimensionDoorWorld managedWorld = new DimensionDoorWorld();
    World world = getWorld(worldName);
    HashMap<String, Boolean> attributes = getAttributes(world);
    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setIsolatedChat(false);
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
    log.info(String.format("[DimensionDoor] - Creating default configuation: %s", managedWorld.getName()));
  }

  static public void manageWorld(World world) {
    DimensionDoorWorld managedWorld = new DimensionDoorWorld();
    HashMap<String, Boolean> attributes = getAttributes(world);
    attributes.put("isolatedChat", false);
    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
    log.info(String.format("[DimensionDoor] - Creating default configuation: %s", managedWorld.getName()));
  }

  public static void setDefaultAttributes() {
    boolean pvp = plugin.getServer().getWorlds().get(0).getPVP();
    boolean allowAnimals = plugin.getServer().getWorlds().get(0).getAllowAnimals();
    boolean allowMonsters = plugin.getServer().getWorlds().get(0).getAllowMonsters();
    defaultAttributes.put("pvp", pvp);
    defaultAttributes.put("spawnAnimals", allowAnimals);
    defaultAttributes.put("spawnMonsters", allowMonsters);
    defaultAttributes.put("isolatedChat", false);
  }

  static public void setPlugin(DimensionDoorPlugin plugin) {
    DimensionDoorWorld.plugin = plugin;
  }

  static private HashMap<String, Boolean> mergeDefaultAttributes(HashMap<String, Boolean> attributes) {
    Set<String> keys = attributes.keySet();
    for (String key : keys) {
      if (!attributes.containsKey(key))
        attributes.put(key, defaultAttributes.get(key));
    }
    return attributes;
  }

  static boolean isLoaded(String worldName) {
    if (plugin.getServer().getWorld(worldName.toLowerCase()) != null)
      return true;
    return false;
  }

  public void applyAttributes() {
    World world = plugin.getServer().getWorld(name);
    world.setPVP(pvp);
    world.setSpawnFlags(spawnMonsters, spawnAnimals);
    chatAttributes.put(world.getName(), isIsolatedChat());
    log.info(String.format("[DimensionDoor] - Applying configuration for %s", world.getName()));
  }

  public HashMap<String, Boolean> getAttributes() {
    HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    attributes.put("pvp", this.isPvp());
    attributes.put("spawnMonsters", this.isSpawnMonsters());
    attributes.put("spawnAnimals", this.isSpawnAnimals());
    attributes.put("isolatedChat", this.isIsolatedChat());
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
    if (!isLoaded(this.getName())) {
      plugin.getServer().createWorld(this.getName(), this.getEnvironment());
    } else {
      log.warning(String.format("[DimensionDoor] Attempted to load %s but it was loaded", this.getName()));
    }
  }

  public void removeWorld() {
    if (isLoaded(this.getName()))
      unloadWorld();
    plugin.getDatabase().delete(this);
  }

  public void setAttributes(HashMap<String, Boolean> attributes) {
    this.setPvp(attributes.get("pvp"));
    this.setSpawnMonsters(attributes.get("spawnMonsters"));
    this.setSpawnAnimals(attributes.get("spawnAnimals"));
    this.setIsolatedChat(attributes.get("isolatedChat"));
    plugin.getDatabase().save(this);
  }

  public void setEnvironment(World.Environment environment) {
    this.environment = environment;
  }

  public void setIsolatedChat(boolean isolatedChat) {
    this.isolatedChat = isolatedChat;
  }

  public void setName(String name) {
    this.name = name.toLowerCase();
  }

  public void setPvp(boolean pvp) {
    this.pvp = pvp;
  }

  public void setSpawnAnimals(boolean spawnAnimals) {
    this.spawnAnimals = spawnAnimals;
  }

  public void setSpawnMonsters(boolean spawnMonsters) {
    this.spawnMonsters = spawnMonsters;
  }

  public void unloadWorld() {
    if (isLoaded(this.getName())) {
      plugin.getServer().unloadWorld(this.getName(), true);
    } else {
      log.warning(String.format("[DimensionDoor] Attempted to unload %s but it was not loaded", this.getName()));
    }
  }

  boolean isLoaded() {
    if (plugin.getServer().getWorld(this.getName()) != null)
      return true;
    return false;
  }
}

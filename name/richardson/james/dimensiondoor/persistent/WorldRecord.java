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

  static public int count(String worldName) {
    return plugin.getDatabase().find(WorldRecord.class).where().ieq("name", worldName.toLowerCase()).findRowCount();
  }

  static public int count(final World world) {
    return plugin.getDatabase().find(WorldRecord.class).where().ieq("name", world.getName().toLowerCase()).findRowCount();
  }

  static public void create(String worldName) {
    final WorldRecord managedWorld = new WorldRecord();
    final World world = plugin.getWorld(worldName);
    final HashMap<String, Boolean> attributes = plugin.getDefaultAttributes();

    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setIsolatedChat(false);
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
  }

  static public void create(World world) {
    final WorldRecord managedWorld = new WorldRecord();
    final HashMap<String, Boolean> attributes = plugin.getWorldAttributes(world);
    attributes.put("isolatedChat", false);

    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setIsolatedChat(false);
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
  }

  static public List<WorldRecord> findAll() {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).findList();
    return worlds;
  }

  static public WorldRecord findFirst(final String worldName) {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).where().ieq("name", worldName).findList();
    return worlds.get(0);
  }

  static public WorldRecord findFirst(final World world) {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).where().ieq("name", world.getName()).findList();
    return worlds.get(0);
  }

  static public boolean isEnvironmentValid(final String environment) {
    for (final Environment type : World.Environment.values())
      if (type.name().equalsIgnoreCase(environment))
        return true;
    return false;
  }

  public static void setup(DimensionDoor plugin) {
    WorldRecord.plugin = DimensionDoor.getInstance();
  }

  public void applyAttributes() {
    final World world = plugin.getServer().getWorld(name);
    world.setPVP(pvp);
    world.setSpawnFlags(spawnMonsters, spawnAnimals);
    chatAttributes.put(world.getName(), isIsolatedChat());
  }

  public void delete() {
    plugin.getDatabase().delete(this);
  }

  public HashMap<String, Boolean> getAttributes() {
    final HashMap<String, Boolean> m = new HashMap<String, Boolean>();
    m.put("pvp", isPvp());
    m.put("spawnMonsters", isSpawnMonsters());
    m.put("spawnAnimals", isSpawnAnimals());
    m.put("isolatedChat", isIsolatedChat());
    return m;
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

}

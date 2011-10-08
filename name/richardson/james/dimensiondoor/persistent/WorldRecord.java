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
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.World.Environment;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "dd_worlds")
public class WorldRecord {

  private static DimensionDoor plugin;

  @NotNull
  private World.Environment environment;

  private String generatorID;

  private String generatorPlugin;

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
  
  @NotNull
  private GameMode gamemode;
  
  @NotNull
  private Difficulty difficulty;

  static public int count(final String worldName) {
    return plugin.getDatabase().find(WorldRecord.class).where().ieq("name", worldName.toLowerCase()).findRowCount();
  }

  static public int count(final World world) {
    return plugin.getDatabase().find(WorldRecord.class).where().ieq("name", world.getName().toLowerCase()).findRowCount();
  }

  static public void create(final String worldName) throws WorldIsNotLoadedException {
    final WorldRecord managedWorld = new WorldRecord();
    final World world = plugin.getWorld(worldName);
    final HashMap<String, Boolean> attributes = plugin.getDefaultAttributes();

    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setIsolatedChat(false);
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
    managedWorld.setDifficulty(world.getDifficulty());
  }

  static public void create(final World world) {
    final WorldRecord managedWorld = new WorldRecord();
    final HashMap<String, Boolean> attributes = plugin.getWorldAttributes(world);
    attributes.put("isolatedChat", false);

    managedWorld.setEnvironment(world.getEnvironment());
    managedWorld.setIsolatedChat(false);
    managedWorld.setName(world.getName());
    managedWorld.setAttributes(attributes);
    managedWorld.setDifficulty(world.getDifficulty());
  }

  static public List<WorldRecord> findAll() {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).findList();
    return worlds;
  }

  static public WorldRecord findFirst(final String worldName) throws WorldIsNotManagedException {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).where().ieq("name", worldName).findList();
    if (worlds.isEmpty())
      throw new WorldIsNotManagedException();
    return worlds.get(0);
  }

  static public WorldRecord findFirst(final World world) throws WorldIsNotManagedException {
    final List<WorldRecord> worlds = plugin.getDatabase().find(WorldRecord.class).where().ieq("name", world.getName()).findList();
    if (worlds.isEmpty())
      throw new WorldIsNotManagedException();
    return worlds.get(0);
  }

  static public boolean isEnvironmentValid(final String environment) {
    for (final Environment type : World.Environment.values())
      if (type.name().equalsIgnoreCase(environment))
        return true;
    return false;
  }

  public static void setup(final DimensionDoor plugin) {
    WorldRecord.plugin = DimensionDoor.getInstance();
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

  public HashMap<String, String> getGeneratorAttributes() {
    final HashMap<String, String> m = new HashMap<String, String>();
    m.put("generatorPlugin", getGeneratorPlugin());
    m.put("generatorID", getGeneratorID());
    return m;
  }

  public String getGeneratorID() {
    return generatorID;
  }

  public String getGeneratorPlugin() {
    return generatorPlugin;
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

  public void setGamemode(GameMode gamemode) {
    this.gamemode = gamemode;
  }

  public void save() {
    plugin.getDatabase().save(this);
  }
  
  public GameMode getGamemode() {
    return gamemode;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setGeneratorAttributes(final HashMap<String, String> attributes) {
    setGeneratorPlugin(attributes.get("generatorPlugin"));
    setGeneratorID(attributes.get("generatorID"));
    plugin.getDatabase().save(this);
  }

  public void setGeneratorID(final String generatorID) {
    this.generatorID = generatorID;
  }

  public void setGeneratorPlugin(final String generatorPlugin) {
    this.generatorPlugin = generatorPlugin;
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
  
  public String toString() {
    return "Name: " + getName() + "Attributes: " + getAttributes().toString() + " / " + getGeneratorAttributes().toString();
    
  }

}

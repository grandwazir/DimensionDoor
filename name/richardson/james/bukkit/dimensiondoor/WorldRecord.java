/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * WorldRecord.java is part of DimensionDoor.
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

import name.richardson.james.bukkit.util.Logger;

@Entity()
@Table(name = "dd_worlds")
public class WorldRecord {

  public enum Attribute {
    PVP,
    SPAWN_MONSTERS,
    SPAWN_ANIMALS,
    ISOLATED_CHAT,
    GAME_MODE,
    DIFFICULTY
  }
  
  private final static Logger logger = new Logger(WorldRecord.class);

  @Id
  private String name;

  @NotNull
  private boolean pvp;

  @NotNull
  private boolean spawnAnimals;

  @NotNull
  private boolean spawnMonsters;

  @NotNull
  private long seed;

  @NotNull
  private boolean isolatedChat;

  @NotNull
  private World.Environment environment;

  private String generatorID;

  private String generatorPlugin;

  @NotNull
  private Difficulty difficulty;

  @NotNull
  private GameMode gamemode;

  public static WorldRecord findByName(DatabaseHandler database, String worldName) {
    return database.getEbeanServer().find(WorldRecord.class).where().ieq("name", worldName).findUnique();
  }

  public static WorldRecord findByWorld(DatabaseHandler database, World world) {
    return database.getEbeanServer().find(WorldRecord.class).where().ieq("name", world.getName()).findUnique();
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public World.Environment getEnvironment() {
    return environment;
  }

  public GameMode getGamemode() {
    return gamemode;
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

  public long getSeed() {
    return seed;
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

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public void setEnvironment(World.Environment environment) {
    this.environment = environment;
  }

  public void setGamemode(GameMode gamemode) {
    this.gamemode = gamemode;
  }

  public void setGeneratorID(String generatorID) {
    this.generatorID = generatorID;
  }

  public void setGeneratorPlugin(String generatorPlugin) {
    this.generatorPlugin = generatorPlugin;
  }

  public void setIsolatedChat(boolean isolatedChat) {
    this.isolatedChat = isolatedChat;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPvp(boolean pvp) {
    this.pvp = pvp;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public void setSpawnAnimals(boolean spawnAnimals) {
    this.spawnAnimals = spawnAnimals;
  }

  public void setSpawnMonsters(boolean spawnMonsters) {
    this.spawnMonsters = spawnMonsters;
  }

  @Override
  public String toString() {
    StringBuilder message = new StringBuilder();
    message.append(this.getClass().getSimpleName() + ": [");
    message.append("name: " + this.name);
    if (environment != null) message.append(", environment: " + this.environment.toString());
    if (gamemode != null) message.append(", gamemode: " + this.gamemode.toString());
    if (difficulty != null) message.append(", difficulty: " + this.difficulty.toString());
    message.append(", seed: " + Long.toString(this.seed));
    message.append(", spawn-animals: " + Boolean.toString(this.spawnAnimals));
    message.append(", spawn-monsters: " + Boolean.toString(this.spawnAnimals));
    message.append(", isolatedChat: " + Boolean.toString(this.isolatedChat));
    message.append(", generator-plugin: " + this.generatorPlugin);
    message.append(", generator-id: " + this.generatorID);
    message.append("].");
    return message.toString();
  }

}

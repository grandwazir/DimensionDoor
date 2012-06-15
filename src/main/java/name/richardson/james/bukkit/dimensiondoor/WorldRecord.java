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

import name.richardson.james.bukkit.utilities.internals.Logger;

@Entity()
@Table(name = "dimensiondoor_worlds")
public class WorldRecord {

  public enum Attribute {
    PVP,
    SPAWN_MONSTERS,
    SPAWN_ANIMALS,
    ISOLATED_CHAT,
    GAME_MODE,
    DIFFICULTY,
    SPAWN_IN_MEMORY
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
  private boolean keepSpawnInMemory;

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

  public static WorldRecord findByName(final DatabaseHandler database, final String worldName) {
    logger.debug(String.format("Attempting to return WorldRecord matching the name %s.", worldName));
    return database.getEbeanServer().find(WorldRecord.class).where().ieq("name", worldName).findUnique();
  }

  public static WorldRecord findByWorld(final DatabaseHandler database, final World world) {
    logger.debug(String.format("Attempting to return WorldRecord matching the name %s.", world.getName()));
    return database.getEbeanServer().find(WorldRecord.class).where().ieq("name", world.getName()).findUnique();
  }

  public Difficulty getDifficulty() {
    return this.difficulty;
  }

  public World.Environment getEnvironment() {
    return this.environment;
  }

  public GameMode getGamemode() {
    return this.gamemode;
  }

  public String getGeneratorID() {
    return this.generatorID;
  }

  public String getGeneratorPlugin() {
    return this.generatorPlugin;
  }

  public String getName() {
    return this.name;
  }

  public long getSeed() {
    return this.seed;
  }

  public boolean isIsolatedChat() {
    return this.isolatedChat;
  }

  public boolean isPvp() {
    return this.pvp;
  }

  public boolean isSpawnAnimals() {
    return this.spawnAnimals;
  }

  public boolean isSpawnMonsters() {
    return this.spawnMonsters;
  }

  public void setDifficulty(final Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public void setEnvironment(final World.Environment environment) {
    this.environment = environment;
  }

  public void setGamemode(final GameMode gamemode) {
    this.gamemode = gamemode;
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
    this.name = name;
  }

  public void setPvp(final boolean pvp) {
    this.pvp = pvp;
  }

  public void setSeed(final long seed) {
    this.seed = seed;
  }

  public void setSpawnAnimals(final boolean spawnAnimals) {
    this.spawnAnimals = spawnAnimals;
  }

  public void setSpawnMonsters(final boolean spawnMonsters) {
    this.spawnMonsters = spawnMonsters;
  }

  @Override
  public String toString() {
    final StringBuilder message = new StringBuilder();
    message.append(this.getClass().getSimpleName() + ": [");
    message.append("name: " + this.name);
    if (this.environment != null) {
      message.append(", environment: " + this.environment.toString());
    }
    if (this.gamemode != null) {
      message.append(", gamemode: " + this.gamemode.toString());
    }
    if (this.difficulty != null) {
      message.append(", difficulty: " + this.difficulty.toString());
    }
    message.append(", seed: " + Long.toString(this.seed));
    message.append(", spawn-animals: " + Boolean.toString(this.spawnAnimals));
    message.append(", spawn-monsters: " + Boolean.toString(this.spawnAnimals));
    message.append(", isolatedChat: " + Boolean.toString(this.isolatedChat));
    message.append(", spawn-in-memory: " + Boolean.toString(this.keepSpawnInMemory));
    message.append(", generator-plugin: " + this.generatorPlugin);
    message.append(", generator-id: " + this.generatorID);
    message.append("].");
    return message.toString();
  }

  public boolean isKeepSpawnInMemory() {
    return keepSpawnInMemory;
  }

  public void setKeepSpawnInMemory(boolean keepSpawnInMemory) {
    this.keepSpawnInMemory = keepSpawnInMemory;
  }

}


package name.richardson.james.dimensiondoor.database;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

import name.richardson.james.dimensiondoor.creation.WorldHandler;

@Entity()
@Table(name = "dd_worlds")
public class WorldRecord extends Record {

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

  protected static final int count(final String name) {
    Record.logger.debug("Attempting to get row count searching for records with the worldName: " + name);
    return Database.getInstance().find(WorldRecord.class).where().ieq("name", name).findRowCount();
  }

  protected static final List<WorldRecord> find(final String name) {
    Record.logger.debug("Attempting to find records with the worldName: " + name);
    return Database.getInstance().find(WorldRecord.class).where().ieq("name", name).findList();
  }

  protected static List<WorldRecord> list() {
    return Database.getInstance().find(WorldRecord.class).findList();
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

  public void setDefaults() {
    Map<String, Object> defaults = WorldHandler.getDefaults();
    pvp = (Boolean) defaults.get("pvp");
    spawnMonsters = (Boolean) defaults.get("spawn-monsters");
    spawnAnimals = (Boolean) defaults.get("spawn-animals");
    difficulty = (Difficulty) defaults.get("difficulty");
    gamemode = (GameMode) defaults.get("game-mode");
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

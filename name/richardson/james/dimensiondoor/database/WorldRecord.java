package name.richardson.james.dimensiondoor.database;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import name.richardson.james.dimensiondoor.WorldHandler;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "dd_worlds")
public class WorldRecord extends Record {

  protected final static WorldHandler handler = new WorldHandler(WorldRecord.class);
  
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
  
  protected static List<WorldRecord> list() {
    return Database.getInstance().find(WorldRecord.class).findList();
  }
  
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return null;
  }



  public String getName() {
    return name;
  }



  public void setName(String name) {
    this.name = name;
  }



  public boolean isPvp() {
    return pvp;
  }



  public void setPvp(boolean pvp) {
    this.pvp = pvp;
  }



  public boolean isSpawnAnimals() {
    return spawnAnimals;
  }



  public void setSpawnAnimals(boolean spawnAnimals) {
    this.spawnAnimals = spawnAnimals;
  }



  public boolean isSpawnMonsters() {
    return spawnMonsters;
  }



  public void setSpawnMonsters(boolean spawnMonsters) {
    this.spawnMonsters = spawnMonsters;
  }



  public boolean isIsolatedChat() {
    return isolatedChat;
  }



  public void setIsolatedChat(boolean isolatedChat) {
    this.isolatedChat = isolatedChat;
  }



  public World.Environment getEnvironment() {
    return environment;
  }



  public void setEnvironment(World.Environment environment) {
    this.environment = environment;
  }



  public String getGeneratorID() {
    return generatorID;
  }



  public void setGeneratorID(String generatorID) {
    this.generatorID = generatorID;
  }



  public String getGeneratorPlugin() {
    return generatorPlugin;
  }



  public void setGeneratorPlugin(String generatorPlugin) {
    this.generatorPlugin = generatorPlugin;
  }



  public Difficulty getDifficulty() {
    return difficulty;
  }



  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }



  public GameMode getGamemode() {
    return gamemode;
  }



  public void setGamemode(GameMode gamemode) {
    this.gamemode = gamemode;
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public void setDefaults() {
    Map<String, Object> defaults = handler.getDefaults();
    pvp = (Boolean) defaults.get("pvp");
    spawnMonsters = (Boolean) defaults.get("spawn-monsters");
    spawnAnimals = (Boolean) defaults.get("spawn-animals");
    difficulty = (Difficulty) defaults.get("difficulty");
    gamemode = (GameMode) defaults.get("game-mode");
  }

}

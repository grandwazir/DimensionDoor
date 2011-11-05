package name.richardson.james.dimensiondoor.configurations;

import java.io.File;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.UnableToCreateConfigurationException;


public abstract class Configuration extends org.bukkit.util.config.Configuration {

  protected final DimensionDoor plugin;
  protected File file;

  public Configuration(File file, DimensionDoor plugin) throws UnableToCreateConfigurationException {
    super(file);
    this.plugin = plugin;
    this.file = file;
    this.checkExistance();
  }

  abstract void setDefaults(File file) throws UnableToCreateConfigurationException;
  
  protected boolean isEmpty() {
    return this.getAll().isEmpty();
  }

  void checkExistance() throws UnableToCreateConfigurationException {
    this.load();
    if (isEmpty()) {
      DimensionDoor.log(Level.WARNING, this.getClass().getSimpleName() + " not found!");
      DimensionDoor.log(Level.INFO, "Creating new " + this.getClass().getSimpleName() + " with default values: " + file.getPath());
      setDefaults(file);
    } else {
      DimensionDoor.log(Level.INFO, this.getClass().getSimpleName() + " loaded: " + file.getPath());
    }
  }
  
}

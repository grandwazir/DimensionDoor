package name.richardson.james.dimensiondoor.configurations;

import java.io.File;
import java.io.IOException;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.UnableToCreateConfigurationException;

public class InventoryProtectionConfiguration extends Configuration {

  public InventoryProtectionConfiguration(File file, DimensionDoor plugin) throws UnableToCreateConfigurationException {
    super(file, plugin);
  }

  @Override
  void setDefaults(File file) throws name.richardson.james.dimensiondoor.exceptions.UnableToCreateConfigurationException {
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();
      this.setHeader("# These settings only apply to creative worlds.");
      this.getString("inventory-settings", "");
      this.getBoolean("inventory-settings.deleteItemInHandOnReturn", true);
      this.getBoolean("inventory-settings.deleteActionBarOnReturn", true);
      this.getString("world-settings", "");
      this.getBoolean("world-settings.preventItemsSpawning", true);
      this.getBoolean("world-settings.preventContainerBlocks", true);
      this.save();
    } catch (final IOException e) {
      throw new UnableToCreateConfigurationException(file.getPath());
    }
  }
  
  

}

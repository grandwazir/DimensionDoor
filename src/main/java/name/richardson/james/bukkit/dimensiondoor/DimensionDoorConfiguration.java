/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * DimensionDoorConfiguration.java is part of DimensionDoor.
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

import java.io.IOException;

import name.richardson.james.bukkit.utilities.configuration.PluginConfiguration;

public class DimensionDoorConfiguration extends PluginConfiguration {

  public DimensionDoorConfiguration(final DimensionDoor plugin) throws IOException {
    super(plugin);
  }

  public boolean isClearingCreativeInventories() {
    return this.getConfiguration().getBoolean("creative-world-transfer-settings.clear-inventories");
  }
  
  public boolean isVanillaSpawningEnabled() {
    return this.getConfiguration().getBoolean("respawn-in-main-world-on-death");
  }

  public boolean isDebugging() {
    return this.getConfiguration().getBoolean("debugging");
  }

}

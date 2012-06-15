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
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class DimensionDoorConfiguration extends PluginConfiguration {

  public DimensionDoorConfiguration(final SkeletonPlugin plugin) throws IOException {
    super(plugin);
  }

  public boolean isClearActionBar() {
    return this.configuration.getBoolean("creative-world-transfer-settings.clear-action-bar");
  }

  public boolean isClearHand() {
    return this.configuration.getBoolean("creative-world-transfer-settings.clear-hand");
  }

  public boolean isDebugging() {
    return this.configuration.getBoolean("debugging");
  }

  public boolean isPreventContainerBlocks() {
    return this.configuration.getBoolean("creative-world-settings.prevent-container-blocks");
  }

  public boolean isPreventItemSpawning() {
    return this.configuration.getBoolean("creative-world-settings.prevent-item-drops");
  }

}

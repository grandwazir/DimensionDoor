/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * EntityListener.java is part of DimensionDoor.
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

package name.richardson.james.bukkit.dimensiondoor.management;

import org.bukkit.World;
import org.bukkit.event.entity.ItemSpawnEvent;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;

public class EntityListener extends org.bukkit.event.entity.EntityListener {

  private final DimensionDoor plugin;

  public EntityListener(final DimensionDoor plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onItemSpawn(final ItemSpawnEvent event) {
    if (event.isCancelled()) return;
    final World world = event.getEntity().getWorld();
    if (this.plugin.getCreativeWorlds().contains(world)) {
      event.setCancelled(true);
    }
  }

}

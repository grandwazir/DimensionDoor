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

package name.richardson.james.dimensiondoor.management;

import org.bukkit.World;
import org.bukkit.event.entity.ItemSpawnEvent;

public class EntityListener extends org.bukkit.event.entity.EntityListener {

  public void onItemSpawn(final ItemSpawnEvent event) {
    if (event.isCancelled()) return;
    final World world = event.getEntity().getWorld();
    if (WorldHandler.getCreativeWorlds().contains(world)) {
      event.setCancelled(true);
    }
  }

}

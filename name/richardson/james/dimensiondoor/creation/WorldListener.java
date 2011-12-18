/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * DimensionDoorWorldListener.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor.creation;

import org.bukkit.World;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import name.richardson.james.dimensiondoor.database.WorldRecordHandler;

public class WorldListener extends org.bukkit.event.world.WorldListener {

  @Override
  public void onWorldInit(final WorldInitEvent event) {
    World world = event.getWorld();
    if (!WorldRecordHandler.isWorldManaged(world)) {
      WorldRecordHandler.createWorldRecord(world);
    }
  }

  @Override
  public void onWorldLoad(final WorldLoadEvent event) {
    WorldHandler.applyWorldAttributes(event.getWorld());
  }

  public void onWorldUnload(final WorldLoadEvent event) {
    WorldHandler.onWorldUnload(event.getWorld());
  }

}

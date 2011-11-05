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
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.dimensiondoor.listeners;

import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class DimensionDoorWorldListener extends WorldListener {

  private final DimensionDoor plugin;

  public DimensionDoorWorldListener(final DimensionDoor plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onWorldInit(final WorldInitEvent event) {
    if (!plugin.isWorldManaged(event.getWorld())) {
      plugin.registerWorld(event.getWorld());
    }
  }

  @Override
  public void onWorldLoad(final WorldLoadEvent event) {
    WorldRecord world;
    try {
      world = WorldRecord.findFirst(event.getWorld());
      plugin.applyWorldAttributes(world);
    } catch (final WorldIsNotManagedException e) {
      DimensionDoor.log(Level.SEVERE, String.format("A world has been loaded but has not been automatically registered: %s", event.getWorld().getName()));
    }
  }

}

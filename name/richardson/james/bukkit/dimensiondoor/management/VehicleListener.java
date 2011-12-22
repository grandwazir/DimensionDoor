/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * VehicleListener.java is part of DimensionDoor.
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
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;

public class VehicleListener extends org.bukkit.event.vehicle.VehicleListener {

  private final DimensionDoor plugin;

  public VehicleListener(DimensionDoor plugin) {
    this.plugin = plugin;
  }

  public void onVehicleCreate(VehicleCreateEvent event) {
    final World world = event.getVehicle().getLocation().getWorld();
    if (plugin.getCreativeWorlds().contains(world) && event.getVehicle() instanceof StorageMinecart) {
      event.getVehicle().remove();
    }
  }

}

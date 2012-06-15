/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BlockListener.java is part of DimensionDoor.
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

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;

public class ContainerBlockListener implements Listener {

  private final DimensionDoor plugin;

  public ContainerBlockListener(final DimensionDoor plugin) {
    this.plugin = plugin;
  }

  // fixes another duplication area where players can place creative
  // items into their inventory through the chest interface.
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockPlace(final BlockPlaceEvent event) {
    if (event.isCancelled()) return;
    final World world = event.getPlayer().getWorld();
    if (this.plugin.getCreativeWorlds().contains(world)) {
      if (isBlackListed(event.getBlock())) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "You may not use those in creative mode.");
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onVehicleCreate(VehicleCreateEvent event) {
    final World world = event.getVehicle().getLocation().getWorld();
    if (plugin.getCreativeWorlds().contains(world) && event.getVehicle() instanceof StorageMinecart) {
      event.getVehicle().remove();
    }
  }

  private boolean isBlackListed(Block block) {
    switch (block.getState().getType()) {
    case CHEST:
      return true;
    case FURNACE:
      return true;
    case DISPENSER:
      return true;
    case ENCHANTMENT_TABLE:
      return true;
    case BREWING_STAND:
      return true;
    case WORKBENCH:
      return true;
    case STORAGE_MINECART:
      return true;
    }
    return false;
  }

}

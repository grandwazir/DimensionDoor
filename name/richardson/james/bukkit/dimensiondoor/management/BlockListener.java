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
import org.bukkit.block.ContainerBlock;
import org.bukkit.event.block.BlockPlaceEvent;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;

public class BlockListener extends org.bukkit.event.block.BlockListener {

  private final DimensionDoor plugin;

  public BlockListener(final DimensionDoor plugin) {
    this.plugin = plugin;
  }

  // fixes another duplication area where players can place creative
  // items into their inventory through the chest interface.
  @Override
  public void onBlockPlace(final BlockPlaceEvent event) {
    final World world = event.getPlayer().getWorld();
    if (this.plugin.getCreativeWorlds().contains(world)) {
      if (event.getBlockPlaced().getState() instanceof ContainerBlock) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "You may not use those in creative mode.");
      }
    }
  }

}

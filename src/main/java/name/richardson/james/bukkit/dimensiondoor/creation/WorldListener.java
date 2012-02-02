/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * WorldListener.java is part of DimensionDoor.
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

package name.richardson.james.bukkit.dimensiondoor.creation;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import name.richardson.james.bukkit.dimensiondoor.DatabaseHandler;
import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;

public class WorldListener implements Listener {

  private final DimensionDoor plugin;
  private final DatabaseHandler database;

  public WorldListener(final DimensionDoor plugin) {
    this.plugin = plugin;
    this.database = plugin.getDatabaseHandler();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWorldInit(final WorldInitEvent event) {
    final World world = event.getWorld();
    final WorldRecord record = WorldRecord.findByWorld(this.database, world);
    if (record == null) {
      this.plugin.addWorld(world);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWorldLoad(final WorldLoadEvent event) {
    this.plugin.applyWorldAttributes(event.getWorld());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWorldUnload(final WorldLoadEvent event) {
    this.plugin.onWorldUnload(event.getWorld());
  }

}

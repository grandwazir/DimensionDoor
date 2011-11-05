/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ListCommand.java is part of DimensionDoor.
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

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ItemSpawnEvent;

import name.richardson.james.dimensiondoor.persistent.WorldRecord;

public class DimensionDoorEntityListener extends EntityListener {

  public HashMap<String, GameMode> worldGameModes = new HashMap<String, GameMode>();

  public DimensionDoorEntityListener() {}
  
  public void onItemSpawn(final ItemSpawnEvent event) {
    Item item = (Item) event.getEntity();
    GameMode gamemode = worldGameModes.get(item.getWorld().getName());
    if (gamemode == GameMode.CREATIVE) {
      event.setCancelled(true);
    }
  }
  
  public void updateCache() {
    WorldRecord.findAll();
    for (WorldRecord record : WorldRecord.findAll()) {
      worldGameModes.put(record.getName(), record.getGamemode());
    }
  }
  
}

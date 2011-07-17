/* 
Copyright 2011 James Richardson.

This file is part of DimensionDoor.

SimpleStats is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SimpleStats is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
*/

package name.richardson.james.dimensiondoor;

import java.util.logging.Logger;

import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;


public class DimensionDoorWorldListener extends WorldListener {
	
	static Logger log = Logger.getLogger("Minecraft");
	@SuppressWarnings("unused")
	private DimensionDoorPlugin plugin;
	
	public DimensionDoorWorldListener(DimensionDoorPlugin plugin) {
		this.plugin = plugin;
	}
	
	// when a world is initialised, check to see if we know about it
	// causing double checks on first start up for some reason so we
	// will ignore it for now
	public void onWorldInit(WorldInitEvent event) {
		/*
		String worldName = event.getWorld().getName();
		if (DimensionDoorWorld.isManaged(event.getWorld())) return;
		// create new world configuration
		log.warning("[DimensionDoor] - No configuration found for " + worldName);
		DimensionDoorWorld.manageWorld(event.getWorld());
		*/
		return;
	}
	
	public void onWorldLoad(WorldLoadEvent event) {
		if (!DimensionDoorWorld.isManaged(event.getWorld())) {
			log.warning(String.format("[DimensionDoor] - No configuration found for %s", event.getWorld().getName()));
			DimensionDoorWorld.manageWorld(event.getWorld());
		}
		DimensionDoorWorld world = DimensionDoorWorld.find(event.getWorld());
		world.applyAttributes();
	}

}
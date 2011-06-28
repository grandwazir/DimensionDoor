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
	public void onWorldInit(WorldInitEvent event) {
		String worldName = event.getWorld().getName();
		log.info("[DimensionDoor] " + worldName + " is initialising!");
		if (DimensionDoorWorld.isManaged(event.getWorld())) return;
		// create new world configuration
		log.warning("[DimensionDoor] - No configuration found for " + worldName);
		DimensionDoorWorld.manageWorld(event.getWorld());
	}

	public void onWorldLoad(WorldLoadEvent event) {
		DimensionDoorWorld.find(event.getWorld()).applyAttributes();
	}

}
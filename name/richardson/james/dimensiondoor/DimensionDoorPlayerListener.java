package name.richardson.james.dimensiondoor;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;



public class DimensionDoorPlayerListener extends PlayerListener {
	
	static Logger log = Logger.getLogger("Minecraft");
	private DimensionDoorPlugin plugin;
	
	public DimensionDoorPlayerListener(DimensionDoorPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String currentWorld = player.getWorld().getName();
		String destinationWorld = event.getRespawnLocation().getWorld().getName();
		// if the respawn location is not in the current world, set a new one
		if (!currentWorld.equals(destinationWorld))
			event.setRespawnLocation(plugin.getServer().getWorld(currentWorld).getSpawnLocation());
	}
	
}

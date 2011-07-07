package name.richardson.james.dimensiondoor;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
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
	
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
		World world = event.getPlayer().getWorld();
		if (DimensionDoorWorld.chatAttributes.get(world.getName())) {
			event.setCancelled(true);
			String message = event.getFormat();
			message = message.replace("%1$s", event.getPlayer().getDisplayName());
			message = message.replace("%2$s", event.getMessage());
			for (Player player : world.getPlayers()) 
				player.sendMessage(message);
			// Emulate Minecraft chat logging
			log.info(message);
		}
	}
	
}

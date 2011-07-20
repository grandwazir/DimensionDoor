/* 
Copyright 2011 James Richardson.

This file is part of DimensionDoor.

DimensionDoor is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DimensionDoor is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.richardson.james.dimensiondoor.listeners;

import java.util.logging.Logger;

import name.richardson.james.dimensiondoor.DimensionDoorPlugin;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DimensionDoorPlayerListener extends PlayerListener {

  private Logger log = Logger.getLogger("Minecraft");
  private DimensionDoorPlugin plugin;

  public DimensionDoorPlayerListener(DimensionDoorPlugin plugin) {
    this.plugin = plugin;
  }

  public void onPlayerChat(PlayerChatEvent event) {
    if (event.isCancelled())
      return;
    if (!WorldRecord.chatAttributes.containsValue(true))
      return;
    World originWorld = event.getPlayer().getWorld();
    // We send all the messages seperatly to avoid a situation where chat
    // 'leaks'
    // from non isolated worlds into isolated worlds. This can lead to
    // situations
    // where players can see chat messages but not reply.
    event.setCancelled(true);
    String message = event.getFormat();
    message = message.replace("%1$s", event.getPlayer().getDisplayName());
    message = message.replace("%2$s", event.getMessage());
    for (Player player : plugin.getServer().getOnlinePlayers()) {
      World recipentWorld = player.getWorld();
      // if the origin world is isolated send message only to people on the same
      // world
      if (WorldRecord.chatAttributes.get(originWorld.getName()) && originWorld.getName().equalsIgnoreCase(recipentWorld.getName())) {
        player.sendMessage(message);
        // else if the origin world is not isolated send to all players (except
        // the ones on isolated worlds)
      } else if (!WorldRecord.chatAttributes.get(recipentWorld.getName()) && !WorldRecord.chatAttributes.get(recipentWorld.getName())) {
        player.sendMessage(message);
      }
    }
    // Emulate Minecraft chat logging
    log.info(message);
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

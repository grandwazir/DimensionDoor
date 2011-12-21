/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * PlayerListener.java is part of DimensionDoor.
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

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.DimensionDoorConfiguration;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {

  private final Logger logger = Logger.getLogger("Minecraft");
  private final DimensionDoor plugin;
  private final boolean isClearActionBar;
  private final boolean isClearHand;

  public PlayerListener(DimensionDoor plugin) {
    this.plugin = plugin;
    this.isClearActionBar = plugin.getPluginConfiguration().isClearActionBar();
    this.isClearHand = plugin.getPluginConfiguration().isClearHand();
  }

  public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
    final Player player = event.getPlayer();
    final World currentWorld = event.getPlayer().getWorld();
    final WorldRecord currentWorldRecord = WorldRecord.findByWorld(plugin.getDatabaseHandler(), currentWorld);
    final World previousWorld = event.getFrom();

    player.setGameMode(currentWorldRecord.getGamemode());

    if (plugin.getCreativeWorlds().contains(previousWorld)) {
      if (this.isClearActionBar) clearActionBar(player);
      if (this.isClearHand) clearItemInHand(player);
    }
  }

  @Override
  public void onPlayerChat(final PlayerChatEvent event) {
    if (event.isCancelled() || plugin.getIsolatedWorlds().isEmpty()) return;
    final World world = event.getPlayer().getWorld();
    String message = event.getFormat();
    message = message.replace("%1$s", event.getPlayer().getDisplayName());
    message = message.replace("%2$s", event.getMessage());
    if (plugin.getIsolatedWorlds().contains(world)) {
      sendIsolatedMessage(message, world);
    } else {
      sendNormalMessage(message);
    }
    event.setCancelled(true);
  }

  @Override
  public void onPlayerRespawn(final PlayerRespawnEvent event) {
    final Player player = event.getPlayer();
    final World currentWorld = player.getWorld();
    final World destinationWorld = event.getRespawnLocation().getWorld();
    if (!currentWorld.equals(destinationWorld)) {
      event.setRespawnLocation(currentWorld.getSpawnLocation());
    }
  }

  private void clearActionBar(Player player) {
    for (int i = 0; i <= 8; i++) {
      player.getInventory().clear(i);
    }
  }

  private void clearItemInHand(Player player) {
    ItemStack itemInHand = player.getInventory().getItemInHand();
    // only clear the item if they are actually hold the item
    // otherwise we get an exception
    if (!itemInHand.getType().equals(Material.AIR)) {
      itemInHand.setAmount(-1);
      player.getInventory().setItemInHand(itemInHand);
    }
  }

  private void sendIsolatedMessage(final String message, final World world) {
    for (final Player player : plugin.getServer().getOnlinePlayers()) {
      if (player != null && player.getWorld().equals(world) && player.isOnline()) {
        player.sendMessage(message);
      }
    }
    logger.info(message);
  }

  private void sendNormalMessage(final String message) {
    for (final Player player : plugin.getServer().getOnlinePlayers()) {
      if (player != null && (!plugin.getIsolatedWorlds().contains(player.getWorld())) && player.isOnline()) {
        player.sendMessage(message);
      }
    }
    logger.info(message);
  }

}

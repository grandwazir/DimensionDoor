/*******************************************************************************
 * Copyright (c) 2013 James Richardson.
 * 
 * SwitchTexturePackTask.java is part of BukkitUtilities.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DimensionDoor. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.dimensiondoor;

import org.bukkit.entity.Player;

public class SwitchTexturePackTask implements Runnable {
  
  private final Player player;
  private final String texturePack;

  public SwitchTexturePackTask(Player player, String texturePack) {
    this.player = player;
    this.texturePack = texturePack;
  }
  
  public void run() {
    this.player.setTexturePack(texturePack);
  }
  
}

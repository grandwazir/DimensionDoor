package name.richardson.james.bukkit.dimensiondoor;

import org.bukkit.entity.Player;

public class SwitchTexturePackTask implements Runnable {
  
  private final Player player;
  private final String texturePack;

  public SwitchTexturePackTask(DimensionDoor plugin, Player player, String texturePack) {
    this.player = player;
    this.texturePack = texturePack;
  }
  
  public void run() {
    this.player.setTexturePack(texturePack);
  }
  
}

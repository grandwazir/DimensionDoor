package name.richardson.james.bukkit.dimensiondoor.management;

import org.bukkit.World;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;


public class VehicleListener extends org.bukkit.event.vehicle.VehicleListener {

  private final DimensionDoor plugin;

  public VehicleListener(DimensionDoor plugin) {
    this.plugin = plugin;
  }
  
  public void onVehicleCreate(VehicleCreateEvent event) {
    final World world = event.getVehicle().getLocation().getWorld();
    if (plugin.getCreativeWorlds().contains(world) && event.getVehicle() instanceof StorageMinecart) {
      event.getVehicle().remove();
    }
  }
  
}

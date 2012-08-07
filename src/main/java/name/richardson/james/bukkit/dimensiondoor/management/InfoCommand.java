/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * InfoCommand.java is part of DimensionDoor.
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

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.World;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class InfoCommand extends AbstractCommand {

  private final DimensionDoor plugin;

  private String worldName;

  public InfoCommand(final DimensionDoor plugin) {
    super(plugin, false);
    this.plugin = plugin;
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    World world = this.plugin.getWorldManager().getWorld(worldName);
    if (world != null) {
      sender.sendMessage(this.getLocalisation().getMessage(this, "header", world.getName()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "enabled"), world.getEnabled()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "seed"), String.valueOf(world.getSeed())));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "environment"), world.getEnvironment()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "difficulty"), world.getDifficulty()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "gamemode"), world.getGameMode()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "generate_structures"), world.isGeneratingStructures()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "isolated_chat"), world.isChatIsolated()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "pvp"), world.isPVP()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "spawn_animals"), world.isSpawningAnimals()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "spawn_monsters"), world.isSpawningMonsters()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "generator_plugin"), world.getGeneratorPluginName()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "generator_id"), world.getGeneratorID()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "keep_spawn_in_memory"), world.isSpawnKeptInMemory()));
      sender.sendMessage(this.getLocalisation().getMessage(this, "list-item", this.getLocalisation().getMessage(this, "world_type"), world.getWorldType()));  
    } else {
      throw new CommandUsageException(this.getLocalisation().getMessage(DimensionDoor.class, "world-is-not-managed", this.worldName));
    }
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(DimensionDoor.class,"must-specify-a-world-name"), null);
    } else {
      this.worldName = arguments[0];
    }
  }
  
}

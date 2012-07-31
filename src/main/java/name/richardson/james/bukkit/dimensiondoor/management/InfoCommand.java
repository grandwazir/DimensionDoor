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
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class InfoCommand extends PluginCommand {

  private final DimensionDoor plugin;

  private String worldName;

  public InfoCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
    this.registerPermissions();
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    World world = this.plugin.getWorldManager().getWorld(worldName);
    if (world != null) {
      sender.sendMessage(this.getSimpleFormattedMessage("header", world.getName()));
      sender.sendMessage(this.getSimpleFormattedMessage("enabled", world.getEnabled()));
      sender.sendMessage(this.getSimpleFormattedMessage("seed", world.getSeed()));
      sender.sendMessage(this.getSimpleFormattedMessage("environment", world.getEnvironment()));
      sender.sendMessage(this.getSimpleFormattedMessage("difficulty", world.getDifficulty()));
      sender.sendMessage(this.getSimpleFormattedMessage("generate_structures", world.isGeneratingStructures()));
      sender.sendMessage(this.getSimpleFormattedMessage("isolated_chat", world.isChatIsolated()));
      sender.sendMessage(this.getSimpleFormattedMessage("pvp", world.isPVP()));
      sender.sendMessage(this.getSimpleFormattedMessage("spawn_animals", world.isSpawningAnimals()));
      sender.sendMessage(this.getSimpleFormattedMessage("spawn_monsters", world.isSpawningMonsters()));
      sender.sendMessage(this.getSimpleFormattedMessage("generator_plugin", world.getGeneratorPluginName()));
      sender.sendMessage(this.getSimpleFormattedMessage("generator_id", world.getGeneratorID()));
      sender.sendMessage(this.getSimpleFormattedMessage("keep_spawn_in_memory", world.isSpawnKeptInMemory()));
      sender.sendMessage(this.getSimpleFormattedMessage("world_type", world.getWorldType()));   
    } else {
      throw new CommandUsageException(this.getSimpleFormattedMessage("world-is-not-managed", this.worldName));
    }
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("load-world-hint"));
    } else {
      this.worldName = arguments[0];
    }
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }
  
}

/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ModifyCommand.java is part of DimensionDoor.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord;
import name.richardson.james.bukkit.dimensiondoor.WorldRecord.Attribute;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class ModifyCommand extends PluginCommand {

  private final DimensionDoor plugin;
  
  private String worldName;
  private Attribute attribute;
  private String value;

  public ModifyCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  private String getAttributesString() {
    final StringBuilder builder = new StringBuilder();
    for (final WorldRecord.Attribute attribute : WorldRecord.Attribute.values()) {
      builder.append(attribute.toString());
      builder.append(", ");
    }
    builder.delete(builder.length() - 2, builder.length());
    return builder.toString();
  }
  

  public void execute(CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException, name.richardson.james.bukkit.utilities.command.CommandPermissionException, CommandUsageException {
    final WorldRecord record = WorldRecord.findByName(this.plugin.getDatabaseHandler(), worldName);


    switch (attribute) {
      case PVP:
        record.setPvp(Boolean.parseBoolean(value));
        value = Boolean.toString(Boolean.parseBoolean(value));
        break;
      case SPAWN_MONSTERS:
        record.setSpawnMonsters(Boolean.parseBoolean(value));
        value = Boolean.toString(Boolean.parseBoolean(value));
        break;
      case SPAWN_ANIMALS:
        record.setSpawnAnimals(Boolean.parseBoolean(value));
        value = Boolean.toString(Boolean.parseBoolean(value));
        break;
      case ISOLATED_CHAT:
        record.setIsolatedChat(Boolean.parseBoolean(value));
        value = Boolean.toString(Boolean.parseBoolean(value));
        break;
      case SPAWN_IN_MEMORY:
        record.setKeepSpawnInMemory(Boolean.parseBoolean(value));
        value = Boolean.toString(Boolean.parseBoolean(value));
        break;
      case DIFFICULTY:
        try {
          value = value.toUpperCase();
          record.setDifficulty(Difficulty.valueOf(value));
          break;
        } catch (final IllegalArgumentException exception) {
          final StringBuilder guidence = new StringBuilder();
          for (final Difficulty difficulty : Difficulty.values()) {
            guidence.append(difficulty.toString() + ", ");
          }
          guidence.deleteCharAt(guidence.length() - 2);
          throw new CommandArgumentException(this.getMessage("must-specify-valid-difficulty"), this.getSimpleFormattedMessage("difficulty-list", guidence.toString()));
        }
      case GAME_MODE:
        try {
          value = value.toUpperCase();
          record.setGamemode(GameMode.valueOf(value));
          break;
        } catch (final IllegalArgumentException exception) {
          final StringBuilder guidence = new StringBuilder();
          for (final GameMode gameMode : GameMode.values()) {
            guidence.append(gameMode.toString() + ", ");
          }
          guidence.deleteCharAt(guidence.length() - 2);
          throw new CommandArgumentException(this.getMessage("must-specify-valid-game-mode"), this.getSimpleFormattedMessage("game-mode-list", guidence.toString()));
        }
    }

    this.plugin.getDatabaseHandler().save(record);

    if (this.plugin.isWorldLoaded(record.getName())) {
      final World world = this.plugin.getWorld(record.getName());
      this.plugin.applyWorldAttributes(world);
    }

    // this.logger.info(String.format("%s has changed %s to %s for %s", sender.getName(), attribute.toString(), value, record.getName()));
    Object[] arguments = {attribute.toString(), value, record.getName()};
    sender.sendMessage(this.getSimpleFormattedMessage("change-report", arguments));

    
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {
    final LinkedList<String> args = new LinkedList<String>();
    args.addAll(Arrays.asList(arguments));
    
    if (!args.isEmpty()) {
      this.worldName = args.remove(0);
    } else {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("load-world-hint"));
    }
    
    if (!args.isEmpty()) {
      try {
        final String name = args.remove(0).toUpperCase();
        attribute = WorldRecord.Attribute.valueOf(name);
      } catch (final IllegalArgumentException exception) {
        throw new CommandArgumentException(this.getMessage("must-specify-valid-attribute"), this.getSimpleFormattedMessage("attribute-list", this.getAttributesString()));
      } catch (final IndexOutOfBoundsException exception) {
        throw new CommandArgumentException(this.getMessage("must-specify-valid-attribute"), this.getSimpleFormattedMessage("attribute-list", this.getAttributesString()));
      }
    } else {
      throw new CommandArgumentException(this.getMessage("must-specify-valid-value"), this.getSimpleFormattedMessage("value-hint", this.getAttributesString()));
    }
    
    if (!args.isEmpty()) {
      this.value = args.remove(0);
    } else {
      throw new CommandArgumentException("You must specify a value!", "This value varies depending on the attribute.");
    }

    
  }

  /*
  
  private void registerAdditionalPermissions() {
    final Permission wildcard = new Permission(ModifyCommand.PERMISSION.getName() + ".*", "Allow a user to set all attributes.", PermissionDefault.OP);
    this.plugin.addPermission(wildcard, true);
    for (final WorldRecord.Attribute attribute : WorldRecord.Attribute.values()) {
      final String permissionNode = ModifyCommand.PERMISSION.getName() + "." + attribute.toString().toLowerCase();
      final String description = String.format("Allow users to modify %s attributes.", attribute.toString().toLowerCase().replace("_", " "));
      final Permission permission = new Permission(permissionNode, description, PermissionDefault.OP);
      permission.addParent(wildcard, true);
      this.plugin.addPermission(permission, false);
    }
  }
  
  */

}

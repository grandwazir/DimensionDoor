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

import java.util.HashMap;
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
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ModifyCommand extends PlayerCommand {

  public static final String NAME = "modify";
  public static final String DESCRIPTION = "Modify world settings.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to modify world settings.";
  public static final String USAGE = "<name> <attribute> <value>";
  public static final Permission PERMISSION = new Permission("dimensiondoor.modify", ModifyCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final DimensionDoor plugin;

  public ModifyCommand(final DimensionDoor plugin) {
    super(plugin, ModifyCommand.NAME, ModifyCommand.DESCRIPTION, ModifyCommand.USAGE, ModifyCommand.PERMISSION_DESCRIPTION, ModifyCommand.PERMISSION);
    this.plugin = plugin;
    this.registerAdditionalPermissions();
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, Object> arguments) throws CommandPermissionException, CommandArgumentException {
    final WorldRecord record = (WorldRecord) arguments.get("record");
    final String attribute = (String) arguments.get("attribute");
    String value = (String) arguments.get("value");
    final String permissionPath = ModifyCommand.PERMISSION.getName() + "." + attribute;

    if (!sender.hasPermission(permissionPath)) throw new CommandPermissionException("You do not have permission to modify that attribute.", ModifyCommand.PERMISSION);

    switch (WorldRecord.Attribute.valueOf(attribute)) {
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
      case DIFFICULTY:
        try {
          record.setDifficulty(Difficulty.valueOf(value));
          break;
        } catch (final IllegalArgumentException exception) {
          final StringBuilder guidence = new StringBuilder();
          guidence.append("You must specify a valid difficulty. Choose between ");
          for (final Difficulty difficulty : Difficulty.values()) {
            guidence.append(difficulty.toString() + ", ");
          }
          guidence.deleteCharAt(guidence.length() - 2);
          throw new CommandArgumentException("You must specify a valid difficulty.", "Choose between " + guidence.toString());
        }
      case GAME_MODE:
        try {
          record.setGamemode(GameMode.valueOf(value));
          break;
        } catch (final IllegalArgumentException exception) {
          final StringBuilder guidence = new StringBuilder();
          guidence.append("You must specify a valid game mode. Choose between ");
          for (final GameMode gameMode : GameMode.values()) {
            guidence.append(gameMode.toString() + ", ");
          }
          guidence.deleteCharAt(guidence.length() - 2);
          throw new CommandArgumentException("You must specify a valid game mode.", "Choose between " + guidence.toString());
        }
    }

    this.plugin.getDatabaseHandler().save(record);

    if (this.plugin.isWorldLoaded(record.getName())) {
      final World world = this.plugin.getWorld(record.getName());
      this.plugin.applyWorldAttributes(world);
    }

    this.logger.info(String.format("%s has changed %s to %s for %s", sender.getName(), attribute.toString(), value, record.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attribute.toString(), value, record.getName()));

  }

  @Override
  public Map<String, Object> parseArguments(final List<String> arguments) throws CommandArgumentException {
    final Map<String, Object> map = new HashMap<String, Object>();

    try {
      final String worldName = arguments.remove(0);
      final WorldRecord record = WorldRecord.findByName(this.plugin.getDatabaseHandler(), worldName);
      if (record == null)
        throw new CommandArgumentException(String.format("%s is not managed by DimensionDoor!", worldName), "Use /dd list for a list of worlds.");
      else {
        map.put("record", record);
      }
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a world!", "Use /dd list for a list of worlds.");
    }

    try {
      final String attributeName = arguments.remove(0).toUpperCase();
      final String attribute = WorldRecord.Attribute.valueOf(attributeName).toString();
      map.put("attribute", attribute);
    } catch (final IllegalArgumentException exception) {
      throw new CommandArgumentException("You must specify a valid attribute!", "Choose between " + this.getAttributesString());
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a valid attribute!", "Choose between " + this.getAttributesString());
    }

    try {
      final String value = arguments.remove(0);
      map.put("value", value);
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandArgumentException("You must specify a value!", "This value varies depending on the attribute.");
    }

    return map;

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

  private void registerAdditionalPermissions() {
    final Permission wildcard = new Permission(ModifyCommand.PERMISSION.getName() + ".*", "Allow a user to set all attributes", PermissionDefault.OP);
    this.plugin.addPermission(wildcard, true);
    for (final WorldRecord.Attribute attribute : WorldRecord.Attribute.values()) {
      final String permissionNode = ModifyCommand.PERMISSION.getName() + "." + attribute.toString().toLowerCase();
      final String description = String.format("Allow users to modify %s attributes.", attribute.toString().toLowerCase().replace("_", " "));
      final Permission permission = new Permission(permissionNode, description, PermissionDefault.OP);
      permission.addParent(wildcard, true);
      this.plugin.addPermission(permission, false);
    }
  }

}

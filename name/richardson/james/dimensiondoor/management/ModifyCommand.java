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

package name.richardson.james.dimensiondoor.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.dimensiondoor.creation.WorldHandler;
import name.richardson.james.dimensiondoor.database.WorldRecord;
import name.richardson.james.dimensiondoor.database.WorldRecordHandler;
import name.richardson.james.dimensiondoor.util.Command;

public class ModifyCommand extends Command {

  public enum Attribute {
    PVP,
    SPAWN_MONSTERS,
    SPAWN_ANIMALS,
    ISOLATED_CHAT,
    GAME_MODE,
    DIFFICULTY
  }

  public ModifyCommand() {
    name = "modify";
    description = "modify an attribute on a world.";
    usage = "/dd modify [world] [attribute] [value]";
    permission = this.registerCommandPermission();
    this.registerAdditionalPermissions();
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) {
    final WorldRecord record = (WorldRecord) arguments.get("record");
    final String attribute = (String) arguments.get("attribute");
    String value = (String) arguments.get("value");
    final String permissionPath = permission.getName() + "." + attribute;

    if (!sender.hasPermission(permissionPath) && !(sender instanceof ConsoleCommandSender)) {
      sender.sendMessage(ChatColor.RED + "You do not have permission to modify that attribute.");
      return;
    }
    
    switch (Attribute.valueOf(attribute)) {
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
        } catch (IllegalArgumentException exception) {
          StringBuilder guidence = new StringBuilder();
          guidence.append("You must specify a valid difficulty. Choose between ");
          for (Difficulty difficulty : Difficulty.values()) {
            guidence.append(difficulty.toString() + ", ");
          }
          guidence.deleteCharAt(guidence.length() - 2);
          throw new IllegalArgumentException(guidence.toString());
        }
      case GAME_MODE:
        try {
          record.setGamemode(GameMode.valueOf(value));
          break;
        } catch (IllegalArgumentException exception) {
          StringBuilder guidence = new StringBuilder();
          guidence.append("You must specify a valid game mode. Choose between ");
          for (GameMode gameMode : GameMode.values()) {
            guidence.append(gameMode.toString() + ", ");
          }
          guidence.deleteCharAt(guidence.length() - 2);
          throw new IllegalArgumentException(guidence.toString());
        }
    }

    WorldRecordHandler.saveWorldRecord(record);

    if (WorldHandler.isWorldLoaded(record.getName())) {
      final World world = WorldHandler.getWorld(record.getName());
      WorldHandler.applyWorldAttributes(world);
    }

    logger.info(String.format("%s has changed %s to %s for %s", sender.getName(), attribute.toString(), value, record.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attribute.toString(), value, record.getName()));

  }

  private String getAttributesString() {
    StringBuilder builder = new StringBuilder();
    for (Attribute attribute : Attribute.values()) {
      builder.append(attribute.toString());
      builder.append(", ");
    }
    builder.delete(builder.length() - 2, builder.length());
    return builder.toString();
  }

  private void registerAdditionalPermissions() {
    Permission wildcard = new Permission(this.permission.getName() + ".*", "Allow a user to set all attributes", PermissionDefault.OP);
    this.registerPermission(wildcard);
    for (Attribute attribute : Attribute.values()) {
      String permissionNode = this.permission.getName() + "." + attribute.toString().toLowerCase();
      String description = String.format("Allow users to modify %s attributes.", attribute.toString().toLowerCase().replace("_", " "));
      Permission permission = new Permission(permissionNode, description, PermissionDefault.OP);
      permission.addParent(wildcard, true);
      this.registerPermission(permission);
    }
  }

  @Override
  protected Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> map = new HashMap<String, Object>();

    try {
      final String worldName = arguments.remove(0);
      final WorldRecord record = WorldRecordHandler.getWorldRecord(worldName);
      map.put("record", record);
    } catch (final IndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("You must specify a world name.");
    }

    try {
      final String attributeName = arguments.remove(0).toUpperCase();
      final String attribute = Attribute.valueOf(attributeName).toString();
      map.put("attribute", attribute);
    } catch (final IllegalArgumentException exception) {
      throw new IllegalArgumentException("You must specify a valid attribute. Choose between " + this.getAttributesString());
    } catch (final IndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("You must specify a valid attribute. Choose between " + this.getAttributesString());
    }

    try {
      final String value = arguments.remove(0);
      map.put("value", value);
    } catch (final IndexOutOfBoundsException exception) {
      throw new IllegalArgumentException("You must specify a value.");
    }

    return map;

  }

}

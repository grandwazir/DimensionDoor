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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.SwitchTexturePackTask;
import name.richardson.james.bukkit.dimensiondoor.World;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class ModifyCommand extends AbstractCommand {

  private final DimensionDoor plugin;

  private String worldName;
  
  private Attribute attribute;
  
  private String value;

  private enum Attribute {
    DIFFICULTY,
    ENABLED,
    GAME_MODE,
    SPAWN_MONSTERS,
    SPAWN_ANIMALS,
    PVP,
    KEEP_SPAWN_IN_MEMORY,
    ISOLATED_CHAT,
    TEXTURE_PACK,
    RESPAWN
  }
  
  public ModifyCommand(final DimensionDoor plugin) {
    super(plugin, true);
    this.plugin = plugin;
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final World world = plugin.getWorldManager().getWorld(worldName);
    final String permissionName = this.getRootPermission().getName().replace("*", "") + "." + attribute.toString().toLowerCase();
    final Permission permission = plugin.getServer().getPluginManager().getPermission(permissionName);
    if (world == null) {
      throw new CommandUsageException(this.getLocalisation().getMessage(DimensionDoor.class, "world-is-not-managed", this.worldName));
    } else if (!sender.hasPermission(permission)) {
      throw new CommandPermissionException(null, permission);
    }
    
    switch (this.attribute) {
    case ENABLED:
      world.setEnabled(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    case PVP:
      world.setPVP(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    case SPAWN_MONSTERS:
      world.setAllowMonsters(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    case SPAWN_ANIMALS:
      world.setAllowAnimals(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    case ISOLATED_CHAT:
      world.setIsolatedChat(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    case KEEP_SPAWN_IN_MEMORY:
      world.setKeepSpawnInMemory(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    case DIFFICULTY:
      try {
        this.value = this.value.toUpperCase();
        world.setDifficulty(Difficulty.valueOf(this.value));
        break;
      } catch (final IllegalArgumentException exception) {
        final StringBuilder guidence = new StringBuilder();
        for (final Difficulty difficulty : Difficulty.values()) {
          guidence.append(difficulty.toString() + ", ");
        }
        guidence.deleteCharAt(guidence.length() - 2);
        throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-valid-difficulty"), this.getLocalisation().getMessage(this, "choose-between", guidence.toString()));
      }
    case GAME_MODE:
      try {
        this.value = this.value.toUpperCase();
        world.setGameMode(GameMode.valueOf(this.value));
        break;
      } catch (final IllegalArgumentException exception) {
        final StringBuilder guidence = new StringBuilder();
        for (final GameMode gameMode : GameMode.values()) {
          guidence.append(gameMode.toString() + ", ");
        }
        guidence.deleteCharAt(guidence.length() - 2);
        throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-valid-game-mode"), this.getLocalisation().getMessage(this, "choose-between", guidence.toString()));
      }
    case TEXTURE_PACK:
      try {
        if (value.equalsIgnoreCase("default")) {
          world.setTexturePack(null);
        } else {
          final URL url = new URL(value);
          world.setTexturePack(url.toString());
        }
        if (world.isLoaded() && world.getTexturePack() != null) {
          SwitchTexturePackTask task = new SwitchTexturePackTask(plugin.getServer().getWorld(world.getName()).getPlayers(), world.getTexturePack());
          this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 10L);
        }
        
      } catch (MalformedURLException e) {
      	throw new CommandArgumentException(this.getLocalisation().getMessage(this, "invalid-url"), null);
			}
    case RESPAWN:
      world.setPlayerRespawn(Boolean.parseBoolean(this.value));
      this.value = Boolean.toString(Boolean.parseBoolean(this.value));
      break;
    }
    this.plugin.getWorldManager().save();
    final Object[] arguments = { this.attribute.toString(), this.value, world.getName() };
    sender.sendMessage(this.getLocalisation().getMessage(this, "success-report", arguments));
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    final LinkedList<String> args = new LinkedList<String>();
    args.addAll(Arrays.asList(arguments));

    if (!args.isEmpty()) {
      this.worldName = args.remove(0);
    } else {
      throw new CommandArgumentException(this.getLocalisation().getMessage(DimensionDoor.class, "must-specify-a-world-name"), null);
    }

    if (!args.isEmpty()) {
      try {
        final String name = args.remove(0).toUpperCase();
        this.attribute = Attribute.valueOf(name);
      } catch (final IllegalArgumentException exception) {
        throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-valid-attribute"), this.getLocalisation().getMessage(this, "attribute-list", this.getAttributesString()));
      } catch (final IndexOutOfBoundsException exception) {
        throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-valid-attribute"), this.getLocalisation().getMessage(this, "attribute-list", this.getAttributesString()));
      }
    } else {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-valid-attribute"), this.getLocalisation().getMessage(this, "attribute-list", this.getAttributesString()));
    }

    if (!args.isEmpty()) {
      this.value = args.remove(0);
    } else {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-valid-value"), this.getLocalisation().getMessage(this, "value-hint", this.getAttributesString()));
    }
  }

  private String getAttributesString() {
    final StringBuilder builder = new StringBuilder();
    for (final Attribute attribute : Attribute.values()) {
      builder.append(attribute.toString());
      builder.append(", ");
    }
    builder.delete(builder.length() - 2, builder.length());
    return builder.toString();
  }
  
  protected void registerPermissions(boolean wildcard) {
    super.registerPermissions(true);
    final String base = this.getRootPermission().getName().replace("*", "");
    for (final Attribute attribute : Attribute.values()) {
      final String permissionNode = base + "." + attribute.toString().toLowerCase();
      final String description = this.getLocalisation().getMessage(this, "permission-per-attribute-description", attribute.toString().toLowerCase().replace("_", " "));
      final Permission permission = new Permission(permissionNode, description, PermissionDefault.OP);
      permission.addParent(this.getRootPermission(), true);
      this.getPermissionManager().addPermission(permission, false);
    }
  }
  
  

}

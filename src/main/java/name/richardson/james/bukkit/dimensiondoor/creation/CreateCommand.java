/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CreateCommand.java is part of DimensionDoor.
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

package name.richardson.james.bukkit.dimensiondoor.creation;

import java.util.Arrays;
import java.util.LinkedList;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.internals.Logger;

public class CreateCommand extends PluginCommand {

  private static Logger logger = new Logger(CreateCommand.class);

  private final DimensionDoor plugin;

  private String worldName;
  private Environment environment;
  private Long seed;
  private String generatorPlugin;
  private String generatorID;

  public CreateCommand(final DimensionDoor plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    sender.sendMessage(this.getSimpleFormattedMessage("world-creation-in-progress", this.worldName));

    if (this.generatorPlugin != null) {
      try {
        this.plugin.createWorld(this.worldName, this.environment, this.seed, this.generatorPlugin, this.generatorID);
      } catch (final IllegalArgumentException exception) {
        throw new CommandUsageException(exception.getMessage());
      }
    } else {
      this.plugin.createWorld(this.worldName, this.environment, this.seed);
    }

    sender.sendMessage(this.getSimpleFormattedMessage("world-creation-in-progress", this.worldName));
    CreateCommand.logger.info(String.format("%s has created a new world called %s", sender.getName(), this.worldName));

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {
    final LinkedList<String> args = new LinkedList<String>();
    args.addAll(Arrays.asList(arguments));

    // null old values
    this.worldName = null;
    this.environment = null;
    this.generatorID = null;
    this.generatorPlugin = null;
    this.seed = null;

    if (args.size() == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-world-name"), this.getMessage("create-name-hint"));
    } else {
      this.worldName = args.remove(0);
    }

    for (String argument : args) {
      if (argument.startsWith("e:")) {
        try {
          this.environment = Environment.valueOf(argument.replaceFirst("e:", ""));
        } catch (final IllegalArgumentException exception) {
          throw new CommandArgumentException(this.getMessage("invalid-environment"), this.getSimpleFormattedMessage("valid-environments", this.buildEnvironmentList()));
        }
      } else if (argument.startsWith("s:")) {
        final String stringSeed = argument.replaceFirst("s:", "");
        long seed = 0;
        try {
          seed = Long.parseLong(stringSeed);
        } catch (final NumberFormatException exception) {
          seed = stringSeed.hashCode();
        }
        this.seed = new Long(seed);
      } else if (argument.startsWith("g:")) {
        argument = argument.replaceFirst("g:", "");
        final String[] a = argument.split(":");
        this.generatorPlugin = a[0];
        if (a.length == 2) {
          this.generatorID = a[1];
        }
      }
    }

  }

  private String buildEnvironmentList() {
    final StringBuilder message = new StringBuilder();
    for (final Environment environment : Environment.values()) {
      message.append(environment.name());
      message.append(", ");
    }
    message.delete(message.length() - 2, message.length());
    return message.toString();
  }

}

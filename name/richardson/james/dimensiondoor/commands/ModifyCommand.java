/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ModifyCommand.java is part of DimensionDoor.
 * 
 * DimensionDoor is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * DimensionDoor is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with DimensionDoor.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.dimensiondoor.commands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import name.richardson.james.dimensiondoor.DimensionDoor;
import name.richardson.james.dimensiondoor.exceptions.InvalidAttributeException;
import name.richardson.james.dimensiondoor.exceptions.NotEnoughArgumentsException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotLoadedException;
import name.richardson.james.dimensiondoor.exceptions.WorldIsNotManagedException;
import name.richardson.james.dimensiondoor.persistent.WorldRecord;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

public class ModifyCommand extends Command {

  public ModifyCommand(final DimensionDoor plugin) {
    super(plugin);
    name = "modify";
    description = "modify an attribute on a specific world";
    usage = "/dd modify [world] [attribute] [value]";
    permission = plugin.getName() + "." + name;
  }

  @Override
  public void execute(final CommandSender sender, final List<String> args) throws WorldIsNotManagedException, NotEnoughArgumentsException,
      InvalidAttributeException, WorldIsNotLoadedException {
    if (args.size() < 3) throw new NotEnoughArgumentsException(name, usage);
    
    // if it is a boolean attribute 
    final WorldRecord worldRecord = WorldRecord.findFirst(args.get(0));
    final HashMap<String, Boolean> attributes = worldRecord.getAttributes();
    final String attributeName = args.get(1);

    if (attributes.containsKey(attributeName)) {
      final boolean attributeValue = Boolean.valueOf(args.get(2));
      attributes.put(attributeName, attributeValue);
      worldRecord.setAttributes(attributes);
      worldRecord.save();
      DimensionDoor.log(Level.INFO, String.format("%s has changed %s to %s for %s", getSenderName(sender), attributeName, Boolean.toString(attributeValue),args.get(0)));
      sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attributeName, Boolean.toString(attributeValue), args.get(0)));
    } else if (attributeName.equalsIgnoreCase("gamemode")) {
      try {
        final GameMode gamemode = GameMode.valueOf(args.get(2).toUpperCase());
        worldRecord.setGamemode(gamemode);
        worldRecord.save();
        DimensionDoor.log(Level.INFO, String.format("%s has changed %s to %s for %s", getSenderName(sender), attributeName, gamemode.name(), args.get(0)));
        sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attributeName, gamemode.name(), args.get(0)));
      } catch (IllegalArgumentException e) {
        StringBuilder guidence = new StringBuilder();
        guidence.append(ChatColor.YELLOW + "Valid gamemodes: ");
        for (GameMode gamemode : GameMode.values()) {
          guidence.append(gamemode.toString() + ", ");
        }
        guidence.deleteCharAt(guidence.length() - 2);
        throw new InvalidAttributeException(attributeName, guidence.toString());
      } 
    } else if (attributeName.equalsIgnoreCase("difficulty")) {
      try {
        final Difficulty difficulty = Difficulty.valueOf(args.get(2).toUpperCase());
        worldRecord.setDifficulty(difficulty);
        worldRecord.save();
        DimensionDoor.log(Level.INFO, String.format("%s has changed %s to %s for %s", getSenderName(sender), attributeName, difficulty.name(), args.get(0)));
        sender.sendMessage(String.format(ChatColor.GREEN + "Set %s to %s for %s", attributeName, difficulty.name(), args.get(0)));
      } catch (IllegalArgumentException e) {
        StringBuilder guidence = new StringBuilder();
        guidence.append(ChatColor.YELLOW + "Valid difficulties: ");
        for (Difficulty difficulty : Difficulty.values()) {
          guidence.append(difficulty.toString() + ", ");
        }
        guidence.deleteCharAt(guidence.length() - 2);
        throw new InvalidAttributeException(attributeName, guidence.toString());
      }   
    } else {
      throw new InvalidAttributeException(attributeName, null);
    }
    
    plugin.applyWorldAttributes(worldRecord);
    
    
  }

}

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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.ValidatingPrompt;

import name.richardson.james.bukkit.dimensiondoor.DimensionDoor;
import name.richardson.james.bukkit.dimensiondoor.World;
import name.richardson.james.bukkit.dimensiondoor.WorldManager;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class CreateCommand extends AbstractCommand {
  
  private final ConversationFactory factory;
  
  private final WorldManager manager;
  
  private final DimensionDoor plugin;

  public CreateCommand(final DimensionDoor plugin) {
    super(plugin, false);
    this.manager = plugin.getWorldManager();
    this.factory = new ConversationFactory(plugin)
    .withModality(true)
    .withPrefix(new WorldCreatePrefix())
    .withFirstPrompt(new WorldNamePrompt())
    .withEscapeSequence("/quit")
    .withTimeout(10)
    .withInitialSessionData(this.getInitalSessionData());
    this.plugin = plugin;
  }

  private Map<Object, Object> getInitalSessionData() {
    Map<Object, Object> map = new HashMap<Object, Object>();
    map.put("step", 1);
    return map;
  }
  
  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    if (sender instanceof Conversable) {
      factory.buildConversation((Conversable) sender).begin();
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws name.richardson.james.bukkit.utilities.command.CommandArgumentException {
    return;
  }

  private class WorldNamePrompt extends ValidatingPrompt {

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-world-name");
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String message) {
      context.setSessionData("world-name", message);
      context.setSessionData("step", 2);
      return new WorldEnvironmentPrompt();
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String message) {
      return (manager.getWorld(message) == null);
    }
    
    protected String getFailedValidationText(ConversationContext context, String message) {
      return getLocalisation().getMessage(CreateCommand.class, "world-already-exists", message);
    }
    
  }
  
  private class WorldEnvironmentPrompt extends FixedSetPrompt {

    public WorldEnvironmentPrompt() {
      super(Environment.NORMAL.toString(), Environment.NETHER.toString(), Environment.THE_END.toString());
    }
    
    public Prompt acceptValidatedInput(ConversationContext context, String message) {
      context.setSessionData("environment", message);
      context.setSessionData("step", 3);
      return new WorldTypePrompt();
    }

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-world-environment", formatFixedSet().toString());
    }
    
  }
  
  private class WorldTypePrompt extends FixedSetPrompt {

    public WorldTypePrompt() {
      super(WorldType.NORMAL.name(), WorldType.FLAT.name(), WorldType.LARGE_BIOMES.name());
    }
    
    public Prompt acceptValidatedInput(ConversationContext context, String message) {
      context.setSessionData("world-type", message);
      context.setSessionData("step", 4);
      return new WorldSeedPrompt();
    }

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-world-type", formatFixedSet().toString());
    }
    
  }
  
  private class WorldSeedPrompt extends StringPrompt {

    public Prompt acceptInput(ConversationContext context, String message) {
      context.setSessionData("step", 5);
      if (message.isEmpty() || message.equalsIgnoreCase(getLocalisation().getMessage(CreateCommand.class, "random"))) {
        context.setSessionData("seed", System.currentTimeMillis());
      } else {
        context.setSessionData("seed", message.hashCode());
      }
      return new WorldGenerateStructuresPrompt();
    }

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-world-seed", getLocalisation().getMessage(CreateCommand.class, "random"));
    }
    
  }
  
  private class WorldGenerateStructuresPrompt extends FixedSetPrompt {

    public WorldGenerateStructuresPrompt() {
      super(getLocalisation().getMessage(CreateCommand.class, "yes"), getLocalisation().getMessage(CreateCommand.class, "no"));
    }
    
    public Prompt acceptValidatedInput(ConversationContext context, String message) {
      if (message.equalsIgnoreCase(getLocalisation().getMessage(CreateCommand.class, "no"))) {
        context.setSessionData("generate-structures", false);
      } else {
        context.setSessionData("generate-structures", true);
      }
      context.setSessionData("step", 6);
      return new WorldGeneratorPluginPrompt();
    }

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-generate-structures", formatFixedSet().toString());
    }
    
  }
  
  private class WorldGeneratorPluginPrompt extends ValidatingPrompt {
    
    public Prompt acceptValidatedInput(ConversationContext context, String message) {
      
      if (!message.equalsIgnoreCase(getLocalisation().getMessage(CreateCommand.class, "none"))) {
        context.setSessionData("generator-plugin", message);
        context.setSessionData("step", 7);
        return new WorldGeneratorIdPrompt();
      } else {
        context.setSessionData("step", 8);
        return new CreateWorldPrompt();
      }
    }
    
    @Override
    protected boolean isInputValid(ConversationContext context, String message) {
      return (message.equalsIgnoreCase(getLocalisation().getMessage(CreateCommand.class, "none")) || Bukkit.getPluginManager().getPlugin(message) != null);
    }
    
    protected String getFailedValidationText(ConversationContext context, String message) {
      return getLocalisation().getMessage(CreateCommand.class, "plugin-not-loaded", message);
    }

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-world-generator", getLocalisation().getMessage(CreateCommand.class, "none"));
    }
    
  }
  
  private class WorldGeneratorIdPrompt extends StringPrompt {
    
    public Prompt acceptInput(ConversationContext context, String message) {
      context.setSessionData("step", 8);
      if (!message.equalsIgnoreCase(getLocalisation().getMessage(CreateCommand.class, "none"))) context.setSessionData("generator-id", message);
      return new CreateWorldPrompt();
    }

    public String getPromptText(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prompt-world-generator-id", getLocalisation().getMessage(CreateCommand.class, "none"));
    }
    
  }
  
  private class CreateWorldPrompt extends MessagePrompt {
    
    public String getPromptText(ConversationContext context) {
      World world = new World(plugin, context.getSessionData("world-name").toString());
      context.getForWhom().sendRawMessage(getLocalisation().getMessage(CreateCommand.class, "creating-world", world.getName()));
      world.setEnvironment(Environment.valueOf(context.getSessionData("environment").toString()));
      world.setWorldType(WorldType.valueOf(context.getSessionData("world-type").toString()));
      world.setSeed(Long.parseLong(context.getSessionData("seed").toString()));
      if (context.getSessionData("generator-plugin") != null) {
        String pluginName = (String) context.getSessionData("generator-plugin");
        world.setGeneratorPluginName(pluginName);
      }
      if (context.getSessionData("generator-id") != null) {
        String pluginId = (String) context.getSessionData("generator-id");
        world.setGeneratorID(pluginId);
      }
      manager.addWorld(world);
      world.load();
      manager.save();
      return getLocalisation().getMessage(CreateCommand.class, "world-created", world.getName());
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext arg0) {
      return Prompt.END_OF_CONVERSATION;
    }
    
  }
  
  private class WorldCreatePrefix implements ConversationPrefix {

    public String getPrefix(ConversationContext context) {
      return getLocalisation().getMessage(CreateCommand.class, "prefix", context.getSessionData("step"));
    } 
    
  }

  
}

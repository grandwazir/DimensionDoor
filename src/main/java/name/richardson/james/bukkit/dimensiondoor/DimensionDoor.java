/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * DimensionDoor.java is part of DimensionDoor.
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

package name.richardson.james.bukkit.dimensiondoor;

import java.io.IOException;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import name.richardson.james.bukkit.dimensiondoor.creation.CreateCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.LoadCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.RemoveCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.UnloadCommand;
import name.richardson.james.bukkit.dimensiondoor.management.ClearCommand;
import name.richardson.james.bukkit.dimensiondoor.management.ListCommand;
import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class DimensionDoor extends SkeletonPlugin {

  private WorldManager manager;

  public String getArtifactID() {
    return "dimension-door";
  }

  protected void loadConfiguration() throws IOException {
    configuration = new DimensionDoorConfiguration(this);
    ConfigurationSerialization.registerClass(World.class);
    this.manager = new WorldManager(this);
    this.logger.info(String.format("%d worlds loaded and configured.", this.manager.configuredWorldCount()));;
  }

  public WorldManager getWorldManager() {
    return manager;
  }
  
  protected void registerCommands() {
    CommandManager cm = new CommandManager(this);
    this.getCommand("dd").setExecutor(cm);
    cm.addCommand(new ClearCommand(this));
    cm.addCommand(new CreateCommand(this));
    cm.addCommand(new ListCommand(this));
    cm.addCommand(new LoadCommand(this));
    cm.addCommand(new RemoveCommand(this));
    cm.addCommand(new UnloadCommand(this));
  }

  protected void registerEvents() {
    this.logger.info(String.format("%d worlds loaded and configured.", this.getServer().getWorlds().size()));
  }
  
}

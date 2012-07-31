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

import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class DimensionDoor extends SkeletonPlugin {

  private WorldManager manager;

  public String getArtifactID() {
    return "dimension-door";
  }

  protected void loadConfiguration() throws IOException {
    configuration = new DimensionDoorConfiguration(this);
    this.manager = new WorldManager(this);
    this.logger.info(String.format("%d worlds loaded and configured.", this.manager.configuredWorldCount()));;
  }

  protected void registerCommands() {
    /*
    final CommandManager cm = new CommandManager(this);
    this.getCommand("dd").setExecutor(cm);
    cm.addCommand(new ClearCommand(this));
    cm.addCommand(new CreateCommand(this));
    cm.addCommand(new InfoCommand(this));
    cm.addCommand(new ListCommand(this));
    cm.addCommand(new LoadCommand(this));
    cm.addCommand(new ModifyCommand(this));
    cm.addCommand(new RemoveCommand(this));
    cm.addCommand(new SpawnCommand(this));
    cm.addCommand(new TeleportCommand(this));
    cm.addCommand(new UnloadCommand(this));
    */
  }

  protected void registerEvents() {
    this.logger.info(String.format("%d worlds loaded and configured.", this.getServer().getWorlds().size()));
  }
  
}

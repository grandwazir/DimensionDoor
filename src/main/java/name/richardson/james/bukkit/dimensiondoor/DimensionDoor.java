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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.dimensiondoor.creation.CreateCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.LoadCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.RemoveCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.UnloadCommand;
import name.richardson.james.bukkit.dimensiondoor.creation.WorldListener;
import name.richardson.james.bukkit.dimensiondoor.management.ClearCommand;
import name.richardson.james.bukkit.dimensiondoor.management.ContainerBlockListener;
import name.richardson.james.bukkit.dimensiondoor.management.InfoCommand;
import name.richardson.james.bukkit.dimensiondoor.management.ItemListener;
import name.richardson.james.bukkit.dimensiondoor.management.ListCommand;
import name.richardson.james.bukkit.dimensiondoor.management.ModifyCommand;
import name.richardson.james.bukkit.dimensiondoor.management.PlayerListener;
import name.richardson.james.bukkit.dimensiondoor.management.SpawnCommand;
import name.richardson.james.bukkit.dimensiondoor.management.TeleportCommand;
import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class DimensionDoor extends SkeletonPlugin {

  private WorldManager manager;

  public String getArtifactID() {
    return "dimension-door";
  }

  protected void loadConfiguration() throws IOException {
    configuration = new DimensionDoorConfiguration(this);
  }

  protected void registerCommands() {
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
  }

  protected void registerEvents() {
    PluginManager pluginManager = this.getServer().getPluginManager();
    this.worldListener = new WorldListener(this);
    this.playerListener = new PlayerListener(this);
    this.blockListener = new ContainerBlockListener(this);
    this.entityListener = new ItemListener(this);
    pluginManager.registerEvents(this.worldListener, this);
    pluginManager.registerEvents(this.playerListener, this);
    if (configuration.isPreventItemSpawning()) pluginManager.registerEvents(this.entityListener, this);
    if (configuration.isPreventContainerBlocks()) pluginManager.registerEvents(this.blockListener, this);
    this.registerMainWorlds();
    this.registerAuxiliaryWorlds();
    this.logger.info(String.format("%d worlds loaded and configured.", this.getServer().getWorlds().size()));
  }

  protected void setupPersistence() throws SQLException {
    this.manager = new WorldManager(this);
    this.logger.info(String.format("%d worlds loaded and configured.", this.manager.configuredWorldCount());
  }

}

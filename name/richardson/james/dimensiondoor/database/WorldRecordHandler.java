/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * WorldRecordHandler.java is part of DimensionDoor.
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

package name.richardson.james.dimensiondoor.database;

import java.util.Collections;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.World;

import name.richardson.james.dimensiondoor.creation.WorldHandler;
import name.richardson.james.dimensiondoor.util.Logger;

public final class WorldRecordHandler {

  protected final static Logger logger = new Logger(WorldRecordHandler.class);

  public static WorldRecord createWorldRecord(World world) {
    logger.debug(String.format("Creating world record for %s.", world.getName()));
    WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    record.setEnvironment(world.getEnvironment());
    record.setSeed(world.getSeed());
    record.setDifficulty(world.getDifficulty());
    record.setGamemode((GameMode) WorldHandler.getDefaults().get("game-mode"));
    record.save();
    return record;
  }

  public static void deleteWorldRecord(String worldName) {
    if (!WorldRecordHandler.isWorldManaged(worldName)) throw new IllegalArgumentException(worldName + " is not managed by DimensionDoor.");
    Record result = WorldRecord.find(worldName).get(0);
    result.delete();
  }

  public static void deleteWorldRecord(World world) {
    if (!WorldRecordHandler.isWorldManaged(world.getName())) throw new IllegalArgumentException(world.getName() + " is not managed by DimensionDoor.");
    Record result = WorldRecord.find(world.getName()).get(0);
    result.delete();
  }

  public static WorldRecord getWorldRecord(String worldName) {
    if (!WorldRecordHandler.isWorldManaged(worldName)) throw new IllegalArgumentException(worldName + " is not managed by DimensionDoor.");
    return (WorldRecord) WorldRecord.find(worldName).get(0);
  }

  public static WorldRecord getWorldRecord(World world) {
    if (!WorldRecordHandler.isWorldManaged(world.getName())) throw new IllegalArgumentException(world.getName() + " is not managed by DimensionDoor.");
    return (WorldRecord) WorldRecord.find(world.getName()).get(0);
  }

  public static List<WorldRecord> getWorldRecordList() {
    return Collections.unmodifiableList(WorldRecord.list());
  }

  public static boolean isWorldManaged(String worldName) {
    if (WorldRecord.count(worldName) == 1) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isWorldManaged(World world) {
    if (WorldRecord.count(world.getName()) == 1) {
      return true;
    } else {
      return false;
    }
  }

  public static WorldRecord saveWorldRecord(WorldRecord record) {
    record.save();
    return record;
  }

}

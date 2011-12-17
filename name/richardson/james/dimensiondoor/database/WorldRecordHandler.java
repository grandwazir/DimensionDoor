/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ReservationRecordHandler.java is part of Reservation.
 * 
 * Reservation is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Reservation is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Reservation. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor.database;

import org.bukkit.GameMode;
import org.bukkit.World;

import name.richardson.james.dimensiondoor.WorldHandler;
import name.richardson.james.dimensiondoor.util.Handler;
import name.richardson.james.dimensiondoor.util.Logger;


public final class WorldRecordHandler extends Handler {

  protected final static Logger logger = new Logger(WorldRecordHandler.class);
  protected final static WorldHandler handler = new WorldHandler(WorldRecordHandler.class);
  
  public WorldRecordHandler(Class<?> owner) {
    super(owner);
  }

  public WorldRecord createWorldRecord(World world) {
    if (this.isWorldManaged(world.getName())) throw new IllegalArgumentException("That world is already managed.");
    WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    record.setEnvironment(world.getEnvironment());
    record.setSeed(world.getSeed());
    record.setDifficulty(world.getDifficulty());
    record.setGamemode((GameMode) handler.getDefaults().get("game-mode"));
    record.save();
    return record;
  }
  
  public void deleteWorldRecord(String worldName) {
    if (!this.isWorldManaged(worldName)) throw new IllegalArgumentException("That world is not managed.");
    WorldRecord record = new WorldRecord();
    record.setName(worldName);
    Record result = WorldRecord.find(record).get(0);
    result.delete();
  }
  
  public void deleteWorldRecord(World world) {
    if (!this.isWorldManaged(world.getName())) throw new IllegalArgumentException("That world is not managed.");
    WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    Record result = WorldRecord.find(record).get(0);
    result.delete();
  }
  
  public WorldRecord getWorldRecord(World world) {
    if (!this.isWorldManaged(world.getName())) throw new IllegalArgumentException("That world is not managed.");
    WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    return (WorldRecord) WorldRecord.find(record).get(0);
  }
  
  public WorldRecord saveWorldRecord(WorldRecord record) {
    record.save();
    return record;
  }
  
  public boolean isWorldManaged(World world) {
    WorldRecord record = new WorldRecord();
    record.setName(world.getName());
    if (WorldRecord.count(record) == 1) {
      return true;
    } else {
      return false;
    }
  }
  
  public boolean isWorldManaged(String worldName) {
    WorldRecord record = new WorldRecord();
    record.setName(worldName);
    if (WorldRecord.count(record) == 1) {
      return true;
    } else {
      return false;
    }
  }


}

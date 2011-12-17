/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * Record.java is part of Reservation.
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

import java.util.List;
import java.util.Set;

import name.richardson.james.dimensiondoor.util.Logger;

import com.avaje.ebean.ExampleExpression;
import com.avaje.ebean.LikeType;

public abstract class Record {

  protected final static Logger logger = new Logger(Record.class);

  /**
   * Return the total number of records matching the example record.
   * 
   * @param exampleRecord
   * The example record that should be used for matching.
   * @return count
   * The number of records that match the example.
   */
  protected static final int count(final Record example) {
    Record.logger.debug("Attempting to get row count using an example.");
    Record.logger.debug(example.toString());
    final ExampleExpression expression = Database.getInstance().getExpressionFactory().exampleLike(example, true, LikeType.EQUAL_TO);
    return Database.getInstance().find(example.getClass()).where().add(expression).findRowCount();
  }

  protected final static int delete(final Set<Record> records) {
    Record.logger.debug("Deleting records from database.");
    return Database.getInstance().delete(records);
  }

  /**
   * Return records that match the example provided.
   * 
   * @param exampleRecord
   * The example record that should be used for matching.
   * @return records
   * A list of records that match the example.
   */
  protected static final List<? extends Record> find(final Record example) {
    Record.logger.debug("Attempting to return records matching an example.");
    Record.logger.debug(example.toString());
    final ExampleExpression expression = Database.getInstance().getExpressionFactory().exampleLike(example, true, LikeType.EQUAL_TO);
    return Database.getInstance().find(example.getClass()).where().add(expression).findList();
  }


  protected final static int save(final Set<Record> records) {
    Record.logger.debug("Saving records to database.");
    return Database.getInstance().save(records);
  }

  @Override
  public abstract String toString();

  /**
   * Return the total number of records associated with this class.
   * 
   * @return count
   * the number of records currently in the database.
   */
  protected final int count() {
    return Database.getInstance().find(this.getClass()).findRowCount();
  }

  protected final void delete() {
    Record.logger.debug("Deleting record from database.");
    Record.logger.debug(this.toString());
    Database.getInstance().delete(this);
  }

  protected final void save() {
    Record.logger.debug("Saving record to database.");
    Record.logger.debug(this.toString());
    Database.getInstance().save(this);
  }

}

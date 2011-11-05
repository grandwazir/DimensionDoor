/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * NotEnoughArgumentsException.java is part of DimensionDoor.
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
package name.richardson.james.dimensiondoor.exceptions;

public class NotEnoughArgumentsException extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String command;
  private String usage;

  public NotEnoughArgumentsException(final String command, final String usage) {
    setUsage(usage);
    setCommand(command);
  }

  public String getCommand() {
    return command;
  }

  public String getUsage() {
    return usage;
  }

  public void setCommand(final String command) {
    this.command = command;
  }

  public void setUsage(final String usage) {
    this.usage = usage;
  }

}

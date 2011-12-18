/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * Logger.java is part of jChat.
 * 
 * jChat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * jChat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * jChat. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package name.richardson.james.dimensiondoor.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;

public class Logger {

  private static boolean currentlyDebugging = false;
  private static final Level debugLevel = Level.FINE;
  private static final java.util.logging.Logger parentLogger = java.util.logging.Logger.getLogger("Minecraft");
  private static final String prefix = "[DimensionDoor] ";
  private static final Set<Logger> registeredLoggers = new HashSet<Logger>();

  private final java.util.logging.Logger logger;

  /**
   * Create a new logger with the specified name.
   * 
   * @param className
   * The name of the logger, should be the class it belongs to.
   */
  public Logger(final Class<?> parentClass) {
    this.logger = java.util.logging.Logger.getLogger(parentClass.getName());
    this.logger.setParent(Logger.parentLogger);
    Logger.registeredLoggers.add(this);
    if (Logger.currentlyDebugging) {
      this.setDebugging(true);
    }
  }

  /**
   * Enable debugging for all loggers.
   * 
   * This basically sets all parentHandlers to a lower log level to ensure
   * that messages are correctly logged. All newly created and existing loggers
   * will also have debugging enabled.
   */
  public static void enableDebugging() {
    Logger.currentlyDebugging = true;
    for (final Handler handler : Logger.parentLogger.getHandlers()) {
      handler.setLevel(Logger.debugLevel);
    }
    for (final Logger logger : Logger.registeredLoggers) {
      logger.setDebugging(true);
    }
    parentLogger.fine(prefix + "Debugging is now enabled.");
  }

  /**
   * Log a configuration message with this logger.
   * 
   * @param message
   * The string that you wish to log.
   */
  public void config(final String message) {
    this.logger.config(Logger.prefix + "<" + this.logger.getName() + "> " + message);
  }

  /**
   * Log a debug message with this logger.
   * 
   * @param message
   * The string that you wish to log.
   */
  public void debug(final String message) {
    this.logger.fine(Logger.prefix + "<" + this.logger.getName() + "> " + message);
  }

  /**
   * Log a general message with this logger.
   * 
   * @param message
   * The string that you wish to log.
   */
  public void info(final String message) {
    this.logger.info(Logger.prefix + message);
  }

  /**
   * Check to see if the logger is logging debug messages.
   * 
   * @return isDebugging true if it is logging debug messages, false otherwise.
   */
  public boolean isDebugging() {
    return this.logger.isLoggable(Logger.debugLevel);
  }

  /**
   * Set if a logger should be logging debug messages or not.
   * 
   * @param setDebugging
   * true if it is should log messages, false otherwise.
   */
  public void setDebugging(final Boolean value) {
    this.logger.setLevel(Logger.debugLevel);
  }

  /**
   * Log a severe (fatal) message with this logger.
   * 
   * @param message
   * The string that you wish to log.
   */
  public void severe(final String message) {
    this.logger.severe(Logger.prefix + message);
  }

  /**
   * Log a warning message with this logger.
   * 
   * @param message
   * The string that you wish to log.
   */
  public void warning(final String message) {
    this.logger.warning(Logger.prefix + message);
  }

}

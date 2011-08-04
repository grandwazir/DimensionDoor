
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

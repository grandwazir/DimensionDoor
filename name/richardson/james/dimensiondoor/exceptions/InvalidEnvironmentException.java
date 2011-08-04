
package name.richardson.james.dimensiondoor.exceptions;

public class InvalidEnvironmentException extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String environment;

  public InvalidEnvironmentException(final String environment) {
    setEnvironment(environment);
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(final String environment) {
    this.environment = environment;
  }

}

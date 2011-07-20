package name.richardson.james.dimensiondoor.exceptions;


public class InvalidEnvironment extends Exception {
  private static final long serialVersionUID = 430445873851296870L;
  private String environment;
  
  public InvalidEnvironment(String environment) {
    this.setEnvironment(environment);
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public String getEnvironment() {
    return environment;
  }
  
}


package name.richardson.james.dimensiondoor.exceptions;

public class InvalidAttributeException extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String attribute;

  public InvalidAttributeException(final String attribute) {
    setAttribute(attribute);
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(final String attribute) {
    this.attribute = attribute;
  }

}


package name.richardson.james.dimensiondoor.exceptions;

public class InvalidAttribute extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String attribute;

  public InvalidAttribute(String attribute) {
    this.setAttribute(attribute);
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

}

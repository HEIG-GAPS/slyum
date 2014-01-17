package classDiagram.verifyName;

public class SyntaxeNameException extends Exception {

  private static final long serialVersionUID = 1L;

  public SyntaxeNameException() {
    super("Error in parsing file. Syntax error.");
  }
}

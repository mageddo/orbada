package pl.mpak.sky.gui.swing.syntax.structure;

public class ParserException extends java.lang.Exception {
  private static final long serialVersionUID = 1L;

  public ParserException() {
  }

  public ParserException(String message) {
    super(message);
  }

  public ParserException(Throwable cause) {
    super(cause);
  }

  public ParserException(String message, Throwable cause) {
    super(message, cause);
  }

}

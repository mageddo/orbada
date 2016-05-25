package pl.mpak.util;

public class GeneratorException extends UtilException {
  private static final long serialVersionUID = 1L;

  public GeneratorException() {
    super();
  }

  public GeneratorException(int code) {
    super(code);
  }

  public GeneratorException(int code, Throwable cause) {
    super(code, cause);
  }

  public GeneratorException(int code, Object[] argCode) {
    super(code, argCode);
  }

  public GeneratorException(String message) {
    super(message);
  }

  public GeneratorException(String message, Object[] pars) {
    super(message, pars);
  }

  public GeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  public GeneratorException(Throwable cause) {
    super(cause);
  }

}

package pl.mpak.usedb.util.access;

public class AccessorException extends Exception {
  private static final long serialVersionUID = -9017392997324361748L;

  public AccessorException() {
    super();
  }

  public AccessorException(String message) {
    super(message);
  }

  public AccessorException(Throwable cause) {
    super(cause);
  }

  public AccessorException(String message, Throwable cause) {
    super(message, cause);
  }

}

package pl.mpak.usedb;
import pl.mpak.util.UtilException;

public class UseDBException extends UtilException {
  private static final long serialVersionUID = 1L;

  public UseDBException() {
    super();
  }

  public UseDBException(String message) {
    super(message);
  }

  public UseDBException(String message, Object[] pars) {
    super(message, pars);
  }

  public UseDBException(String message, Throwable cause) {
    super(message, cause);
  }

  public UseDBException(Throwable cause) {
    super(cause);
  }

}

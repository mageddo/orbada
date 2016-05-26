package pl.mpak.sky;

import pl.mpak.util.UtilException;

public class SkyException extends UtilException {
  private static final long serialVersionUID = 1L;

  public SkyException() {
    super();
  }

  public SkyException(String message) {
    super(message);
  }

  public SkyException(String message, Object[] pars) {
    super(message, pars);
  }

  public SkyException(String message, Throwable cause) {
    super(message, cause);
  }

  public SkyException(Throwable cause) {
    super(cause);
  }

}

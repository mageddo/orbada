package pl.mpak.orbada;

import pl.mpak.util.UtilException;

public class OrbadaException extends UtilException {
  private static final long serialVersionUID = 1L;

  static {
    registerErrorMessages(OrbadaException.class, new ErrorMessages());
  }
  
  public OrbadaException() {
  }

  public OrbadaException(int code) {
    super(code);
  }

  public OrbadaException(int code, Throwable cause) {
    super(code, cause);
  }

  public OrbadaException(int code, Object[] argCode) {
    super(code, argCode);
  }

  public OrbadaException(String message) {
    super(message);
  }

  public OrbadaException(String code, String message) {
    super(code, message);
  }

  public OrbadaException(String message, Object[] pars) {
    super(message, pars);
  }

  public OrbadaException(String message, Throwable cause) {
    super(message, cause);
  }

  public OrbadaException(Throwable cause) {
    super(cause);
  }

}

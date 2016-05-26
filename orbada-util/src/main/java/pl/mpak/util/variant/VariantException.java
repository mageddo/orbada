package pl.mpak.util.variant;

import pl.mpak.util.UtilException;

public class VariantException extends UtilException {
  private static final long serialVersionUID = 1L;
  
  public static final int ERR_02001_CANT_WRITE = 2001;
  public static final int ERR_02002_CANT_READ = 2002;
  public static final int ERR_02003_CANT_CONVERT = 2003;
  public static final int ERR_02004_DIVIDE_BY_ZERO = 2004;
  public static final int ERR_02005_READ_ARRAY_PROBLEM = 2005;

  public VariantException() {
    super();
  }

  public VariantException(int code) {
    super(code);
  }

  public VariantException(int code, Throwable cause) {
    super(code, cause);
  }

  public VariantException(int code, Object[] argCode) {
    super(code, argCode);
  }

  public VariantException(String message) {
    super(message);
  }

  public VariantException(String message, Object[] pars) {
    super(message, pars);
  }

  public VariantException(String message, Throwable cause) {
    super(message, cause);
  }

  public VariantException(Throwable cause) {
    super(cause);
  }

}

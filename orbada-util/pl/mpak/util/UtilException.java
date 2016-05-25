package pl.mpak.util;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Andrzej Ka³u¿a
 * 
 */
public class UtilException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private static HashMap<Class<? extends UtilException>, ErrorMessages> messagesList;
  
  static {
    messagesList = new HashMap<Class<? extends UtilException>, ErrorMessages>();
    registerErrorMessages(UtilException.class, new ErrorMessages());
  }
  
  public static void registerErrorMessages(Class<? extends UtilException> clazz, ErrorMessages errorMessages) {
    messagesList.put(clazz, errorMessages);
  }
  
  public static ErrorMessages getErrorMessages(Class<? extends UtilException> clazz) {
    ErrorMessages result = messagesList.get(clazz);
    if (result == null) {
      Class<?> c = clazz.getSuperclass();
      while (c != null) {
        result = messagesList.get(c);
        if (result != null) {
          break;
        }
        c = clazz.getSuperclass();
      }
    }
    return result;
  }
  
  private String code;
  private Object[] argCode;

  public UtilException() {
    super();
  }

  public UtilException(int code) {
    super();
    this.code = getErrorMessages(getClass()).getKey(code);
  }

  public UtilException(int code, Throwable cause) {
    super(cause);
    this.code = getErrorMessages(getClass()).getKey(code);
  }

  public UtilException(int code, Object[] argCode) {
    super();
    this.code = getErrorMessages(getClass()).getKey(code);
    if (argCode != null) {
      this.argCode = Arrays.copyOf(argCode, argCode.length);
    }
    else {
      this.argCode = null;
    }
  }

  public UtilException(String message) {
    super(message);
  }

  public UtilException(String code, String message) {
    super(code +": " +message);
    this.code = code;
  }

  public UtilException(String message, Object[] pars) {
    super(String.format(message, pars));
  }

  public UtilException(String message, Throwable cause) {
    super(message, cause);
  }

  public UtilException(Throwable cause) {
    super(cause);
  }
  
  public String getCode() {
    return code;
  }
  
  public Object[] getArgCode() {
    return argCode;
  }
  
  public String getMessage() {
    String message = super.getMessage();
    if (code != null || StringUtil.equals(message, "")) {
      if (argCode != null) {
        message = getErrorMessages(getClass()).getString(code, argCode);
      }
      else {
        message = getErrorMessages(getClass()).getString(code);
      }
    }
    if (getCause() != null) {
      if (getCause() instanceof UtilException) {
        message = message +"\n" +getCause().getMessage();
      }
      else {
        message = message +"\n" +getCause().getClass().getName() +": " +getCause().getMessage();
      }
    }
    return message;
  }
  
}

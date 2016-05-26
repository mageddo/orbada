package pl.mpak.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringManager {
  private ResourceBundle resourceBundle;

  StringManager(String packageName, ClassLoader loader) {
    super();
    resourceBundle = ResourceBundle.getBundle("i18n-" + packageName, Locale.getDefault(), loader);
  }

  public String getString(String key) {
    if (key == null) {
      throw new IllegalArgumentException("key == null");
    }

    try {
      return resourceBundle.getString(key);
    } catch (MissingResourceException ex) {
      String msg = "No resource string found for key '" + key + "'";
      ExceptionUtil.processException(ex);
      return msg;
    }
  }

  public String getString(String key, Object arg) {
    if (key == null) {
      throw new IllegalArgumentException("key == null");
    }

    Object[] args;
    if (arg == null) {
      args = new Object[0];
    } else {
      args = new Object[] { arg };
    }

    return getString(key, args);
  }

  public String getString(String key, Object[] args) {
    if (key == null) {
      throw new IllegalArgumentException("key == null");
    }

    if (args == null) {
      args = new Object[0];
    }

    final String str = getString(key);
    try {
      return MessageFormat.format(str, args);
    } catch (IllegalArgumentException ex) {
      String msg = "Error formatting i18 string. Key is '" + key + "'";
      ExceptionUtil.processException(ex);
      return msg + ": " + ex.toString();
    }
  }
}
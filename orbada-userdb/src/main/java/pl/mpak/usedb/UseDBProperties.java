package pl.mpak.usedb;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UseDBProperties {

  private static ResourceBundle properties = ResourceBundle.getBundle("usedb");
  
  public static String getAttribute(String name) {
    try {
      return properties.getString(name);
    }
    catch (MissingResourceException e) {
      return null;
    }
  }
  
  public static String getAttribute(String name, String defaultValue) {
    String value = null;
    try {
      value = properties.getString(name);
    }
    catch (MissingResourceException e) {
      ;
    }
    return ("".equals(value) || value == null) ? defaultValue : value;
  }
  
  public static String getTempDirectory() {
    return getAttribute("temp_directory", System.getProperty("java.io.tmpdir") +"/.usedb");
  }

  public static int getFetchRecordCount() {
    return Integer.parseInt(UseDBProperties.getAttribute("fetch_record_count", "50"));
  }

  public static int getCacheRecordCount() {
    return Integer.parseInt(UseDBProperties.getAttribute("cache_record_count", "200"));
  }
  
  public static String getUserSettingsPath() {
    return UseDBProperties.getAttribute("user_settings_path", System.getProperty("user.home") +"/.usedb/config");
  }

}

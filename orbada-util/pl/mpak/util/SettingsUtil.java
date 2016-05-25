package pl.mpak.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

public class SettingsUtil {
  
  private static HashMap<String, Properties> propertyList = new HashMap<String, Properties>();

  public static String getConfigPath(String project) {
    String path = System.getProperty("user.home") +"/.mPak/." +project;
    new File(path).mkdirs();
    return path;
  }
  
  public static String getConfigFile(String name) {
    return getConfigFile("util", name);
  }
  
  public static String getConfigFile(String project, String name) {
    return getConfigPath(project) +"/" +name +".properties";
  }
  
  public static Properties get(String name) {
    return get("util", name);
  }
  
  public static Properties get(String project, String name) {
    Properties props = propertyList.get(project +"/" +name);
    if (props == null) {
      props = new Properties();
      try {
        props.load(new FileInputStream(getConfigFile(project, name)));
      } catch (Exception e) {
        ;
      }
      propertyList.put(project +"/" +name, props);
    }
    return props;
  }
  
  public static void store(String name) {
    store("util", name);
  }
  
  public static void store(String project, String name) {
    Properties props = propertyList.get(project +"/" +name);
    if (props != null) {
      try {
        props.store(new FileOutputStream(getConfigFile(project, name)), "mPak Studio Util Library Settings For \"" +project +"\".\"" +name +"\"");
      } catch (Exception e) {
        ExceptionUtil.processException(e);
      }
    }
  }
  
}

package pl.mpak.util;

import java.util.HashMap;
import java.util.Map;

public class StringManagerFactory {
  private static final Map<String, StringManager> managerMap = new HashMap<String, StringManager>();

  public static synchronized StringManager getStringManager(Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException("clazz == null");
    }

    final String packageName = getPackageName(clazz);
    StringManager mgr = (StringManager) managerMap.get(packageName);
    if (mgr == null) {
      mgr = new StringManager(packageName, clazz.getClassLoader());
      managerMap.put(packageName, mgr);
    }
    return mgr;
  }

  public static synchronized StringManager getStringManager(Class<?> clazz, String packageName) {
    if (packageName == null) {
      throw new IllegalArgumentException("packageName == null");
    }

    StringManager mgr = (StringManager)managerMap.get(packageName);
    if (mgr == null) {
      mgr = new StringManager(packageName, clazz.getClassLoader());
      managerMap.put(packageName, mgr);
    }
    return mgr;
  }

  private static String getPackageName(Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException("clazz == null");
    }

    final String clazzName = clazz.getName();
    return clazzName.substring(0, clazzName.lastIndexOf('.'));
  }
}

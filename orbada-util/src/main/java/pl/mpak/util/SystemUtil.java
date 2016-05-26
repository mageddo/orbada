package pl.mpak.util;

public class SystemUtil {

  public static boolean isMacOs() {
    return System.getProperty("os.name").toLowerCase().startsWith("mac os");
  }
  
  public static boolean isLinux() {
    return System.getProperty("os.name").toLowerCase().startsWith("linux");
  }
  
  public static boolean isUnix() {
    return System.getProperty("os.name").toLowerCase().startsWith("x11");
  }
  
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().startsWith("windows");
  }
  
}

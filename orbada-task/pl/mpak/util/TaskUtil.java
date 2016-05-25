package pl.mpak.util;

public class TaskUtil {
  
  public static void sleep(long millis) {
    try {
      Thread.sleep(Math.max(1, millis));
    }
    catch (InterruptedException e) {
      ExceptionUtil.processException(e);
    }
  }
  
}

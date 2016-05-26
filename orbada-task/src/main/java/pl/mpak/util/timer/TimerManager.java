package pl.mpak.util.timer;

import java.util.HashMap;

public class TimerManager {
  
  private static HashMap<String, TimerQueue> timerList = new HashMap<String, TimerQueue>();

  public static TimerQueue getGlobal() {
    return getTimer("pl.mpak.timer.Global");
  }
  
  /**
   * <p>Wywo³anie utworzy Timer o nazwe name. Jeœli name istnieje to zostanie odszukany
   * i zwrócony.
   * @param name
   * @param isDaemon
   * @return
   */
  public static TimerQueue getTimer(String name) {
    synchronized (timerList) {
      TimerQueue timer = timerList.get(name);
      if (timer == null) {
        timer = new TimerQueue(name);
        timerList.put(name, timer);
      }
      return timer;
    }
  }
  
  /**
   * <p>Usuwa z listy TimerQueue o podanej nazwie wczeœniej anuluj¹c go.
   * @param name
   */
  public static void remove(String name) {
    synchronized (timerList) {
      TimerQueue timer = timerList.get(name);
      if (timer != null) {
        timer.dispose();
        timerList.remove(name);
      }
    }
  }
  
  public static HashMap<String, TimerQueue> getLock() {
    return timerList;
  }
  
}

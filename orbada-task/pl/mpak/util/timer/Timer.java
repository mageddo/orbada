package pl.mpak.util.timer;

public abstract class Timer implements Runnable {

  long interval = 0;
  private boolean enabled = true;
  private boolean cancel = false;
  private boolean once = false;
  TimerQueue timerManager = null;
  long lastExecuted = System.nanoTime(); //System.currentTimeMillis();
  long nextExecution;
  
  /**
   * @param interval - czas w mili sekundach
   */
  public Timer(long interval) {
    super();
    this.interval = interval *1000000L;
    nextExecution = lastExecuted +this.interval;
  }
  
  public Timer(long interval, boolean once) {
    this(interval);
    this.once = once;
  }

  public void setInterval(long interval) {
    this.interval = interval *1000000L;
    if (isEnabled() && !isCancel() && !isOnce()) {
      restart();
    }
  }

  public long getInterval() {
    return interval /1000000L;
  }

  public void setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      if (this.enabled) {
        lastExecuted = System.nanoTime(); //System.currentTimeMillis();
        nextExecution = lastExecuted +this.interval;
        synchronized (timerManager) {
          timerManager.notify();
        }
      }
    }
  }
  
  public void restart() {
    setEnabled(false);
    setEnabled(true);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void cancel() {
    this.cancel = true;
  }

  public boolean isCancel() {
    return cancel;
  }

  public boolean isOnce() {
    return once;
  }

}

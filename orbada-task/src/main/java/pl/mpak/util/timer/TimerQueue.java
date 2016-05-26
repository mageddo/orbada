package pl.mpak.util.timer;

import java.util.ArrayList;

import pl.mpak.util.ExceptionUtil;

public class TimerQueue extends Thread {
  
  private ArrayList<Timer> timerArray = new ArrayList<Timer>();
  private boolean stopExecute = false;

  public TimerQueue(String name) {
    super(name);
    //setPriority(MIN_PRIORITY);
    setDaemon(true);
    start();
  }
  
  public void dispose() {
    stopExecute = true;
    synchronized(this) {
      this.notify();
    }
    while (stopExecute) {
      yield();
    }
    synchronized(timerArray) {
      while (timerArray.size() > 0) {
        timerArray.remove(0);
      }
    }
    timerArray = null;
  }
  
  public void add(Timer timer) {
    synchronized(timerArray) {
      timerArray.add(timer);
      timer.lastExecuted = System.nanoTime(); //System.currentTimeMillis();
      timer.timerManager = this;
    }
    synchronized(this) {
      this.notify();
    }
  }
  
  public void run(Timer timer) {
    timer.lastExecuted = 0;
    synchronized(this) {
      this.notify();
    }
  }
  
  public int getCount() {
    synchronized(timerArray) {
      return timerArray.size();
    }
  }
  
  public Timer getTimer(int index) {
    synchronized(timerArray) {
      return timerArray.get(index);
    }
  }
  
  private long calcWait() {
    long waitTime = 10000000000L;
    long currTime = System.nanoTime(); //System.currentTimeMillis();
    
    synchronized (timerArray) {
      for (int i=0; i<timerArray.size(); i++) {
        Timer timer = timerArray.get(i);
        if (timer.isEnabled()) {
          if (waitTime > timer.nextExecution -currTime) {
            waitTime = timer.nextExecution -currTime;
          }
        }
      }
    }
    return waitTime < 1 ? 1 : waitTime;
  }

  public void run() {
    stopExecute = false;
    while (!stopExecute) {
      long waitTime = calcWait();
      
      try {
        synchronized(this) {
          wait(waitTime /1000000L, (int)(waitTime %1000000L));
        }
      }
      catch (InterruptedException e) {
        ;
      }
      
      if (!stopExecute) {
        synchronized (timerArray) {
          int i = 0;
          while (i < timerArray.size()) {
            Timer timer = timerArray.get(i);
            if (timer.isCancel()) {
              timerArray.remove(i);
            }
            else {
              long time = System.nanoTime();
              if (timer.isEnabled() && timer.nextExecution <= time) {
                timer.nextExecution = time +timer.interval;
//              if (timer.isEnabled() && timer.nextExecution <= System.currentTimeMillis()) {
//                timer.nextExecution = System.currentTimeMillis() +timer.getInterval();
                ;
                try {
                  timer.run();
                }
                catch(Throwable e) {
                  ExceptionUtil.processException(e);
                }
                timer.lastExecuted = time; //System.currentTimeMillis();
                if (timer.isOnce()) {
                  timer.cancel();
                }
              }
              if (timer.isCancel()) {
                timerArray.remove(i);
              }
              else {
                i++;
              }
            }
          }
        }
      }
    }
    stopExecute = false;
  }
}

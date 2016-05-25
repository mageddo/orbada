package pl.mpak.util.task;

import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.Generator;

public class TaskExecutor extends Thread {

  private TaskPool taskPool = null;
  private static Generator sequence = new Generator();
  private Task currentTask;
  
  public TaskExecutor(TaskPool taskSpool) {
    super("TaskExecuter-" +(new Long(sequence.getNextVal())).toString());
    this.taskPool = taskSpool;
    setPriority(Thread.MIN_PRIORITY);
    setDaemon(true);
  }
  
  static void pool(TaskPool pool) {
    (new TaskExecutor(pool)).start();
  }

  public void run() {
    taskPool.beginPool(this);
    try {
      while (true) {
        currentTask = taskPool.nextTask();
        if (currentTask != null && !currentTask.isCanceled()) {
          try {
            currentTask.startup();
            taskPool.beginTask(this, currentTask);
            currentTask.setExecuting(true);
            try {
              currentTask.run();
            }
            finally {
              currentTask.setExecuting(false);
              taskPool.endTask(this, currentTask);
              currentTask.finished();
              currentTask = null;
            }
          }
          catch(Throwable e) {
            ExceptionUtil.processException(e);
          }
        }
        else {
          break;
        }
      }
    }
    finally {
      taskPool.endPool(this);
    }
  }
  
  public Task getCurrentTask() {
    return currentTask;
  }

}

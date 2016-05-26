package pl.mpak.util.task;


public abstract class Task implements Runnable {
  
  private String description = null;
  private TaskPool taskPool;
  private volatile boolean canceled = false; 
  private volatile int percenExecution = 0;
  private volatile boolean executing = false;
  
  public Task() {}

  public Task(String description) {
    this.description = description;
  }
  
  protected void startup() {}
  
  protected void finished() {}
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  void setTaskPool(TaskPool taskPool) {
    this.taskPool = taskPool;
  }

  public TaskPool getTaskPool() {
    return taskPool;
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
    if (this.canceled && taskPool != null && !executing) {
      taskPool.removeTask(this);
    }
  }

  public int getPercenExecution() {
    return percenExecution;
  }

  public void setPercenExecution(int percenExecution) {
    this.percenExecution = percenExecution;
    if (this.percenExecution > 100) {
      this.percenExecution = 100;
    }
  }

  public boolean isExecuting() {
    return executing;
  }

  void setExecuting(boolean executing) {
    this.executing = executing;
  }
  
}

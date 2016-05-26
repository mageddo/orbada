package pl.mpak.util.task;

import java.util.EventObject;

public class TaskPoolEvent extends EventObject {
  private static final long serialVersionUID = -1L;

  private TaskExecutor taskExecutor;
  private Task task;
  
  public TaskPoolEvent(TaskExecutor taskExecutor, Task task) {
    super(taskExecutor);
    this.taskExecutor = taskExecutor;
    this.task = task;
  }

  public TaskExecutor getTaskExecuter() {
    return taskExecutor;
  }

  public Task getTask() {
    return task;
  }
}

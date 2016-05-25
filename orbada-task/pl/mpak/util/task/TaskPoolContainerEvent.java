package pl.mpak.util.task;

import java.util.EventObject;

public class TaskPoolContainerEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  private Task task;
  
  public TaskPoolContainerEvent(Task task) {
    super(task);
    this.task = task;
  }
  
  public Task getTask() {
    return task;
  }

}

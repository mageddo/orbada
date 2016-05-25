package pl.mpak.util.task;

import java.util.EventListener;

public interface TaskPoolContainerListener extends EventListener {

  public void addTask(TaskPoolContainerEvent e);

  public void removeTask(TaskPoolContainerEvent e);
  
}

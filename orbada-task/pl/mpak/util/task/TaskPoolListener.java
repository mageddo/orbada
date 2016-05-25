package pl.mpak.util.task;

import java.util.EventListener;

public interface TaskPoolListener extends EventListener {

  public void beginPool(TaskPoolEvent e);

  public void endPool(TaskPoolEvent e);

  public void beginTask(TaskPoolEvent e);

  public void endTask(TaskPoolEvent e);

}

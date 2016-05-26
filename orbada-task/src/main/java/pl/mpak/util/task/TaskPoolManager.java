package pl.mpak.util.task;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

public class TaskPoolManager {

  private static HashMap<String,TaskPool> taskPoolList = new HashMap<String,TaskPool>();
  private static ArrayList<TaskPool> taskPoolArray = new ArrayList<TaskPool>();
  private static EventListenerList taskPoolListenerList = new EventListenerList();

  public enum EventPool {
    BEGIN_POOL,
    END_POOL,
    BEGIN_TASK,
    END_TASK
  }

  public enum EventContainer {
    ADD_TASK,
    REMOVE_TASK,
  }

  public static int getTaskCount() {
    int result = 0;
    synchronized (taskPoolList) {
      for (int i=0; i<getCount(); i++) {
        result += getTaskPool(i).getCount();
      }
    }
    return result;
  }
  
  public static TaskPool getGlobal() {
    return getTaskPool("pl.mpak.task.Global");
  }

  public static TaskPool getTaskPool(String name) {
    TaskPool taskPool;
    synchronized (taskPoolList) {
      taskPool = taskPoolList.get(name);
      if (taskPool == null) {
        taskPool = new TaskPool(name);
        taskPoolList.put(name, taskPool);
        taskPoolArray.add(taskPool);
      }
    }
    return taskPool;
  }
  
  public static void remove(String name) {
    synchronized (taskPoolList) {
      taskPoolArray.remove(taskPoolList.get(name));
      taskPoolList.remove(name);
    }
  }
  
  public static int getCount() {
    return taskPoolArray.size();
  }
  
  public static TaskPool getTaskPool(int index) {
    synchronized (taskPoolList) {
      return taskPoolArray.get(index);
    }
  }
  
  public static Object getLock() {
    return taskPoolList;
  }
  
  public static void addTaskPoolListener(TaskPoolListener listener) {
    taskPoolListenerList.add(TaskPoolListener.class, listener);
  }
  
  public static void removeTaskPoolListener(TaskPoolListener listener) {
    taskPoolListenerList.remove(TaskPoolListener.class, listener);
  }
  
  public static void fireTaskPoolListener(EventPool event, TaskExecutor taskExecutor, Task task) {
    TaskPoolListener[] listeners = taskPoolListenerList.getListeners(TaskPoolListener.class);
    for (int i=0; i<listeners.length; i++) {
      switch(event) {
        case BEGIN_POOL:
          listeners[i].beginPool(new TaskPoolEvent(taskExecutor, task));
          break;
        case END_POOL:
          listeners[i].endPool(new TaskPoolEvent(taskExecutor, task));
          break;
        case BEGIN_TASK:
          listeners[i].beginTask(new TaskPoolEvent(taskExecutor, task));
          break;
        case END_TASK:
          listeners[i].endTask(new TaskPoolEvent(taskExecutor, task));
          break;
      }
    }
  }

  public static void addTaskPoolContainerListener(TaskPoolContainerListener listener) {
    taskPoolListenerList.add(TaskPoolContainerListener.class, listener);
  }
  
  public static void removeTaskPoolContainerListener(TaskPoolContainerListener listener) {
    taskPoolListenerList.remove(TaskPoolContainerListener.class, listener);
  }
  
  public static void fireTaskPoolContainerListener(EventContainer event, Task task) {
    TaskPoolContainerListener[] listeners = taskPoolListenerList.getListeners(TaskPoolContainerListener.class);
    for (int i=0; i<listeners.length; i++) {
      switch(event) {
        case ADD_TASK:
          listeners[i].addTask(new TaskPoolContainerEvent(task));
          break;
        case REMOVE_TASK:
          listeners[i].removeTask(new TaskPoolContainerEvent(task));
          break;
      }
    }
  }

}

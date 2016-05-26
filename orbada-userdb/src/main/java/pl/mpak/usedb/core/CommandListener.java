package pl.mpak.usedb.core;

import java.util.EventListener;
import java.util.EventObject;

public interface CommandListener extends EventListener {
  
  public void beforeExecute(EventObject e);
  
  public void afterExecute(EventObject e);
  
  public void errorPerformed(EventObject e);
}

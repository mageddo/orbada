package pl.mpak.usedb.core;

import java.util.EventListener;

public interface DatabaseManagerListener extends EventListener {

  public void databaseAdded(DatabaseManagerEvent e);
  
  public void databaseRemoved(DatabaseManagerEvent e);
  
}

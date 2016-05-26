package pl.mpak.usedb.core;

import java.util.EventListener;

public interface DatabaseQueryListener extends EventListener {

  public void queryAdded(DatabaseQueryEvent e);
  
  public void queryRemoved(DatabaseQueryEvent e);
  
}

package pl.mpak.usedb.core;

import java.util.EventListener;

public interface DatabaseCommandListener extends EventListener {

  public void commandAdded(DatabaseCommandEvent e);
  
  public void commandRemoved(DatabaseCommandEvent e);
  
}

package pl.mpak.usedb.core;

import java.util.EventListener;
import java.util.EventObject;

public interface DatabaseListener extends EventListener {

  public void beforeConnect(EventObject e);
  public void afterConnect(EventObject e);
  
  public void beforeDisconnect(EventObject e);
  public void afterDisconnect(EventObject e);

}

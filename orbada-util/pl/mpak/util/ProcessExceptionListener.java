package pl.mpak.util;

import java.util.EventListener;
import java.util.EventObject;

public interface ProcessExceptionListener extends EventListener {
  public void processException(EventObject e);
}

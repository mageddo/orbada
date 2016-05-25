package pl.mpak.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

public class ExceptionUtil {
  
  private final static EventListenerList exceptionListenerList = new EventListenerList();

  /**
   * Funkcja tworzy ci¹g znaków z opisem stosu Java w przypadku wyst¹pienia wyj¹tku
   * 
   * @param e
   * @return
   */
  public static String getStackTrace(Throwable e) {
    StringWriter sw = new StringWriter();
    try {
      PrintWriter pw = new PrintWriter(sw);
      try {
        e.printStackTrace(pw);
        return sw.toString();
      }
      finally {
        pw.close();
      }
    }
    finally {
      try {
        sw.close();
      }
      catch (IOException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  public static void addProcessExceptionListener(ProcessExceptionListener listener) {
    synchronized (exceptionListenerList) {
      exceptionListenerList.add(ProcessExceptionListener.class, listener);
    }
  }
  
  public static void removeProcessExceptionListener(ProcessExceptionListener listener) {
    synchronized (exceptionListenerList) {
      exceptionListenerList.remove(ProcessExceptionListener.class, listener);
    }
  }
  
  /**
   * Mechanizm globalnej obs³uga wyj¹tków
   * Domyœlnie, jeœli nie dodano ¿adnych listenerów to wywo³ywany jest Exception.printStackTrace()
   * @param e obs³ugiwany wyj¹tek 
   */
  public static void processException(Throwable e) {
    synchronized (exceptionListenerList) {
      ProcessExceptionListener listeners[] = exceptionListenerList.getListeners(ProcessExceptionListener.class);
      if (listeners.length == 0) {
        e.printStackTrace();
      }
      else {
        EventObject eo = new EventObject(e);
        for (int i=0; i<listeners.length; i++) {
          listeners[i].processException(eo);
        }
      }
    }
  }
  
}

package pl.mpak.usedb.core;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author akaluza
 * <p> Metody wywo³ywane przy zmianie pozycji kursora oraz przy otwieraniu, 
 * zamykaniu Query i odczytywaniu rekordów.  
 */
public interface QueryListener extends EventListener {
  
  public void beforeScroll(EventObject e);
  
  public void afterScroll(EventObject e);
  
  public void beforeOpen(EventObject e);
  
  public void afterOpen(EventObject e);
  
  public void beforeClose(EventObject e);
  
  public void afterClose(EventObject e);
  
  public void flushedPerformed(EventObject e);
  
  public void errorPerformed(EventObject e);
  
}

package pl.mpak.sky.gui.swing;

import java.util.EventListener;

/**
 * @author akaluza
 * <p>Globalny nas³uchiwacz wszystkich akcji
 */
public interface ActionGlobalListener extends EventListener {

  /**
   * <p>Zdarzenie wywo³ywane PRZED wywo³aniem dowolnego zdarzenia
   * @param e
   */
  public void beforeAction(ActionGlobalEvent e);

  /**
   * <p>Zdarzenie wywo³ywane PO wywo³aniem dowolnego zdarzenia.<br>
   * Zdarzenie bêdzie wywo³ane nawet wtedy gdy sama akcja wygeneruje b³¹d.
   * @param e
   */
  public void afterAction(ActionGlobalEvent e);
  
}

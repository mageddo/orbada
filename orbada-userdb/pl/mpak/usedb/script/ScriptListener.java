package pl.mpak.usedb.script;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author akaluza
 *
 */
public interface ScriptListener extends EventListener {

  public void beforeScript(EventObject e);
  
  /**
   * <p>W e.getSource() znajduje siê obiekt Command
   * @param e
   */
  public void beforeCommand(EventObject e);
  
  /**
   * <p>W e.getSource() znajduje siê obiekt Command
   * @param e
   */
  public void afterCommand(EventObject e);
  
  /**
   * <p>W e.getSource() znajduje siê obiekt Query
   * @param e
   */
  public void beforeQuery(EventObject e);
  
  /**
   * <p>W e.getSource() znajduje siê obiekt Query
   * @param e
   */
  public void afterQuery(EventObject e);
  
  /**
   * <p>Wyst¹pi³ b³ad. Funkcja powinna zwróciæ true jeœli wykonywanie 
   * skryptu ma byæ przerwane.
   * @param e
   * @return
   */
  public boolean errorOccured(ErrorScriptEventObject e);
  
  public void afterScript(EventObject e);
  
}

package pl.mpak.sky.gui.swing;

import java.awt.event.ActionEvent;
import java.util.EventObject;

/**
 * @author akaluza
 * <p>Zdarzenie globalne wszystkich akcji.
 * <p>W source znajduje siê obiekt klasy Action, a w actionEvent jego zdarzenie
 */
public class ActionGlobalEvent extends EventObject {
  private static final long serialVersionUID = -922402589954978868L;

  private transient ActionEvent actionEvent;
  
  /**
   * @param source Akcja która zosta³a wywo³ana
   * @param actionEvent Zdarzenie wywo³ywanej akcji
   */
  public ActionGlobalEvent(Action source, ActionEvent actionEvent) {
    super(source);
    this.actionEvent = actionEvent;
  }

  /**
   * <p>Zdarzenie akcji
   * @return
   */
  public ActionEvent getActionEvent() {
    return actionEvent;
  }

}

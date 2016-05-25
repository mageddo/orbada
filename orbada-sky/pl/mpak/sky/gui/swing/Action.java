package pl.mpak.sky.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.event.EventListenerList;

/**
 * @author akaluza
 * <p>Klasa akcji.
 * <p>Zawsze implementacja powinna dodawaæ zdarzenie akcji do listenera:
 * <pre><code>public class ExampleAction extends Action {
 *   public ExampleAction() {
 *     addActionListener(createActionListener());
 *   } 
 *   private ActionListener createActionListener() {
 *     return new ActionListener() {
 *       public void actionPerformed(ActionEvent e) {
 *         ...
 *       }
 *     };
 *   }
 * }</code></pre>
 * Nie przeci¹¿aj funkcji <code>public void actionPerformed(ActionEvent e)</code>
 */
public class Action extends BaseAction {
  private static final long serialVersionUID = 8565323239442303791L;

  private final static EventListenerList actionGlobalListenerList = new EventListenerList();
  private enum GlobalEvent {
    BEFORE_ACTION,
    AFTER_ACTION
  }
  
  private final EventListenerList actionListenerList = new EventListenerList();
  
  public Action() {
    super();
  }

  public Action(String title) {
    super(title);
  }

  public Action(String title, Icon icon) {
    super(title, icon);
  }
  
  public void addActionListener(ActionListener listener) {
    actionListenerList.add(ActionListener.class, listener);
  }

  public void removeActionListener(ActionListener listener) {
    actionListenerList.remove(ActionListener.class, listener);
  }

  public void actionPerformed(ActionEvent e) {
    fireActionGlobalListener(GlobalEvent.BEFORE_ACTION, this, e);
    try {
      for (ActionListener listener : actionListenerList.getListeners(ActionListener.class)) {
        listener.actionPerformed(e);
      }
    }
    finally {
      fireActionGlobalListener(GlobalEvent.AFTER_ACTION, this, e);
    }
  }
  
  /**
   * <p>Wywo³uje zdarzenie akcji
   */
  public void performe() {
    if (isEnabled()) {
      actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommandKey()));
    }
  }

  public static void addActionGlobalListener(ActionGlobalListener listener) {
    actionGlobalListenerList.add(ActionGlobalListener.class, listener);
  }

  public static void removeActionGlobalListener(ActionGlobalListener listener) {
    actionGlobalListenerList.remove(ActionGlobalListener.class, listener);
  }
  
  private static void fireActionGlobalListener(GlobalEvent event, Action a, ActionEvent e) {
    ActionGlobalEvent ge = new ActionGlobalEvent(a, e);
    for (ActionGlobalListener listener : actionGlobalListenerList.getListeners(ActionGlobalListener.class)) {
      switch (event) {
        case BEFORE_ACTION:
          listener.beforeAction(ge);
          break;
        case AFTER_ACTION:
          listener.afterAction(ge);
          break;
      }
    }
  }

}

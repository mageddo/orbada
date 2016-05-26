/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.actionstats;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPopupMenu;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.ActionGlobalEvent;

/**
 *
 * @author akaluza
 */
public class ActionInfo {

  private String commandKey;
  private String className;
  private final ArrayList<String> parentList = new ArrayList<String>();
  private final HashMap<String, String> propsValues = new HashMap<String, String>();
  private int keyCode;
  private int keyModifiers;
  private long callCount;
  private long callTime;

  public ActionInfo(ActionGlobalEvent e) {
    Action a = (Action)e.getSource();
    commandKey = a.getActionCommandKey() == null ? (a.getText() == null ? "" : a.getText()) : a.getActionCommandKey();
    className = a.getClass().getCanonicalName();
    for (Object o : a.getKeys()) {
      propsValues.put(o.toString(), a.getValue(o.toString()).toString());
    }
    Object src = e.getActionEvent().getSource();
    while (src != null) {
      final String className = src.getClass().getCanonicalName();
      if (className != null && !className.startsWith("javax.swing.")) {
        parentList.add(className);
      }
      src = getParent(src);
    }
    if (a.getShortCut() != null) {
      keyCode = a.getShortCut().getKeyCode();
      keyModifiers = a.getShortCut().getModifiers();
    }
  }

  protected Component getParent(Object src) {
    Component parent = null;
    if (src != null) {
      if (src instanceof Component) {
        Component comp = (Component)src;
        if (comp != null) {
          if (comp instanceof JPopupMenu) {
            parent = ((JPopupMenu)comp).getInvoker();
          }
          else {
            parent = comp.getParent();
          }
        }
      }
    }
    return parent;
  }

  public String getClassName() {
    return className;
  }

  public String getCommandKey() {
    return commandKey;
  }

  public int getKeyCode() {
    return keyCode;
  }

  public int getKeyModifiers() {
    return keyModifiers;
  }

  public ArrayList<String> getParentList() {
    return parentList;
  }

  public HashMap<String, String> getPropsValues() {
    return propsValues;
  }

  public long getCallCount() {
    return callCount;
  }

  public void setCallCount(long callCount) {
    this.callCount = callCount;
  }

  public void incCallCount() {
    this.callCount++;
  }

  public long getCallTime() {
    return callTime;
  }

  public void setCallTime(long callTime) {
    this.callTime = callTime;
  }

  public void incCallTime(long callTime) {
    this.callTime+= callTime;
  }

  public String toString() {
    return
      "[commandKey:" +commandKey +
      ",className:" +className +
      ",parentList:" +parentList +
      ",propeValues:" +propsValues +
      "]";
  }

}

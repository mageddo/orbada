/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleFunctionInfo;

/**
 *
 * @author akaluza
 */
public abstract class FunctionItemListener implements ItemListener {

  private OracleFunctionInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleFunctionInfo) {
      OracleFunctionInfo info = (OracleFunctionInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleFunctionInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleFunctionInfo info);

}

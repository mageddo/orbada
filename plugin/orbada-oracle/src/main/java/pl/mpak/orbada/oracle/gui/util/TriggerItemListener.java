/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleTableInfo;
import pl.mpak.orbada.oracle.dbinfo.OracleTriggerInfo;

/**
 *
 * @author akaluza
 */
public abstract class TriggerItemListener implements ItemListener {

  private OracleTriggerInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleTriggerInfo) {
      OracleTriggerInfo info = (OracleTriggerInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleTriggerInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleTriggerInfo info);

}

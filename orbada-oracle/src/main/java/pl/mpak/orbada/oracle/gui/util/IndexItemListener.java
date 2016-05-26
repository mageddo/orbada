/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleIndexInfo;

/**
 *
 * @author akaluza
 */
public abstract class IndexItemListener implements ItemListener {

  private OracleIndexInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleIndexInfo) {
      OracleIndexInfo info = (OracleIndexInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleIndexInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleIndexInfo info);

}

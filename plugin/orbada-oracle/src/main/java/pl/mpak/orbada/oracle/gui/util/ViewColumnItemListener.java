/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleViewColumnInfo;

/**
 *
 * @author akaluza
 */
public abstract class ViewColumnItemListener implements ItemListener {

  private OracleViewColumnInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleViewColumnInfo) {
      OracleViewColumnInfo info = (OracleViewColumnInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleViewColumnInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleViewColumnInfo info);

}

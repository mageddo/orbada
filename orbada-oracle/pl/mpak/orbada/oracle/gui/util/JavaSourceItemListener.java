/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleJavaSourceInfo;

/**
 *
 * @author akaluza
 */
public abstract class JavaSourceItemListener implements ItemListener {

  private OracleJavaSourceInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleJavaSourceInfo) {
      OracleJavaSourceInfo info = (OracleJavaSourceInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleJavaSourceInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleJavaSourceInfo info);

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleJavaClassInfo;

/**
 *
 * @author akaluza
 */
public abstract class JavaClassItemListener implements ItemListener {

  private OracleJavaClassInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleJavaClassInfo) {
      OracleJavaClassInfo info = (OracleJavaClassInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleJavaClassInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleJavaClassInfo info);

}

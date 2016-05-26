/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import pl.mpak.orbada.oracle.dbinfo.OracleSchemaInfo;

/**
 *
 * @author akaluza
 */
public abstract class SchemaItemListener implements ItemListener {

  private OracleSchemaInfo last;
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof OracleSchemaInfo) {
      OracleSchemaInfo info = (OracleSchemaInfo)e.getItem();
      if (!info.equals(last) || last == null) {
        itemChanged(info);
        last = info;
      }
    }
  }
  
  public OracleSchemaInfo getLast() {
    return last;
  }

  abstract public void itemChanged(OracleSchemaInfo info);

}

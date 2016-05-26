/*
 * TableActionProvider.java
 *
 * Created on 2007-10-30, 20:22:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.event.ActionEvent;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.providers.abs.ActionProvider;

/**
 *
 * @author akaluza
 */
public abstract class TableActionProvider extends ActionProvider {
  
  protected JTable table;

  public void setTable(JTable table) {
    this.table = table;
  }

  protected JTable getTable(ActionEvent event) {
    if (table == null) {
      return (JTable)getParent(event, JTable.class);
    }
    return table;
  }
  
  /**
   * <p>Tutaj powinno odbyæ siê sprawdzenie czy element ma byæ aktywny
   */
  public void checkEnable() {
    
  }
  
}

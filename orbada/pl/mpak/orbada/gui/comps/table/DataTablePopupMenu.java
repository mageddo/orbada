/*
 * DataTablePopupMenu.java
 *
 * Created on 2007-10-19, 22:41:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table;

import pl.mpak.orbada.gui.comps.table.cm.SelectTableColumnAction;
import pl.mpak.usedb.gui.swing.QueryTable;

/**
 *
 * @author akaluza
 */
public class DataTablePopupMenu extends TablePopupMenu {
  
  public DataTablePopupMenu(QueryTable popupComponent) {
    super(popupComponent);
    addSeparator();
    add(new SelectTableColumnAction(popupComponent, "<html><tr><td width=20 align=right>%1$s</td><td width=150><b>%2$s</b></td><td align=right>%3$s</td><td>%4$s</td></tr>"));
  }
  
}

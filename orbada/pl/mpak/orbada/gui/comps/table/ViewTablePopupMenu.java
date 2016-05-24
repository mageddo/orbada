/*
 * ViewTablePopupMenu.java
 * 
 * Created on 2007-10-11, 21:54:12
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.gui.comps.table;

import javax.swing.JTable;
import pl.mpak.orbada.gui.comps.table.cm.PasteFindTextAction;
import pl.mpak.orbada.gui.comps.table.cm.RestoreOryginalViewTableColumnPropsAction;
import pl.mpak.orbada.gui.comps.table.cm.SelectTableColumnAction;

/**
 *
 * @author akaluza
 */
public class ViewTablePopupMenu extends TablePopupMenu {

  public ViewTablePopupMenu(JTable popupComponent) {
    super(popupComponent);
    addSeparator();
    add(new SelectTableColumnAction(popupComponent, "<html><tr><td width=25 align=right>%1$s</td><td width=150><b>%2$s</b></td></tr>"));
    if (popupComponent instanceof ViewTable) {
      add(new RestoreOryginalViewTableColumnPropsAction((ViewTable)popupComponent));
      add(new PasteFindTextAction((ViewTable)popupComponent));
    }
  }

}

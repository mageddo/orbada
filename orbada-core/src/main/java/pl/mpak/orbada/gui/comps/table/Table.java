/*
 * Table.java
 *
 * Created on 2007-10-23, 21:34:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table;

import java.awt.Rectangle;
import javax.swing.JTable;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.actions.CmTab;
import pl.mpak.sky.gui.swing.comp.actions.CmTabBackward;

/**
 *
 * @author akaluza
 */
public class Table extends JTable {
  
  public Table() {
    super();
    setComponentPopupMenu(new ViewTablePopupMenu(Table.this));
    SwingUtil.addAction(Table.this, "cmTab", new CmTab(Table.this));
    SwingUtil.addAction(Table.this, "cmTabBackward", new CmTabBackward(Table.this));
  }
  
  public void changeSelection(final int row, final int column) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        changeSelection(row, column, false, false);
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            Rectangle rect = getCellRect(row, column, true);
            scrollRectToVisible(rect);
          }
        });
      }
    });
  }
  
}

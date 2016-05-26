/*
 * ColumnMoveToEndAction.java
 *
 * Created on 2007-10-08, 22:05:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import pl.mpak.orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ColumnMoveToEndAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private JTable table;
  
  public ColumnMoveToEndAction(JTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("ColumnMoveToEnd-text"));
    setShortCut(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK);
    setActionCommandKey("cmColumnMoveToEnd");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (table.getSelectedColumn() >= 0) {
          int sc = table.getSelectedColumn();
          table.getColumnModel().moveColumn(sc, table.getColumnCount() -1);
          table.changeSelection(table.getSelectedRow(), sc, false, false);
          table.revalidate();
          table.repaint();
        }
      }
    };
  }
  
}

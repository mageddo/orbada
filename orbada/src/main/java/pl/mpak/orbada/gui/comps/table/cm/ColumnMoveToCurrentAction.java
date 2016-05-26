/*
 * ColumnMoveToCurrentAction.java
 *
 * Created on 2007-10-08, 22:03:08
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
public class ColumnMoveToCurrentAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private JTable table;
  
  public ColumnMoveToCurrentAction(JTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("ColumnMoveToCurrent-text"));
    setShortCut(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK);
    setActionCommandKey("cmColumnMoveToCurrent");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (table.getSelectedColumn() >= 0) {
          int sc = table.getSelectedColumn();
          table.getColumnModel().moveColumn(table.getColumnCount() -1, sc);
          table.changeSelection(table.getSelectedRow(), sc, false, false);
          table.revalidate();
          table.repaint();
        }
      }
    };
  }
  
}

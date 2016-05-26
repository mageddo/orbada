/*
 * RowIncHeightAction.java
 *
 * Created on 2007-10-08, 21:55:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JTable;

import orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RowIncHeightAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private JTable table;
  
  public RowIncHeightAction(JTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("RowIncHeight-text"));
    setShortCut(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK);
    setActionCommandKey("cmRowIncHeight");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (table.getSelectedRow() >= 0) {
          if (table.getRowHeight(table.getSelectedRow()) +10 < 100) {
            table.setRowHeight(table.getSelectedRow(), table.getRowHeight(table.getSelectedRow()) +10);
            table.revalidate();
            table.repaint();
          }
        }
      }
    };
  }
  
}

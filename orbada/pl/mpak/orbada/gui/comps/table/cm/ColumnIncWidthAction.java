/*
 * ColumnIncWidthAction.java
 * 
 * Created on 2007-10-08, 20:44:26
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import pl.mpak.orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ColumnIncWidthAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private JTable table;

  public ColumnIncWidthAction(JTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("ColumnIncWidth-text"));
    setShortCut(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK);
    setActionCommandKey("cmColumnIncWidth");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (table.getSelectedColumn() >= 0) {
          TableColumn tc = table.getColumnModel().getColumn(table.getSelectedColumn());
          if (tc.getPreferredWidth() +10 < table.getWidth()) {
            tc.setPreferredWidth(tc.getPreferredWidth() +10);
            table.revalidate();
            table.repaint();
          }
        }
      }
    };
  }
  
}

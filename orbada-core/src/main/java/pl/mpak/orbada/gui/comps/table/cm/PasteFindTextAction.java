/*
 * SelectTableColumnAction.java
 *
 * Created on 2007-10-09, 17:11:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.table.TableModel;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.TextTransfer;

/**
 *
 * @author akaluza
 */
public class PasteFindTextAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private ViewTable table;
  
  public PasteFindTextAction(ViewTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("PasteFindTextAction-text"));
    setShortCut(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
    setActionCommandKey("cmPasteFindText");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String text = new TextTransfer().getClipboardContents();
        if (table.getSelectedColumn() >= 0 && !StringUtil.equals(text, "")) {
          TableModel tm = table.getModel();
          int modelIndex = table.getColumnModel().getColumn(table.getSelectedColumn()).getModelIndex();
          int r = 0;
          while (r < tm.getRowCount()) {
            Object value = tm.getValueAt(r, modelIndex);
            if (value != null && value.toString().toUpperCase().startsWith(text.toUpperCase())) {
              final int row = r;
              java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                  table.changeSelection(row, table.getSelectedColumn());
                }
              });
              return;
            }
            r++;
          }
        }
      }
    };
  }
  
}

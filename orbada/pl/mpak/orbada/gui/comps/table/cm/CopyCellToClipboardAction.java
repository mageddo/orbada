/*
 * CopyCellToClipboardAction.java
 *
 * Created on 2007-10-09, 22:40:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import pl.mpak.orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class CopyCellToClipboardAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private JTable table;
  
  public CopyCellToClipboardAction(JTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("CopyCellToClipoard-text"));
    setShortCut(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
    setActionCommandKey("cmCopyCellToClipboard");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (table.getSelectedColumn() >= 0 && table.getSelectedRow() >= 0) {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          String value;
          Object o = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
          if (o instanceof Variant) {
            try {
              value = ((Variant)o).getString();
            } catch (Exception ex) {
              value = "";
            }
          }
          else {
            if (o != null) {
              value = o.toString();
            }
            else {
              value = "";
            }
          }
          StringSelection data = new StringSelection(value);
          clipboard.setContents(data, data);
        }
      }
    };
  }
  
}

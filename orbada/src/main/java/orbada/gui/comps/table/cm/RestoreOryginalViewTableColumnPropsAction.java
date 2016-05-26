/*
 * ViewValueAction.java
 *
 * Created on 2007-11-26, 18:22:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import orbada.Consts;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RestoreOryginalViewTableColumnPropsAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private ViewTable table;

  public RestoreOryginalViewTableColumnPropsAction(ViewTable table) {
    this.table = table;
    setText(stringManager.getString("RestoreOryginalViewTableColumnPropsAction-text"));
    setShortCut(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
    setActionCommandKey("cmRestoreOryginalViewTableColumnProps");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        table.restoreOryginalColumnProps();
      }
    };
  }
  
}

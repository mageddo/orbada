/*
 * RefreshAction.java
 *
 * Created on 2007-10-13, 20:20:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RefreshAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private QueryTable table;
  
  public RefreshAction(QueryTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("Refresh-text"));
    setShortCut(KeyEvent.VK_F5, 0);
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif"));
    setActionCommandKey("cmRefresh");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int row = -1;
          int column = -1;
          if (table.getQuery().isActive()) {
            row = table.getSelectedRow();
            column = table.getSelectedColumn();
          }
          table.getQuery().refresh();
          if (row != -1) {
            table.changeSelection(row, column);
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    };
  }
  
}

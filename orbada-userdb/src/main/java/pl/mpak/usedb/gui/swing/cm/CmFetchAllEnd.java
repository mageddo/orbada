package pl.mpak.usedb.gui.swing.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.Messages;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.util.ExceptionUtil;

public class CmFetchAllEnd extends Action {
  private static final long serialVersionUID = 1L;
  
  private QueryTable table;
  
  public CmFetchAllEnd(QueryTable table) {
    super(Messages.getString("CmFetchAllEnd.text")); //$NON-NLS-1$
    setShortCut(KeyEvent.VK_END, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK);
    this.table = table;
    setActionCommandKey("cmFetchAllEnd"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (table != null && table.getQuery() != null && table.getQuery().isActive()) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              try {
                table.getQuery().flushAll();
                if (table.getQuery().getFlushMode() == Query.FlushMode.fmNone || table.getQuery().getFlushMode() == Query.FlushMode.fmSynch) {
                  table.changeSelection(table.getRowCount() -1, table.getSelectedColumn());
                }
              } catch (Exception ex) {
                ExceptionUtil.processException(ex);
              }
            }
          });
        }
      }
    };
  }

}

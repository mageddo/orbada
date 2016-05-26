package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;

public class CmEnter extends Action {
  private static final long serialVersionUID = 1L;

  private JTable table;
  
  public CmEnter(JTable table) {
    super();
    this.table = table;
    setText(Messages.getString("CmEnter.text")); //$NON-NLS-1$
    setShortCut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
    setActionCommandKey("cmEnter"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int index = table.getSelectedColumn();
        if (index == -1 && table.getColumnCount() > 0) {
          table.changeSelection(table.getSelectedRow(), 0, false, false);
        }
        else if (index +1 < table.getColumnCount()) {
          table.changeSelection(table.getSelectedRow(), index +1, false, false);
        }
        else {
          table.changeSelection(table.getSelectedRow(), 0, false, false);
        }
      }
    };
  }

}

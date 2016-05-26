package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;

public class CmTab extends Action {
  private static final long serialVersionUID = 5838612703525568618L;

  private Component component;
  
  public CmTab(Component component) {
    super();
    this.component = component;
    setText(Messages.getString("CmTab.text")); //$NON-NLS-1$
    setShortCut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
    setActionCommandKey("cmTab"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        component.transferFocus();
      }
    };
  }

}

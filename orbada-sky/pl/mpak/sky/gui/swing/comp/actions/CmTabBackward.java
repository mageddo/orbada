package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import pl.mpak.sky.gui.swing.Action;

public class CmTabBackward extends Action {
  private static final long serialVersionUID = -40184329295642555L;

  private Component component;
  
  public CmTabBackward(Component component) {
    super();
    this.component = component;
    setText("TabBackward");
    setShortCut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK));
    setActionCommandKey("cmTabBackward");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        component.transferFocusBackward();
      }
    };
  }

}

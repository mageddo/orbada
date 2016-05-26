package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;

public class CmSelectAllEdit extends Action {
  private static final long serialVersionUID = 1L;

  private JTextComponent textComponent;
  
  public CmSelectAllEdit(JTextComponent textComponent) {
    super();
    this.textComponent = textComponent;
    setText(Messages.getString("CmSelectAllEdit.text")); //$NON-NLS-1$
    setShortCut(KeyEvent.VK_A, KeyEvent.CTRL_MASK);
    setActionCommandKey("cmSelectAllEdit"); //$NON-NLS-1$
    addActionListener(createActionListener());
  } 

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textComponent.selectAll();
      }
    };
  }

}

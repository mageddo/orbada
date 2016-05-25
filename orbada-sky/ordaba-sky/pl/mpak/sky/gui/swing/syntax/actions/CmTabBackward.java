package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.SkySetting;

public class CmTabBackward extends CmTextArea {
  private static final long serialVersionUID = -6235761592004399179L;

  public CmTabBackward(JTextArea textArea) {
    super(textArea, Messages.getString("CmTabBackward.text"), KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmTabBackward"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textArea.getSelectionEnd() == textArea.getSelectionStart() || !SkySetting.getBoolean(SkySetting.CmTab_TabMoveSelected, true)) {
          textArea.transferFocusBackward();
        }
        else {
          CmShiftSelectedLeft.shift(textArea);
        }
      }
    };
  }

}

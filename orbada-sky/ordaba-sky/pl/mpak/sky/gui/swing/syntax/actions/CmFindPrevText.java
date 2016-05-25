package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.FindTextDialog;

public class CmFindPrevText extends CmTextArea {
  private static final long serialVersionUID = 5203368814455225289L;

  public CmFindPrevText(JTextArea textArea) {
    super(textArea, Messages.getString("CmFindPrevText.text"), KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmFindPrevText"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FindTextDialog.findPrev(textArea);
      }
    };
  }
  
}

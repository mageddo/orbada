package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

public class CmSelectCurrentText extends CmTextArea {
  private static final long serialVersionUID = -7829859405804367189L;

  public CmSelectCurrentText(JTextArea textArea) {
    super(textArea, Messages.getString("CmSelectCurrentText.text"), KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmSelectCurrentText"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textArea instanceof SyntaxEditor) {
          ((SyntaxEditor)textArea).getCurrentText();
          if (((SyntaxEditor)textArea).getStartCurrentText() != ((SyntaxEditor)textArea).getEndCurrentText()) {
            ((SyntaxEditor)textArea).setSelectionStart(((SyntaxEditor)textArea).getStartCurrentText());
            ((SyntaxEditor)textArea).setSelectionEnd(((SyntaxEditor)textArea).getEndCurrentText());
          }
        }
      }
    };
  }

}

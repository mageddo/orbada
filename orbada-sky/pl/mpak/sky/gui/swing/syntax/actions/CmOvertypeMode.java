package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

public class CmOvertypeMode extends CmTextArea {
  private static final long serialVersionUID = -2437398811403008799L;

  public CmOvertypeMode(JTextArea textArea) {
    super(textArea, Messages.getString("CmOvertypeMode.text"), KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0)); //$NON-NLS-1$
    setActionCommandKey("cmOvertypeMode"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textArea instanceof SyntaxEditor) {
          ((SyntaxEditor)textArea).setOvertypeMode(!((SyntaxEditor)textArea).isOvertypeMode());
        }
      }
    };
  }

}

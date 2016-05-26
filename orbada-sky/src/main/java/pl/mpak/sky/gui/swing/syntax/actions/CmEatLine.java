package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import pl.mpak.sky.Messages;

public class CmEatLine extends CmTextArea {
  private static final long serialVersionUID = -4510401006602454456L;

  public CmEatLine(JTextArea textArea) {
    super(textArea, Messages.getString("CmEatLine.text"), KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmEatLine"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!textArea.isEditable()) {
          return;
        }
        try {
          int start = Utilities.getRowStart(textArea, textArea.getCaretPosition());
          int end = Utilities.getRowEnd(textArea, textArea.getCaretPosition());
          textArea.getDocument().remove(start, end -start +1);
        } catch (BadLocationException e1) {
          ;
        }
      }
    };
  }
  
}

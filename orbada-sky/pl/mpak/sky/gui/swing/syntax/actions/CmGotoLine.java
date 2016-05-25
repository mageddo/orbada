package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.GotoLineDialog;

public class CmGotoLine extends CmTextArea {
  private static final long serialVersionUID = 5203368814455225289L;

  public CmGotoLine(JTextArea textArea) {
    super(textArea, Messages.getString("CmGotoLine.text"), KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmGotoLine"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          int newLine = GotoLineDialog.show(SwingUtil.getRootFrame(), textArea.getLineCount());
          if (newLine != -1) {
            textArea.setCaretPosition(textArea.getLineStartOffset(newLine));
          }
        } catch (BadLocationException e1) {
          ;
        }
      }
    };
  }
  
}

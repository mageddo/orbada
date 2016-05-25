package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;

public class CmSetBookmark extends CmTextArea {
  private static final long serialVersionUID = -4871280213916428055L;

  private int index;
  
  public CmSetBookmark(JTextArea textArea, int index) {
    super(textArea, Messages.getString("CmSetBookmark.text") +index, KeyStroke.getKeyStroke(KeyEvent.VK_0 +index, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)); //$NON-NLS-1$
    this.index = index;
    setActionCommandKey("cmSetBookmark" +index); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textArea.getDocument() instanceof SyntaxDocument) {
          SyntaxDocument sd = (SyntaxDocument)textArea.getDocument();
          try {
            int line = textArea.getLineOfOffset(textArea.getCaretPosition());
            sd.setBookmark(index, line, textArea.getCaretPosition() -textArea.getLineStartOffset(line));
            textArea.repaint();
          } catch (BadLocationException e1) {
            ;
          }
        }
      }
    };
  }
  
}

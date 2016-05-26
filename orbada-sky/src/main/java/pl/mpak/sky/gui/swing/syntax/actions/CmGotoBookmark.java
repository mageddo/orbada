package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.Bookmark;

public class CmGotoBookmark extends CmTextArea {
  private static final long serialVersionUID = -4871280213916428055L;

  private int index;
  
  public CmGotoBookmark(JTextArea textArea, int index) {
    super(textArea, Messages.getString("CmGotoBookmark.text") +index, KeyStroke.getKeyStroke(KeyEvent.VK_0 +index, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    this.index = index;
    setActionCommandKey("cmGotoBookmark" +index); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textArea.getDocument() instanceof SyntaxDocument) {
          SyntaxDocument sd = (SyntaxDocument)textArea.getDocument();
          Bookmark bm = sd.getBookmark(index);
          if (bm != null && bm.getLine() < textArea.getLineCount()) {
            try {
              int endOffset = textArea.getLineEndOffset(bm.getLine()) -1;
              textArea.setCaretPosition(Math.min(textArea.getLineStartOffset(bm.getLine()) +bm.getColumn(), endOffset));
            } catch (BadLocationException e1) {
              ;
            }
          }
        }
      }
    };
  }
  
}

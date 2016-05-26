package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;

public class CmSelectWord extends CmTextArea {
  private static final long serialVersionUID = -3214162298393341515L;

  public CmSelectWord(JTextArea textArea) {
    super(textArea, Messages.getString("CmSelectWord.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.selectWordAction);
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int end = textArea.getCaretPosition();
        if (textArea.getCaretPosition() < textArea.getDocument().getLength() && textArea.getText().charAt(textArea.getCaretPosition()) != '\n') {
          end = SwingUtil.getNextWord(textArea, textArea.getCaretPosition());
        }
        int start = SwingUtil.getPreviousWord(textArea, end);
        textArea.setSelectionStart(start);
        textArea.setSelectionEnd(end);
      }
    };
  }
  
}

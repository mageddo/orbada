package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;

public class CmEatLeftWord extends CmTextArea {
  private static final long serialVersionUID = -344150579088985336L;

  public CmEatLeftWord(JTextArea textArea) {
    super(textArea, Messages.getString("CmEatLeftWord.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.deletePrevWordAction);
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!textArea.isEditable()) {
          return;
        }
        try {
          int end = textArea.getCaretPosition();
          int start = SwingUtil.getPreviousWord(textArea, end);
          textArea.getDocument().remove(start, end -start);
        } catch (BadLocationException e1) {
          ;
        }
      }
    };
  }
  
}

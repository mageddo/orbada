package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;

public class CmMoveToPreviousWord extends CmTextArea {
  private static final long serialVersionUID = -9019626498963843682L;

  public CmMoveToPreviousWord(JTextArea textArea) {
    super(textArea, Messages.getString("CmMoveToPreviousWord.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.previousWordAction);
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textArea.setCaretPosition(SwingUtil.getPreviousWord(textArea, textArea.getCaretPosition()));
      }
    };
  }
  
}

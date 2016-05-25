package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;

public class CmSelectPreviousTextPart extends CmTextArea {
  private static final long serialVersionUID = -6205013545760984586L;

  public CmSelectPreviousTextPart(JTextArea textArea) {
    super(textArea, Messages.getString("CmSelectPreviousTextPart.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.selectionPreviousWordAction);
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int pos = SwingUtil.getPreviousWord(textArea, textArea.getCaretPosition());
        textArea.moveCaretPosition(pos);
      }
    };
  }
  
}

package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;

public class CmMoveToNextWord extends CmTextArea {
  private static final long serialVersionUID = 3456614198845151510L;

  public CmMoveToNextWord(JTextArea textArea) {
    super(textArea, Messages.getString("CmMoveToNextWord.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.nextWordAction);
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textArea.setCaretPosition(SwingUtil.getNextWord(textArea, textArea.getCaretPosition()));
      }
    };
  }
  
}

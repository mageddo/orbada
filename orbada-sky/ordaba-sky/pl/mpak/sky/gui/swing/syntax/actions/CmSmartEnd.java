package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.SkySetting;

public class CmSmartEnd extends CmTextArea {
  private static final long serialVersionUID = 706308687913470749L;

  public CmSmartEnd(JTextArea textArea) {
    super(textArea, Messages.getString("CmSmartEnd.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.endLineAction);
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          int pos = textArea.getCaretPosition();
          int line = textArea.getLineOfOffset(pos);
          int start = textArea.getLineStartOffset(line);
          int end = textArea.getLineEndOffset(line);
          if (line != textArea.getLineOfOffset(end)) {
            end--;
          }
          if (SkySetting.getBoolean(SkySetting.SyntaxEditor_SmartEnd, false)) {
            String text = textArea.getText();
            int index = end;
            while (index > start && Character.isWhitespace(text.charAt(index -1))) {
              index--;
            }
            if (pos < index || pos == end) {
              textArea.setCaretPosition(index);
            }
            else {
              textArea.setCaretPosition(end);
            }
          }
          else {
            textArea.setCaretPosition(end);
          }
        } catch (BadLocationException e1) {
          ;
        }
      }
    };
  }
  
}

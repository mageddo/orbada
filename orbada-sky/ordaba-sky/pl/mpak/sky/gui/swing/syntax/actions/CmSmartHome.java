package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.SkySetting;

public class CmSmartHome extends CmTextArea {
  private static final long serialVersionUID = -86002292173491970L;

  public CmSmartHome(JTextArea textArea) {
    super(textArea, Messages.getString("CmSmartHome.text"), null); //$NON-NLS-1$
    setActionCommandKey(DefaultEditorKit.beginLineAction);
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
          int index = start;
          if (line != textArea.getLineOfOffset(end)) {
            end--;
          }
          if (SkySetting.getBoolean(SkySetting.SyntaxEditor_SmartHome, false)) {
            String text = textArea.getText(); 
            while (index < end && Character.isWhitespace(text.charAt(index))) {
              index++;
            }
            if (pos > index || pos == start) {
              textArea.setCaretPosition(index);
            }
            else {
              textArea.setCaretPosition(start);
            }
          }
          else {
            textArea.setCaretPosition(start);
          }
        } catch (BadLocationException e1) {
          ;
        }
      }
    };
  }
  
}

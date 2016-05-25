package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.util.array.StringList;

public class CmShiftSelectedLeft extends CmTextArea {
  private static final long serialVersionUID = -3559244065829721430L;

  public CmShiftSelectedLeft(JTextArea textArea) {
    super(textArea, Messages.getString("CmShiftSelectedLeft.text"), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmShiftSelectedLeft"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        shift(textArea);
      }
    };
  }
  
  public static void shift(JTextArea textArea) {
    if (!textArea.isEditable()) {
      return;
    }
    int start = textArea.getSelectionStart();
    String text = textArea.getSelectedText();
    if (text != null) {
      if (text.length() > 0 && text.charAt(text.length() -1) == '\n') {
        textArea.setSelectionEnd(textArea.getSelectionEnd() -1);
        text = textArea.getSelectedText();
      }
      StringList sl = new StringList(false);
      sl.setText(text);
      for (int i=0; i<sl.size(); i++) {
        if (sl.get(i).length() > 0 && sl.get(i).charAt(0) == ' ') {
          sl.set(i, sl.get(i).substring(1));
        }
      }
      text = sl.getText();
      textArea.replaceSelection(text);
      textArea.setSelectionStart(start);
      textArea.setSelectionEnd(start +text.length());
    }
  }
  
}

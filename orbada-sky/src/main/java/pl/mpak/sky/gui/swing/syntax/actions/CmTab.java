package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.StringUtil;


public class CmTab extends CmTextArea {
  private static final long serialVersionUID = 50178489662692979L;

  public CmTab(JTextArea textArea) {
    super(textArea, Messages.getString("CmTab.text"), KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0)); //$NON-NLS-1$
    setActionCommandKey("cmTab"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textArea.getSelectionEnd() == textArea.getSelectionStart() || !SkySetting.getBoolean(SkySetting.CmTab_TabMoveSelected, true)) {
          if (textArea instanceof SyntaxEditor && ((SyntaxEditor)textArea).getDocument() instanceof SyntaxDocument) {
            SyntaxEditor se = (SyntaxEditor)textArea;
            SyntaxDocument sd = (SyntaxDocument)se.getDocument();
            if (sd.applySnippet(null, false) != null) {
              return;
            }
          }
          if (SkySetting.getBoolean(SkySetting.CmTab_TabAsSpaces, false)) {
            textArea.insert(StringUtil.padLeft("", SkySetting.getInteger(SkySetting.SyntaxEditor_TabToSpaceCount, 2)), textArea.getCaretPosition());
          }
          else {
            textArea.transferFocus();
          }
        }
        else {
          CmShiftSelectedRight.shift(textArea);
        }
      }
    };
  }

}

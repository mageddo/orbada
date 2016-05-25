package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

public class CmAutoComplete extends CmTextArea {
  private static final long serialVersionUID = -7829859405804367189L;

  public CmAutoComplete(JTextArea textArea) {
    super(textArea, Messages.getString("CmAutoComplete.text"), KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmAutoComplete"); //$NON-NLS-1$
    addActionListener(createActionListener());
    textArea.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }
      public void keyReleased(KeyEvent e) {
      }
      public void keyTyped(KeyEvent e) {
        String achs = SkySetting.getString(SkySetting.CmAutoComplete_AutoCompleteActiveChars, SkySetting.Default_CmAutoComplete_AutoCompleteActiveChars);
        String ichs = SkySetting.getString(SkySetting.CmAutoComplete_AutoCompleteInactiveChars, SkySetting.Default_CmAutoComplete_AutoCompleteInactiveChars);
        if (SkySetting.getBoolean(SkySetting.CmAutoComplete_AutoCompleteDot, true)) {
          if (achs.indexOf(e.getKeyChar()) >= 0 && e.getModifiers() == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                activate();
              }
            });
          }
          else if (ichs.indexOf(e.getKeyChar()) >= 0 && e.getModifiers() == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                inactivate();
              }
            });
          }
        }
      }
    });
  }
  
  private void inactivate() {
    if (textArea instanceof SyntaxEditor && textArea.isEditable()) {
      ((SyntaxEditor)textArea).getAutoComplete().setActive(false);
    }
  }

  private void activate() {
    if (textArea instanceof SyntaxEditor && textArea.isEditable()) {
      ((SyntaxEditor)textArea).getAutoComplete().setBracketMode(false);
      ((SyntaxEditor)textArea).getAutoComplete().setActive(true);
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        activate();
      }
    };
  }

}

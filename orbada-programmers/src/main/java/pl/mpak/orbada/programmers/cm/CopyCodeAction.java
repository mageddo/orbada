/*
 * CopyCodeAction.java
 *
 * Created on 2007-11-03, 14:52:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.orbada.programmers.gui.CopyCodeDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class CopyCodeAction extends Action {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("programmers");

  private SyntaxEditor editor;
  private IApplication application;
  private boolean calling = false;
  
  public CopyCodeAction(IApplication application) {
    super();
    setText(stringManager.getString("CopyCodeAction-text"));
    this.application = application;
    setSmallIcon(new ImageIcon(getClass().getResource("/res/icons/copy-code16.gif")));
    setShortCut(KeyEvent.VK_C, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setTooltip(stringManager.getString("CopyCodeAction-hint"));
    setActionCommandKey("CopyCodeAction");
    addActionListener(createActionListener());
    setEnabled(false);
  }
  
  public void setEditor(SyntaxEditor editor) {
    this.editor = editor;
  }
  
  @Override
  public void setEnabled(boolean set) {
    if (!calling) {
      super.setEnabled(set);
    }
    else {
      super.setEnabled(false);
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (editor == null && FocusManager.getCurrentManager().getFocusOwner() instanceof SyntaxEditor) {
          editor = (SyntaxEditor)FocusManager.getCurrentManager().getFocusOwner();
        }
        if (editor != null) {
          final String text = editor.getCurrentText();
          if (!StringUtil.isEmpty(text)) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                setEnabled(false);
                calling = true;
                try {
                  CopyCodeDialog.show(text, application);
                }
                finally {
                  calling = false;
                }
              }
            });
          }
        }
      }
    };
  }
  
}

/*
 * PasteCodeAction.java
 *
 * Created on 2007-11-03, 14:56:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.cm;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.array.StringList;

/**
 *
 * @author akaluza
 */
public class PasteCodeAction extends Action {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("programmers");

  private SyntaxEditor editor;
  private IApplication application;
  
  public PasteCodeAction(IApplication application) {
    super();
    setText(stringManager.getString("PasteCodeAction-text"));
    this.application = application;
    setSmallIcon(new ImageIcon(getClass().getResource("/pl/mpak/res/icons/paste-code16.gif")));
    setShortCut(KeyEvent.VK_V, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setTooltip(stringManager.getString("PasteCodeAction-hint"));
    setActionCommandKey("PasteCodeAction");
    addActionListener(createActionListener());
    setEnabled(false);
  }
  
  public void setEditor(SyntaxEditor editor) {
    this.editor = editor;
  }
  
  private String stripCode(String data) {
    StringList sl = new StringList();
    sl.setText(data);
    for (int i=0; i<sl.size(); i++) {
      String line = sl.get(i);
      if (!StringUtil.isEmpty(line) && line.indexOf("\"") < 0 && line.indexOf("'") < 0) {
        sl.set(i, "/* " +line +" */");
        continue;
      }
      int aFirst = line.indexOf("'");
      int cFirst = line.indexOf("\"");
      int last = Math.max(line.lastIndexOf("'"), line.lastIndexOf("\""));
      if (cFirst >= 0 && (aFirst < 0 || aFirst > cFirst)) {
        line = line.substring(cFirst +1, last);
        aFirst = -1;
      }
      else if (aFirst >= 0 && (cFirst < 0 || cFirst > aFirst) && last > aFirst) {
        line = line.substring(aFirst +1, last);
        cFirst = -1;
      }
      if (line.lastIndexOf("\\n") >= 0) {
        line = line.substring(0, line.lastIndexOf("\\n"));
      }
      StringBuilder sb = new StringBuilder();
      for (int c=0; c<line.length(); c++) {
        char ch = line.charAt(c);
        if (ch == '\\' && c < line.length() -1 && 
          (line.charAt(c +1) == '"' || line.charAt(c +1) == '\'' || line.charAt(c +1) == '\\')) {
          continue;
        }
        if (ch == '\'' && c < line.length() -1 && line.charAt(c +1) == '\'' && aFirst != -1) {
          sb.append(ch);
          c++;
          continue;
        }
        if (ch == '"' && c < line.length() -1 && line.charAt(c +1) == '"' && cFirst != -1) {
          sb.append(ch);
          c++;
          continue;
        }
        sb.append(ch);
      }
      sl.set(i, sb.toString());
    }
    return sl.getText();
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (editor == null && FocusManager.getCurrentManager().getFocusOwner() instanceof SyntaxEditor) {
          editor = (SyntaxEditor)FocusManager.getCurrentManager().getFocusOwner();
        }
        if (editor != null) {
          try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable content = clipboard.getContents(null);
            if (content != null) {
              String data = (String)content.getTransferData(DataFlavor.stringFlavor);
              data = stripCode(data);
              if (data != null) {
                if (editor.getSelectionEnd() != editor.getSelectionStart()) {
                  editor.replaceSelection(data);
                }
                else {
                  editor.insert(data, editor.getCaretPosition());
                }
              }
            }
          } catch (Throwable ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    };
  }
  
}

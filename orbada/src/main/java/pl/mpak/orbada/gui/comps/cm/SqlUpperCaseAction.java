package pl.mpak.orbada.gui.comps.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import pl.mpak.orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SqlUpperCaseAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public SqlUpperCaseAction() {
    setText(stringManager.getString("SqlUpperCaseAction-text"));
    setShortCut(KeyEvent.VK_UP, KeyEvent.ALT_MASK);
    setActionCommandKey("cmSqlUpperCase");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SyntaxEditor se = (SyntaxEditor)getParent(e, SyntaxEditor.class);
        if (se != null) {
          String selectedText = se.getSelectedText();
          if (selectedText != null) {
            selectedText = SQLUtil.sqlChangeCharCase(selectedText, StringUtil.CharCase.ecUpperCase);
            se.replaceSelection(selectedText);
          }
        }
      }
    };
  }
  
}

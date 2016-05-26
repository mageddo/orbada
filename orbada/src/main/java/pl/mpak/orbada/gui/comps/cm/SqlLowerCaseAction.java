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
public class SqlLowerCaseAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public SqlLowerCaseAction() {
    setText(stringManager.getString("SqlLowerCaseAction-text"));
    setShortCut(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK);
    setActionCommandKey("cmSqlLowerCase");
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
            selectedText = SQLUtil.sqlChangeCharCase(selectedText, StringUtil.CharCase.ecLowerCase);
            se.replaceSelection(selectedText);
          }
        }
      }
    };
  }
  
}

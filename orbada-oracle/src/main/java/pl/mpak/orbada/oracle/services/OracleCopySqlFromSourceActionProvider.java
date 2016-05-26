/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.util.OracleUtil;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.TextTransfer;

/**
 *
 * @author akaluza
 */
public class OracleCopySqlFromSourceActionProvider extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof SyntaxEditor) {
          SyntaxEditor textArea = (SyntaxEditor)getComponent();
          try {
            new TextTransfer().setClipboardContents(OracleUtil.prepareSqlFromPLSQL(textArea.getTokensCurrentText()));
          } catch (BadLocationException ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    };
  }

  @Override
  public boolean isForComponent(Database database, String actionType) {
    if (database == null || !OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType())) {
      return false;
    }
    if (StringUtil.anyOfString(actionType, new String[] {
      "oracle-package-source-actions",
      "oracle-function-source-actions",
      "oracle-procedure-source-actions",
      "oracle-type-source-actions",
      "oracle-trigger-source-actions"}) == -1) {
      return false;
    }

    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/sky/res/copy.gif"));
    setText(getDescription());
    setTooltip(stringManager.getString("OracleCopySqlFromSourceActionProvider-hint"));
    setActionCommandKey("OracleCopySqlFromSourceActionProvider");
    addActionListener(createActionListener());
    
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("OracleCopySqlFromSourceActionProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

}

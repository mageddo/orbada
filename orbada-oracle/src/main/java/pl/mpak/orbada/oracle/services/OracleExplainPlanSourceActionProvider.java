/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.universal.ExplainPlanDialog;
import pl.mpak.orbada.oracle.util.OracleUtil;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class OracleExplainPlanSourceActionProvider extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof SyntaxEditor) {
          SyntaxEditor textArea = (SyntaxEditor)getComponent();
          try {
            String sqlText = OracleUtil.prepareSqlFromPLSQL(textArea.getTokensCurrentText());
            ExplainPlanDialog.show(database, sqlText);
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

    setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/explain_plan.gif"));
    setText(getDescription());
    setTooltip(stringManager.getString("OracleExplainPlanSourceActionProvider-hint"));
    setActionCommandKey("OracleExplainPlanSourceActionProvider");
    addActionListener(createActionListener());
    
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("OracleExplainPlanSourceActionProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

}

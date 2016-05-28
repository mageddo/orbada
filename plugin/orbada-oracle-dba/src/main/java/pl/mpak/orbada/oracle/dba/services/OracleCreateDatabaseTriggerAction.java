/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.gui.wizards.CreateDatabaseTriggerWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleCreateDatabaseTriggerAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-dba");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateDatabaseTriggerWizard(database, OracleDbInfoProvider.getCurrentSchema(database)), true);
      }
    };
  }

  @Override
  public boolean isForComponent(Database database, String actionType) {
    if (database == null || !OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType())) {
      return false;
    }
    if (!"oracle-triggers-actions".equals(actionType) && 
        !OrbadaOraclePlugin.specjalOracleWizardsActions.equals(actionType)) {
      return false;
    }

    setText(getDescription());
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trigger.gif"));
    setTooltip(stringManager.getString("OracleCreateDatabaseTriggerAction-hint"));
    setActionCommandKey("OracleCreateDatabaseTriggerAction");
    addActionListener(createActionListener());
    
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("OracleCreateDatabaseTriggerAction-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

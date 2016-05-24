/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.gui.wizards.CompressTableWizard;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleCompressTableAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String schemaName = vt.getQuery().fieldByName("schema_name").getString();
              String tableName = vt.getQuery().fieldByName("table_name").getString();
              SqlCodeWizardDialog.show(new CompressTableWizard(getDatabase(), schemaName, tableName), true);
            } catch (Exception ex) {
              MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
            }
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
    if (!"oracle-tables-actions".equals(actionType)) {
      return false;
    }

    setText(getDescription());
    setTooltip(stringManager.getString("OracleCompressTableAction-hint"));
    setActionCommandKey("OracleCompressTableAction");
    addActionListener(createActionListener());
    
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("OracleCompressTableAction-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

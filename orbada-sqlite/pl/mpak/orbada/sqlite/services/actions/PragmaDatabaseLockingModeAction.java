/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.services.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.core.Application;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.gui.wizards.PragmaDatabaseLockingModeWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PragmaDatabaseLockingModeAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public PragmaDatabaseLockingModeAction() {
    super();
  }

  public PragmaDatabaseLockingModeAction(Database database) {
    super();
    setDatabase(database);
    setApplication(Application.get());
    setProperties();
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new PragmaDatabaseLockingModeWizard(getDatabase(), vt.getQuery().fieldByName("name").getString()), true);
              if (result != null && getComponent() != null) {
                IRootTabObjectInfo ip = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, getComponent());
                if (ip != null) {
                  ip.refresh();
                }
              }
            } catch (Exception ex) {
              MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
            }
          }
        }
      }
    };
  }

  private void setProperties() {
    setText(getDescription());
    setActionCommandKey("PragmaDatabaseLockingModeAction");
    addActionListener(createActionListener());
  }

  @Override
  public boolean isForComponent(Database database, String actionType) {
    if (database == null || !OrbadaSQLitePlugin.driverType.equals(database.getDriverType())) {
      return false;
    }
    if (!"sqlite-databases-actions".equals(actionType)) {
      return false;
    }

    setProperties();
    return true;
  }

  @Override
  public boolean isToolButton() {
    return false;
  }

  public String getDescription() {
    return stringManager.getString("database-locking-mode");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.services.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DetachDatabaseAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            IRootTabObjectInfo ip = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, getComponent());
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String catalogName = vt.getQuery().fieldByName("name").getString();
              if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("detach"), stringManager.getString("DetachDatabaseAction-detach-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                getDatabase().executeCommand("DETACH DATABASE " +SQLUtil.createSqlName(catalogName));
                ip.refresh();
              }
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
    if (database == null || !OrbadaSQLitePlugin.driverType.equals(database.getDriverType())) {
      return false;
    }
    if (!"sqlite-databases-actions".equals(actionType)) {
      return false;
    }

    setText(getDescription());
    setTooltip(stringManager.getString("DetachDatabaseAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_detach.gif"));
    setActionCommandKey("DetachDatabaseAction");
    addActionListener(createActionListener());

    return true;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("DetachDatabaseAction-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }

}

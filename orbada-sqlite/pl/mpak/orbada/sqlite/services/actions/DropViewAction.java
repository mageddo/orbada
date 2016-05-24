/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.services.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.comps.table.ViewTable;
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
public class DropViewAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          IRootTabObjectInfo ip = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, getComponent());
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String catalogName = vt.getQuery().fieldByName("database").getString();
              String tableName = vt.getQuery().fieldByName("name").getString();
              if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("deleting"), stringManager.getString("DropViewAction-drop-view-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                getDatabase().executeCommand("DROP VIEW " +SQLUtil.createSqlName(catalogName, tableName));
                ip.refresh(null);
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
    if (!"sqlite-views-actions".equals(actionType)) {
      return false;
    }
    
    setText(getDescription());
    setTooltip(stringManager.getString("DropViewAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif"));
    setActionCommandKey("DropViewAction");
    addActionListener(createActionListener());
    
    return true;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("DropViewAction-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }

}

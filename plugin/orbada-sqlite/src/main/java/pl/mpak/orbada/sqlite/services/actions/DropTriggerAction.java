/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.services.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectInfo;
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
public class DropTriggerAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String catalogName = vt.getQuery().fieldByName("database").getString();
              String triggerName = vt.getQuery().fieldByName("name").getString();
              if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("deleting"), stringManager.getString("DropTriggerAction-drop-trigger-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                getDatabase().executeCommand("DROP TRIGGER " +SQLUtil.createSqlName(catalogName, triggerName));
                ITabObjectInfo trigt = (ITabObjectInfo)SwingUtil.getOwnerComponent(ITabObjectInfo.class, getComponent());
                if (trigt != null) {
                  trigt.refresh();
                }
                else {
                  IRootTabObjectInfo trigvv = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, getComponent());
                  if (trigvv != null) {
                    trigvv.refresh(null);
                  }
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

  @Override
  public boolean isForComponent(Database database, String actionType) {
    if (database == null || !OrbadaSQLitePlugin.driverType.equals(database.getDriverType())) {
      return false;
    }
    if (!"sqlite-triggers-actions".equals(actionType) && 
        !"sqlite-table-triggers-actions".equals(actionType) &&
        !"sqlite-view-triggers-actions".equals(actionType)) {
      return false;
    }
    
    setText(getDescription());
    setTooltip(stringManager.getString("DropTriggerAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif"));
    setActionCommandKey("DropTriggerAction");
    addActionListener(createActionListener());
    
    return true;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("DropTriggerAction-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }

}

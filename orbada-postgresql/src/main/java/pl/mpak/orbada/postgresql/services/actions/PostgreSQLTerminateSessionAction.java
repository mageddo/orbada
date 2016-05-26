/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.services.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.admin.SessionsPanelView;
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
public class PostgreSQLTerminateSessionAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            try {
              SessionsPanelView spv = (SessionsPanelView)SwingUtil.getOwnerComponent(SessionsPanelView.class, getComponent());
              //LocksPanelView lpv = (LocksPanelView)SwingUtil.getOwnerComponent(LocksPanelView.class, getComponent());
              
              vt.getQuery().getRecord(vt.getSelectedRow());
              String session = vt.getQuery().fieldByName("pid").getString();
              int result =
                MessageBox.show(spv,
                  //(spv != null ? spv : (lpv != null ? lpv : getComponent())), 
                  stringManager.getString("session"), 
                  stringManager.getString("terminate-selected-session-q", session),
                  ModalResult.YESNO,
                  MessageBox.QUESTION);
              if (result == ModalResult.YES) {
                getDatabase().executeCommand("select pg_terminate_backend(" +session +")");
                if (spv != null) {
                  spv.refresh();
                }
//                else if (lpv != null) {
//                  lpv.refresh();
//                }
                else {
                  vt.getQuery().refresh();
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
    if (database == null || !OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType())) {
      return false;
    }
    if (!"postgresql-sessions-actions".equals(actionType) &&
        !"postgresql-session-locks-actions".equals(actionType)) {
      return false;
    }
    
    setText(getDescription());
    setTooltip(stringManager.getString("PostgreSQLTerminateSessionAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/stop16.gif"));
    setActionCommandKey("PostgreSQLTerminateSessionAction");
    addActionListener(createActionListener());
    
    return true;
  }
  
  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLTerminateSessionAction-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }
  
}

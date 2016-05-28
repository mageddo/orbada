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
import pl.mpak.orbada.oracle.dba.gui.sessions.LocksPanelView;
import pl.mpak.orbada.oracle.dba.gui.sessions.SessionsPanelView;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
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
public class OracleKillSessionAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-dba");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            try {
              SessionsPanelView spv = (SessionsPanelView)SwingUtil.getOwnerComponent(SessionsPanelView.class, getComponent());
              LocksPanelView lpv = (LocksPanelView)SwingUtil.getOwnerComponent(LocksPanelView.class, getComponent());
              
              vt.getQuery().getRecord(vt.getSelectedRow());
              String session = 
                vt.getQuery().fieldByName("sid").getInteger() +", " +
                vt.getQuery().fieldByName("serial#").getInteger();
              if (vt.getQuery().findFieldByName("inst_id") != null) {
                session = session +", @" +vt.getQuery().fieldByName("inst_id").getInteger();
              }
              int result =
                MessageBox.show(
                  (spv != null ? spv : (lpv != null ? lpv : getComponent())), 
                  stringManager.getString("session"), 
                  stringManager.getString("kill-selected-session-q", session), 
                  new String[] {
                    stringManager.getString("kill-selected-yes"),
                    stringManager.getString("kill-selected-immediate"),
                    stringManager.getString("kill-selected-no"),
                  }, 
                  MessageBox.QUESTION);
              if (result == 0 || result == 1) {
                getDatabase().executeCommand("ALTER SYSTEM KILL SESSION '" +session +"'" +(result == 1 ? " IMMEDIATE" : ""));
                if (spv != null) {
                  spv.refresh();
                }
                else if (lpv != null) {
                  lpv.refresh();
                }
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
    if (database == null || !OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType())) {
      return false;
    }
    if (!"oracle-sessions-actions".equals(actionType) &&
        !"oracle-session-locks-actions".equals(actionType)) {
      return false;
    }
    
    setText(getDescription());
    setTooltip(stringManager.getString("OracleKillSessionAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/stop16.gif"));
    setActionCommandKey("OracleKillSessionAction");
    addActionListener(createActionListener());
    
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("OracleKillSessionAction-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }
  
}

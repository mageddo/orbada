/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTabbedPane;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.gui.sessions.LocksPanelView;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class OracleGotoLockSIDAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-dba");

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              JTabbedPane tabPane = (JTabbedPane)SwingUtil.getOwnerComponent(JTabbedPane.class, getComponent());
              UniversalViewTabs panel = (UniversalViewTabs)SwingUtil.getOwnerComponent(UniversalViewTabs.class, getComponent());
              LocksPanelView locks = (LocksPanelView)SwingUtil.getTabbedPaneComponent(LocksPanelView.class, getComponent());
              if (locks == null) {
                locks = (LocksPanelView)panel.getAccesibilities().getPerspectiveAccesibilities().createView(new OracleLocksView());
              }
              if (locks != null) {
                tabPane.setSelectedComponent(locks);
                locks.refresh(vt.getQuery().fieldByName("sid").getString());
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
    if (database == null || 
        !OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType()) ||
        !StringUtil.toBoolean(database.getUserProperties().getProperty("lock-view-role"))) {
      return false;
    }
    if (!"oracle-sessions-actions".equals(actionType)) {
      return false;
    }
    
    setText(getDescription());
    setTooltip(stringManager.getString("OracleGotoLockSIDAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/lock.gif"));
    setActionCommandKey("OracleGotoLockSIDAction");
    addActionListener(createActionListener());
    
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("OracleGotoLockSIDAction-text");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }
  
}

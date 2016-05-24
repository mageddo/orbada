/*
 * ExplainPlanProvider.java
 * 
 * Created on 2007-10-30, 21:30:16
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.services.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.ExplainPlanPanel;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ExplainPlanService extends UniversalActionProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public ExplainPlanService() {
    super();
    setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    setText(stringManager.getString("ExplainPlanProvider-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/explain_plan.gif"));
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public boolean addToolButton() {
    return true;
  }

  @Override
  public boolean addMenuItem() {
    return false;
  }

  @Override
  public boolean addToEditor() {
    return true;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("ExplainPlanProvider-description");
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    String sqlCode = accessibilities.getSyntaxEditor().getCurrentText();
    Component[] array = accessibilities.getResultTabs(ExplainPlanPanel.class);
    if (array.length > 0) {
      ExplainPlanPanel panel = (ExplainPlanPanel)array[0];
      panel.updatePlan(sqlCode);
      accessibilities.setSelectedTab(panel);
    }
    else {
      ExplainPlanPanel panel =  new ExplainPlanPanel(accessibilities.getViewAccesibilities().getDatabase());
      panel.updatePlan(sqlCode);
      accessibilities.addResultTab(getText(), panel);
    }
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }

}

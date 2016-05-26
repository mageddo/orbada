/*
 * UniversalTableColumnProvider.java
 * 
 * Created on 2007-10-30, 21:30:16
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.ExplainPlanPanel;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ExplainPlanProvider extends UniversalActionProvider {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public ExplainPlanProvider() {
    super();
    setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    setText(stringManager.getString("ExplainPlanProvider-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/explain_plan.gif"));
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  public boolean addToolButton() {
    return true;
  }

  public boolean addMenuItem() {
    return true;
  }

  public boolean addToEditor() {
    return true;
  }

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

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}

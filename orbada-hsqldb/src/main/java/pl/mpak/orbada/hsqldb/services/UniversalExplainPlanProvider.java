/*
 * UniversalTableColumnProvider.java
 * 
 * Created on 2007-10-30, 21:30:16
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.gui.ExplainPlanPanel;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalExplainPlanProvider extends UniversalActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  public UniversalExplainPlanProvider() {
    super();
    setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    setText(stringManager.getString("UniversalExplainPlanProvider-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/explain_plan.gif"));
    setActionCommandKey("UniversalExplainPlanProvider");
    addActionListener(createActionListener());
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType());
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
    return stringManager.getString("UniversalExplainPlanProvider-description");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
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
    };
  }

  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }

}

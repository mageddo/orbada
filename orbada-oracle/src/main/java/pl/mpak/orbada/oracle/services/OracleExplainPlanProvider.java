package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.universal.ExplainPlanPanel;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleExplainPlanProvider extends UniversalActionProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private ISettings settings;

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    if (OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType())) {
      setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
      setText(stringManager.getString("OracleExplainPlanProvider-text"));
      setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/explain_plan.gif"));
      setActionCommandKey("OracleExplainPlanProvider");
      addActionListener(createActionListener());
      settings = application.getSettings(OracleSettingsProvider.settingsName);
      return true;
    }
    return false;
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
    return stringManager.getString("OracleExplainPlanProvider-description");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String sqlCode = accessibilities.getSyntaxEditor().getCurrentText();
        Component[] array = accessibilities.getResultTabs(ExplainPlanPanel.class);
        if (array.length > 0 && !settings.getValue(OracleSettingsProvider.setMultiExplainPlan, false)) {
          ExplainPlanPanel panel = (ExplainPlanPanel)array[0];
          panel.updatePlan(sqlCode);
          accessibilities.setSelectedTab(panel);
          accessibilities.setTabTooltip(panel, SQLUtil.createTooltipFromSql(sqlCode));
        }
        else {
          ExplainPlanPanel panel =  new ExplainPlanPanel(accessibilities.getViewAccesibilities().getDatabase(), sqlCode);
          accessibilities.addResultTab(getText(), panel);
          accessibilities.setTabTooltip(panel, SQLUtil.createTooltipFromSql(sqlCode));
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

package pl.mpak.orbada.jaybird.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.gui.ExplainPlanPanel;
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
public class JaybirdExplainPlanService extends UniversalActionProvider {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);
  private ISettings settings;

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    if (OrbadaJaybirdPlugin.firebirdDriverType.equals(database.getDriverType()) &&
        OrbadaJaybirdPlugin.jaybirdDriverClass.equals(database.getDriverClassName())) {
      setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
      setText(stringManager.getString("explain_plan"));
      setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/explain_plan.gif"));
      setActionCommandKey("JaybirdExplainPlanProvider");
      addActionListener(createActionListener());
      settings = application.getSettings(JaybirdGeneralSettingsService.settingsName);
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
    return OrbadaJaybirdPlugin.firebirdDriverType +" Explain Plan Provider";
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String sqlCode = accessibilities.getSyntaxEditor().getCurrentText();
        Component[] array = accessibilities.getResultTabs(ExplainPlanPanel.class);
        if (array.length > 0 && !settings.getValue(JaybirdGeneralSettingsService.SET_MultiExplainPlan, false)) {
          ExplainPlanPanel panel = (ExplainPlanPanel)array[0];
          panel.updatePlan(sqlCode);
          accessibilities.setSelectedTab(panel);
          accessibilities.setTabTooltip(panel, SQLUtil.createTooltipFromSql(sqlCode));
        }
        else {
          ExplainPlanPanel panel =  new ExplainPlanPanel(accessibilities.getViewAccesibilities().getDatabase());
          panel.updatePlan(sqlCode);
          accessibilities.addResultTab(getText(), panel);
          accessibilities.setTabTooltip(panel, SQLUtil.createTooltipFromSql(sqlCode));
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaJaybirdPlugin.firebirdDriverType;
  }

}

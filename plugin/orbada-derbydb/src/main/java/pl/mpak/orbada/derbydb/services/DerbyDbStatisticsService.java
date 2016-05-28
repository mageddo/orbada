package pl.mpak.orbada.derbydb.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.derbydb.universal.StatisticsPanel;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DerbyDbStatisticsService extends UniversalActionProvider {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  public DerbyDbStatisticsService() {
    super();
    setShortCut(KeyEvent.VK_T, KeyEvent.CTRL_MASK);
    setText(stringManager.getString("DerbyDbStatisticsService-text"));
    setTooltip("<html>" +getText() +"<br>\n" +stringManager.getString("DerbyDbStatisticsService-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/autotrace.gif"));
    setActionCommandKey("cmDerbyDbStatisticsService");
    addActionListener(createActionListener());
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    if (OrbadaDerbyDbPlugin.apacheDerbyDriverType.equals(database.getDriverType())) {
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
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType +" " +getText();
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        openResultTab();
      }
    };
  }
  
  private void openResultTab() {
    Component[] array = accessibilities.getResultTabs(StatisticsPanel.class);
    if (array.length > 0) {
      StatisticsPanel panel = (StatisticsPanel)array[0];
      accessibilities.setSelectedTab(panel);
    }
    else {
      StatisticsPanel panel =  new StatisticsPanel(accessibilities.getViewAccesibilities().getDatabase());
      panel.afterSql();
      accessibilities.addResultTab(getText(), panel);
    }
  }
  
  private void beforeSql() {
    Component[] array = accessibilities.getResultTabs(StatisticsPanel.class);
    if (array.length > 0) {
      StatisticsPanel panel = (StatisticsPanel)array[0];
      panel.beforeSql();
    }
  }

  private void afterSql() {
    Component[] array = accessibilities.getResultTabs(StatisticsPanel.class);
    if (array.length > 0) {
      StatisticsPanel panel = (StatisticsPanel)array[0];
      panel.afterSql();
    }
  }

  @Override
  public void beforeOpenQuery(Query query) {
    beforeSql();
  }

  @Override
  public void afterOpenQuery(Query query) {
    afterSql();
  }

  @Override
  public void beforeExecuteCommand(Command command) {
    beforeSql();
  }

  @Override
  public void afterExecuteCommand(Command command) {
    afterSql();
  }

  public String getGroupName() {
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType;
  }

}

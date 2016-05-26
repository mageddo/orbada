package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.AutotracePanel;
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
public class MySQLAutotraceProvider extends UniversalActionProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    if (OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      setShortCut(KeyEvent.VK_T, KeyEvent.CTRL_MASK);
      setText("Statustyki po³aczenia");
      setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/autotrace.gif"));
      setActionCommandKey("MySQLAutotraceProvider");
      addActionListener(createActionListener());
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
    return OrbadaMySQLPlugin.driverType +" " +getText();
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
    Component[] array = accessibilities.getResultTabs(AutotracePanel.class);
    if (array.length > 0) {
      AutotracePanel panel = (AutotracePanel)array[0];
      accessibilities.setSelectedTab(panel);
    }
    else {
      AutotracePanel panel =  new AutotracePanel(accessibilities.getViewAccesibilities().getDatabase());
      panel.afterSql();
      accessibilities.addResultTab(getText(), panel);
    }
  }
  
  private void beforeSql() {
    Component[] array = accessibilities.getResultTabs(AutotracePanel.class);
    if (array.length > 0) {
      AutotracePanel panel = (AutotracePanel)array[0];
      panel.beforeSql();
    }
  }

  private void afterSql() {
    Component[] array = accessibilities.getResultTabs(AutotracePanel.class);
    if (array.length > 0) {
      AutotracePanel panel = (AutotracePanel)array[0];
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
    return OrbadaMySQLPlugin.driverType;
  }

}

package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.settings.TemplatesSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLTemplatesSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public static String settingsName = "mysql-templates";
  public static String setTrigger = "trigger";
  public static String setFunction = "function";
  public static String setProcedure = "procedure";

  @Override
  public String getSettingsPath() {
    return OrbadaMySQLPlugin.driverType +stringManager.getString("MySQLTemplatesSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new TemplatesSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("MySQLTemplatesSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}

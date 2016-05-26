/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.DbmsOutputConnectionSettingsPanel;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleDbmsOutputConnectionSettingsProvider extends PerspectiveSettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public static String settingsName = "oracle-dbms-output";
  public static String setRefreshInterval = "refresh-interval";
  public static String setOnStartupViewEnable = "on-startup-view-enable";
  public static String setBufferSize = "buffer-size";
  public static String setUseGlobalSettings = "use-global-settings";

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType +stringManager.getString("OracleDbmsOutputSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent(Database database) {
    return new DbmsOutputConnectionSettingsPanel(application, database);
  }

  public String getDescription() {
    return stringManager.getString("OracleDbmsOutputSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.DbmsOutputSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleDbmsOutputSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public static String settingsName = "oracle-dbms-output";
  public static String setRefreshInterval = "refresh-interval";
  public static String setOnStartupViewEnable = "on-startup-view-enable";
  public static String setBufferSize = "buffer-size";
  public static String setUseGlobalSettings = "use-global-settings";

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType +stringManager.getString("OracleDbmsOutputSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new DbmsOutputSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("OracleDbmsOutputSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.CompileErrorSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleCompileErrorSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public static String settingsName = "oracle-compile-error";
  public static String setOnErrorGoToTab = "on-error-goto-tab";
  public static String setErrorLineColor = "error-line-color";

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType +stringManager.getString("OracleCompileErrorSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new CompileErrorSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("OracleCompileErrorSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.system.serives;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.system.OrbadaSystemPlugin;
import pl.mpak.orbada.system.gui.SystemSettingsPanel;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SystemSettingsProvider extends SettingsProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSystemPlugin.class);
  public static String settingsName = "orbada-system";
  public static String hideTime = "hide-time";
  public static String hideRunTime = "hide-run-time";

  @Override
  public String getSettingsPath() {
    return stringManager.getString("program-status");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new SystemSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("basis-plugin-settings");
  }

  public String getGroupName() {
    return OrbadaSystemPlugin.systemGroupName;
  }

}

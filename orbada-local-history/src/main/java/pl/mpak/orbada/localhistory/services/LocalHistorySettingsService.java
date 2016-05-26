/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.gui.GeneralSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class LocalHistorySettingsService extends SettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  public static String settingsName = "orbada-local-history";
  public static String setDeleteAfterDays = "delete-after-days";
  public static String setGlobalSettings = "global-settings";
  public static String setTurnedOn = "turned-on";
  
  @Override
  public String getSettingsPath() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new GeneralSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("LocalHistorySettingsService-description");
  }

  public String getGroupName() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.tray.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.tray.OrbadaTrayPlugin;
import pl.mpak.orbada.tray.gui.TraySettingsPanel;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TraySettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("tray");

  @Override
  public String getSettingsPath() {
    return OrbadaUniversalPlugin.universalGroupName +stringManager.getString("TraySettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new TraySettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("TraySettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaTrayPlugin.pluginGroupName;
  }

}

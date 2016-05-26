/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell.services;

import pl.mpak.orbada.beanshell.gui.StartupShutdownSettingsPanel;
import pl.mpak.orbada.beanshell.*;
import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class StartupShutdownSettingsService extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  @Override
  public String getSettingsPath() {
    return OrbadaBeanshellPlugin.rootSettingsPath +stringManager.getString("StartupShutdownSettingsService-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new StartupShutdownSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("StartupShutdownSettingsService-description");
  }

  public String getGroupName() {
    return OrbadaBeanshellPlugin.beanshellGroupName;
  }
  
}

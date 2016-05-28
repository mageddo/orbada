/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.beanshell.OrbadaBeanshellPlugin;
import pl.mpak.orbada.beanshell.gui.BshActionsSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class BshActionsSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  @Override
  public String getSettingsPath() {
    return OrbadaBeanshellPlugin.rootSettingsPath +stringManager.getString("BshActionsSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new BshActionsSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("BshActionsSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaBeanshellPlugin.beanshellGroupName;
  }

}

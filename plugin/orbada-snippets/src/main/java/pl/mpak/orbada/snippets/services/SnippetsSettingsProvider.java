/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.snippets.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.snippets.OrbadaSnippetsPlugin;
import pl.mpak.orbada.snippets.gui.SnippetsSettingsPanel;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SnippetsSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("snippets");

  @Override
  public String getSettingsPath() {
    return OrbadaUniversalPlugin.universalGroupName +stringManager.getString("SnippetsSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new SnippetsSettingsPanel(application);
  }

  @Override
  public String getDescription() {
    return stringManager.getString("OrbadaSnippetsPlugin-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaSnippetsPlugin.pluginGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.universal.gui.UniversalSessionSettingsProviderPanel;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalSessionSettingsProvider extends PerspectiveSettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  @Override
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  @Override
  public Component getSettingsComponent(Database database) {
    return new UniversalSessionSettingsProviderPanel(application, database);
  }

  @Override
  public String getSettingsPath() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  public String getDescription() {
    return stringManager.getString("UniversalSessionSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

}

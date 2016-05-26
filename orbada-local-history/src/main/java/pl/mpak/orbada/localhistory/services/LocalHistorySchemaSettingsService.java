/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.gui.SchemaSettingsPanel;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class LocalHistorySchemaSettingsService extends PerspectiveSettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("local-history");

  @Override
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  @Override
  public Component getSettingsComponent(Database database) {
    return new SchemaSettingsPanel(application, database);
  }

  @Override
  public String getSettingsPath() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  public String getDescription() {
    return stringManager.getString("LocalHistorySchemaSettingsService-description");
  }

  public String getGroupName() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }

}

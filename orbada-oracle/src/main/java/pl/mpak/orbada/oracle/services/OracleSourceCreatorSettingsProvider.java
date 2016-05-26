/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.SourceCreatorSettingsPanel;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleSourceCreatorSettingsProvider extends PerspectiveSettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public Component getSettingsComponent(Database database) {
    return new SourceCreatorSettingsPanel(application, database);
  }

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType +stringManager.getString("OracleSourceCreatorSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  public String getDescription() {
    return stringManager.getString("OracleSourceCreatorSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

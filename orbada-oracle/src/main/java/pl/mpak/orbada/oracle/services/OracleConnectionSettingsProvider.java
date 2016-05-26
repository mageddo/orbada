/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.OracleConnectionSettingsPanel;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleConnectionSettingsProvider extends PerspectiveSettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent(Database database) {
    return new OracleConnectionSettingsPanel(application, database);
  }

  public String getDescription() {
    return stringManager.getString("OracleSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

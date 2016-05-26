/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlmacro.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.sqlmacro.OrbadaSqlMacrosPlugin;
import pl.mpak.orbada.sqlmacro.gui.SqlMacrosSettingsPanel;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SqlMacrosSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sql-macro");

  @Override
  public String getSettingsPath() {
    return OrbadaUniversalPlugin.universalGroupName +stringManager.getString("SqlMacrosSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new SqlMacrosSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("SqlMacrosSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaSqlMacrosPlugin.pluginGroupName;
  }

}

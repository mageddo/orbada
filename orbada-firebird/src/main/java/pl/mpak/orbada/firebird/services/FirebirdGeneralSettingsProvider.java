/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.gui.GeneralSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FirebirdGeneralSettingsProvider extends SettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  public final static String settingsName = "orbada-firebird-settings";
  public final static String SET_ConnectionTransaction = "connection-transaction";

  @Override
  public String getSettingsPath() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
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
    return stringManager.getString("FirebirdGeneralSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.gui.GeneralSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JaybirdGeneralSettingsService extends SettingsProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("jaybird");
  public final static String settingsName = "orbada-firebird-settings";
  public final static String SET_MultiExplainPlan = "multi-explain-plan";

  @Override
  public String getSettingsPath() {
    return OrbadaJaybirdPlugin.firebirdDriverType +"/Jaybird";
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
    return stringManager.getString("JaybirdGeneralSettingsService.description");
  }

  public String getGroupName() {
    return OrbadaJaybirdPlugin.firebirdDriverType;
  }

}

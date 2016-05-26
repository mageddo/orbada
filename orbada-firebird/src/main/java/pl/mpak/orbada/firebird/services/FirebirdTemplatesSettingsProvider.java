package pl.mpak.orbada.firebird.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.gui.TemplatesSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FirebirdTemplatesSettingsProvider extends SettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  public static String settingsName = "firebird-templates";
  public static String setTrigger = "trigger";
  public static String setProcedure = "procedure";

  @Override
  public String getSettingsPath() {
    return OrbadaFirebirdPlugin.firebirdDriverType +stringManager.getString("FirebirdTemplatesSettingsProvider-templates");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new TemplatesSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("FirebirdTemplatesSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }

}

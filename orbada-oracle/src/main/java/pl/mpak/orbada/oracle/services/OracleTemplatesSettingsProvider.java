package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.TemplatesSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleTemplatesSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public static String settingsName = "oracle-templates";
  public static String setTrigger = "trigger";
  public static String setFunction = "function";
  public static String setProcedure = "procedure";
  public static String setPackage = "package";
  public static String setPackageBody = "package-body";
  public static String setJavaSource = "java-source";
  public static String setObjectType = "object-type";
  public static String setObjectTypeBody = "object-type-body";
  public static String setTableType = "table-type";
  public static String setVarrayType = "varray-type";

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType +stringManager.getString("OracleTemplatesSettingsProvider-settings-path");
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
    return stringManager.getString("OracleTemplatesSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

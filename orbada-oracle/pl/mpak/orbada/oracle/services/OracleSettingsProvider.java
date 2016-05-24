/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.settings.OracleSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public static String settingsName = "oracle-settings";
  public static String setUseGlobalSettings = "global-settings";
  public static String setMultiExplainPlan = "multi-explain-plan";
  public static String setPackageTabText = "package-tab-text";
  public static String setEditorHighlightSynonyms = "editor-highlight-synonyms";
  public static String setSolveTableMetadataProblem = "table-outer-join";

  @Override
  public String getSettingsPath() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new OracleSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("OracleSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

}

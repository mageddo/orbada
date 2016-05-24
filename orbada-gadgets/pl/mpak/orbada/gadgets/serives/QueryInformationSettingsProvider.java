/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gadgets.serives;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.gadgets.OrbadaGadgetsPlugin;
import pl.mpak.orbada.gadgets.gui.QueryInformationSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class QueryInformationSettingsProvider extends SettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaGadgetsPlugin.class);

  @Override
  public String getSettingsPath() {
    return OrbadaGadgetsPlugin.gadgetsGroupName +stringManager.getString("QueryInformationSettingsProvider-settings-path");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new QueryInformationSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("QueryInformationSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaGadgetsPlugin.gadgetsGroupName;
  }

}

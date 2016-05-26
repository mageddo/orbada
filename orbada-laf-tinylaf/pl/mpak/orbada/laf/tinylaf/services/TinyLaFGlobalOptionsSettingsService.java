/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.tinylaf.services;

import java.awt.Component;
import javax.swing.Icon;
import orbada.Consts;
import pl.mpak.orbada.laf.tinylaf.OrbadaLafTinyLaFPlugin;
import pl.mpak.orbada.laf.tinylaf.gui.GlobalOptionsSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TinyLaFGlobalOptionsSettingsService extends SettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLafTinyLaFPlugin.class);

  @Override
  public Component getSettingsComponent() {
    return new GlobalOptionsSettingsPanel(application);
  }

  @Override
  public String getSettingsPath() {
    return getGroupName() +"/" +OrbadaLafTinyLaFPlugin.tinyLaFName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("TinyLaFGlobalOptionsSettingsService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

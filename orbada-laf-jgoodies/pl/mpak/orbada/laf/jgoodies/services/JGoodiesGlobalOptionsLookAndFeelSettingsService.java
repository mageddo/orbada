/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jgoodies.OrbadaLafJGoodiesPlugin;
import pl.mpak.orbada.laf.jgoodies.gui.GlobalOptionsSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JGoodiesGlobalOptionsLookAndFeelSettingsService extends SettingsProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLafJGoodiesPlugin.class);

  @Override
  public Component getSettingsComponent() {
    return new GlobalOptionsSettingsPanel(application);
  }

  @Override
  public String getSettingsPath() {
    return getGroupName() +"/" +OrbadaLafJGoodiesPlugin.jgoodiesName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("JGoodiesGlobalOptionsLookAndFeelSettingsService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

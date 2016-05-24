/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.laf.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.laf.CrossPlatformLookAndFeelSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CrossPlatformLookAndFeelSettingsService extends SettingsProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  @Override
  public Component getSettingsComponent() {
    return new CrossPlatformLookAndFeelSettingsPanel(application);
  }

  @Override
  public String getSettingsPath() {
    return getGroupName() +"/Cross platform";
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("CrossPlatformLookAndFeelSettingsService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

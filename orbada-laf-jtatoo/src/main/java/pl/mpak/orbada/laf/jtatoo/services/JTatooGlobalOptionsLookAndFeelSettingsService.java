/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.OrbadaLafJTatooPlugin;
import pl.mpak.orbada.laf.jtatoo.gui.GlobalOptionsSettingsPanel;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JTatooGlobalOptionsLookAndFeelSettingsService extends SettingsProvider {

  private StringManager i18n = StringManagerFactory.getStringManager(OrbadaLafJTatooPlugin.class);

  @Override
  public Component getSettingsComponent() {
    return new GlobalOptionsSettingsPanel(application);
  }

  @Override
  public String getSettingsPath() {
    return getGroupName() +"/" +OrbadaLafJTatooPlugin.pluginName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getDescription() {
    return i18n.getString("JTatooGlobalOptionsLookAndFeelSettingsService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

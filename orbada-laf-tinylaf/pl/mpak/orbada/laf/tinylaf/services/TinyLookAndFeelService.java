/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.tinylaf.services;

import orbada.Consts;
import pl.mpak.orbada.laf.tinylaf.OrbadaLafTinyLaFPlugin;
import pl.mpak.orbada.laf.tinylaf.starters.TinyLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TinyLookAndFeelService extends LookAndFeelProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLafTinyLaFPlugin.class);

  public final static String lookAndFeelId = "tiny-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return TinyLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("TinyLookAndFeelService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

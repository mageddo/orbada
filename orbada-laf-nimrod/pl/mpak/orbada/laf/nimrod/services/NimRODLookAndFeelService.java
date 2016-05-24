/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.nimrod.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.nimrod.OrbadaLafNimRODPlugin;
import pl.mpak.orbada.laf.nimrod.starters.NimRODLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class NimRODLookAndFeelService extends LookAndFeelProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLafNimRODPlugin.class);

  public final static String lookAndFeelId = "nimrod-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return NimRODLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("NimRODLookAndFeelService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

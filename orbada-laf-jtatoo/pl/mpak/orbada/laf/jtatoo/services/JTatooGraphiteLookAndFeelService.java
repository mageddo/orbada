/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo.services;

import orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.starters.JTatooGraphiteLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class JTatooGraphiteLookAndFeelService extends LookAndFeelProvider {

  public final static String lookAndFeelId = "jtatoo-graphite-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return JTatooGraphiteLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Graphite (JTatoo)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

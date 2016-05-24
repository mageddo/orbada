/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.starters.JTatooLunaLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class JTatooLunaLookAndFeelService extends LookAndFeelProvider {

  public final static String lookAndFeelId = "jtatoo-luna-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return JTatooLunaLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Luna (JTatoo)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

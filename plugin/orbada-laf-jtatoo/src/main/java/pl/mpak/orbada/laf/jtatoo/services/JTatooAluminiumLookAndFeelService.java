/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.starters.JTatooAluminiumLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class JTatooAluminiumLookAndFeelService extends LookAndFeelProvider {

  public final static String lookAndFeelId = "jtatoo-aluminium-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return JTatooAluminiumLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Aluminium (JTatoo)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

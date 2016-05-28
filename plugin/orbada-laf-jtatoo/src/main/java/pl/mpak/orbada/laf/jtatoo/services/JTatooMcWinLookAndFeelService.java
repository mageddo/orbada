/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.starters.JTatooMcWinLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class JTatooMcWinLookAndFeelService extends LookAndFeelProvider {

  public final static String lookAndFeelId = "jtatoo-mcwin-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return JTatooMcWinLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "McWin (JTatoo)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

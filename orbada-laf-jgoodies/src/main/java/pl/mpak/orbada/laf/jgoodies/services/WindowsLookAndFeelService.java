/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.services;

import orbada.Consts;
import pl.mpak.orbada.laf.jgoodies.starters.WindowsLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class WindowsLookAndFeelService extends LookAndFeelProvider {

  public final static String lookAndFeelId = "jgoodies-windows-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return WindowsLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Windows (JGoodies)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

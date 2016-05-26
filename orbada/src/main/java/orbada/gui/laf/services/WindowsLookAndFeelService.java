/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.laf.services;

import orbada.Consts;
import orbada.gui.laf.WindowsLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class WindowsLookAndFeelService extends LookAndFeelProvider {

  @Override
  public String getLookAndFeelId() {
    return "windows-look-and-feel-service";
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return WindowsLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Windows (Java standard)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.laf.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.laf.SystemLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class SystemLookAndFeelService extends LookAndFeelProvider {

  @Override
  public String getLookAndFeelId() {
    return "system-look-and-feel-service";
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return SystemLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "System (Java standard)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

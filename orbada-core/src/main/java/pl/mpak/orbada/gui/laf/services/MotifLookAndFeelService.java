/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.laf.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.laf.MotifLookAndFeelStarter;
import pl.mpak.orbada.gui.laf.MotifLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class MotifLookAndFeelService extends LookAndFeelProvider {

  @Override
  public String getLookAndFeelId() {
    return "motif-look-and-feel-service";
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return MotifLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Motif (Java standard)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.laf.services;

import orbada.Consts;
import orbada.gui.laf.NimbusLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class NimbusLookAndFeelService extends LookAndFeelProvider {

  @Override
  public String getLookAndFeelId() {
    return "nimbus-look-and-feel-service";
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return NimbusLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Nimbus (Java standard)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

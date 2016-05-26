/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.laf.services;

import orbada.Consts;
import orbada.gui.laf.CrossPlatformLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class CrossPlatformLookAndFeelService extends LookAndFeelProvider {

  public final static String loogAndFeelId = "cross-platform-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return loogAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return CrossPlatformLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Cross platform (Java standard)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

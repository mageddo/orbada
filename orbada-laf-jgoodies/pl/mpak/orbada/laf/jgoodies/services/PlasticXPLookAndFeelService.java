/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.services;

import orbada.Consts;
import pl.mpak.orbada.laf.jgoodies.starters.PlasticXPLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;

/**
 *
 * @author akaluza
 */
public class PlasticXPLookAndFeelService extends LookAndFeelProvider {

  public final static String lookAndFeelId = "jgoodies-plastic-xp-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return PlasticXPLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Plastic XP (JGoodies)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

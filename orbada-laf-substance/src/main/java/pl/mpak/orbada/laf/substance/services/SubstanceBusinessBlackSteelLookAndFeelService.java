/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.substance.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.substance.OrbadaLafSubstancePlugin;
import pl.mpak.orbada.laf.substance.starters.SubstanceBusinessBlackSteelLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SubstanceBusinessBlackSteelLookAndFeelService extends LookAndFeelProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("laf-substance");

  public final static String lookAndFeelId = "substance-business-black-steel-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return SubstanceBusinessBlackSteelLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Business Black Steel (Substance)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.substance.services;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.substance.OrbadaLafSubstancePlugin;
import pl.mpak.orbada.laf.substance.starters.SubstanceMarinerLookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SubstanceMarinerLookAndFeelService extends LookAndFeelProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("laf-substance");

  public final static String lookAndFeelId = "substance-mariner-look-and-feel-service";

  @Override
  public String getLookAndFeelId() {
    return lookAndFeelId;
  }

  @Override
  public Class<? extends ILookAndFeelStarter> getLookAndFeelClass() {
    return SubstanceMarinerLookAndFeelStarter.class;
  }

  @Override
  public String getDescription() {
    return "Mariner (Substance)";
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaLookAndFeelGroupName;
  }

}

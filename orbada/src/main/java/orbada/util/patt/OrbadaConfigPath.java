/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.util.patt;

import pl.mpak.util.patt.ResolvableModel;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class OrbadaConfigPath implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.config";
  }

  @Override
  public String getResolve() {
    return Resolvers.expand("$(orbada.home)/config");
  }

}

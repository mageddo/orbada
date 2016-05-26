/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.patt;

import pl.mpak.util.patt.ResolvableModel;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class OrbadaHomePath implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.home";
  }

  @Override
  public String getResolve() {
    return Resolvers.expand("$(user.home)/.orbada");
  }

}

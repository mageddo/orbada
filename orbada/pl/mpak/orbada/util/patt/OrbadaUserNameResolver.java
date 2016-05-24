/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.patt;

import pl.mpak.orbada.core.Application;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class OrbadaUserNameResolver implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.user.name";
  }

  @Override
  public String getResolve() {
    return Application.get().getUserName();
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.util.patt;

import orbada.core.Application;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class OrbadaUserIdResolver implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.user.id";
  }

  @Override
  public String getResolve() {
    return Application.get().getUserId();
  }

}

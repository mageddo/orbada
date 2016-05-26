/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.util.patt;

import pl.mpak.util.id.UniqueID;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class UniqueIdResolver implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.unique.id";
  }

  @Override
  public String getResolve() {
    return new UniqueID().toString();
  }

}

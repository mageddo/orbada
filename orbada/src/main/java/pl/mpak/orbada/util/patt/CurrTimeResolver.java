/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.patt;

import java.util.Date;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class CurrTimeResolver implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.current.time";
  }

  @Override
  public String getResolve() {
    return String.format("%1$tH:%1$tM:%1$tS", new Object[] {new Date().getTime()});
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.util.patt;

import java.util.Date;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class CurrDateResolver implements ResolvableModel {

  public String getModel() {
    return "orbada.current.date";
  }

  public String getResolve() {
    return String.format("%1$tY-%1$tm-%1$td", new Object[] {new Date().getTime()});
  }

}

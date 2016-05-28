/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.patt;

import java.lang.management.ManagementFactory;

import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class JvmPIDResolver implements ResolvableModel {

  @Override
  public String getModel() {
    return "orbada.jvm.pid";
  }

  @Override
  public String getResolve() {
    try {
      String jvmName = ManagementFactory.getRuntimeMXBean().getName();
      int index = jvmName.indexOf('@');

      if (index < 1) {
          // part before '@' empty (index = 0) / '@' not found (index = -1)
        return "";
      }

      try {
        return Long.toString(Long.parseLong(jvmName.substring(0, index)));
      } catch (NumberFormatException e) {
          // ignore
      }
    }
    catch (Throwable e) {
      ExceptionUtil.processException(e);
    }
    return "";
  }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class DriverClassLoaderManager {

  private static HashMap<String, DriverClassLoader> driverMap = new HashMap<String, DriverClassLoader>();

  public static java.sql.Driver getDriver(String source, String extraLibs, String className) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (!StringUtil.isEmpty(source) && new File(source).exists()) {
      DriverClassLoader dcl = driverMap.get(source);
      if (dcl == null) {
        dcl = new DriverClassLoader(source);
        if (!StringUtil.isEmpty(extraLibs)) {
          StringTokenizer st = new StringTokenizer(extraLibs, ";");
          while (st.hasMoreTokens()) {
            File lib = new File(st.nextToken());
            if (lib.exists()) {
              dcl.addURL(lib.toURI().toURL());
            }
          }
        }
        driverMap.put(source, dcl);
      }
      return dcl.getDriver(className);
    }
    else {
      return (java.sql.Driver)Class.forName(className).newInstance();
    }
  }

  public static void reset() {
    driverMap.clear();
  }

}

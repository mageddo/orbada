/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

import pl.mpak.orbada.db.DriverClassLoader;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class DriverClassLoaderManager {

  private static HashMap<String, DriverClassLoader> driverMap = new HashMap<String, DriverClassLoader>();

  /**
   * Check the existent file for the passed paths and return the first or null if
   * not one exists
   * @param sources
   * @return
   */
  private static File getDriverJar(final String ... sources){
    for(String source: sources){
      if(!StringUtil.isEmpty(source)){

        final File file = new File(source);
        if(file.exists()){
          return file;
        }

      }
    }
    return null;
  }

  public static java.sql.Driver getDriver(final String source, final String extraLibs, final String className) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

    /*
      try get the jar driver from classpath resources(not classloader)
     */
    final URL driverFromClassPathUri = ClassLoader.getSystemClassLoader().getResource(source);
    String driverFromClassPath = null;
    if(driverFromClassPathUri != null){
      driverFromClassPath = driverFromClassPathUri.getFile();
    }

    final File driverJar = getDriverJar(source,driverFromClassPath);
    if (driverJar != null) {
      final String theSource = driverJar.getAbsolutePath();
      DriverClassLoader dcl = driverMap.get(source);
      if (dcl == null) {
        dcl = new DriverClassLoader(theSource);
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
    }else {
     // FIXME EFS driver was not found, using generic driver (alert the user)
      return (java.sql.Driver)Class.forName(className).newInstance();
    }
  }

  public static void reset() {
    driverMap.clear();
  }

}

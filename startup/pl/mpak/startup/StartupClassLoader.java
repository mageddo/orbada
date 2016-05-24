/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.startup;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author akaluza
 */
public class StartupClassLoader extends URLClassLoader {
  
  public StartupClassLoader(ClassLoader parent) {
    super(new URL[] {}, parent);
  }
  
  public StartupClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }
  
  public StartupClassLoader(URL[] urls) {
    super(urls);
  }
  
  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }
  
}

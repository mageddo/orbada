/*
 * IOrbadaPluginProvider.java
 * 
 * Created on 2007-10-24, 17:46:09
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers.abs;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.plugins.spi.IPluginProvider;

/**
 *
 * @author akaluza
 */
public interface IOrbadaPluginProvider extends IPluginProvider {
  
  public void setApplication(IApplication application);
  
  public String getDescription();

}

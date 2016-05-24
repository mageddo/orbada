/*
 * StatusPanelProvider.java
 *
 * Created on 2007-10-26, 22:37:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;

/**
 *
 * @author akaluza
 */
public abstract class StatusBarProvider implements IOrbadaPluginProvider {
  
  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return false;
  }
  
  public abstract Component getComponent();
  
}

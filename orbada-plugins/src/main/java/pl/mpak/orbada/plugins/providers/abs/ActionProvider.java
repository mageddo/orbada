/*
 * ActionProvider.java
 *
 * Created on 2007-10-22, 21:07:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers.abs;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.sky.gui.swing.Action;

/**
 *
 * @author akaluza
 */
public abstract class ActionProvider extends Action implements IOrbadaPluginProvider {
  
  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return false;
  }
  
}

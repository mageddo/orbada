/*
 * GlobalFocusProvider.java
 * 
 * Created on 2007-10-30, 19:39:54
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import javax.swing.JComponent;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;

/**
 * <p>Pozwala zdefiniowaæ globalnego dostawcê zmiany focusa
 * @author akaluza
 */
public abstract class GlobalFocusProvider implements IOrbadaPluginProvider {

  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return false;
  }
  
  public abstract void focusGained(JComponent comp);
  
  public abstract void focusLost(JComponent comp);
  
}

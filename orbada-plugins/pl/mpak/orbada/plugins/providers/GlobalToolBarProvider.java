/*
 * GlobalActionProvider.java
 * 
 * Created on 2007-11-03, 13:58:21
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import javax.swing.JToolBar;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;

/**
 * <p>S³u¿o do dostarczenia globalnego ToolBar-a, który dodany zostanie w g³ównym oknie aplikacji<br>
 * Po utworzeniu toolBar bêdzie dostêpny, nastêpnie wywo³ana bêdzie funkcja initialize()
 * @author akaluza
 */
public abstract class GlobalToolBarProvider implements IOrbadaPluginProvider {

  protected IApplication application;
  protected JToolBar toolBar;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return true;
  }
  
  public void setToolBar(JToolBar toolBar) {
    this.toolBar = toolBar;
  }
  
  public JToolBar getToolBar() {
    return toolBar;
  }
  
  public abstract void initialize();
  
}

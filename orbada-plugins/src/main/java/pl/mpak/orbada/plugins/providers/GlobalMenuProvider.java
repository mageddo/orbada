/*
 * GlobalMenuProvider.java
 * 
 * Created on 2007-11-03, 14:03:17
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import javax.swing.JMenu;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.plugins.spi.IPluginProvider;

/**
 * <p>S³u¿o do dostarczenia globalnego Menu-a, które dodane zostanie w g³ównym oknie aplikacji<br>
 * Po utworzeniu menu bêdzie dostêpny, nastêpnie wywo³ana bêdzie funkcja initialize()
 * @author akaluza
 */
public abstract class GlobalMenuProvider implements IPluginProvider {

  protected IApplication application;
  protected JMenu menu;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return true;
  }
  
  public void setMenu(JMenu menu) {
    this.menu = menu;
  }
  
  public JMenu getMenu() {
    return menu;
  }
  
  public abstract void initialize();
  
}

/*
 * ApplicationListener.java
 * 
 * Created on 2007-10-07, 15:20:10
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.util.EventListener;

/**
 *
 * @author akaluza
 */
public interface ApplicationListener extends EventListener {

  public void activated();
  
  public void closing();
  
  public void deactivated();
  
  public void iconified();
  
  public void deiconified();
  
}

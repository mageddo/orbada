/*
 * ApplicationListener.java
 * 
 * Created on 2007-10-07, 15:20:10
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 * @author akaluza
 */
public interface SyntaxDatabaseObjectListener extends EventListener {

  public void beforeChanged(EventObject e);
  public void afterChanged(EventObject e);
  public void beforeStoreObject(EventObject e);
  public void afterStoreObject(EventObject e);
  public void storeObjectError(EventObject e);
  
}

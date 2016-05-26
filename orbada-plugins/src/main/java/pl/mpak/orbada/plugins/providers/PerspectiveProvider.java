/*
 * PerspectiveProvider.java
 * 
 * Created on 2007-11-11, 19:16:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public abstract class PerspectiveProvider implements IOrbadaPluginProvider, IProcessMessagable {

  protected IApplication application;
  protected IPerspectiveAccesibilities accesibilities;
  
  public void setApplication(IApplication application) {
    this.application = application;
    this.application.registerRequestMessager(this);
  }
  
  public void setAccesibilities(IPerspectiveAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
  }
  
  public boolean isSharedProvider() {
    return false;
  }
  
  public IPerspectiveAccesibilities getAccesibilities() {
    return accesibilities;
  }
  
  /**
   * <p>Powinna zwróciæ informacjê czy serwis przeznaczony jest dla tej bazy danych
   * @param database 
   * @return 
   */
  public abstract boolean isForDatabase(Database database);
  
  public abstract void initialize();
  
  public void perspectiveShow() {}
  
  public void perspectiveHide() {}
  
  public void perspectiveClose() {
    this.application.unregisterRequestMessager(this);
  }
  
  public void processMessage(PluginMessage message) {}
  
}

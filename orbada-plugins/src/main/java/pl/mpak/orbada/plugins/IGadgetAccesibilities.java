/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public interface IGadgetAccesibilities {

  public Database getDatabase();

  public IApplication getApplication();
  
  public IPerspectiveAccesibilities getPerspectiveAccesibilities();
  
  /**
   * <p>Powinno byæ wywo³ane jeœli coœ w gad¿ecie siê zmieni³o, np tytu³
   */
  public void updated();
  
}

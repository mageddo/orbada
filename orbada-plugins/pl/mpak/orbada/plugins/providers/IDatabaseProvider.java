/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public interface IDatabaseProvider extends IOrbadaPluginProvider {
  
  /**
   * <p>Powinna zwróciæ informacjê czy serwis przeznaczony jest dla tej bazy danych
   * @param database 
   * @return 
   */
  public boolean isForDatabase(Database database);
  
  /**
   * <p>Wywo³ywane jest po nawi¹zaniu po³¹czenia
   * @param database
   */
  public void afterConnection(Database database);
  
  /**
   * <p>Wywo³ywane jest przed zamkniêciem po³¹czenia
   * @param database
   */
  public void beforeDisconnect(Database database);
  
}

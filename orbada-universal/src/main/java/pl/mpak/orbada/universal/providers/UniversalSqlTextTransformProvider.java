/*
 * UniversalSqlTextTransformProvider.java
 * 
 * Created on 2007-10-31, 19:49:51
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.providers;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.usedb.core.Database;

/**
 * <p>S³u¿y do transformacji wykonywanego polecenia SQL
 * @author akaluza
 */
public abstract class UniversalSqlTextTransformProvider implements IOrbadaPluginProvider {

  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return true;
  }

  public abstract boolean isForDatabase(Database database);
  
  /**
   * <p>Jeœli zwróci null to znaczy, ¿e nie dokonano transformacji
   * @param database 
   * @param sqlText 
   * @return 
   */
  public abstract String transformSqlText(Database database, String sqlText);
  
}

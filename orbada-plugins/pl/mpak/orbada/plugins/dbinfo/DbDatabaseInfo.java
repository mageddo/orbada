/*
 * DbDatabaseInfo.java
 *
 * Created on 2007-11-13, 21:04:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public abstract class DbDatabaseInfo extends DbObjectContainer {

  private Database database;
  /**
   * <p>Faktyczna wersja baza danych, pobrana z samej bazy danych
   */
  private String version;
  /**
   * <p>Informacje opisowe o bazie danych, przedstawiaj¹ce j¹
   */
  private String banner;

  public DbDatabaseInfo(Database database) {
    super(database.getUrl() +"@" +database.getUserName(), null);
    this.database = database;
  }
  
  public DbDatabaseInfo(String name) {
    super(name, null);
  }
  
  public Database getDatabase() {
    return database;
  }
  
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getBanner() {
    return banner;
  }

  public void setBanner(String banner) {
    this.banner = banner;
  }
  
  /**
   * <p>Powinna zwróciæ informacje o tym co to jest za baza danych i czy jest s¹ to dedykowane informacje
   * <p>np. "Universal Jdbc Database Info Provider"
   * @return 
   */
  public abstract String getDescription();
  
}

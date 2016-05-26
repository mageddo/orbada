package pl.mpak.orbada.mysql.services;

import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLDatabaseProvider extends DatabaseProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
    Query query = database.createQuery();
    try {
      query.open("select DATABASE() name");
      database.getUserProperties().setProperty("database-name", query.fieldByName("name").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    } finally {
      query.close();
    }
  }

  @Override
  public void beforeDisconnect(Database database) {
  }

  public String getDescription() {
    return stringManager.getString("MySQLDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
}

package pl.mpak.orbada.sqlite.services;

import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SQLiteDatabaseProvider extends DatabaseProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaSQLitePlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
//    Query query = database.createQuery();
//    try {
//      query.open("SELECT 0 FROM RDB$RELATION_FIELDS WHERE RDB$RELATION_NAME = 'RDB$GENERATORS' AND RDB$FIELD_NAME = 'RDB$DESCRIPTION'");
//      if (query.eof()) {
//        database.getUserProperties().setProperty("generator-description", "false");
//      }
//      else {
//        database.getUserProperties().setProperty("generator-description", "true");
//      }
//    }
//    catch (Exception ex) {
//      ExceptionUtil.processException(ex);
//    }
//    finally {
//      query.close();
//    }
      database.getUserProperties().put("dict-persistent-query", "true");
  }

  @Override
  public void beforeDisconnect(Database database) {
  }

  public String getDescription() {
    return stringManager.getString("SQLiteDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }
}

package pl.mpak.orbada.firebird.services;

import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
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
public class FirebirdDatabaseProvider extends DatabaseProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
    Query query = database.createQuery();
    try {
      query.open("SELECT 0 FROM RDB$RELATION_FIELDS WHERE RDB$RELATION_NAME = 'RDB$GENERATORS' AND RDB$FIELD_NAME = 'RDB$DESCRIPTION'");
      if (query.eof()) {
        database.getUserProperties().setProperty("generator-description", "false");
      }
      else {
        database.getUserProperties().setProperty("generator-description", "true");
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }

  @Override
  public void beforeDisconnect(Database database) {
  }

  public String getDescription() {
    return stringManager.getString("FirebirdDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }
}

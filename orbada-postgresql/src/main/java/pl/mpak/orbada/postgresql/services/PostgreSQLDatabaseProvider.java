package pl.mpak.orbada.postgresql.services;

import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLDatabaseProvider extends DatabaseProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
    Query query = database.createQuery();
    try {
      database.executeCommand(Sql.getSetAppInfo());
      query.open(Sql.getCurrentSchema());
      database.getUserProperties().put("schema-name", query.fieldByName("schema_name").getString());
      database.getUserProperties().put("dict-persistent-query", "true");
      query.open(Sql.getIsSuperuser());
      database.getUserProperties().put("superuser", query.fieldByName("usesuper").getString());
      database.getUserProperties().put("version", PostgreSQLDbInfoProvider.instance.getVersion(database));
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

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLDatabaseProvider-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
}

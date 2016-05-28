package pl.mpak.orbada.hsqldb.services;

import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class HsqlDbDatabaseProvider extends DatabaseProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
    database.getUserProperties().put("session-admin-role", "true");
    String version = HSqlDbInfoProvider.instance.getDatabaseInfo(database).getVersion();
    database.getUserProperties().put("version", version);
    database.getUserProperties().put(OrbadaHSqlDbPlugin.hsqlDb20, HSqlDbInfoProvider.getVersionTest(database) >= HSqlDbInfoProvider.hsqlDb20 ? "true" : "false");
    database.getUserProperties().put(OrbadaHSqlDbPlugin.hsqlDb18, HSqlDbInfoProvider.getVersionTest(database) >= HSqlDbInfoProvider.hsqlDb18 ? "true" : "false");
  }

  @Override
  public void beforeDisconnect(Database database) {
  }

  public String getDescription() {
    return stringManager.getString("HsqlDbDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }
}

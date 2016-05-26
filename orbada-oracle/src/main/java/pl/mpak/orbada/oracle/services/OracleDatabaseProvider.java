package pl.mpak.orbada.oracle.services;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class OracleDatabaseProvider extends DatabaseProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
    Query query = database.createQuery();
    try {
      query.open(Sql.getDbaRole());
      if (!query.eof()) {
        database.getUserProperties().put("dba-role", "true");
      }
      else {
        database.getUserProperties().put("dba-role", "false");
      }
      database.executeCommand(Sql.getSetModule());
      query.open(Sql.getCurrentSchema());
      database.getUserProperties().put("schema-name", query.fieldByName("schema_name").getString());
      String version = OracleDbInfoProvider.instance.getVersion(database);
      database.getUserProperties().put("version", version);
      VersionID vid = new VersionID(version);
      database.getUserProperties().put("Ora11+", vid.getMajor() >= 11 ? "true" : "false");
      database.getUserProperties().put("Ora10+", vid.getMajor() >= 10 ? "true" : "false");
      database.getUserProperties().put("Ora9+", vid.getMajor() >= 9 ? "true" : "false");
      database.getUserProperties().put("Ora8+", vid.getMajor() >= 8 ? "true" : "false");
      try {
        query.open(Sql.getDbParamsViewCount());
        if (query.fieldByName("cnt").getInteger() == 1) {
          query.open(Sql.getDatabaseParameters());
          while (!query.eof()) {
            database.getUserProperties().put(
              query.fieldByName("name").getString(),
              query.fieldByName("value").getString()
            );
            query.next();
          }
        }
      }
      catch (Exception ex) {
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
    return stringManager.getString("OracleDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
}

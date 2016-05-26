package pl.mpak.orbada.oracle.dba.services;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.Sql;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class OracleDbaDatabaseProvider extends DatabaseProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

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
      if (!StringUtil.toBoolean(database.getUserProperties().getProperty("dba-role"))) {
        query.open(Sql.getLockViewRole());
        if (query.fieldByName("cnt").getInteger() == 3) {
          database.getUserProperties().put("lock-view-role", "true");
        }
        else {
          database.getUserProperties().put("lock-view-role", "false");
        }
        query.open(Sql.getSessionViewRole());
        if (query.fieldByName("cnt").getInteger() == 3) {
          database.getUserProperties().put("session-view-role", "true");
        }
        else {
          database.getUserProperties().put("session-view-role", "false");
        }
        query.open(Sql.getSqlViewRole());
        if (query.fieldByName("cnt").getInteger() == 2) {
          database.getUserProperties().put("sql-view-role", "true");
        }
        else {
          database.getUserProperties().put("sql-view-role", "false");
        }
        query.open(Sql.getSessStatViewRole());
        if (query.fieldByName("cnt").getInteger() == 2) {
          database.getUserProperties().put("sess-stat-view-role", "true");
        }
        else {
          database.getUserProperties().put("sess-stat-view-role", "false");
        }
      }
      else {
        database.getUserProperties().put("lock-view-role", "true");
        database.getUserProperties().put("session-view-role", "true");
        database.getUserProperties().put("sql-view-role", "true");
        database.getUserProperties().put("sess-stat-view-role", "true");
      }
      if (StringUtil.toBoolean(database.getUserProperties().getProperty("session-view-role", "false"))) {
        try {
          query.open(Sql.getSessionId());
          if (!query.eof()) {
            database.getUserProperties().put("sid", query.fieldByName("sid").getString());
            database.getUserProperties().put("serial", query.fieldByName("serial").getString());
            database.getUserProperties().put("sessionid", query.fieldByName("audsid").getString());
          }
        }
        catch (Exception ex) {
          //ExceptionUtil.processException(ex);
        }
        try {
          try {
            query.open(Sql.getClusterDetect_GV());
          }
          catch (Exception ex) {
            query.open(Sql.getClusterDetect_V());
          }
          if (query.fieldByName("gvcount").getInteger() > 1) {
            database.getUserProperties().put("rac-detect", "true");
          }
          else {
            database.getUserProperties().put("rac-detect", "false");
          }
        }
        catch (Exception ex) {
          database.getUserProperties().put("rac-detect", "false");
        }
      }
      else {
        database.getUserProperties().put("rac-detect", "false");
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
    return stringManager.getString("OracleDbaDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
}

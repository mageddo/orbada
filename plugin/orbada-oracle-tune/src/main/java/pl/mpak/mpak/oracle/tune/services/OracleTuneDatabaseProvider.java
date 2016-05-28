/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.mpak.oracle.tune.services;

import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
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
public class OracleTuneDatabaseProvider extends DatabaseProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-tune");

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
      query.open(Sql.getStatsViewCount());
      if (!query.eof() && query.fieldByName("cnt").getInteger() == 2) {
        query.open(Sql.getStatsRoleCount());
        if (!query.eof() && query.fieldByName("cnt").getInteger() >= 1) {
          database.getUserProperties().put("autotrace", "true");
        }
        else {
          database.getUserProperties().put("autotrace", "false");
        }
      }
      else {
        database.getUserProperties().put("autotrace", "false");
      }

      if (StringUtil.toBoolean(database.getUserProperties().getProperty("dba-role"))) {
        database.getUserProperties().put("v$sql_plan", "true");
      }
      else {
        query.open(Sql.getSqlPlanTableExists());
        if (query.fieldByName("cnt").getInteger() == 4) {
          database.getUserProperties().put("v$sql_plan", "true");
        }
        else {
          query.open(Sql.getSqlColumnCheckList());
          while (!query.eof()) {
            if ("LAST_ACTIVE_TIME".equals(query.fieldByName("column_name").getString())) {
              database.getUserProperties().put("V%SQL.LAST_ACTIVE_TIME", "true");
            }
            else if ("PARSING_SCHEMA_NAME".equals(query.fieldByName("column_name").getString())) {
              database.getUserProperties().put("V%SQL.PARSING_SCHEMA_NAME", "true");
            }
            query.next();
          }
          database.getUserProperties().put("v$sql_plan", "false");
        }
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
    return "Database provider for Oracle Tune plug-in";
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}

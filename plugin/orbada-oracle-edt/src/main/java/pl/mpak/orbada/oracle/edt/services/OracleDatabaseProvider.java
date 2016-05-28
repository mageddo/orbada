package pl.mpak.orbada.oracle.edt.services;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.edt.core.OracleOPAQUEWrapper;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.QueryColumnTypeWrapper;

/**
 *
 * @author akaluza
 */
public class OracleDatabaseProvider extends DatabaseProvider {

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
    try {
      if (QueryColumnTypeWrapper.wrapperMap.get("oracle.sql.OPAQUE") == null) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
          Thread.currentThread().setContextClassLoader(database.getDriver().getClass().getClassLoader());
          Class clazz = Thread.currentThread().getContextClassLoader().loadClass("oracle.xdb.XMLType");
          QueryColumnTypeWrapper.wrapperMap.put("oracle.sql.OPAQUE", new OracleOPAQUEWrapper(clazz));
        }
        finally {
          Thread.currentThread().setContextClassLoader(cl);
        }
      }
    } catch (Throwable ex) {
      //ExceptionUtil.processException(ex);
    }
  }

  @Override
  public void beforeDisconnect(Database database) {
  }

  public String getDescription() {
    return "";
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
}

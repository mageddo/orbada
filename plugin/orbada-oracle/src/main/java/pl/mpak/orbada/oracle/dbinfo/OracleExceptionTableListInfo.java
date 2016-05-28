package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleExceptionTableListInfo extends DbObjectContainer<OracleExceptionTableInfo> {
  
  public OracleExceptionTableListInfo(OracleDatabaseInfo owner) {
    super("EXCEPTION TABLES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Schemat"};
  }

  public String[] getMemberNames() {
    return new String[] {};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {};
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      Query query = getDatabase().createQuery();
      try {
        query.setSqlText(Sql.getExceptionsTable());
        query.open();
        while (!query.eof()) {
          OracleExceptionTableInfo info = new OracleExceptionTableInfo(query.fieldByName("table_name").getString(), this);
          info.setTableOwner(query.fieldByName("schema_name").getString());
          put(info);
          query.next();
        }
      }
      finally {
        query.close();
      }
    }
    finally {
      setRefreshed(true);
    }
  }
  
}

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTableTypeListInfo extends DbObjectContainer<OracleTableTypeInfo> {
  
  public OracleTableTypeListInfo(OracleDatabaseInfo owner) {
    super("TABLE TYPES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {};
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
        query.setResultSet(getDatabase().getMetaData().getTableTypes());
        while (!query.eof()) {
          put(new OracleTableTypeInfo(query.fieldByName("table_type").getString(), this));
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

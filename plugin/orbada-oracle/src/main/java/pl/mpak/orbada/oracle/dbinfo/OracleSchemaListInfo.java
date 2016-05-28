package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleSchemaListInfo extends DbObjectContainer<OracleSchemaInfo> {
  
  public OracleSchemaListInfo(OracleDatabaseInfo owner) {
    super("SCHEMAS", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Id", "Utworzony"};
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
        query.setSqlText(Sql.getFullSchemaList());
        query.open();
        while (!query.eof()) {
          OracleSchemaInfo info = new OracleSchemaInfo(query.fieldByName("schema_name").getString(), this);
          info.setId(query.fieldByName("user_id").getLong());
          info.setCreated(query.fieldByName("created").getTimestamp());
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

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDataTypeListInfo extends DbObjectContainer<OracleDataTypeInfo> {
  
  public OracleDataTypeListInfo(OracleDatabaseInfo owner) {
    super("DATA TYPES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Typ SQL", "Wielkoœæ", "Parametry"};
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
        query.setResultSet(getDatabase().getMetaData().getTypeInfo());
        while (!query.eof()) {
          OracleDataTypeInfo info = new OracleDataTypeInfo(query.fieldByName("type_name").getString(), this);
          info.setSqlType(query.fieldByName("data_type").getInteger());
          info.setPrecision(query.fieldByName("precision").getInteger());
          info.setCreateParams(query.fieldByName("create_params").getString());
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

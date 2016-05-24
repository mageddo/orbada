package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleNlsCharsetListInfo extends DbObjectContainer<OracleNlsCharsetInfo> {
  
  public OracleNlsCharsetListInfo(DbObjectContainer owner) {
    super("NLS CHARSET NAMES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Id"};
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
        query.setSqlText(Sql.getNlsCharsetList());
        query.open();
        while (!query.eof()) {
          OracleNlsCharsetInfo info = new OracleNlsCharsetInfo(query.fieldByName("name").getString(), this);
          info.setId(query.fieldByName("id").getLong());
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

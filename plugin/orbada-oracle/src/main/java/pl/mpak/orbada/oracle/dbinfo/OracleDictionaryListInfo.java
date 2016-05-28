package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDictionaryListInfo extends DbObjectContainer<OracleDictionaryInfo> {
  
  public OracleDictionaryListInfo(OracleDatabaseInfo owner) {
    super("DICTIONARY", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Komentarz"};
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
        query.setSqlText(Sql.getDictionaryList(getFilter()));
        query.open();
        while (!query.eof()) {
          OracleDictionaryInfo dict = new OracleDictionaryInfo(query.fieldByName("table_name").getString(), this);
          dict.setRemarks(query.fieldByName("comments").getString());
          put(dict);
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

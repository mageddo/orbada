package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDictionaryColumnListInfo extends DbObjectContainer<OracleDictionaryColumnInfo> {
  
  public OracleDictionaryColumnListInfo(OracleDictionaryInfo owner) {
    super("COLUMNS", owner);
  }

  public OracleDictionaryInfo getTable() {
    DbObjectIdentified o = getOwner(OracleDictionaryInfo.class);
    if (o != null) {
      return (OracleDictionaryInfo)o;
    }
    return null;
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
        query.setSqlText(Sql.getDictionaryColumnList(getFilter()));
        query.paramByName("table_name").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          OracleDictionaryColumnInfo column = new OracleDictionaryColumnInfo(query.fieldByName("column_name").getString(), this);
          column.setRemarks(query.fieldByName("comments").getString());
          put(column);
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

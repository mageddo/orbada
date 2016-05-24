package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbTableListInfo extends DbObjectContainer<JdbcDbTableInfo> {
  
  public JdbcDbTableListInfo(DbObjectIdentified owner) {
    super("TABLES", owner);
  }

  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Katalog", "Komentarz"};
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
        JdbcDbSchemaInfo schema = getSchema();
        query.setResultSet(getDatabase().getMetaData().getTables(schema == null ? null : schema.getCatalog(), schema == null ? null : schema.getName(), null, new String[] {"TABLE", "SYSTEM TABLE"}));
        while (!query.eof()) {
          JdbcDbTableInfo table = new JdbcDbTableInfo(query.fieldByName("table_name").getString(), this);
          table.setObjectName(query.fieldByName("table_name").getString());
          table.setCatalog(query.fieldByName("table_cat").getString());
          table.setRemarks(query.fieldByName("remarks").getString());
          put(table);
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

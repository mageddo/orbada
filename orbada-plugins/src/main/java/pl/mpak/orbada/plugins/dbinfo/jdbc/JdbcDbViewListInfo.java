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
public class JdbcDbViewListInfo extends DbObjectContainer<JdbcDbViewInfo> {
  
  public JdbcDbViewListInfo(DbObjectIdentified owner) {
    super("VIEWS", owner);
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
        query.setResultSet(getDatabase().getMetaData().getTables(schema == null ? null : schema.getCatalog(), schema == null ? null : schema.getName(), null, new String[] {"VIEW"}));
        while (!query.eof()) {
          String name = query.fieldByName("table_name").getString();
          if (!StringUtil.isEmpty(query.fieldByName("table_cat").getString())) {
            name = query.fieldByName("table_cat").getString() +"." +name;
          }
          JdbcDbViewInfo view = new JdbcDbViewInfo(name, this);
          view.setObjectName(query.fieldByName("table_name").getString());
          view.setCatalog(query.fieldByName("table_cat").getString());
          view.setRemarks(query.fieldByName("remarks").getString());
          if (StringUtil.isEmpty(view.getCatalog())) {
            put(view);
          }
          else {
            put(view.getCatalog() +"." +view.getName(), view);
          }
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

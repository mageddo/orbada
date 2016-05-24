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
public class JdbcDbFunctionListInfo extends DbObjectContainer<JdbcDbFunctionInfo> {
  
  public JdbcDbFunctionListInfo(DbObjectIdentified owner) {
    super("FUNCTIONS", owner);
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
        query.setResultSet(getDatabase().getMetaData().getFunctions(null, schema == null ? null : schema.getName(), null));
        while (!query.eof()) {
          String name = query.fieldByName("function_name").getString();
          if (!StringUtil.isEmpty(query.fieldByName("function_cat").getString())) {
            name = query.fieldByName("function_cat").getString() +"." +name;
          }
          JdbcDbFunctionInfo func = new JdbcDbFunctionInfo(name, this);
          func.setObjectName(query.fieldByName("function_name").getString());
          func.setCatalog(query.fieldByName("function_cat").getString());
          func.setRemarks(query.fieldByName("remarks").getString());
          put(func);
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

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
public class JdbcDbProcedureListInfo extends DbObjectContainer<JdbcDbProcedureInfo> {
  
  public JdbcDbProcedureListInfo(DbObjectIdentified owner) {
    super("PROCEDURES", owner);
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
        query.setResultSet(getDatabase().getMetaData().getProcedures(schema == null ? null : schema.getCatalog(), schema == null ? null : schema.getName(), null));
        while (!query.eof()) {
          String name = query.fieldByName("procedure_name").getString();
          if (!StringUtil.isEmpty(query.fieldByName("procedure_cat").getString())) {
            name = query.fieldByName("procedure_cat").getString() +"." +name;
          }
          JdbcDbProcedureInfo proc = new JdbcDbProcedureInfo(name, this);
          proc.setObjectName(query.fieldByName("procedure_name").getString());
          proc.setCatalog(query.fieldByName("procedure_cat").getString());
          proc.setRemarks(query.fieldByName("remarks").getString());
          put(proc);
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

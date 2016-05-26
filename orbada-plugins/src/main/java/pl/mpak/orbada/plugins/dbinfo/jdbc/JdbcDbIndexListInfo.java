package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbIndexListInfo extends DbObjectContainer<JdbcDbIndexInfo> {
  
  public JdbcDbIndexListInfo(DbObjectIdentified owner) {
    super("INDEXES", owner);
  }

  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Tabela", "Pozycja", "Nazwa kolumny", "Unikalny", "AscDesc"};
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
        query.setResultSet(getDatabase().getMetaData().getIndexInfo(schema == null ? null : schema.getCatalog(), schema == null ? null : schema.getName(), "%", false, false));
        while (!query.eof()) {
          JdbcDbIndexInfo index = new JdbcDbIndexInfo(query.fieldByName("index_name").getString(), this);
          index.setTableName(query.fieldByName("table_name").getString());
          index.setColumnName(query.fieldByName("column_name").getString());
          index.setPosition(query.fieldByName("ordinal_position").getInteger());
          if ("A".equals(query.fieldByName("asc_or_desc").getString())) {
            index.setAscDesc("ASC");
          }
          else if ("D".equals(query.fieldByName("asc_or_desc").getString())) {
            index.setAscDesc("DESC");
          }
          else {
            index.setAscDesc("");
          }
          index.setUnique(!query.fieldByName("non_unique").getBoolean());
          put(index.getName() +"." +index.getColumnName(), index);
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

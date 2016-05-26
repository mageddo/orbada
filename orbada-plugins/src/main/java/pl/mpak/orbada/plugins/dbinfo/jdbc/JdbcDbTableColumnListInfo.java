package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbTableColumnListInfo extends DbObjectContainer<JdbcDbTableColumnInfo> {
  
  public JdbcDbTableColumnListInfo(JdbcDbTableInfo owner) {
    super("COLUMNS", owner);
  }

  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
    }
    return null;
  }

  public JdbcDbTableInfo getTable() {
    DbObjectIdentified o = getOwner(JdbcDbTableInfo.class);
    if (o != null) {
      return (JdbcDbTableInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Pozycja", "Typ", "Rozmiar", "Prec.", "Null?", "Wartoœæ domyœlna", "Komentarz"};
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
        query.setResultSet(getDatabase().getMetaData().getColumns(getTable().getCatalog(), schema == null ? null : schema.getName(), getTable().getObjectName(), "%"));
        while (!query.eof()) {
          JdbcDbTableColumnInfo column = new JdbcDbTableColumnInfo(query.fieldByName("column_name").getString(), this);
          column.setDefaultValue(query.fieldByName("column_def").getString());
          column.setPosition(query.fieldByName("ordinal_position").getInteger());
          column.setDataType(query.fieldByName("type_name").getString());
          column.setDataSize(query.fieldByName("column_size").getInteger());
          if (!query.fieldByName("decimal_digits").isNull()) {
            column.setDigits(query.fieldByName("decimal_digits").getInteger());
          }
          column.setNullable(query.fieldByName("nullable").getInteger() == 1);
          column.setRemarks(query.fieldByName("remarks").getString());
          put(column);
          query.next();
        }
      } finally {
        query.close();
      }
    } 
    finally {
      setRefreshed(true);
    }
  }
  
}

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
public class OracleTableColumnListInfo extends DbObjectContainer<OracleTableColumnInfo> {
  
  public OracleTableColumnListInfo(OracleTableInfo owner) {
    super("COLUMNS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleTableInfo getTable() {
    DbObjectIdentified o = getOwner(OracleTableInfo.class);
    if (o != null) {
      return (OracleTableInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Pozycja", "Typ", "Wartoœæ domyœlna", "Null?", "Komentarz"};
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
        query.setSqlText(Sql.getColumnList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("table_name").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          OracleTableColumnInfo column = new OracleTableColumnInfo(query.fieldByName("column_name").getString(), this);
          column.setDefaultValue(query.fieldByName("data_default").getString());
          column.setRemarks(query.fieldByName("remarks").getString());
          column.setPosition(query.fieldByName("column_id").getInteger());
          column.setType(query.fieldByName("display_type").getString());
          column.setNullable("YES".equalsIgnoreCase(query.fieldByName("nullable").getString()));
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

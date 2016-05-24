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
public class OracleViewColumnListInfo extends DbObjectContainer<OracleViewColumnInfo> {
  
  public OracleViewColumnListInfo(OracleViewInfo owner) {
    super("COLUMNS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleViewInfo getView() {
    DbObjectIdentified o = getOwner(OracleViewInfo.class);
    if (o != null) {
      return (OracleViewInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Pozycja", "Typ", "Null?", "Zmiana", "Komentarz"};
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
        query.setSqlText(Sql.getViewColumnList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("view_name").setString(getView().getName());
        query.open();
        while (!query.eof()) {
          OracleViewColumnInfo column = new OracleViewColumnInfo(query.fieldByName("column_name").getString(), this);
          column.setRemarks(query.fieldByName("remarks").getString());
          column.setPosition(query.fieldByName("column_id").getInteger());
          column.setType(query.fieldByName("display_type").getString());
          column.setNullable("YES".equalsIgnoreCase(query.fieldByName("nullable").getString()));
          column.setUpdatable("YES".equalsIgnoreCase(query.fieldByName("updatable").getString()));
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

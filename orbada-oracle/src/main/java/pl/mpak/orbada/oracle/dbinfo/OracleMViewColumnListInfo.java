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
public class OracleMViewColumnListInfo extends DbObjectContainer<OracleMViewColumnInfo> {
  
  public OracleMViewColumnListInfo(OracleMViewInfo owner) {
    super("COLUMNS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleMViewInfo getMView() {
    DbObjectIdentified o = getOwner(OracleMViewInfo.class);
    if (o != null) {
      return (OracleMViewInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Pozycja", "Typ", "Null?", "Zmiana"};
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
        query.paramByName("view_name").setString(getMView().getName());
        query.open();
        while (!query.eof()) {
          OracleMViewColumnInfo column = new OracleMViewColumnInfo(query.fieldByName("column_name").getString(), this);
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

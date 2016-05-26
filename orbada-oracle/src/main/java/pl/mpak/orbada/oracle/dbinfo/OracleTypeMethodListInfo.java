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
public class OracleTypeMethodListInfo extends DbObjectContainer<OracleTypeMethodInfo> {
  
  public OracleTypeMethodListInfo(OracleTypeInfo owner) {
    super("METHODS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleTypeInfo getType() {
    DbObjectIdentified o = getOwner(OracleTypeInfo.class);
    if (o != null) {
      return (OracleTypeInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Lp", "Zakres", "Typ"};
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
        query.setSqlText(Sql.getTypeMethodList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("type_name").setString(getType().getName());
        query.open();
        while (!query.eof()) {
          OracleTypeMethodInfo info = new OracleTypeMethodInfo(query.fieldByName("method_name").getString(), this);
          info.setMethodRange(query.fieldByName("method_range").getString());
          info.setMethodType(query.fieldByName("method_type").getString());
          info.setMethodNo(query.fieldByName("method_no").getLong());
          put(info);
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

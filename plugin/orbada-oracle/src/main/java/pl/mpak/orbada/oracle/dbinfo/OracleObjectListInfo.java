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
public class OracleObjectListInfo extends DbObjectContainer<OracleObjectInfo> {
  
  public OracleObjectListInfo(OracleSchemaInfo owner) {
    super("ALL OBJECTS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Nazwa obiektu", "Typ obiektu", "Utworzony", "Ostatnia zmiana", "Status"};
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
        query.setSqlText(Sql.getAllObjectList() +" where schema_name = :SCHEMA_NAME");
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleObjectInfo info = new OracleObjectInfo(query.fieldByName("object_name").getString(), this);
          info.setSchemaName(query.fieldByName("schema_name").getString());
          info.setObjectName(query.fieldByName("object_name").getString());
          info.setObjectType(query.fieldByName("object_type").getString());
          info.setCreated(query.fieldByName("created").getTimestamp());
          info.setLastDdlTime(query.fieldByName("last_ddl_time").getTimestamp());
          info.setTimestamp(query.fieldByName("timestamp").getString());
          info.setStatus(query.fieldByName("status").getString());
          put(info.getSchemaName() +"." +info.getObjectType() +"." +info.getObjectName(), info);
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

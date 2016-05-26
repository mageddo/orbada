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
public class OracleJavaSourceListInfo extends DbObjectContainer<OracleJavaSourceInfo> {
  
  public OracleJavaSourceListInfo(OracleSchemaInfo owner) {
    super("JAVA SOURCES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Status", "Utworzony"};
  }

  public String[] getMemberNames() {
    return new String[] {};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {};
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
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
        query.setSqlText(Sql.getObjectsName());
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("object_type").setString("JAVA SOURCE");
        query.open();
        while (!query.eof()) {
          OracleJavaSourceInfo info = new OracleJavaSourceInfo(query.fieldByName("object_name").getString(), this);
          info.setStatus(query.fieldByName("status").getString());
          info.setCreated(query.fieldByName("created").getTimestamp());
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

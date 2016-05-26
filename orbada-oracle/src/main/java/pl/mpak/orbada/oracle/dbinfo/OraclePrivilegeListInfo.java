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
public class OraclePrivilegeListInfo extends DbObjectContainer<OraclePrivilegeInfo> {
  
  public OraclePrivilegeListInfo(DbObjectContainer owner) {
    super("PRIVILEGES", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Schemat", "Nazwa obiektu", "Nada³", "Dla", "Admin"};
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
        query.setSqlText(Sql.getPrivilegeList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("table_name").setString(((DbObjectIdentified)getObjectOwner()).getName());
        query.open();
        while (!query.eof()) {
          OraclePrivilegeInfo info = new OraclePrivilegeInfo(query.fieldByName("privilege").getString(), this);
          info.setObjectSchema(query.fieldByName("table_schema").getString());
          info.setObjectName(query.fieldByName("table_name").getString());
          info.setGrantee(query.fieldByName("grantee").getString());
          info.setGrantor(query.fieldByName("grantor").getString());
          info.setGrantable(query.fieldByName("grantable").getString());
          put(info.getObjectSchema() +"." +info.getObjectName() +"." +info.getName(), info);
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

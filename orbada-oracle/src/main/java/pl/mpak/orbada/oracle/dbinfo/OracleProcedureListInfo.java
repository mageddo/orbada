package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleProcedureListInfo extends DbObjectContainer<OracleProcedureInfo> {
  
  public OracleProcedureListInfo(OracleSchemaInfo owner) {
    super("PROCEDURES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Status"};
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
        query.setSqlText(Sql.getFunctionList(getFilter(), OracleDbInfoProvider.instance.getMajorVersion(getDatabase())));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("function_type").setString("PROCEDURE");
        query.open();
        while (!query.eof()) {
          OracleProcedureInfo info = new OracleProcedureInfo(query.fieldByName("object_name").getString(), this);
          info.setStatus(query.fieldByName("status").getString());
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

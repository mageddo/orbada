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
public class OracleMViewListInfo extends DbObjectContainer<OracleMViewInfo> {
  
  public OracleMViewListInfo(OracleSchemaInfo owner) {
    super("MATERIALIZED VIEWS", owner);
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
        query.setSqlText(Sql.getMViewList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleMViewInfo info = new OracleMViewInfo(query.fieldByName("view_name").getString(), this);
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

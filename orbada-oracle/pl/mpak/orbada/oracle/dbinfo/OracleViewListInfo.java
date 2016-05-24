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
public class OracleViewListInfo extends DbObjectContainer<OracleViewInfo> {
  
  public OracleViewListInfo(OracleSchemaInfo owner) {
    super("VIEWS", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Status", "Komentarz"};
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
        query.setSqlText(Sql.getViewList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleViewInfo info = new OracleViewInfo(query.fieldByName("view_name").getString(), this);
          info.setRemarks(query.fieldByName("remarks").getString());
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

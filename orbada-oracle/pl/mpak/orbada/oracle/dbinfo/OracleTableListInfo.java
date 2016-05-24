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
public class OracleTableListInfo extends DbObjectContainer<OracleTableInfo> {
  
  public OracleTableListInfo(OracleSchemaInfo owner) {
    super("TABLES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Klaster", "Przestrzeñ danych", "Komentarz"};
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
        query.setSqlText(Sql.getTableList(getFilter(), false));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleTableInfo info = new OracleTableInfo(query.fieldByName("table_name").getString(), this);
          info.setRemarks(query.fieldByName("remarks").getString());
          info.setClusterName(query.fieldByName("cluster_name").getString());
          info.setTablespaceName(query.fieldByName("tablespace_name").getString());
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

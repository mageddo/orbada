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
public class OracleDbLinkListInfo extends DbObjectContainer<OracleDbLinkInfo> {
  
  public OracleDbLinkListInfo(OracleSchemaInfo owner) {
    super("DB LINKS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"U¿ytkownik", "Serwer docelowy", "Utworzony"};
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
        query.setSqlText(Sql.getDbLinkList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleDbLinkInfo info = new OracleDbLinkInfo(query.fieldByName("db_link").getString(), this);
          info.setUserName(query.fieldByName("username").getString());
          info.setHost(query.fieldByName("host").getString());
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

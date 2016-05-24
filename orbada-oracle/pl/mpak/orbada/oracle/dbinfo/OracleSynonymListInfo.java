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
public class OracleSynonymListInfo extends DbObjectContainer<OracleSynonymInfo> {
  
  public OracleSynonymListInfo(OracleSchemaInfo owner) {
    super("SYNONYMS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Schemat obiektu", "Nazwa obiektu", "Typ obiektu", "Po³¹czenie zdalne", "Stan"};
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
        query.setSqlText(Sql.getSynonymList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleSynonymInfo info = new OracleSynonymInfo(query.fieldByName("synonym_name").getString(), this);
          info.setTableOwner(query.fieldByName("table_owner").getString());
          info.setTableName(query.fieldByName("table_name").getString());
          info.setObjectType(query.fieldByName("object_type").getString());
          info.setDbLink(query.fieldByName("db_link").getString());
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

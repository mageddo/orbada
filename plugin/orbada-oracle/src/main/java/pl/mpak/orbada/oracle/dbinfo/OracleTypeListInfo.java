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
public class OracleTypeListInfo extends DbObjectContainer<OracleTypeInfo> {
  
  public OracleTypeListInfo(OracleSchemaInfo owner) {
    super("TYPES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Rodzaj", "Status nag³.", "Status cia³a"};
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
        query.setSqlText(Sql.getTypeList(getFilter(), OracleDbInfoProvider.instance.getMajorVersion(getDatabase())));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleTypeInfo info = new OracleTypeInfo(query.fieldByName("type_name").getString(), this);
          info.setTypeCode(query.fieldByName("typecode").getString());
          info.setHeadStatus(query.fieldByName("head_status").getString());
          info.setBodyStatus(query.fieldByName("body_status").getString());
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

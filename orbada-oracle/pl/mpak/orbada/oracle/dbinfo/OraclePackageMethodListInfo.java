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
public class OraclePackageMethodListInfo extends DbObjectContainer<OraclePackageMethodInfo> {
  
  public OraclePackageMethodListInfo(OraclePackageInfo owner) {
    super("METHODS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OraclePackageInfo getPackage() {
    DbObjectIdentified o = getOwner(OraclePackageInfo.class);
    if (o != null) {
      return (OraclePackageInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Typ"};
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
        query.setSqlText(Sql.getPackageMethodList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("package_name").setString(getPackage().getName());
        query.open();
        while (!query.eof()) {
          OraclePackageMethodInfo info = new OraclePackageMethodInfo(query.fieldByName("method_name").getString(), this);
          info.setMethodType(query.fieldByName("method_type").getString());
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

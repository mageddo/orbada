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
public class OracleFunctionArgumentListInfo extends DbObjectContainer<OracleFunctionArgumentInfo> {
  
  public OracleFunctionArgumentListInfo(OracleFunctionInfo owner) {
    super("ARGUMENTS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleFunctionInfo getFunction() {
    DbObjectIdentified o = getOwner(OracleFunctionInfo.class);
    if (o != null) {
      return (OracleFunctionInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Lp", "In/Out", "Typ", "Wartoœæ domyœlna"};
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
        query.setSqlText(Sql.getFunctionArgumentList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("function_name").setString(getFunction().getName());
        query.open();
        while (!query.eof()) {
          OracleFunctionArgumentInfo info = new OracleFunctionArgumentInfo(query.fieldByName("argument_name").getString(), this);
          info.setPosition(query.fieldByName("position").getLong());
          info.setInOut(query.fieldByName("in_out").getString());
          info.setDataType(query.fieldByName("data_type").getString());
          info.setDefaultValue(query.fieldByName("default_value").getString());
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

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
public class OracleProcedureArgumentListInfo extends DbObjectContainer<OracleProcedureArgumentInfo> {
  
  public OracleProcedureArgumentListInfo(OracleProcedureInfo owner) {
    super("ARGUMENTS", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleProcedureInfo getProcedure() {
    DbObjectIdentified o = getOwner(OracleProcedureInfo.class);
    if (o != null) {
      return (OracleProcedureInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Lp", "In/Out", "Typ", "Warto�� domy�lna"};
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
        query.paramByName("function_name").setString(getProcedure().getName());
        query.open();
        while (!query.eof()) {
          OracleProcedureArgumentInfo info = new OracleProcedureArgumentInfo(query.fieldByName("argument_name").getString(), this);
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

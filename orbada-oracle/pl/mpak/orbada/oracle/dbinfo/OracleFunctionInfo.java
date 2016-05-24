package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleFunctionInfo extends DbObjectContainer implements DbObjectInfo {
  
  private String status;
  
  public OracleFunctionInfo(String name, OracleFunctionListInfo owner) {
    super(name, owner);
  }
  
  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {};
  }
  
  public String[] getMemberNames() {
    return new String[] {"Status"};
  }
  
  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(status)
    };
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new OracleFunctionArgumentListInfo(this));
      put(new OraclePrivilegeListInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }

  public String getObjectType() {
    return "FUNCTION";
  }
  
}

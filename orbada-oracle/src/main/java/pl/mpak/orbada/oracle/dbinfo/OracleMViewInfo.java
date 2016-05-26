package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleMViewInfo extends DbObjectContainer implements DbObjectInfo {
  
  private String status;
  
  public OracleMViewInfo(String name, OracleMViewListInfo owner) {
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
      new Variant(status),
      new Variant(getRemarks())
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
      put(new OracleMViewColumnListInfo(this));
      put(new OracleTriggerListInfo(this));
      put(new OraclePrivilegeListInfo(this));
      put(new OracleIndexListInfo(this));
      put(new OracleContentListInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }

  public String getObjectType() {
    return "MATERIALIZED VIEW";
  }
  
}

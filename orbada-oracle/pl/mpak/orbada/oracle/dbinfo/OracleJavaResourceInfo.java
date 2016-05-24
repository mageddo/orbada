package pl.mpak.orbada.oracle.dbinfo;

import java.sql.Timestamp;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleJavaResourceInfo extends DbObjectContainer implements DbObjectInfo {
  
  private String status;
  private Timestamp created;
  
  public OracleJavaResourceInfo(String name, OracleJavaResourceListInfo owner) {
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
    return new String[] {"Status", "Utworzony"};
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }
  
  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(status),
      new Variant(created)
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
      //put(new OracleFunctionArgumentListInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }

  public String getObjectType() {
    return "JAVA RESOURCE";
  }
  
}

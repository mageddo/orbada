package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTableInfo extends DbObjectContainer implements DbObjectInfo {
  
  private String tablespaceName;
  private String clusterName;
  
  public OracleTableInfo(String name, OracleTableListInfo owner) {
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
    return new String[] {"Klaster", "Przestrzeñ danych", "Komentarz"};
  }
  
  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getClusterName()),
      new Variant(getTablespaceName()),
      new Variant(getRemarks())
    };
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getTablespaceName() {
    return tablespaceName;
  }

  public void setTablespaceName(String tablespaceName) {
    this.tablespaceName = tablespaceName;
  }
  
  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new OracleTableColumnListInfo(this));
      put(new OracleIndexListInfo(this));
      put(new OracleConstraintListInfo(this));
      put(new OracleTriggerListInfo(this));
      put(new OraclePrivilegeListInfo(this));
      put(new OracleContentListInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }

  public String getObjectType() {
    return "TABLE";
  }
  
}

/*
 * DerbyDbTableInfo.java
 *
 * Created on 2007-11-15, 20:02:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTypeInfo extends DbObjectContainer implements DbObjectInfo {
  
  private String typeCode;
  private String headStatus;
  private String bodyStatus;
  
  public OracleTypeInfo(String name, OracleTypeListInfo owner) {
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
    return new String[] {"Rodzaj", "Status nag³.", "Status cia³a"};
  }
  
  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(typeCode),
      new Variant(headStatus),
      new Variant(bodyStatus)
    };
  }

  public String getBodyStatus() {
    return bodyStatus;
  }

  public void setBodyStatus(String bodyStatus) {
    this.bodyStatus = bodyStatus;
  }

  public String getHeadStatus() {
    return headStatus;
  }

  public void setHeadStatus(String headStatus) {
    this.headStatus = headStatus;
  }

  public String getTypeCode() {
    return typeCode;
  }

  public void setTypeCode(String typeCode) {
    this.typeCode = typeCode;
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new OracleTypeAttrListInfo(this));
      put(new OracleTypeMethodListInfo(this));
      put(new OraclePrivilegeListInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }

  public String getObjectType() {
    return "TYPE";
  }
  
}

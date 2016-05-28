/*
 * DerbyDbTableColumnInfo.java
 *
 * Created on 2007-11-15, 19:41:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTypeMethodInfo extends DbObjectIdentified {
  
  private Long methodNo;
  private String methodRange;
  private String methodType;
  
  public OracleTypeMethodInfo(String name, OracleTypeMethodListInfo owner) {
    super(name, owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public OracleTypeInfo getType() {
    DbObjectIdentified o = getOwner(OracleTypeInfo.class);
    if (o != null) {
      return (OracleTypeInfo)o;
    }
    return null;
  }
  
  public String[] getMemberNames() {
    return new String[] {"Lp", "Zakres", "Typ"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(methodNo), 
      new Variant(methodRange),
      new Variant(methodType)
    };
  }

  public Long getMethodNo() {
    return methodNo;
  }

  public void setMethodNo(Long methodNo) {
    this.methodNo = methodNo;
  }

  public String getMethodRange() {
    return methodRange;
  }

  public void setMethodRange(String methodRange) {
    this.methodRange = methodRange;
  }

  public String getMethodType() {
    return methodType;
  }

  public void setMethodType(String methodType) {
    this.methodType = methodType;
  }

  public void refresh() throws Exception {
  }
  
}

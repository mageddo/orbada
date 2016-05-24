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
public class OraclePackageMethodInfo extends DbObjectIdentified {
  
  private String methodType;
  
  public OraclePackageMethodInfo(String name, OraclePackageMethodListInfo owner) {
    super(name, owner);
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
  
  public String[] getMemberNames() {
    return new String[] {"Typ"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(methodType)
    };
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

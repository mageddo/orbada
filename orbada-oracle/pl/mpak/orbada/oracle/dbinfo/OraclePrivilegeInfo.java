/*
 * DerbyDbTriggerInfo.java
 *
 * Created on 2007-11-15, 20:26:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OraclePrivilegeInfo extends DbObjectIdentified {
  
  private String objectName;
  private String objectSchema;
  private String grantee;
  private String grantable;
  private String grantor;
  
  public OraclePrivilegeInfo(String name, OraclePrivilegeListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Schemat", "Nazwa obiektu", "Nada³", "Dla", "Admin"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(objectSchema),
      new Variant(objectName),
      new Variant(grantor),
      new Variant(grantee),
      new Variant(grantable)
    };
  }

  public String getGrantable() {
    return grantable;
  }

  public void setGrantable(String grantable) {
    this.grantable = grantable;
  }

  public String getGrantee() {
    return grantee;
  }

  public void setGrantee(String grantee) {
    this.grantee = grantee;
  }

  public String getGrantor() {
    return grantor;
  }

  public void setGrantor(String grantor) {
    this.grantor = grantor;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getObjectSchema() {
    return objectSchema;
  }

  public void setObjectSchema(String objectSchema) {
    this.objectSchema = objectSchema;
  }

  public void refresh() throws Exception {
  }

}

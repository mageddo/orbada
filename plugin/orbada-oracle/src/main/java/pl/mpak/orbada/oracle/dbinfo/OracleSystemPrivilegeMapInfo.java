/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
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
public class OracleSystemPrivilegeMapInfo extends DbObjectIdentified {
  
  private Long privilege;
  private Long property;
  
  public OracleSystemPrivilegeMapInfo(String name, OracleSystemPrivilegeMapListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Id prawa", "W³aœciwoœæ"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(privilege),
      new Variant(property)
    };
  }

  public Long getPrivilege() {
    return privilege;
  }

  public void setPrivilege(Long privilege) {
    this.privilege = privilege;
  }

  public Long getProperty() {
    return property;
  }

  public void setProperty(Long property) {
    this.property = property;
  }

  public void refresh() throws Exception {
  }

}

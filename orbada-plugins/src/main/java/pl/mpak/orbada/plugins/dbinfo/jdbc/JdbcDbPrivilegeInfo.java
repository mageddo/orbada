/*
 * DerbyDbTableColumnInfo.java
 *
 * Created on 2007-11-15, 19:41:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbPrivilegeInfo extends DbObjectIdentified {
  
  private String grantor;
  private String grantee;
  private String grantable;
  
  public JdbcDbPrivilegeInfo(String name, DbObjectIdentified owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Nada³", "Dla", "Admin"};
  }

  public String getGrantor() {
    return grantor;
  }

  public void setGrantor(String grantor) {
    this.grantor = grantor;
  }

  public String getGrantee() {
    return grantee;
  }

  public void setGrantee(String grantee) {
    this.grantee = grantee;
  }

  public String getGrantable() {
    return grantable;
  }

  public void setGrantable(String grantable) {
    this.grantable = grantable;
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getGrantor()), 
      new Variant(getGrantee()), 
      new Variant(getGrantable())
    };
  }

  public void refresh() throws Exception {
  }
  
}

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
public class OracleExceptionTableInfo extends DbObjectIdentified {
  
  private String tableOwner;

  public OracleExceptionTableInfo(String name, OracleExceptionTableListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Schemat"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(tableOwner),
    };
  }

  public String getTableOwner() {
    return tableOwner;
  }

  public void setTableOwner(String tableOwner) {
    this.tableOwner = tableOwner;
  }
  
  public void refresh() throws Exception {
  }

}

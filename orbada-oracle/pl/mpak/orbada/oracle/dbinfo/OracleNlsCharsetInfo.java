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
public class OracleNlsCharsetInfo extends DbObjectIdentified {
  
  private Long id;
  
  public OracleNlsCharsetInfo(String name, OracleNlsCharsetListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Id"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(id)
    };
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void refresh() throws Exception {
  }

}

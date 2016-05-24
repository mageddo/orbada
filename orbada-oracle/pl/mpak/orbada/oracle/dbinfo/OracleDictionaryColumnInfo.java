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
public class OracleDictionaryColumnInfo extends DbObjectIdentified {
  
  public OracleDictionaryColumnInfo(String name, OracleDictionaryColumnListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
  }

}

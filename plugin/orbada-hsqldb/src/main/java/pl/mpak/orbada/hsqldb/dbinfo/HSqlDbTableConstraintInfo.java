/*
 * DerbyDbTableColumnInfo.java
 *
 * Created on 2007-11-15, 19:41:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbTableConstraintInfo extends DbObjectIdentified {
  
  private String type;
  
  public HSqlDbTableConstraintInfo(String name, HSqlDbTableConstraintListInfo owner) {
    super(name, owner);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String[] getMemberNames() {
    return new String[] {"Typ", "Opis"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getType()),
      new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
  }
  
}

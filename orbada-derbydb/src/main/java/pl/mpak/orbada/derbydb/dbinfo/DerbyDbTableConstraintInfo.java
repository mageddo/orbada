/*
 * DerbyDbTableColumnInfo.java
 *
 * Created on 2007-11-15, 19:41:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbTableConstraintInfo extends DbObjectIdentified {
  
  private String type;
  private boolean enable;
  
  public DerbyDbTableConstraintInfo(String name, DerbyDbTableConstraintListInfo owner) {
    super(name, owner);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String[] getMemberNames() {
    return new String[] {"Typ", "W³¹czony", "Opis"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getType()),
      new Variant(isEnable()),
      new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
  }
  
}

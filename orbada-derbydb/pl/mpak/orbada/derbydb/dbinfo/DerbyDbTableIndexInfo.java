/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
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
public class DerbyDbTableIndexInfo extends DbObjectIdentified {
  
  private String columnName;
  private String type;
  private boolean unique;
  private boolean constraint;
  
  public DerbyDbTableIndexInfo(String name, DerbyDbTableIndexListInfo owner) {
    super(name, owner);
  }
  
  public String[] getMemberNames() {
    return new String[] {"Nazwa kolumny", "Typ", "Unikalny", "Ogranicz."};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getColumnName()), 
      new Variant(getType()), 
      new Variant(isUnique()), 
      new Variant(isConstraint())};
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public boolean isConstraint() {
    return constraint;
  }

  public void setConstraint(boolean constraint) {
    this.constraint = constraint;
  }

  public void refresh() throws Exception {
  }
  
}

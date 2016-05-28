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
public class OracleViewColumnInfo extends DbObjectIdentified {
  
  private int position;
  private String type;
  private boolean nullable;
  private boolean updatable;
  
  public OracleViewColumnInfo(String name, OracleViewColumnListInfo owner) {
    super(name, owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public OracleViewInfo getView() {
    DbObjectIdentified o = getOwner(OracleViewInfo.class);
    if (o != null) {
      return (OracleViewInfo)o;
    }
    return null;
  }
  
  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public boolean isUpdatable() {
    return updatable;
  }

  public void setUpdatable(boolean updatable) {
    this.updatable = updatable;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Typ", "Null?", "Zmiana", "Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(position), 
      new Variant(type),
      new Variant(nullable),
      new Variant(updatable),
      new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
  }
  
}

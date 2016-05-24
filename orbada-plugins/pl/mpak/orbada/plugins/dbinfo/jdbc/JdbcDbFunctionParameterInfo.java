/*
 * DerbyDbFunctionPropInfo.java
 *
 * Created on 2007-11-15, 17:19:05
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
public class JdbcDbFunctionParameterInfo extends DbObjectIdentified {
  
  private int position;
  private String type;
  private String inOutReturn;

  public JdbcDbFunctionParameterInfo(String name,JdbcDbFunctionInfo owner) {
    super(name, owner);
  }
  
  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
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

  public String getInOutReturn() {
    return inOutReturn;
  }

  public void setInOutReturn(String inOutReturn) {
    this.inOutReturn = inOutReturn;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Type", "Metoda", "Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getPosition()), 
      new Variant(getType()), 
      new Variant(getInOutReturn()), 
      new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
  }
  
}

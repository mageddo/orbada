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
public class JdbcDbTableColumnInfo extends DbObjectIdentified {
  
  private int position;
  private String dataType;
  private Integer dataSize;
  private Integer digits;
  private boolean nullable;
  private String defaultValue;
  
  public JdbcDbTableColumnInfo(String name, JdbcDbTableColumnListInfo owner) {
    super(name, owner);
  }

  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
    }
    return null;
  }

  public JdbcDbTableInfo getTable() {
    DbObjectIdentified o = getOwner(JdbcDbTableInfo.class);
    if (o != null) {
      return (JdbcDbTableInfo)o;
    }
    return null;
  }
  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Integer getDataSize() {
    return dataSize;
  }

  public void setDataSize(Integer dataSize) {
    this.dataSize = dataSize;
  }

  public Integer getDigits() {
    return digits;
  }

  public void setDigits(Integer digits) {
    this.digits = digits;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Typ", "Rozmiar", "Prec.", "Null?", "Wartoœæ domyœlna", "Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getPosition()), 
      new Variant(getDataType()), 
      new Variant(getDataSize()), 
      new Variant(getDigits()), 
      new Variant(isNullable()), 
      new Variant(getDefaultValue()), 
      new Variant(getRemarks())
    };
  }

  public void refresh() throws Exception {
  }
  
  public boolean equals(Object o) {
    if (o instanceof JdbcDbTableColumnInfo) {
      return getName().equals(((JdbcDbTableColumnInfo)o).getName());
    }
    else if (o instanceof String) {
      return getName().equals(o);
    }
    return false;
  }
  
}

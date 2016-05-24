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
public class DerbyDbTableColumnInfo extends DbObjectIdentified {
  
  private int position;
  private String type;
  private String defaultValue;
  private boolean nullable;
  private boolean autoIncremental;
  
  public DerbyDbTableColumnInfo(String name, DerbyDbTableColumnListInfo owner) {
    super(name, owner);
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
    setNullable(type.indexOf("NOT NULL") > 0);
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public boolean isAutoIncremental() {
    return autoIncremental;
  }

  public void setAutoIncremental(boolean autoIncremental) {
    this.autoIncremental = autoIncremental;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Typ", "Wartoœæ domyœlna", "Auto Inc"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getPosition()), 
      new Variant(getType()),
      new Variant(getDefaultValue()),
      new Variant(isAutoIncremental())};
  }

  public void refresh() throws Exception {
  }
  
}

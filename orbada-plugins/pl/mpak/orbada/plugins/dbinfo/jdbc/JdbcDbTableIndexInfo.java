/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
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
public class JdbcDbTableIndexInfo extends DbObjectIdentified {
  
  private int position;
  private String columnName;
  private String ascDesc;
  private boolean unique;
  
  public JdbcDbTableIndexInfo(String name,JdbcDbTableIndexListInfo owner) {
    super(name, owner);
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getAscDesc() {
    return ascDesc;
  }

  public void setAscDesc(String ascDesc) {
    this.ascDesc = ascDesc;
  }

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }
  
  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Nazwa kolumny", "Unikalny", "AscDesc"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getPosition()),
      new Variant(getColumnName()),
      new Variant(isUnique()),
      new Variant(getAscDesc())
    };
  }

  
  public void refresh() throws Exception {
  }
  
}

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
public class DerbyDbIndexInfo extends DbObjectIdentified {
  
  private String schemaName;
  private String tableName;
  
  public DerbyDbIndexInfo(String name, DerbyDbIndexListInfo owner) {
    super(name, owner);
  }
  
  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String[] getMemberNames() {
    return new String[] {"Schemat", "Tabela"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getSchemaName()), 
      new Variant(getTableName())};
  }

  public void refresh() throws Exception {
  }
  
}

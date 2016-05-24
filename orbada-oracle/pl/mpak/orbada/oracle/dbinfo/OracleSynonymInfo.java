/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
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
public class OracleSynonymInfo extends DbObjectIdentified {
  
  private String tableOwner;
  private String tableName;
  private String objectType;
  private String dbLink;
  private String status;
  
  public OracleSynonymInfo(String name, OracleSynonymListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Schemat obiektu", "Nazwa obiektu", "Typ obiektu", "Po³¹czenie zdalne", "Stan"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(tableOwner),
      new Variant(tableName), 
      new Variant(objectType), 
      new Variant(dbLink), 
      new Variant(status)
    };
  }

  public String getDbLink() {
    return dbLink;
  }

  public void setDbLink(String dbLink) {
    this.dbLink = dbLink;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableOwner() {
    return tableOwner;
  }

  public void setTableOwner(String tableOwner) {
    this.tableOwner = tableOwner;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public void refresh() throws Exception {
  }

}

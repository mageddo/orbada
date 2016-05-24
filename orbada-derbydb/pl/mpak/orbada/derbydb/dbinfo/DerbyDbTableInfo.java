/*
 * DerbyDbTableInfo.java
 *
 * Created on 2007-11-15, 20:02:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DefaultDbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbTableInfo extends DbObjectContainer {
  
  public DerbyDbTableInfo(String name, DerbyDbTableListInfo owner) {
    super(name, owner);
  }
  
  public DerbyDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(DerbyDbSchemaInfo.class);
    if (o != null) {
      return (DerbyDbSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {};
  }
  
  public String[] getMemberNames() {
    return new String[] {};
  }
  
  public Variant[] getMemberValues() {
    return new Variant[] {};
  }
  
  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new DerbyDbTableColumnListInfo(this));
      put(new DerbyDbTableTriggerListInfo(this));
      put(new DerbyDbTableIndexListInfo(this));
      put(new DerbyDbTableConstraintListInfo(this));
      put(new DerbyDbTableContentInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }
  
}

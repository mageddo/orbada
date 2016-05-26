/*
 * DerbyDbSchemaInfo.java
 *
 * Created on 2007-11-13, 21:40:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbSchemaInfo extends DbObjectContainer<DbObjectContainer> {
  
  public DerbyDbSchemaInfo(String name, DerbyDbSchemaListInfo owner) {
    super(name, owner);
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
      put(new DerbyDbTableListInfo(this));
      put(new DerbyDbViewListInfo(this));
      put(new DerbyDbFunctionListInfo(this));
      put(new DerbyDbProcedureListInfo(this));
      put(new DerbyDbIndexListInfo(this));
      put(new DerbyDbTriggerListInfo(this));
      put(new DerbyDbFileListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }
  
}

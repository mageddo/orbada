/*
 * DerbyDbSchemaInfo.java
 *
 * Created on 2007-11-13, 21:40:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbSchemaInfo extends DbObjectContainer<DbObjectContainer> {
  
  public HSqlDbSchemaInfo(String name, HSqlDbSchemaListInfo owner) {
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
      put(new HSqlDbTableListInfo(this));
      put(new HSqlDbViewListInfo(this));
      put(new HSqlDbProcedureListInfo(this));
      put(new HSqlDbIndexListInfo(this));
      put(new HSqlDbTriggerListInfo(this));
      put(new HSqlDbSequenceListInfo(this));
      put(new HSqlDbAliasListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }
  
}

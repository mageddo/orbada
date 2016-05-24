/*
 * DerbyDbViewInfo.java
 *
 * Created on 2007-11-15, 19:31:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DefaultDbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbViewInfo extends DbObjectContainer {
  
  public HSqlDbViewInfo(String name, HSqlDbViewListInfo owner) {
    super(name, owner);
  }
  
  public HSqlDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(HSqlDbSchemaInfo.class);
    if (o != null) {
      return (HSqlDbSchemaInfo)o;
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
      put(new HSqlDbViewColumnListInfo(this));
      put(new DefaultDbObjectIdentified("SOURCE", this));
      put(new HSqlDbViewContentInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }
  
}

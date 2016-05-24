/*
 * DefaultDbObjectContainer.java
 *
 * Created on 2007-11-17, 11:46:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo;

import pl.mpak.util.variant.Variant;

/**
 *
 * @param T 
 * @author akaluza
 */
public class DefaultDbObjectContainer<T extends DbObjectIdentified> extends DbObjectContainer<T> {
  
  public DefaultDbObjectContainer(String name, DbObjectIdentified owner) {
    super(name, owner);
  }

  public DefaultDbObjectContainer(String name, DbObjectIdentified owner, boolean refreshed) {
    super(name, owner, refreshed);
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
      DbObjectIdentified o = getOwner();
      if (o != null) {
        o.refresh();
      }
    }
    finally {
      setRefreshed(true);
    }
  }
  
}

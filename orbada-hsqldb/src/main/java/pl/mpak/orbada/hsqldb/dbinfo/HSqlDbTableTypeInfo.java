/*
 * DerbyDbTableTypeInfo.java
 *
 * Created on 2007-11-21, 21:36:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbTableTypeInfo extends DbObjectIdentified {
  
  public HSqlDbTableTypeInfo(String name, HSqlDbTableTypeListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {};
  }

  public void refresh() throws Exception {
  }
  
}

/*
 * DefaultDbObjectIdentified.java
 *
 * Created on 2007-11-17, 11:44:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo;

import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DefaultDbObjectIdentified extends DbObjectIdentified {
  
  public DefaultDbObjectIdentified(String name, DbObjectIdentified owner) {
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

/*
 * DerbyDbViewInfo.java
 *
 * Created on 2007-11-15, 19:31:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDictionaryInfo extends DbObjectContainer implements DbObjectInfo {
  
  public OracleDictionaryInfo(String name, OracleDictionaryListInfo owner) {
    super(name, owner);
  }
  
  public String[] getColumnNames() {
    return new String[] {};
  }

  public String[] getMemberNames() {
    return new String[] {"Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new OracleDictionaryColumnListInfo(this));
      put(new OracleDictionaryContentInfo(this));
      put(new OracleContentListInfo(this));
    } 
    finally {
      setRefreshed(true);
    }
  }

  public String getObjectType() {
    return "DICTIONARY";
  }
  
}

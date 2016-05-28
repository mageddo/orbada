/*
 * DerbyDbFileListInfo.java
 *
 * Created on 2007-11-17, 12:12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbTypeListInfo extends DbObjectContainer<DerbyDbTypeInfo> {
  
  public DerbyDbTypeListInfo(DerbyDbDatabaseInfo owner) {
    super("TYPES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Typ SQL", "Wielkoœæ", "Parametry"};
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
      Query query = getDatabase().createQuery();
      try {
        query.setResultSet(getDatabase().getMetaData().getTypeInfo());
        while (!query.eof()) {
          DerbyDbTypeInfo info = new DerbyDbTypeInfo(query.fieldByName("type_name").getString(), this);
          info.setSqlType(query.fieldByName("data_type").getInteger());
          info.setPrecision(query.fieldByName("precision").getInteger());
          info.setCreateParams(query.fieldByName("create_params").getString());
          put(info);
          query.next();
        }
      }
      finally {
        query.close();
      }
    }
    finally {
      setRefreshed(true);
    }
  }
  
}

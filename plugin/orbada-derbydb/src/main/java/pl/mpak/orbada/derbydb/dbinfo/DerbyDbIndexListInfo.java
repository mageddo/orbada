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
public class DerbyDbIndexListInfo extends DbObjectContainer<DerbyDbIndexInfo> {
  
  public DerbyDbIndexListInfo(DerbyDbSchemaInfo owner) {
    super("INDEXES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Schemat", "Tabela"};
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
        query.setSqlText(DerbyDbSql.getObjectsByType());
        query.paramByName("schemaname").setString(((DerbyDbSchemaInfo)getOwner()).getName());
        query.paramByName("objecttype").setString("INDEX");
        query.open();
        while (!query.eof()) {
          DerbyDbIndexInfo index = new DerbyDbIndexInfo(query.fieldByName("objectname").getString(), this);
          index.setSchemaName(query.fieldByName("tableschema").getString());
          index.setTableName(query.fieldByName("tablename").getString());
          put(index);
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

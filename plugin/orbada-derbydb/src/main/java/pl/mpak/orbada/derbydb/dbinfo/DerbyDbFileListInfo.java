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
public class DerbyDbFileListInfo extends DbObjectContainer<DerbyDbFileInfo> {
  
  public DerbyDbFileListInfo(DerbyDbSchemaInfo owner) {
    super("FILES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Nazwa pliku"};
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
        query.paramByName("objecttype").setString("FILE");
        query.open();
        while (!query.eof()) {
          DerbyDbFileInfo file = new DerbyDbFileInfo(query.fieldByName("objectname").getString(), this);
          file.setFileName(query.fieldByName("filename").getString());
          put(file);
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

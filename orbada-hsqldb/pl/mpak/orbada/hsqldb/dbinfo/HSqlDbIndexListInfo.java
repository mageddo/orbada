/*
 * DerbyDbFileListInfo.java
 *
 * Created on 2007-11-17, 12:12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbIndexListInfo extends DbObjectContainer<HSqlDbIndexInfo> {
  
  public HSqlDbIndexListInfo(HSqlDbSchemaInfo owner) {
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
        query.setSqlText(Sql.getObjectsName(HSqlDbInfoProvider.getVersionTest(getDatabase())));
        query.paramByName("schema_name").setString(((HSqlDbSchemaInfo)getOwner()).getName());
        query.paramByName("object_type").setString("INDEX");
        query.open();
        while (!query.eof()) {
          HSqlDbIndexInfo index = new HSqlDbIndexInfo(query.fieldByName("object_name").getString(), this);
          index.setSchemaName(query.fieldByName("table_schema").getString());
          index.setTableName(query.fieldByName("table_name").getString());
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

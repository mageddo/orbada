/*
 * DerbyDbFileListInfo.java
 *
 * Created on 2007-11-17, 12:12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbSchemaListInfo extends DbObjectContainer<JdbcDbSchemaInfo> {
  
  public JdbcDbSchemaListInfo(JdbcDbDatabaseInfo owner) {
    super("SCHEMAS", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Katalog"};
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
        query.setResultSet(getDatabase().getMetaData().getSchemas());
        while (!query.eof()) {
          JdbcDbSchemaInfo schema = new JdbcDbSchemaInfo(query.fieldByName("table_schem").getString(), this);
          if (query.findFieldByName("table_catalog") != null) {
            schema.setCatalog(query.fieldByName("table_catalog").getString());
          }
          put(schema);
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

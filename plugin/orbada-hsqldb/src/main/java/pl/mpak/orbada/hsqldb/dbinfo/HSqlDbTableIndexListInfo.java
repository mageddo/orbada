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
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbTableIndexListInfo extends DbObjectContainer<HSqlDbTableIndexInfo> {
  
  public HSqlDbTableIndexListInfo(HSqlDbTableInfo owner) {
    super("INDEXES", owner);
  }

  public HSqlDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(HSqlDbSchemaInfo.class);
    if (o != null) {
      return (HSqlDbSchemaInfo)o;
    }
    return null;
  }

  public HSqlDbTableInfo getTable() {
    DbObjectIdentified o = getOwner(HSqlDbTableInfo.class);
    if (o != null) {
      return (HSqlDbTableInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Nazwa kolumny", "Typ", "Unikalny", "Ogranicz."};
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
        query.setSqlText(Sql.getIndexList(null));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("table_name").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          HSqlDbTableIndexInfo index = new HSqlDbTableIndexInfo(query.fieldByName("index_name").getString(), this);
          index.setColumnName(query.fieldByName("column_name").getString());
          index.setType(query.fieldByName("index_type").getString());
          index.setUnique("YES".equals(query.fieldByName("unique_index").getString()));
          index.setConstraint("YES".equals(query.fieldByName("pk_index").getString()));
          put(index.getName() +"." +index.getColumnName(), index);
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

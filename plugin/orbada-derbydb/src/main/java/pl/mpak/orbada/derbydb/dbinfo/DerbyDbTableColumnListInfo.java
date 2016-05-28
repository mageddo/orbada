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
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbTableColumnListInfo extends DbObjectContainer<DerbyDbTableColumnInfo> {
  
  public DerbyDbTableColumnListInfo(DerbyDbTableInfo owner) {
    super("COLUMNS", owner);
  }

  public DerbyDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(DerbyDbSchemaInfo.class);
    if (o != null) {
      return (DerbyDbSchemaInfo)o;
    }
    return null;
  }

  public DerbyDbTableInfo getTable() {
    DbObjectIdentified o = getOwner(DerbyDbTableInfo.class);
    if (o != null) {
      return (DerbyDbTableInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {"Pozycja", "Typ", "Wartoœæ domyœlna", "Auto Inc"};
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
        query.setSqlText(DerbyDbSql.getTableColumnList(null));
        query.paramByName("schemaname").setString(getSchema().getName());
        query.paramByName("tablename").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          DerbyDbTableColumnInfo column = new DerbyDbTableColumnInfo(query.fieldByName("columnname").getString(), this);
          column.setPosition(query.fieldByName("columnnumber").getInteger());
          column.setType(query.fieldByName("columndatatype").getString());
          column.setDefaultValue(query.fieldByName("columndefault").getString());
          column.setAutoIncremental(!query.fieldByName("autoincrementinc").isNull());
          put(column);
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

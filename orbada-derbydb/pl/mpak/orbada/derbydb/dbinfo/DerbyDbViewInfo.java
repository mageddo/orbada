/*
 * DerbyDbViewInfo.java
 *
 * Created on 2007-11-15, 19:31:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DefaultDbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DefaultDbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbViewInfo extends DbObjectContainer {
  
  public DerbyDbViewInfo(String name, DerbyDbViewListInfo owner) {
    super(name, owner);
    put(new DefaultDbObjectContainer("COLUMNS", this) {
      public String[] getColumnNames() {
        return new String[] {"Pozycja", "Typ"};
      }
    });
    put(new DefaultDbObjectIdentified("SOURCE", this));
    put(new DefaultDbObjectIdentified("CONTENT", this));
  }
  
  public DerbyDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(DerbyDbSchemaInfo.class);
    if (o != null) {
      return (DerbyDbSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {};
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
    DbObjectContainer columns = (DbObjectContainer)get("COLUMNS");
    columns.setRefreshing(true);
    try {
      columns.clear();
      Query query = getDatabase().createQuery();
      try {
        query.setSqlText(DerbyDbSql.getTableColumnList(null));
        query.paramByName("schemaname").setString(getSchema().getName());
        query.paramByName("tablename").setString(getName());
        query.open();
        while (!query.eof()) {
          DerbyDbViewColumnInfo column = new DerbyDbViewColumnInfo(query.fieldByName("columnname").getString(), this);
          column.setPosition(query.fieldByName("columnnumber").getInteger());
          column.setType(query.fieldByName("columndatatype").getString());
          columns.put(column);
          query.next();
        }
      }
      finally {
        query.close();
      }
    }
    finally {
      setRefreshed(true);
      columns.setRefreshed(true);
    }
  }
  
}

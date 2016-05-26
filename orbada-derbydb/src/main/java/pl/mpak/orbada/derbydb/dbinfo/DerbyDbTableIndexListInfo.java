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
public class DerbyDbTableIndexListInfo extends DbObjectContainer<DerbyDbTableIndexInfo> {
  
  public DerbyDbTableIndexListInfo(DerbyDbTableInfo owner) {
    super("INDEXES", owner);
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
        query.setSqlText(DerbyDbSql.getTableIndexList(null));
        query.paramByName("schemaname").setString(getSchema().getName());
        query.paramByName("tablename").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          DerbyDbTableIndexInfo index = new DerbyDbTableIndexInfo(query.fieldByName("index_name").getString(), this);
          index.setColumnName(query.fieldByName("columnname").getString());
          index.setType(query.fieldByName("descriptor").getString());
          index.setUnique(query.fieldByName("isunique").getInteger() == 1);
          index.setConstraint(query.fieldByName("isconstraint").getInteger() == 1);
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

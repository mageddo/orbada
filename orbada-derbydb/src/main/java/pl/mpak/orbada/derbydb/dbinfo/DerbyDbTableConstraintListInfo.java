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
public class DerbyDbTableConstraintListInfo extends DbObjectContainer<DerbyDbTableConstraintInfo> {
  
  public DerbyDbTableConstraintListInfo(DerbyDbTableInfo owner) {
    super("CONSTRAINTS", owner);
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
    return new String[] {"Typ", "W³¹czony", "Opis"};
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
        query.setSqlText(DerbyDbSql.getTableConstraintList(null));
        query.paramByName("schemaname").setString(getSchema().getName());
        query.paramByName("tablename").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          DerbyDbTableConstraintInfo constraint = new DerbyDbTableConstraintInfo(query.fieldByName("constraintname").getString(), this);
          constraint.setType(query.fieldByName("type").getString());
          constraint.setEnable("Y".equals(query.fieldByName("enabled").getString()));
          constraint.setRemarks(query.fieldByName("remarks").getString());
          put(constraint);
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

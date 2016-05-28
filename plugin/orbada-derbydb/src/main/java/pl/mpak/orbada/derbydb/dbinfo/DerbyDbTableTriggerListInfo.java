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
public class DerbyDbTableTriggerListInfo extends DbObjectContainer<DerbyDbTableTriggerInfo> {
  
  public DerbyDbTableTriggerListInfo(DerbyDbTableInfo owner) {
    super("TRIGGERS", owner);
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
    return new String[] {"Utworzony", "Stan", "Zda¿enie", "Kiedy", "Typ"};
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
        query.setSqlText(DerbyDbSql.getTableTriggerList(null));
        query.paramByName("schemaname").setString(getSchema().getName());
        query.paramByName("tablename").setString(getTable().getName());
        query.open();
        while (!query.eof()) {
          DerbyDbTableTriggerInfo trigger = new DerbyDbTableTriggerInfo(query.fieldByName("triggername").getString(), this);
          trigger.setCreated(query.fieldByName("creationtimestamp").getTimestamp());
          trigger.setState(query.fieldByName("enabled").getString());
          trigger.setEvent(query.fieldByName("event").getString());
          trigger.setFiringTime(query.fieldByName("firingtime").getString());
          trigger.setType(query.fieldByName("type").getString());
          put(trigger);
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

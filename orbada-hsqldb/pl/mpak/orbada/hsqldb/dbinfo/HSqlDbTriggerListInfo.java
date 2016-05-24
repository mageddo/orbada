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
public class HSqlDbTriggerListInfo extends DbObjectContainer<HSqlDbTriggerInfo> {
  
  public HSqlDbTriggerListInfo(HSqlDbSchemaInfo owner) {
    super("TRIGGERS", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Tabela", "Utworzony", "Stan", "Zda¿enie", "Kiedy", "Typ"};
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
        query.setSqlText(Sql.getTriggerList(null, HSqlDbInfoProvider.getVersionTest(getDatabase())));
        query.paramByName("schema_name").setString(((HSqlDbSchemaInfo)getOwner()).getName());
        query.paramByName("table_name").clearValue();
        query.open();
        while (!query.eof()) {
          HSqlDbTriggerInfo trigger = new HSqlDbTriggerInfo(query.fieldByName("trigger_name").getString(), this);
          trigger.setTableName(query.fieldByName("table_name").getString());
          trigger.setState(query.fieldByName("status").getString());
          trigger.setEvent(query.fieldByName("triggering_event").getString());
          trigger.setFiringTime(query.fieldByName("trigger_type").getString());
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

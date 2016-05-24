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
public class HSqlDbSequenceListInfo extends DbObjectContainer<HSqlDbSequenceInfo> {
  
  public HSqlDbSequenceListInfo(HSqlDbSchemaInfo owner) {
    super("SEQUENCES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Wartoœæ min.", "Wartoœæ max.", "Inc", "Start od"};
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
        query.setSqlText(Sql.getSequenceList(null, HSqlDbInfoProvider.getVersionTest(getDatabase())));
        query.paramByName("schema_name").setString(((HSqlDbSchemaInfo)getOwner()).getName());
        query.open();
        while (!query.eof()) {
          HSqlDbSequenceInfo seq = new HSqlDbSequenceInfo(query.fieldByName("sequence_name").getString(), this);
          seq.setMinimumValue(query.fieldByName("minimum_value").getLong());
          seq.setMaximumValue(query.fieldByName("maximum_value").getLong());
          seq.setIncrement(query.fieldByName("increment").getLong());
          seq.setStartWith(query.fieldByName("start_with").getLong());
          put(seq);
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

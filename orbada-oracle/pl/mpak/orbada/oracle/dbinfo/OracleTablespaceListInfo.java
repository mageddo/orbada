package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTablespaceListInfo extends DbObjectContainer<OracleTablespaceInfo> {
  
  public OracleTablespaceListInfo(DbObjectContainer owner) {
    super("TABLESPACES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Rozmiar bloku", "Init.Ext.", "Next Ext.", "Min Ext.", "Max Ext.", "Status", "Zawartoœæ"};
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
        if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false"))) {
          query.setSqlText(Sql.getDbaTablespaceList());
        }
        else {
          query.setSqlText(Sql.getTablespaceList());
        }
        query.open();
        while (!query.eof()) {
          OracleTablespaceInfo info = new OracleTablespaceInfo(query.fieldByName("tablespace_name").getString(), this);
          info.setBlockSize(query.fieldByName("block_size").getLong());
          info.setInitialExtent(query.fieldByName("initial_extent").getBigDecimal().toBigInteger());
          if (!query.fieldByName("next_extent").isNull()) {
            info.setNextExtent(query.fieldByName("next_extent").getBigDecimal().toBigInteger());
          }
          if (!query.fieldByName("min_extents").isNull()) {
            info.setMinExtents(query.fieldByName("min_extents").getBigDecimal().toBigInteger());
          }
          if (!query.fieldByName("max_extents").isNull()) {
            info.setMaxExtents(query.fieldByName("max_extents").getBigDecimal().toBigInteger());
          }
          info.setStatus(query.fieldByName("status").getString());
          info.setContents(query.fieldByName("contents").getString());
          put(info);
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

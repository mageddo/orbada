package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleSystemPrivilegeMapListInfo extends DbObjectContainer<OracleSystemPrivilegeMapInfo> {
  
  public OracleSystemPrivilegeMapListInfo(DbObjectContainer owner) {
    super("SYSTEM PRIVILEGE MAP", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Id prawa", "W³aœciwoœæ"};
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
        query.setSqlText(Sql.getSystemPrivilegeMapList());
        query.open();
        while (!query.eof()) {
          OracleSystemPrivilegeMapInfo info = new OracleSystemPrivilegeMapInfo(query.fieldByName("name").getString(), this);
          info.setPrivilege(query.fieldByName("privilege").getLong());
          info.setProperty(query.fieldByName("property").getLong());
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

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDirectoryListInfo extends DbObjectContainer<OracleDirectoryInfo> {
  
  public OracleDirectoryListInfo(DbObjectContainer owner) {
    super("DIRECTORIES", owner);
  }

  public String[] getColumnNames() {
    return new String[] {"Schemat", "Œcie¿ka"};
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
        query.setSqlText(Sql.getDirectoryList());
        query.open();
        while (!query.eof()) {
          OracleDirectoryInfo info = new OracleDirectoryInfo(query.fieldByName("directory_name").getString(), this);
          info.setDirectoryOwner(query.fieldByName("owner").getString());
          info.setDirectoryPath(query.fieldByName("directory_path").getString());
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

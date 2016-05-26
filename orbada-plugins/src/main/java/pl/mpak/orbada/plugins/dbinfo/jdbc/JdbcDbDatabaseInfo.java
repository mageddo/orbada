package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbDatabaseInfo extends DbDatabaseInfo {
  
  public JdbcDbDatabaseInfo(Database database) {
    super(database);
  }

  public String[] getColumnNames() {
    return new String[] {};
  }

  public String[] getMemberNames() {
    return new String[] {"Version", "Banner"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {new Variant(getVersion()), new Variant(getBanner())};
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      setVersion(getDatabase().getMetaData().getDatabaseProductVersion());
      setBanner(
        "Database: " +getDatabase().getMetaData().getDatabaseProductName() +", " +getDatabase().getMetaData().getDatabaseProductVersion() +"\n" +
        "Driver: " +getDatabase().getMetaData().getDriverName() +", " +getDatabase().getMetaData().getDriverVersion());
      setRemarks(getBanner());
      String[] schemaArray = getDatabase().getSchemaArray();
      if (schemaArray == null || schemaArray.length == 0) {
        put(new JdbcDbTableListInfo(this));
        put(new JdbcDbViewListInfo(this));
        put(new JdbcDbFunctionListInfo(this));
        put(new JdbcDbProcedureListInfo(this));
        put(new JdbcDbIndexListInfo(this));
      }
      else {
        put(new JdbcDbSchemaListInfo(this));
      }
      put(new JdbcDbTableTypeListInfo(this));
      put(new JdbcDbTypeListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }

  public String getDescription() {
    return "Universal Jdbc Database Info Provider";
  }
  
}

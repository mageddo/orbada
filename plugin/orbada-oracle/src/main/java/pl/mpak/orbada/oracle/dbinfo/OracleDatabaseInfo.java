package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDatabaseInfo extends DbDatabaseInfo {
  
  public OracleDatabaseInfo(Database database) {
    super(database);
    try {
      refresh();
    } catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
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
      setVersion(OracleDbInfoProvider.getVersionInfo(getDatabase()));
      setBanner(
        "Database:\n" +OracleDbInfoProvider.getBannerInfo(getDatabase()) +"\n\n" +
        "Driver:\n" +getDatabase().getMetaData().getDriverName() +", " +getDatabase().getMetaData().getDriverVersion());
      setRemarks(getBanner());
      put(new OracleSchemaListInfo(this));
      put(new OracleDictionaryListInfo(this));
      put(new OracleTableTypeListInfo(this));
      put(new OracleDataTypeListInfo(this));
      if (OracleDbInfoProvider.instance.getMajorVersion(getDatabase()) >= 10) {
        put(new OracleRecyclebinListInfo(this));
      }
      put(new OracleTablespaceListInfo(this));
      put(new OracleNlsCharsetListInfo(this));
      put(new OracleSystemPrivilegeMapListInfo(this));
      put(new OracleDirectoryListInfo(this));
      put(new OracleSessionPrivilegeListInfo(this));
      put(new OracleExceptionTableListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }

  public String getDescription() {
    return OrbadaOraclePlugin.oracleDriverType +" Database Info Provider";
  }
  
}

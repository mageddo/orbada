/*
 * DerbyDbDatabaseInfo.java
 *
 * Created on 2007-11-13, 21:25:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDatabaseInfo extends DbDatabaseInfo {
  
  public HSqlDatabaseInfo(Database database) {
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
      setVersion(getDatabase().getMetaData().getDatabaseProductVersion());
      setBanner(
        "Database: " +getDatabase().getMetaData().getDatabaseProductName() +", " +getDatabase().getMetaData().getDatabaseProductVersion() +"\n" +
        "Driver: " +getDatabase().getMetaData().getDriverName() +", " +getDatabase().getMetaData().getDriverVersion());
      setRemarks(getBanner());
      put(new HSqlDbSchemaListInfo(this));
      put(new HSqlDbTableTypeListInfo(this));
      put(new HSqlDbTypeListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }

  public String getDescription() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType +" Database Info Provider";
  }
  
}

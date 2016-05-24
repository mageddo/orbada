/*
 * DerbyDbDatabaseInfo.java
 *
 * Created on 2007-11-13, 21:25:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbDatabaseInfo extends DbDatabaseInfo {
  
  public DerbyDbDatabaseInfo(Database database) {
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
      put(new DerbyDbSchemaListInfo(this));
      put(new DerbyDbTableTypeListInfo(this));
      put(new DerbyDbTypeListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }

  public String getDescription() {
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType +" Database Info Provider";
  }
  
}

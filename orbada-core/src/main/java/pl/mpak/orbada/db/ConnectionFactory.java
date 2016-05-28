package pl.mpak.orbada.db;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import pl.mpak.orbada.ErrorMessages;
import pl.mpak.orbada.OrbadaException;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.ErrorMessages;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.Driver;
import pl.mpak.orbada.db.DriverType;
import pl.mpak.orbada.db.Schema;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author akaluza
 */
public class ConnectionFactory {
  
  private String url;
  private String user;
  private String password;
  private String sch_id;
  
  private Schema schema;
  private Driver driver;
  
  private Database cloneDatabase;
  
  /**
   * <p>Klonowanie po³¹czenia. Zostan¹ jedynie skopiowane podstawowe parametry po³¹czenia.
   * @param cloneDatabase
   */
  public ConnectionFactory(Database cloneDatabase) {
    this.cloneDatabase = cloneDatabase;
    this.url = cloneDatabase.getUrl();
    this.user = cloneDatabase.getUserName();
    this.password = cloneDatabase.getPassword();
    this.sch_id = cloneDatabase.getUserProperties().getProperty("schemaId");
  }
  
  public ConnectionFactory(String url, String user, String password, String sch_id) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.sch_id = sch_id;
  }
  
  public ConnectionFactory(String url, String user, String password, Schema schema) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.schema = schema;
  }
  
  public static ConnectionFactory createInstance() {
    return new ConnectionFactory(null, null, null, (String)null);
  }
  
  public static ConnectionFactory createInstance(String url, String user, String password, String sch_id) {
    return new ConnectionFactory(url, user, password, sch_id);
  }
  
  public static ConnectionFactory createInstance(String url, String user, String password, Schema schema) {
    return new ConnectionFactory(url, user, password, schema);
  }
  
  public Database createDatabase() throws UseDBException, OrbadaException, VariantException, SQLException {
    return createDatabase(false);
  }
  
  public Database createDatabase(boolean testConnection) throws UseDBException, OrbadaException, VariantException, SQLException {
    if (schema == null) {
      schema = new Schema(InternalDatabase.get(), sch_id);
    }
    if (!schema.fieldByName("sch_drv_id").getValue().isNullValue()) {
      driver = schema.getDriver();
    }
    if (getDriverClass() == null) {
      throw new OrbadaException(ErrorMessages.ORBADA_01001_NO_DRIVER_FOUND);
    }
    if (url == null || "".equals(url)) {
      url = schema.replacePatts(driver.fieldByName("drv_url_template").getValue().toString());
      if (url == null || "".equals(url)) {
        throw new OrbadaException(ErrorMessages.ORBADA_01005_NO_URL);
      }
    }
    if (!Application.get().isUserAdmin() && url.equalsIgnoreCase(InternalDatabase.get().getUrl()) && user.equalsIgnoreCase(InternalDatabase.get().getUserName())) {
      throw new OrbadaException(ErrorMessages.ORBADA_01004_NO_RIGHTS);
    }
    final OrbadaDatabase database;
    String driverClass = getDriverClass();
    database = DatabaseManager.createDatabase(OrbadaDatabase.class);
    try {
      database.setDriver(DriverClassLoaderManager.getDriver(getLibrarySource(), getExtraLibrary(), driverClass));
    } catch (Exception ex) {
      throw new OrbadaException(ErrorMessages.ORBADA_01001_NO_DRIVER_FOUND, ex);
    }
    database.setUrl(url);
    database.setUserName(user);
    database.setPassword(password);
    Properties properties = schema.getProperties();
    if (properties != null) {
      Iterator<Entry<Object, Object>> i = properties.entrySet().iterator();
      while (i.hasNext()) {
        Entry<Object, Object> e = i.next();
        database.getProperties().put(e.getKey(), e.getValue());
      }
    }
    properties = schema.getUserProperties();
    if (properties != null) {
      Iterator<Entry<Object, Object>> i = properties.entrySet().iterator();
      while (i.hasNext()) {
        Entry<Object, Object> e = i.next();
        database.getUserProperties().put(e.getKey(), e.getValue());
      }
    }
    database.connect();
    
    if (!testConnection) {
      if (cloneDatabase == null) {
        database.setPublicName(schema.getPublicNameResolved(user));
      }
      else {
        database.setPublicName(cloneDatabase.getPublicName() +" (Kopia)");
      }
      try {
        DriverType dt = driver.getDriverType();
        database.getUserProperties().setProperty("dtp_id", dt.getId());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      database.getUserProperties().setProperty("schemaId", sch_id.toString());
      database.setDriverType(driver.fieldByName("DRV_TYPE_NAME").getValue().toString());
      database.setServerName(schema.fieldByName("SCH_DATABASE").getValue().toString());
      try {
        if (database.getMetaData().supportsTransactions()) {
          database.setAutoCommit(StringUtil.toBoolean(schema.fieldByName("sch_auto_commit").getValue().getString()));
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      schema.fieldByName("sch_selected").setValue(new Variant(System.currentTimeMillis()));
      try {
        DatabaseInfoProvider[] dip = Application.get().getServiceArray(DatabaseInfoProvider.class);
        if (dip != null && dip.length > 0) {
          for (int i=0; i<dip.length; i++) {
            if (dip[i].isForDatabase(database)) {
              schema.fieldByName("sch_db_version").setValue(new Variant(dip[i].getVersion(database)));
            }
          }
        }
        else {
          DatabaseMetaData dmd = database.getConnection().getMetaData();
          schema.fieldByName("sch_db_version").setValue(new Variant(dmd.getDatabaseMajorVersion() +"." +dmd.getDatabaseMinorVersion()));
        }
      } catch (SQLException ex) {
        ExceptionUtil.processException(ex);
      }
      if (cloneDatabase == null) {
        try {
          schema.applyUpdate();
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
      DatabaseProvider[] dpa = Application.get().getServiceArray(DatabaseProvider.class);
      for (DatabaseProvider dp : dpa) {
        if (dp.isForDatabase(database)) {
          dp.afterConnection(database);
        }
      }
      if (cloneDatabase == null) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            Application.get().getMainFrame().openPerspectiveFor(database, schema);
          }
        });
      }
    }
    database.createSession(schema.getSchId());
    return database;
  }
  
  public String getDriverClass() {
    if (driver != null) {
      return driver.fieldByName("drv_class_name").getValue().toString();
    }
    return null;
  }
  
  public String getLibrarySource() {
    if (driver != null && !driver.fieldByName("DRV_LIBRARY_SOURCE").isNull()) {
      return driver.fieldByName("DRV_LIBRARY_SOURCE").getValue().toString();
    }
    return null;
  }
  
  public String getExtraLibrary() {
    if (driver != null && !driver.fieldByName("DRV_EXTRA_LIBRARY").isNull()) {
      return driver.fieldByName("DRV_EXTRA_LIBRARY").getValue().toString();
    }
    return null;
  }

  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public String getUser() {
    return user;
  }
  
  public void setUser(String user) {
    this.user = user;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
}

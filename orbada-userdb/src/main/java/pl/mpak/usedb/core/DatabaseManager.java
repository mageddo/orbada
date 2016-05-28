package pl.mpak.usedb.core;

import javax.swing.event.EventListenerList;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.UseDBObject;
import pl.mpak.util.StringUtil;

/**
 * @author Andrzej Kaluza
 * 
 * Menadzer obiektow Database
 * Pozwala uzyskac dostep do listy otwartych polaczen.
 * Pozwala zdefiniowac globalne zdazenia dotyczace utworzenia polaczenia 
 * lub jego zamkniecia
 *
 */
public class DatabaseManager extends UseDBObject {
  private static final long serialVersionUID = 3812230028151762948L;
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

  public final static String LK_DRIVER_CLASS_NAME = "DriverClassName";
  public final static String LK_DRIVER_TYPE       = "DriverType";
  public final static String LK_DATABASE_LOCKED   = "DatabaseLocked";
  public final static String LK_PUBLIC_NAME       = "PublicName";

  private static DatabaseManager databaseManager = new DatabaseManager();
  private ArrayList<Database> databaseList;

  public enum Event {
    DATABASE_ADDED,
    DATABASE_REMOVED
  }
  private EventListenerList databaseManagerListenerList = new EventListenerList();
  
  public DatabaseManager() {
    super();
    databaseList = new ArrayList<Database>();
  }
  
  public static DatabaseManager getManager() {
    return databaseManager;
  }
  
  public void addDatabaseManagerListener(DatabaseManagerListener listener) {
    synchronized (databaseManagerListenerList) {
      databaseManagerListenerList.add(DatabaseManagerListener.class, listener);
    }
  }
  
  public void removeDatabaseManagerListener(DatabaseManagerListener listener) {
    synchronized (databaseManagerListenerList) {
      databaseManagerListenerList.remove(DatabaseManagerListener.class, listener);
    }
  }
  
  public void fireDatabaseManagerListener(Event event, Database database) {
    synchronized (databaseManagerListenerList) {
      DatabaseManagerListener[] listeners = databaseManagerListenerList.getListeners(DatabaseManagerListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case DATABASE_ADDED: 
            listeners[i].databaseAdded(new DatabaseManagerEvent(this, database));
            break;
          case DATABASE_REMOVED: 
            listeners[i].databaseRemoved(new DatabaseManagerEvent(this, database));
            break;
        }
      }
    }  
  }
  
  void addDatabase(Database database) {
    if (databaseList.indexOf(database) == -1) {
      databaseList.add(database);
      fireDatabaseManagerListener(Event.DATABASE_ADDED, database);
    }
  }
  
  void removeDatabase(Database database) {
    databaseList.remove(database);
    fireDatabaseManagerListener(Event.DATABASE_REMOVED, database);
  }

  public static <T extends Database> T createDatabase(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      LOGGER.warn("M=createDatabase", e);
      return null;
    } catch (IllegalAccessException e) {
      LOGGER.warn("M=createDatabase", e);
      return null;
    }
  }
  
  public static Database createDatabase() {
    return new Database();
  }
  
  public static <T extends Database> T createDatabase(Class<T> clazz, String driverClassName, String url, String userName, String password) throws UseDBException {
    T database = createDatabase(clazz);
    
    try {
      database.setDriverClassName(driverClassName);
      database.setUserName(userName);
      database.setPassword(password);
      database.setUrl(url);
      
      database.connect();
    } catch (Exception e) {
      throw new UseDBException(e);
    }
    
    return database;
  }
  
  public static Database createDatabase(
      String driverClassName, String url, String userName, String password) throws UseDBException {
      Database database = createDatabase();
      
      try {
        database.setDriverClassName(driverClassName);
        database.setUserName(userName);
        database.setPassword(password);
        database.setUrl(url);
        
        database.connect();
      } catch (Exception e) {
        throw new UseDBException(e);
      }
      
      return database;
    }
    
  public static Database createDatabase(String driverClassName, String url) throws UseDBException {
    return createDatabase(driverClassName, url, null, null);
  }

  public static <T extends Database> T createDatabase(Class<T> clazz, Driver driver, String url, String userName, String password) throws UseDBException {
    T database = createDatabase(clazz);
    LOGGER.info("M=createDatabase, database={}, databaseClass={}", database, database.getClass());
    try {
      database.setDriver(driver);
      database.setUserName(userName);
      database.setPassword(password);
      database.setUrl(url);

      LOGGER.info("M=createDatabase, action=connecting");
      database.connect();
    } catch (Exception e) {
      throw new UseDBException(e);
    }
    
    return database;
  }
  
  public static Database createDatabase(
      Driver driver, String url, String userName, String password) throws UseDBException {
      Database database = createDatabase();
      
      try {
        database.setDriver(driver);
        database.setUserName(userName);
        database.setPassword(password);
        database.setUrl(url);
        
        database.connect();
      } catch (Exception e) {
        throw new UseDBException(e);
      }
      
      return database;
    }
    
  public static Database createDatabase(Driver driver, String url) throws UseDBException {
    return createDatabase(driver, url, null, null);
  }

  public static DatabasePool createDatabasePool() {
    return new DatabasePool();
  }
  
  public static DatabasePool createDatabasePool(String driverClassName, String url) {
    return createDatabasePool(driverClassName, url, null, null);
  }
  
  public static DatabasePool createDatabasePool(String driverClassName, String url, String userName, String password) {
    DatabasePool dp = createDatabasePool();
    
    dp.setDriverClassName(driverClassName);
    dp.setUserName(userName);
    dp.setPassword(password);
    dp.setUrl(url);
    
    return dp;
  }
  
  public int getDatabaseCount() {
    return databaseList.size();
  }
  
  public Database getDatabase(int index) {
    return databaseList.get(index);
  }
  
  /**
   * <p>Pozwala utworzy? list? dost?pnych po??cze? zgodnie z kluczem i warto?ci.
   * @param key zgodnie ze sta?ymi LK_... lub null
   * @param value
   * @return
   */
  public List<Database> getDatabaseList(String key, String value) {
    ArrayList<Database> list = new ArrayList<Database>();
    for (int i=0; i<getDatabaseCount(); i++) {
      Database d = getDatabase(i);
      if (LK_DRIVER_CLASS_NAME.equals(key)) {
        if (value.equals(d.getDriverClassName())) {
          list.add(d);
        }
      }
      else if (LK_DRIVER_TYPE.equals(key)) {
        if (value.equals(d.getDriverType())) {
          list.add(d);
        }
      }
      else if (LK_DATABASE_LOCKED.equals(key)) {
        if (StringUtil.toBoolean(value) == d.isLocked()) {
          list.add(d);
        }
      }
      else if (LK_PUBLIC_NAME.equals(key)) {
        String publicName = d.getPublicName().replaceAll("<.*?>", "");
        if (value.equals(publicName)) {
          list.add(d);
        }
      }
      else {
        list.add(d);
      }
    }
    return list;
  }
  
}

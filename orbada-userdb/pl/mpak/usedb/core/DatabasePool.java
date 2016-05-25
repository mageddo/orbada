package pl.mpak.usedb.core;

import java.io.Closeable;
import java.util.ArrayList;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.UseDBObject;

/**
 * @author akaluza
 * <p>Tworzy listê otwartych i dostêpnych do urzycia po³¹czeñ baz danych
 * tej samej konfiguracji.
 * <p>Najlepiej utworzyæ DatabasePool przy pomocy DatabaseManager.
 * <p>Aby pobraæ po³¹czenie nale¿y wywo³aæ lockDatabase()
 * <p>Po zakoñczonych operacjach nale¿y koniecznie wywo³aæ unlockDatabase(Database)
 */
public class DatabasePool extends UseDBObject implements Closeable {
  private static final long serialVersionUID = 7065631229517129492L;

  private String driverClassName;
  private String url;
  private String userName;
  private String password;
  private boolean autoCommit = true;
  private int poolSize = 10;
  private boolean closeAfterUnlock = false;
  private long closeAfterMillis = 0;
  
  private ArrayList<Database> databaseList;
  
  public DatabasePool() {
    super();
    databaseList = new ArrayList<Database>();
  }

  /**
   * <p>Zwraca utworzony i po³¹czony object Database.
   * @return
   * @throws UseDBException
   */
  public Database lockDatabase() throws UseDBException {
    return lockDatabase(true);
  }
  
  private Database findDatabase() throws UseDBException {
    for (int i=0; i<databaseList.size(); i++) {
      Database database = databaseList.get(i);
      if (!database.isLocked()) {
        return database;
      }
    }
    if (databaseList.size() < poolSize) {
      Database database = DatabaseManager.createDatabase(driverClassName, url, userName, password);
      databaseList.add(database);
      return database;
    }
    return null;
  }
  
  /**
   * <p>Zwraca utworzony i po³¹czony object Database.
   * @param waitfor ustawiony na true powoduje oczekiwanie na wolny obiekt Database
   * @return
   * @throws UseDBException
   * @see unlockDatabase
   */
  public Database lockDatabase(boolean waitfor) throws UseDBException {
    synchronized (this) {
      Database database = findDatabase();
      if (database != null) {
        database.lock();
        return database;
      }
    }
    while (waitfor) {
      synchronized (this) {
        Database database = findDatabase();
        if (database != null) {
          database.lock();
          return database;
        }
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new UseDBException(e);
      }
    }
    return null;
  }
  
  /**
   * <p>Zwalnia do u¿ytku obiekt Database.
   * <p>Jeœli ustawiona jest w³aœciwoœæ closeAfterUnlock to po³¹czenie jest automatycznie zamykane.
   * @param database
   * @throws UseDBException
   * @see closeAfterUnlock
   */
  public void unlockDatabase(Database database) throws UseDBException {
    synchronized (this) {
      database.unlock();
      if (closeAfterUnlock) {
        closeDatabase(database);
      }
      else {
        closeUnlockDatabases();
      }
    }
  }
  
  public void closeDatabase(Database database) {
    synchronized (this) {
      for (int i=0; i<databaseList.size(); i++) {
        if (databaseList.get(i) == database) {
          database.close();
          databaseList.remove(i);
        }
      }
    }
  }
  
  private void closeUnlockDatabases() {
    if (closeAfterMillis > 0) {
      synchronized (this) {
        int i = 0;
        while (i<databaseList.size()) {
          Database database = databaseList.get(i); 
          if (!database.isLocked() && System.currentTimeMillis() -database.getUnlockMillis() > closeAfterMillis) {
            closeDatabase(database);
          }
          else {
            i++;
          }
        }
      }
    }
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  public boolean isAutoCommit() {
    return autoCommit;
  }

  /**
   * <p>Pozwala ustawiæ maksymaln¹ iloœæ nawi¹zanych po³¹czeñ.
   * @param poolSize
   */
  public void setPoolSize(int poolSize) {
    this.poolSize = poolSize;
  }

  public int getPoolSize() {
    return poolSize;
  }

  public void close() {
    while (databaseList.size() > 0) {
      databaseList.get(0).close();
      databaseList.remove(0);
    }
  }

  public void setCloseAfterUnlock(boolean closeAfterUnlock) {
    this.closeAfterUnlock = closeAfterUnlock;
  }

  public boolean isCloseAfterUnlock() {
    return closeAfterUnlock;
  }

  public void setCloseAfterMillis(long closeAfterMillis) {
    this.closeAfterMillis = closeAfterMillis;
  }

  public long getCloseAfterMillis() {
    return closeAfterMillis;
  }

}

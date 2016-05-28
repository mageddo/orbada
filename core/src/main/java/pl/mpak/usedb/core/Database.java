package pl.mpak.usedb.core;

import static pl.mpak.usedb.core.DatabaseManager.getManager;

import javax.swing.event.EventListenerList;
import java.io.Closeable;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.EventObject;

import org.slf4j.Logger;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.UseDBObject;
import pl.mpak.usedb.UseDBProperties;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.Languages;
import pl.mpak.util.StringUtil;
import pl.mpak.util.files.PatternFileFilter;
import pl.mpak.util.files.WildCard;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.task.TaskPool;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Klasa do zarz¹dzania po³¹czeniem z baz¹ danych poprzez JDBC
 * Pozwala okreœliæ podstawowe parametry po³¹czenia, nawi¹zaæ po³¹czenie i uzyskaæ
 * dostêp do podstawowych obiektów tego po³¹czenia
 *
 */
public class Database extends UseDBObject implements Closeable {
  private static final long serialVersionUID = 3148856365067175991L;
  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Database.class);
  private static Languages language = new Languages(Database.class);

  public final static String sqlNameCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz$#_";
  
  public final static String useDbParamStatementMaxRows = "usedb.statement-max-rows";
  public final static String useDbParamStatementFetchSize = "usedb.statement-fetch-size";
  public final static String useDbParamFetchRecordCount = "usedb.fetch-record-count";
  public final static String useDbParamCacheRecordCount = "usedb.cache-record-count";
  public final static String useDbCallPrepareStatement = "usedb.call-prepare-statement";
  public final static String useDbAutoCommandQuotedName = "usedb.auto-command-quoted-name";
  public final static String useDbEscapeProcessing = "usedb.escape-processing";
  public final static String[] useDbParameters = {
    useDbParamStatementMaxRows,
    useDbParamStatementFetchSize,
    useDbParamFetchRecordCount,
    useDbParamCacheRecordCount,
    useDbCallPrepareStatement,
    useDbAutoCommandQuotedName,
    useDbEscapeProcessing
  };

  private String uniqueID = (new UniqueID()).toString();

  private int fetchRecordCount = UseDBProperties.getFetchRecordCount();
  private int cacheRecordCount = UseDBProperties.getCacheRecordCount();
  private String tempDirectory;
  
  private transient Driver driver;
  private String driverType;
  private String driverClassName;
  private String publicName;

  private String serverName;
  private String url;
  private String userName;
  private String password;
  private transient Connection connection;
  private java.util.Properties properties;
  private java.util.Properties userProperties;
  private long connectedTime = 0;
  private boolean autoCommit = true;
  private boolean autoConnect = true;
  private boolean autoDisconnect = false;
  private boolean transactionStarted = false;
  private String quoter;
  private String extraNameCharacters;
  private boolean quotedNames = true;
  private String sqlKeywords[];
  private String catalogSeparator;
  private StringUtil.CharCase nameCharCase = StringUtil.CharCase.ecUpperCase;
  
  private boolean locked = false;
  private long unlockMillis; 
  
  private transient DatabaseMetaData metaData;
  
  // statistics range package
  int statConnectionMades;
  int statCreatedCommands; // included query
  int statCreatedQueries;
  
  public enum Event {
    BEFORE_CONNECT,
    BEFORE_DISCONNECT,
    AFTER_CONNECT,
    AFTER_DISCONNECT
  }
  public enum QueryEvent {
    QUERY_ADDED,
    QUERY_REMOVED
  }
  public enum CommandEvent {
    COMMAND_ADDED,
    COMMAND_REMOVED
  }
  private final EventListenerList databaseListenerList = new EventListenerList();
  private final EventListenerList commandListenerList = new EventListenerList();
  private final EventListenerList executableListenerList = new EventListenerList();
  
  private final ArrayList<Query> queryList = new ArrayList<Query>();
  private final ArrayList<Command> commandList = new ArrayList<Command>();

  public Database() {
    super();
    properties = new java.util.Properties();
    userProperties = new java.util.Properties();
    setTempDirectory(UseDBProperties.getTempDirectory());
  }

  protected void finalize() throws Throwable {
    try {
      close();
    }
    finally {
      super.finalize();
    }
  }

  public void addDatabaseListener(DatabaseListener listener) {
    synchronized (databaseListenerList) {
      databaseListenerList.add(DatabaseListener.class, listener);
    }
  }
  
  public void removeDatabaseListener(DatabaseListener listener) {
    synchronized (databaseListenerList) {
      databaseListenerList.remove(DatabaseListener.class, listener);
    }
  }
  
  public void fireDatabaseListener(Event event) {
    synchronized (databaseListenerList) {
      DatabaseListener[] listeners = databaseListenerList.getListeners(DatabaseListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case BEFORE_CONNECT: 
            listeners[i].beforeConnect(new EventObject(this));
            break;
          case AFTER_CONNECT: 
            listeners[i].afterConnect(new EventObject(this));
            break;
          case BEFORE_DISCONNECT: 
            listeners[i].beforeDisconnect(new EventObject(this));
            break;
          case AFTER_DISCONNECT: 
            listeners[i].afterDisconnect(new EventObject(this));
            break;
        }
      }
    }  
  }
  
  public void addDatabaseQueryListener(DatabaseQueryListener listener) {
    synchronized (databaseListenerList) {
      databaseListenerList.add(DatabaseQueryListener.class, listener);
    }
  }
  
  public void removeDatabaseQueryListener(DatabaseQueryListener listener) {
    synchronized (databaseListenerList) {
      databaseListenerList.remove(DatabaseQueryListener.class, listener);
    }
  }
  
  public void fireDatabaseQueryListener(QueryEvent event, Query query) {
    synchronized (databaseListenerList) {
      DatabaseQueryListener[] listeners = databaseListenerList.getListeners(DatabaseQueryListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case QUERY_ADDED: 
            listeners[i].queryAdded(new DatabaseQueryEvent(this, query));
            break;
          case QUERY_REMOVED: 
            listeners[i].queryRemoved(new DatabaseQueryEvent(this, query));
            break;
        }
      }
    }  
  }
  
  public void addDatabaseCommandListener(DatabaseCommandListener listener) {
    synchronized (databaseListenerList) {
      databaseListenerList.add(DatabaseCommandListener.class, listener);
    }
  }
  
  public void removeDatabaseCommandListener(DatabaseCommandListener listener) {
    synchronized (databaseListenerList) {
      databaseListenerList.remove(DatabaseCommandListener.class, listener);
    }
  }
  
  public void fireDatabaseCommandListener(CommandEvent event, Command query) {
    synchronized (databaseListenerList) {
      DatabaseCommandListener[] listeners = databaseListenerList.getListeners(DatabaseCommandListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case COMMAND_ADDED: 
            listeners[i].commandAdded(new DatabaseCommandEvent(this, query));
            break;
          case COMMAND_REMOVED: 
            listeners[i].commandRemoved(new DatabaseCommandEvent(this, query));
            break;
        }
      }
    }  
  }
  
  public void addCommandListener(CommandListener listener) {
    synchronized (commandListenerList) {
      commandListenerList.add(CommandListener.class, listener);
    }
  }
  
  public void removeCommandListener(CommandListener listener) {
    synchronized (commandListenerList) {
      commandListenerList.remove(CommandListener.class, listener);
    }
  }
  
  public void fireCommandListener(Command.Event event, Command command) {
    synchronized (commandListenerList) {
      CommandListener[] listeners = commandListenerList.getListeners(CommandListener.class);
      EventObject eo = new EventObject(command);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case BEFORE_EXECUTE:
            listeners[i].beforeExecute(eo);
            break;
          case AFTER_EXECUTE:
            listeners[i].afterExecute(eo);
            break;
          case ERROR:
            listeners[i].errorPerformed(eo);
            break;
        }
      }
    }
  }
  
  /**
   * <p>Listenery zwracaj¹ce informacjê o tym czy polecenie Command/Query mo¿na wykonaæ czy nie 
   * @param listener
   */
  public void addExecutableListener(ExecutableListener listener) {
    synchronized (executableListenerList) {
      executableListenerList.add(ExecutableListener.class, listener);
    }
  }
  
  /**
   * <p>Listenery zwracaj¹ce informacjê o tym czy polecenie Command/Query mo¿na wykonaæ czy nie 
   * @param listener
   */
  public void removeExecutableListener(ExecutableListener listener) {
    synchronized (executableListenerList) {
      executableListenerList.remove(ExecutableListener.class, listener);
    }
  }
  
  public boolean fireExecutableListener(ParametrizedCommand command) throws SQLException  {
    synchronized (executableListenerList) {
      ExecutableListener[] listeners = executableListenerList.getListeners(ExecutableListener.class);
      for (int i=0; i<listeners.length; i++) {
        if (!listeners[i].canExecute(new EventObject(command))) {
          return false;
        }
      }
    }  
    return true;
  }
  
  /**
   * <p>Pozwala ustawiæ klasê sterownika bazy danych.
   * <p>Podanie prwid³owej nazwy klasy spowoduje za³adowanie i utworzenie
   * odpowiedniego obiektu klasy Driver, który nastêpnie mo¿e byæ pobrany
   * przy pomocy getDriver
   * @param driverClassName
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @throws ClassNotFoundException 
   * @see #getDriver
   */
  public void setDriverClassName(String driverClassName) 
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (this.driverClassName == null || !this.driverClassName.equals(driverClassName)) {
      this.driverClassName = driverClassName;
      if (!"".equals(this.driverClassName)) {
        driver = (Driver)Class.forName(driverClassName).newInstance();
      }
      else {
        driver = null;
      }
    }
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriver(Driver driver) {
    if (this.driver != driver) {
      this.driver = driver;
      if (this.driver != null) {
        driverClassName = this.driver.getClass().getName();
      }
      else {
        driverClassName = null;
      }
    }
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public Driver getDriver() {
    return driver;
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

  /**
   * <p>Dodatkowe parametry po³¹czenia
   * @return
   */
  public java.util.Properties getProperties() {
    return properties;
  }
  
  /**
   * <p>Parametry dla u¿ytkowników, programistów, do zastosowañ wewnêtrznych.
   * @return
   */
  public java.util.Properties getUserProperties() {
    return userProperties;
  }
  
  protected void updateConnectionInfo() throws SQLException {
    quotedNames = StringUtil.toBoolean(getUserProperties().getProperty(Database.useDbAutoCommandQuotedName, "true")); //$NON-NLS-1$
    if (isConnected()) {
      connectedTime = System.currentTimeMillis();
      metaData = connection.getMetaData();
      
      try {
        String productName = getMetaData().getDatabaseProductName();
        if (productName != null) {
          if ("PostgreSQL".equalsIgnoreCase(productName)) {
            nameCharCase = StringUtil.CharCase.ecLowerCase;
          }
          else if ("SQLite".equalsIgnoreCase(productName)) {
            nameCharCase = StringUtil.CharCase.ecNormal;
          }
          else if (productName.toLowerCase().startsWith("firebird")) {
            nameCharCase = StringUtil.CharCase.ecNormal;
          }
        }
      } catch (SQLException e) {
        ;
      }
      
      catalogSeparator = ".";
      try {
        catalogSeparator = getMetaData().getCatalogSeparator();
      } catch (SQLException e) {
        ;
      }
      finally {
        catalogSeparator = StringUtil.evl(catalogSeparator, ".");
      }
            
      quoter = null;
      try {
        quoter = getMetaData().getIdentifierQuoteString();
      } catch (SQLException e) {
        ;
      }
      finally {
        quoter = StringUtil.evl(quoter, "\"");
        quoter = quoter.trim();
      }
      
      extraNameCharacters = null;
      try {
        extraNameCharacters = getMetaData().getExtraNameCharacters();
      } catch (SQLException e) {
        ;
      }
      finally {
        extraNameCharacters = StringUtil.evl(extraNameCharacters, "$");
      }
      String keywords = null;
      try {
        keywords = getMetaData().getSQLKeywords();
      } catch (SQLException e) {
        ;
      }
      finally {
        keywords = StringUtil.evl(keywords, "select,from,where,order,by,group,having,join,and,or,delete,insert,update,values,create,drop,grant,union,all,exists,in,outer,case,when,then");
      }
      sqlKeywords = StringUtil.tokenizeList(keywords, ",");
    }
    else {
      connectedTime = 0;
      metaData = null;
      quoter = null;
      extraNameCharacters = null;
    }
  }
  
  /**
   * <p>Pozwala nazwi¹zaæ oddzielne po³¹czenie nie zwi¹zane z jakimœ konkretnym
   * "Database".
   * @param url
   * @return
   * @throws SQLException
   */
  public static Connection getConnection(String url) throws SQLException {
    return DriverManager.getConnection(url);
  }

  /**
   * <p>Pozwala nazwi¹zaæ oddzielne po³¹czenie nie zwi¹zane z jakimœ konkretnym
   * "Database".
   * @param url
   * @param user
   * @param password
   * @return
   * @throws SQLException
   */
  public static Connection getConnection(String url, String user, String password) throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  /**
   * <p>Pozwala nazwi¹zaæ oddzielne po³¹czenie nie zwi¹zane z jakimœ konkretnym
   * "Database".
   * @param url
   * @param properties
   * @return
   * @throws SQLException
   */
  public static Connection getConnection(String url, java.util.Properties properties) throws SQLException {
    return DriverManager.getConnection(url, properties);
  }

  private Connection getDriverConnection(String url, String user, String password) throws SQLException {
    
    if (!StringUtil.isEmpty(userName)) {
      properties.put("user", userName);
    }
    if (!StringUtil.isEmpty(password)) {
      properties.put("password", password);
    }

    if (driver != null) {
      return driver.connect(url, properties);
    }
    else {
      return getConnection(url, properties);
    }
  }

  /**
   * Jesi wszystkie parametry sa okreslone prawidlowo to zostanie nawiazane 
   * polaczenie z okreslona baza danych
   * @throws SQLException
   */
  public void connect() throws SQLException {
    if (!isConnected()) {
      fireDatabaseListener(Event.BEFORE_CONNECT);
      connection = getDriverConnection(url, userName, password);
      try {
        if (connection.getMetaData().supportsTransactions()) {
          connection.setAutoCommit(autoCommit);
        }
      }
      catch (SQLException e) {
        ;
      }
      try {
        try {
          connection.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        }
        catch (SQLException e) {
          connection.setHoldability(connection.getMetaData().getResultSetHoldability());
        }
      }
      catch (java.lang.AbstractMethodError e) {
        ;
      }
      catch (java.lang.UnsupportedOperationException e) {
        ;
      }
      updateConnectionInfo();
      getManager().addDatabase(this);
      try {
        String frc = getUserProperties().getProperty(useDbParamFetchRecordCount);
        if (frc != null) {
          fetchRecordCount = Integer.parseInt(frc);
        }
        String crc = getUserProperties().getProperty(useDbParamCacheRecordCount);
        if (crc != null) {
          cacheRecordCount = Integer.parseInt(crc);
        }
      }
      catch (Throwable ex) {
        ExceptionUtil.processException(ex);
      }
      statConnectionMades++;
      fireDatabaseListener(Event.AFTER_CONNECT);
    }
  }

  /**
   * <p>Zamyka i usuwa z listy query wszystki otwarte zapytania
   */
  public void closeQueries() {
    synchronized (queryList) {
      int count = queryList.size();
      while (queryList.size() > 0) {
        queryList.get(0).close();
        count--;
        if (count == 0) {
          break;
        }
      }
    }
  }
  
  /**
   * Zamyka wszystkie otwarte Query i
   * rozlacza z baza danych
   * @throws SQLException
   */
  public void disconnect() throws SQLException {
    if (isConnected()) {
      fireDatabaseListener(Event.BEFORE_DISCONNECT);
      closeQueries();
      try {
        connection.close();
      }
      catch (java.sql.SQLRecoverableException e) {
        // does not matter for what reason have failed to close
        ;
      }
      connection = null;
      updateConnectionInfo();
      fireDatabaseListener(Event.AFTER_DISCONNECT);
      getManager().removeDatabase(this);
    }
  }
  
  /**
   * Rozlacza z baza danych, a nastepnie ponownie laczy.
   * Rozlaczenie spowoduje zamkniecie wszystkich otwartych kursorow.
   * @throws SQLException
   */
  public void reconnect() throws SQLException {
    disconnect();
    connect();
  }

  /**
   * Ma to samo znaczenie co disconnect()
   * Oprocz tego nie zwraca wyjatku, tlumi go przekazujac do processException()
   */
  public void close() {
    try {
      disconnect();
    }
    catch (SQLException e) {
      ExceptionUtil.processException(e);
    }
  }

  public boolean isConnected() {
    try {

      if(connection == null){
        return false;
      }
      Statement statement = null;
      try{
         statement = this.connection.createStatement();
        statement.execute("SELECT 1");
      }catch(SQLTransientConnectionException e){
        LOGGER.warn("M=isConnected, msg=connection is invalid");
        return false;
      }catch(Exception e){
        LOGGER.debug("test querie failed: {}", e.getMessage());
      }finally {
        if(statement != null) {
          statement.close();
        }
      }
      return !connection.isClosed();
    } catch(SQLException e) {
      LOGGER.debug("M=isConnected, CALLING=#isClosed()", e);
      return false;
    }
  }

  /**
   * Funkcja sluzy do zainicjowania obiektu Database zewnetrznie otrzymanym
   * polaczeniem.
   * @param connection
   * @throws SQLException
   */
  public void setConnection(Connection connection) throws SQLException {
    if (this.connection != connection) {
      this.connection = connection;
      updateConnectionInfo();
      if (this.connection != null) {
        // driver = ???
      }
      else {
        driver = null;
      }
    }
  }

  /**
   * Pozwala uzyskac dostep do obiektu Connection.
   * Jesli obiekt Database nie jest polaczony z baza danych,
   * a jest ustawiony autoConnect na true to automatycznie nawiaze polaczenie. 
   * @return
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {

    final boolean isDisconnected = !isConnected();
    LOGGER.info("M=getConnection, isAutoConnect={}, isDisconnected={}", autoConnect, isDisconnected);
    if (isAutoConnect() && isDisconnected) {
        LOGGER.info("M=getConnection, message=no available connection opening a new");
      connect();
    }else{
      LOGGER.info("M=getConnection, message=using existent connection");
    }
    return connection;
  }
  
  /**
   * Zwraca informacje o rozpoczeciu polaczenia przez obiekt
   * @return
   */
  public long getConnectionTime() {
    return connectedTime;
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException {
    this.autoCommit = autoCommit;
    transactionStarted = !this.autoCommit;
    if (isConnected() && connection.getMetaData().supportsTransactions()) {
      connection.setAutoCommit(this.autoCommit);
    }
  }

  public boolean isAutoCommit() {
    return autoCommit;
  }

  public String getUniqueID() {
    return uniqueID;
  }

  public void setAutoConnect(boolean autoConnect) {
    this.autoConnect = autoConnect;
  }

  public boolean isAutoConnect() {
    return autoConnect;
  }

  public void setAutoDisconnect(boolean autoDisconnect) {
    this.autoDisconnect = autoDisconnect;
  }

  public boolean isAutoDisconnect() {
    return autoDisconnect;
  }

  public void setMetaData(DatabaseMetaData metaData) {
    this.metaData = metaData;
  }

  public DatabaseMetaData getMetaData() {
    return metaData;
  }
  
  public TaskPool getTaskPool() {
    return TaskPool.getTaskPool(Database.class.getName() +"." +uniqueID);
  }

  public void setFetchRecordCount(int fetchRecordCount) {
    this.fetchRecordCount = fetchRecordCount;
  }

  public int getFetchRecordCount() {
    return fetchRecordCount;
  }

  public void setCacheRecordCount(int cacheRecordCount) {
    this.cacheRecordCount = cacheRecordCount;
  }

  public int getCacheRecordCount() {
    return cacheRecordCount;
  }

  void addQuery(Query query) {
    synchronized (queryList) {
      if (queryList.indexOf(query) == -1) {
        queryList.add(query);
        fireDatabaseQueryListener(QueryEvent.QUERY_ADDED, query);
      }
    }
  }
  
  void removeQuery(Query query) {
    synchronized (queryList) {
      queryList.remove(query);
      fireDatabaseQueryListener(QueryEvent.QUERY_REMOVED, query);
    }
  }
  
  public int getQueryCount() {
    synchronized (queryList) {
      return queryList.size();
    }
  }
  
  public Query getQuery(int index) {
    synchronized (queryList) {
      return queryList.get(index);
    }
  }
  
  void addCommand(Command command) {
    synchronized (commandList) {
      if (commandList.indexOf(command) == -1) {
        commandList.add(command);
        fireDatabaseCommandListener(CommandEvent.COMMAND_ADDED, command);
      }
    }
  }
  
  void removeCommand(Command command) {
    synchronized (commandList) {
      commandList.remove(command);
      fireDatabaseCommandListener(CommandEvent.COMMAND_REMOVED, command);
    }
  }
  
  public int getCommandCount() {
    synchronized (commandList) {
      return commandList.size();
    }
  }
  
  public Command getCommand(int index) {
    synchronized (commandList) {
      return commandList.get(index);
    }
  }
  
  public Command createCommand() {
    return new Command(this);
  }
  
  public Command createCommand(String sqlText, boolean execute) throws Exception {
    Command command = new Command(this);
    if (execute) {
      command.setParamCheck(false);
    }
    command.setSqlText(sqlText);
    if (execute) {
      command.execute();
    }
    return command;
  }
  
  public Query createQuery() {
    return new Query(this);
  }

  /**
   * <p>Tworzy Query i otwiera zapytanie
   * @param sqlText
   * @return
   * @throws Exception
   */
  public Query createQuery(String sqlText) throws Exception {
    return new Query(this, sqlText, true);
  }

  public Query createQuery(String sqlText, boolean open) throws Exception {
    return new Query(this, sqlText, open);
  }
  
  /**
   * <p>Pozwala wykonaæ pojedyñcze polecenie SQL
   * @param sqlText
   * @throws Exception
   */
  public void executeCommand(String sqlText) throws Exception {
    createCommand(sqlText, true);
  }
  
  public void executeScript(String sqlText) throws UseDBException {
    SimpleSQLScript script = new SimpleSQLScript(this);
    if (!script.executeScript(sqlText)) {
      throw new UseDBException(script.getErrors());
    }
  }

  /**
   * Ustawia tymczasowy katalog biblioteki UseDB
   * Jeœli nie ma katalogu - jest tworzony
   * Jeœli katalog istnieje - wszystkie pliki "pl.mpak.usedb.*.temp" zostan¹ usuniête 
   * @param tempDirectory
   */
  public boolean setTempDirectory(String tempDirectory) {
    if (!StringUtil.equals(this.tempDirectory, tempDirectory)) {
      this.tempDirectory = tempDirectory;
      if (!StringUtil.equals(this.tempDirectory, "")) {
        File dir = new File(this.tempDirectory);
        if (!dir.exists()) {
          return dir.mkdirs();
        }
        else {
          boolean result = true;
          dir = new File(this.tempDirectory);
          File files[] = dir.listFiles(new PatternFileFilter(WildCard.getRegex("query.*.temp")));
          for (int i=0; i<files.length; i++) {
            result = result && files[i].delete();
          }
          return result;
        }
      }
    }
    return true;
  }

  /**
   * Zwraca tymczasowy katalog biblioteki UseDB
   * Nazwa katalogu pobrana jest z pliku ".properties", w³aœciwoœæ temp_directory
   * @return
   */
  public String getTempDirectory() {
    return tempDirectory;
  }
  
  void lock() throws UseDBException {
    synchronized (this) {
      if (locked) {
        throw new UseDBException(language.getString("database_locked", new Object[] {url}));
      }
      locked = true;
      unlockMillis = System.currentTimeMillis();
    }
  }

  void unlock() throws UseDBException {
    synchronized (this) {
      if (!locked) {
        throw new UseDBException(language.getString("database_unlocked", new Object[] {url}));
      }
      locked = false;
      unlockMillis = System.currentTimeMillis();
    }
  }
  
  public boolean isLocked() {
    synchronized (this) {
      return locked;
    }
  }
  
  public long getUnlockMillis() {
    return unlockMillis;
  }

  public String getDriverType() {
    return driverType;
  }

  /**
   * <p>Pozwala okreœliæ typ sterownika, np. Oracle, HSQLDB, DerbyDB, jest to opis
   * organizacyjny.
   * @param driverType
   * @see DatabaseManager
   */
  public void setDriverType(String driverType) {
    this.driverType = driverType;
  }

  public String getPublicName() {
    return publicName;
  }

  /**
   * <p>Pozwala okreœliæ nazwê publiczn¹, widoczn¹ dla u¿ytkownika.
   * @param publicName
   */
  public void setPublicName(String publicName) {
    this.publicName = publicName;
  }
  
  public boolean startTransactionAvailable() throws SQLException {
    return isAutoCommit() && connection.getMetaData().supportsTransactions();
  }
  
  public boolean isStartTransaction() {
    return transactionStarted;
  }
  
  public void startTransaction() throws SQLException {
    if (!transactionStarted && isAutoCommit() && connection.getMetaData().supportsTransactions()) {
      connection.setAutoCommit(false);
      transactionStarted = true;
    }
  }
  
  public void commit() throws SQLException {
    connection.commit();
    if (transactionStarted && isAutoCommit() && connection.getMetaData().supportsTransactions()) {
      connection.setAutoCommit(true);
      transactionStarted = false;
    }
  }
  
  public void rollback() throws SQLException {
    connection.rollback();
    if (transactionStarted && isAutoCommit() && connection.getMetaData().supportsTransactions()) {
      connection.setAutoCommit(true);
      transactionStarted = false;
    }
  }
  
  public String toString() {
    if (publicName == null) {
      return userName +" (" +url +")";
    }
    return publicName +" (" +url +")";
  }
  
  public Object getCommandListLocker() {
    return commandList;
  }

  public Object getQueryListLocker() {
    return queryList;
  }
  
  public String[] getSchemaArray() throws Exception {
    if (!isConnected()) {
      return null;
    }
    Query query = createQuery();
    try {
      query.setResultSet(getMetaData().getSchemas());
      return QueryUtil.queryToArray("{table_schem}", query);
    }
    finally {
      query.close();
    }
  }
  
  /**
   * <p>Zwraca informacjê o tym czy w tej chwili wykonywane jest jakiekolwiek polecenie SQL
   * @return
   */
  public boolean isExecutingSql() {
    synchronized (queryList) {
      for (Query query : queryList) {
        if (query.getState() == Query.State.OPENING) {
          return true;
        }
      }
    }
    synchronized (commandList) {
      for (Command command : commandList) {
        if (command.getState() == Command.State.EXECUTEING) {
          return true;
        }
      }
    }
    return false;
  }
  
  public String getQuoter() {
    return quoter;
  }

  /**
   * Zmiena wielkoœc liter zgodnie z baz¹ danych i jeœli jest taka koniecznoœc dodaje znak ograniczenia nazwy
   * najczêœciej cudzys³ów<br>
   * U¿ywana powinna byc w przypadku nazw podawanych przez u¿ytkownika
   * @param name
   * @return
   */
  public String normalizeName(String name) {
    if (name == null) {
      return null;
    }
    if (name.startsWith(quoter) && name.endsWith(quoter)) {
      return name;
    }
    if (nameCharCase == StringUtil.CharCase.ecUpperCase) {
      name = name.toUpperCase();
    }
    else if (nameCharCase == StringUtil.CharCase.ecLowerCase) {
      name = name.toLowerCase();
    }
    return quoteName(name);
  }
  
  /**
   * Dodaje znak ograniczenia nzwy jeœli jest potrzebna<br>
   * U¿ywana powinna byc w przypadku gdy nazwa pobierana jest z bazy danych 
   * @param name
   * @return
   */
  public boolean needQuote(String name) {
    if (name != null && name.startsWith(quoter) && name.endsWith(quoter)) {
      return false;
    }
    for (int i=0; i<name.length(); i++) {
      if (!((Character.isUnicodeIdentifierStart(name.codePointAt(i)) && i == 0) ||
            (Character.isUnicodeIdentifierPart(name.codePointAt(i)) && i > 0)) &&
            name.charAt(i) != '_' &&
          extraNameCharacters.indexOf(name.codePointAt(i)) == -1) {
        return true;
      }
    }
    if (StringUtil.anyOfString(name, sqlKeywords, true) != -1) {
      return true;
    }
    if (nameCharCase == StringUtil.CharCase.ecUpperCase) {
      if (!name.equals(name.toUpperCase())) {
        return true;
      }
    }
    else if (nameCharCase == StringUtil.CharCase.ecLowerCase) {
      if (!name.equals(name.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @param name
   * @return
   */
  public String quoteName(String name) {
    if (name == null) {
      return null;
    }
    if (!quotedNames) {
      return name;
    }
    if (!needQuote(name)) {
      return name;
    }
    return quoter +name +quoter;
  }
  
  public String quoteName(String schemaName, String name) {
    String result = "";
    if (!StringUtil.isEmpty(schemaName)) {
      result = result +quoteName(schemaName) +catalogSeparator;
    }
    return result +quoteName(name);
  }

  public String quoteName(String catalogName, String schemaName, String name) {
    String result = "";
    if (!StringUtil.isEmpty(catalogName)) {
      result = result +quoteName(catalogName) +catalogSeparator;
    }
    if (!StringUtil.isEmpty(schemaName)) {
      result = result +quoteName(schemaName) +catalogSeparator;
    }
    return result +quoteName(name);
  }

  public String quoteName(String catalogName, String schemaName, String tableName, String name) {
    String result = "";
    if (!StringUtil.isEmpty(catalogName)) {
      result = result +quoteName(catalogName) +catalogSeparator;
    }
    if (!StringUtil.isEmpty(schemaName)) {
      result = result +quoteName(schemaName) +catalogSeparator;
    }
    if (!StringUtil.isEmpty(tableName)) {
      result = result +quoteName(tableName) +catalogSeparator;
    }
    return result +quoteName(name);
  }

}

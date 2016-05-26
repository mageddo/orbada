package pl.mpak.usedb.core;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import pl.mpak.sky.SkyException;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.gui.swing.types.FirebirdDBKeyType;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.usedb.util.SerialStruct;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.Languages;
import pl.mpak.util.Order;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPool;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

/**
 * @author Andrzej Ka³u¿a
 * 
 * 
 * <p>
 * Sposób postêpowania z Query.next() jest taki
 * 
 * <pre>
 * while (!query.eof()) {
 *   query.next();
 * }
 * </pre>
 * 
 * <p>
 * Zaraz po otwarciu Query wywo³ywany jest first() Dla cacheData = false -
 * first() oznacza wywo³anie resultSet.next() wiêc nie ma potrzeby wywo³ywania
 * tej metody samodzielnie za pierwszym razem.
 * <p>
 * <b>Kiedy ju¿ Query nie bêdzie potrzebne nale¿y go zamkn¹æ inaczej GC go nie
 * zwolni bo jest na liœcie otwartych kursorów obiektu Database.</b>
 */
public class Query extends ParametrizedCommand implements Closeable, Cloneable {
  private static final long serialVersionUID = 6003429921261803478L;

  private static Languages language = new Languages(Query.class);
  public final String uniqueID = (new UniqueID()).toString();

  /**
   * FlushMode dzia³a tylko gdy ustawiony jest cacheData na true
   */
  public enum FlushMode {
    /**
     * Nie pobieraæ automatycznie wszystkich danych
     */
    fmNone,
    /**
     * Pobieranie danych odbêdzie siê synchronicznie, powrót z funkcji open()
     * lub flushAll() dopiero po zakoñczeniu przenoszenia danych do lokalnego
     * bufora
     */
    fmSynch,
    /**
     * Pobieranie danych odbêdzie siê asynchronicznie, powrót z funkcji open()
     * lub flushAll() zaraz po jej wywo³aniu, getFlushing() pozwala sprawdziæ
     * czy buforowanie jest w toku
     */
    fmAsynch
  };

  private FlushMode flushMode = FlushMode.fmNone;

  /**
   * statusy obiektu Query
   */
  public enum State {
    NONE, OPENING, OPENED, CHECKING, CHECKED, CLOSING, CLOSE
  };

  private volatile State state = State.NONE;

  public enum Event {
    BEFORE_OPEN, AFTER_OPEN, BEFORE_CLOSE, AFTER_CLOSE, BEFORE_SCROLL, AFTER_SCROLL, FLUSHED, ERROR
  }

  private transient Statement statement = null;
  private transient ResultSet resultSet = null;
  private File cacheFile = null;
  private transient RandomAccessFile cacheRAF = null;
  private ArrayList<Long> recordFilePos = null;
  private ArrayList<CacheRecord> cacheRecords = null;
  private int currentRecordIndex = -1;
  private int currentCacheRecord = -1;
  private boolean hasNext = true;
  private boolean afterLast = true;
  private boolean empty = true;
  private long openingTime = 0;
  private long openTime = 0;
  private final EventListenerList queryListenerList = new EventListenerList();
  private final EventListenerList updateListenerList = new EventListenerList();
  private transient QueryFieldList fields = null;
  private boolean cacheData = false;
  private boolean flushing = false;
  private boolean flushed = false;
  private int disableScrollsFlag = 0;
  private boolean closeResultAfterOpen = false;
  private boolean cacheFileDeleteOnExit = false;
  private String orygPreparedSqlTextOrderByAction;
  private int resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
  private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
  // after open, if sql query have RDB$DB_KEY then below true
  private boolean hasFirebirdDBKeyField = false;
  private boolean isClosedExists = true;

  public Query() {
    super();
    fields = new QueryFieldList(this);
  }

  public Query(Database database) {
    super(database);
    fields = new QueryFieldList(this);
  }

  public Query(String sqlText, boolean open) throws Exception {
    this();
    setSqlText(sqlText);
    if (open) {
      open();
    }
  }

  public Query(Database database, String sqlText, boolean open) throws Exception {
    this(database);
    setSqlText(sqlText);
    if (open) {
      open();
    }
  }

  protected void finalize() throws Throwable {
    try {
      close();
    }
    finally {
      super.finalize();
    }
  }
  
  private void checkIsClosedExists() {
    isClosedExists = false;
    if (resultSet != null) {
      Method m = null;
      try {
        m = resultSet.getClass().getMethod("isClosed");
        isClosedExists = m != null;
      } catch (Exception e) {
      }
    }
  }

  public void addQueryListener(QueryListener listener) {
    synchronized (queryListenerList) {
      queryListenerList.add(QueryListener.class, listener);
    }
  }

  public void removeQueryListener(QueryListener listener) {
    synchronized (queryListenerList) {
      queryListenerList.remove(QueryListener.class, listener);
    }
  }

  public void fireQueryListener(Event event) {
    if (disableScrollsFlag > 0 && (event == Event.BEFORE_SCROLL || event == Event.AFTER_SCROLL)) {
      return;
    }
    synchronized (queryListenerList) {
      EventObject eo = new EventObject(this);
      QueryListener[] listeners = queryListenerList.getListeners(QueryListener.class);
      for (int i = 0; i < listeners.length; i++) {
        switch (event) {
          case BEFORE_OPEN:
            listeners[i].beforeOpen(eo);
            break;
          case AFTER_OPEN:
            listeners[i].afterOpen(eo);
            break;
          case BEFORE_CLOSE:
            listeners[i].beforeClose(eo);
            break;
          case AFTER_CLOSE:
            listeners[i].afterClose(eo);
            break;
          case BEFORE_SCROLL:
            listeners[i].beforeScroll(eo);
            break;
          case AFTER_SCROLL:
            listeners[i].afterScroll(eo);
            break;
          case FLUSHED:
            listeners[i].flushedPerformed(eo);
            break;
          case ERROR:
            listeners[i].errorPerformed(eo);
            break;
        }
      }
    }
  }

  public void addUpdateListener(UpdateListener listener) {
    synchronized (updateListenerList) {
      updateListenerList.add(UpdateListener.class, listener);
    }
  }

  public void removeUpdateListener(UpdateListener listener) {
    synchronized (updateListenerList) {
      updateListenerList.remove(UpdateListener.class, listener);
    }
  }

  public void fireUpdateListener(UpdateListener.Event event) {
    synchronized (updateListenerList) {
      EventObject eo = new EventObject(this);
      UpdateListener[] listeners = updateListenerList.getListeners(UpdateListener.class);
      for (int i = 0; i < listeners.length; i++) {
        switch (event) {
          case BEFORE_APPEND:
            listeners[i].beforeInsert(eo);
            break;
          case AFTER_APPEND:
            listeners[i].afterInsert(eo);
            break;
          case BEFORE_DELETE:
            listeners[i].beforeDelete(eo);
            break;
          case AFTER_DELETE:
            listeners[i].afterDelete(eo);
            break;
          case BEFORE_CANCEL:
            listeners[i].beforeCancel(eo);
            break;
          case AFTER_CANCEL:
            listeners[i].afterCancel(eo);
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * Definiuje w jaki sposób i czy wogóle maj¹ byæ pobierane po otwarciu
   * zapytania dane i wstawiane do bufora.
   * 
   * @param flushMode
   *          Jeœli != FlushMode.fmNone to automatycznie ustawiane jest
   *          cacheData na true
   * @throws UseDBException
   */
  public void setFlushMode(FlushMode flushMode) throws UseDBException {
    if (this.flushMode != flushMode) {
      if (flushMode != FlushMode.fmSynch && closeResultAfterOpen) {
        throw new UseDBException(language.getString("err_db_closeResultAfterOpen", new Object[] {"flushMode"}));
      }
      this.flushMode = flushMode;
      if (this.flushMode != FlushMode.fmNone) {
        setCacheData(true);
      }
    }
  }

  public FlushMode getFlushMode() {
    return flushMode;
  }

  public State getState() {
    return state;
  }

  private final void checkOpened() throws UseDBException {
    if (!isActive()) {
      throw new UseDBException(language.getString("err_db_QueryIsClosed"));
    }
  }

  private void checkClose() throws UseDBException {
    if (isActive()) {
      throw new UseDBException(language.getString("err_db_QueryIsOpened"));
    }
  }

  private void checkDatabase() throws UseDBException {
    if (getDatabase() == null || getDatabase().getMetaData() == null) {
      throw new UseDBException(language.getString("err_db_NoConnection"));
    }
  }

  /**
   * Pozwala otworzyæ zapytanie i pobraæ od razu wszystkie rekordy lub pobraæ je
   * w tle w zale¿noœci od parametru FlushMode
   * 
   * @param sqlText
   * @throws Exception
   */
  public Query open(String sqlText) throws Exception {
    setSqlText(sqlText);
    if (!open()) {
      return null;
    }
    return this;
  }

  public boolean open() throws Exception {
    if (isActive() || state == State.OPENING) {
      return false;
    }
    if (!database.fireExecutableListener(this)) {
      return false;
    }
    hasFirebirdDBKeyField = false;
    flushed = false;
    checkDatabase();
    state = State.OPENING;
    openTime = System.currentTimeMillis();
    try {
      database.addQuery(this);
      synchronized (this) {
        String executingSqlText = preparedSqlText;
        fireQueryListener(Event.BEFORE_OPEN);
        hasNext = true;
        if (paramCheck && parameterList.parameterCount() > 0) {
          executingSqlText = parameterList.replaceParameters(executingSqlText);
          if (database.getMetaData().supportsTransactions()) {
            statement = database.getConnection().prepareStatement(executingSqlText, resultSetType, resultSetConcurrency);
          }
          else {
            statement = database.getConnection().prepareStatement(executingSqlText);
          }
          parameterList.bindParameters((PreparedStatement)statement);
        } else {
          if (database.getMetaData().supportsTransactions()) {
            statement = database.getConnection().createStatement(resultSetType, resultSetConcurrency);
          }
          else {
            statement = database.getConnection().createStatement();
          }
        }
        try {
          String maxRows = database.getUserProperties().getProperty(Database.useDbParamStatementMaxRows);
          if (maxRows != null) {
            statement.setMaxRows(Integer.parseInt(maxRows));
          }
        } catch (Throwable ex) {
        }
        try {
          String fetchSize = database.getUserProperties().getProperty(Database.useDbParamStatementFetchSize);
          if (fetchSize != null) {
            statement.setFetchSize(Integer.parseInt(fetchSize));
          }
        } catch (Throwable ex) {
        }
        openingTime = System.nanoTime();
        try {
          if (statement instanceof PreparedStatement) {
            resultSet = ((PreparedStatement)statement).executeQuery();
            //parameterList.assigOutParameters((CallableStatement)statement);
          } else {
            resultSet = statement.executeQuery(executingSqlText);
          }
          checkIsClosedExists();
          openingTime = System.nanoTime() - openingTime;
        } catch (SQLException e) {
          openingTime = 0;
          throw e;
        }
        fields.createFields(resultSet);
        hasFirebirdDBKeyField = (getSqlText().toLowerCase().indexOf("rdb$db_key") >= 0);
        state = State.OPENED;
        if (cacheData) {
          openCacheBuffer();
          writeFields();
          first();
          if (flushMode != FlushMode.fmNone && !eof()) {
            flushAll();
          }
        } else {
          first();
        }
        fireQueryListener(Event.AFTER_OPEN);
        if (closeResultAfterOpen) {
          closeStatement();
        }
      }
    } catch (Throwable e) {
      // poni¿ej ustrawiany jest ponownie status OPENING gdy¿ b³¹d mug³ wyst¹piæ w trakcie 
      // pobierania pierwszych danych
      state = State.OPENING;
      fireQueryListener(Event.ERROR);
      database.removeQuery(this);
      state = State.NONE;
      close();
      if (e instanceof Exception) {
        throw (Exception)e;
      }
      throw new Exception(e);
    }
    return true;
  }
  
  private boolean isFirebirdDBKeyField(int index) {
    return 
      hasFirebirdDBKeyField &&
      "FIREBIRD".equalsIgnoreCase(getDatabase().getDriverType()) &&
      "DB_KEY".equalsIgnoreCase(getField(index -1).getFieldName());
  }

  public boolean isOpening() {
    return state == State.OPENING;
  }

  /**
   * Sprawdza zapytanie i pozwala uzyskaæ dostêp do meta danych Nie wykonuje
   * zapytania, a jedynie przygotowuje do wykonania. Przygotowanie to nie jest
   * zwi¹zane z otwarciem zapytania.
   * 
   * @throws UseDBException
   * @throws SQLException
   * @throws Exception
   */
  public void check() throws UseDBException, SQLException {
    if (isActive() || state == State.OPENING) {
      return;
    }
    checkDatabase();
    state = State.CHECKING;
    try {
      synchronized (this) {
        hasNext = false;
        statement = database.getConnection().prepareStatement(getSqlText());
        if (((PreparedStatement) statement).getMetaData() != null) {
          fields.createFields(((PreparedStatement) statement).getMetaData());
        } else {
          fields.clear();
        }
        state = State.CHECKED;
      }
    } catch (SQLException e) {
      fireQueryListener(Event.ERROR);
      state = State.NONE;
      throw e;
    }
  }

  /**
   * Zwraca czas otwierania zapytania SQL w nano sekundach
   * 
   * @return
   */
  public long getOpeningTime() {
    return openingTime;
  }

  /**
   * Zwraca datê i godzinê rozpoczêcia otwarcia zapytania
   * 
   * @return
   */
  public long getOpenTime() {
    return openTime;
  }
  
  private void closeStatement() throws SQLException {
    if (statement != null) {
      statement.close();
    } else if (resultSet != null) {
      resultSet.close();
    }
    statement = null;
    resultSet = null;
    checkIsClosedExists();
  }
  
  public void close() {
    if (!isActive()) {
      return;
    }
    if (state == State.OPENING) {
      try {
        cancel();
      } catch (SQLException e) {
        ;
      }
    }
    state = State.CLOSING;
    while (flushing) {
      Thread.yield();
    }
    try {
      synchronized (this) {
        fireQueryListener(Event.BEFORE_CLOSE);
        if (cacheData) {
          closeCacheBuffer();
        }
        fields.clear();
        closeStatement();
        state = State.CLOSE;
        hasNext = false;
        empty = true;
        fireQueryListener(Event.AFTER_CLOSE);
      }
      getDatabase().removeQuery(this);
    } catch (Exception e) {
      fireQueryListener(Event.ERROR);
      getDatabase().removeQuery(this);
      state = State.NONE;
      ExceptionUtil.processException(e);
    }
  }

  public void cancel() throws SQLException {
    if (statement != null && state == State.OPENING) {
      statement.cancel();
    }
  }

  /**
   * Zamyka i otwiera zapytanie
   * 
   * @throws Exception
   */
  public void refresh() throws Exception {
    close();
    open();
  }

  public void doUpdateSQLText() {
    orygPreparedSqlTextOrderByAction = null;
    try {
      close();
    } catch (Exception e) {
      ;
    }
  }

  /**
   * Pozwala uzyskac dostêp do pola o podanej nazwie Wywo³uje wyj¹tek jeœli pole
   * o podanej nazwie nie istnieje Aby sprawdziæ czy podane pole istnieje na
   * liœcie nale¿y siê pos³u¿yæ getFieldList().findFieldByName();
   * 
   * @param name
   * @return
   * @throws UseDBException
   * @throws SkyException
   */
  public QueryField fieldByName(String name) throws UseDBException {
    return fields.fieldByName(name);
  }

  public QueryField findFieldByName(String name) {
    return fields.findFieldByName(name);
  }

  public QueryFieldList getFieldList() {
    return fields;
  }

  public int getFieldCount() {
    return fields.getFieldCount();
  }

  public QueryField getField(int index) {
    return fields.getField(index);
  }
  
  public boolean first() throws UseDBException, VariantException, SQLException, IOException {
    boolean result = firstInternal();
    empty = !result;
    return result;
  }

  private boolean firstInternal() throws UseDBException, VariantException, SQLException, IOException {
    checkOpened();

    fireQueryListener(Event.BEFORE_SCROLL);
    if (cacheData) {
      synchronized (cacheRecords) {
        currentCacheRecord = -1;
        if (currentRecordIndex == -1 && (hasNext = internalNext())) {
          readCacheRecords();
        }
        if (recordFilePos.size() > 0) {
          hasNext = true;
          currentRecordIndex = 0;
          fireQueryListener(Event.AFTER_SCROLL);
          return true;
        } else {
          fireQueryListener(Event.AFTER_SCROLL);
          flushed = true;
          return false;
        }
      }
    } else {
      if (hasNext = internalNext()) {
        fireQueryListener(Event.AFTER_SCROLL);
        return true;
      }
      return false;
    }
  }

  private boolean internalNext() throws SQLException {
    if (!isClosed() && resultSet != null) {
      afterLast = !resultSet.next();
      return !afterLast;
    }
    return false;
  }

  public boolean next() throws UseDBException, VariantException, SQLException, IOException {
    checkOpened();

    fireQueryListener(Event.BEFORE_SCROLL);
    if (cacheData) {
      synchronized (cacheRecords) {
        currentCacheRecord = -1;
        while (currentRecordIndex + 1 >= recordFilePos.size() && !isAfterLast()) {
          readCacheRecords();
        }
        if (currentRecordIndex < recordFilePos.size()) {
          currentRecordIndex++;
          fireQueryListener(Event.AFTER_SCROLL);
          return hasNext = (currentRecordIndex < recordFilePos.size());
        } else {
          fireQueryListener(Event.AFTER_SCROLL);
          return false;
        }
      }
    } else {
      if (hasNext = internalNext()) {
        fireQueryListener(Event.AFTER_SCROLL);
        return true;
      }
      return false;
    }
  }

  public boolean prior() throws SQLException, UseDBException {
    checkOpened();

    fireQueryListener(Event.BEFORE_SCROLL);
    if (cacheData) {
      synchronized (cacheRecords) {
        currentCacheRecord = -1;
        if (currentRecordIndex >= 0) {
          currentRecordIndex--;
        }
        fireQueryListener(Event.AFTER_SCROLL);
        return currentRecordIndex >= 0;
      }
    } else {
      if (!isClosed() && resultSet.previous()) {
        fireQueryListener(Event.AFTER_SCROLL);
        return true;
      }
      return false;
    }
  }

  public boolean last() throws UseDBException, VariantException, SQLException, IOException {
    checkOpened();

    fireQueryListener(Event.BEFORE_SCROLL);
    if (cacheData) {
      synchronized (cacheRecords) {
        currentCacheRecord = -1;
        while (!isClosed() && !resultSet.isAfterLast()) {
          readCacheRecords();
        }
        currentRecordIndex = recordFilePos.size() - 1;
        fireQueryListener(Event.AFTER_SCROLL);
        return currentRecordIndex >= 0;
      }
    } else {
      if (!isClosed() && resultSet.last()) {
        fireQueryListener(Event.AFTER_SCROLL);
        return true;
      }
      return false;
    }
  }

  public boolean eof() throws UseDBException {
    checkOpened();

    return !hasNext;
  }

  public boolean bof() throws SQLException, UseDBException {
    checkOpened();

    if (cacheData) {
      return !(currentRecordIndex >= 0);
    } else {
      return !isClosed() && resultSet.isBeforeFirst();
    }
  }

  public boolean isEmpty() throws SQLException, UseDBException {
    return empty;
  }
  
  private void openCacheBuffer(String fileName) throws FileNotFoundException, IOException, VariantException {
    cacheFile = new File(fileName);
    cacheRAF = new RandomAccessFile(cacheFile, "rw");
    recordFilePos = new ArrayList<Long>(500);
    cacheRecords = new ArrayList<CacheRecord>();
    currentRecordIndex = -1;
    cacheFileDeleteOnExit = false;
  }

  private void openCacheBuffer() throws FileNotFoundException, IOException, VariantException {
    openCacheBuffer(database.getTempDirectory() + "/query." + uniqueID + ".temp");
    cacheFile.deleteOnExit();
    cacheFileDeleteOnExit = true;
  }
  
  private void writeFields() throws VariantException, IOException {
    fields.write(cacheRAF);
  }
  
  private void readFields() throws VariantException, IOException, SQLException {
    fields.read(cacheRAF);
  }

  private void closeCacheBuffer() throws IOException {
    if (cacheFile != null) {
      synchronized (cacheRecords) {
        currentRecordIndex = -1;
        cacheRecords.clear();
        cacheRecords = null;
        recordFilePos.clear();
        recordFilePos = null;
        cacheRAF.close();
        cacheRAF = null;
        if (cacheFileDeleteOnExit) {
          try {
            cacheFile.delete();
          } catch (Exception e) {
            ;
          }
        }
        setCacheFile(null);
      }
    }
  }

  /**
   * <p>
   * Usuwa bierz¹cy rekord z listy rekordów.
   * <p>
   * Musi byæ ustawiona w³aœciwoœæ cacheData inaczej nic siê nie stanie.
   * 
   * @throws UseDBException
   */
  public void delete() throws UseDBException {
    if (!cacheData || recordFilePos.size() == 0) {
      checkOpened();
    }
    if (recordFilePos.size() > 0 && cacheData) {
      try {
        delete(getCurrentRecord());
      } catch (Exception e) {
        fireQueryListener(Event.ERROR);
      }
    }
  }

  /**
   * <p>
   * Usuwa wybrany rekord z listy rekordów w pamiêci.
   * <p>
   * Musi byæ ustawiona w³aœciwoœæ cacheData inaczej nic siê nie stanie.
   * <p>
   * <b>Wykonanie tego polecenia nie zmienia danych w bazie danych.</b>
   * 
   * @param cr
   * @throws UseDBException
   */
  public void delete(CacheRecord cr) throws UseDBException {
    if (!cacheData || recordFilePos.size() == 0) {
      checkOpened();
    }
    synchronized (recordFilePos) {
      if (recordFilePos.size() > 0 && cacheData) {
        synchronized (cacheRecords) {
          int inx = findRecordCache(cr.getIndex());
          if (inx != -1) {
            fireUpdateListener(UpdateListener.Event.BEFORE_DELETE);
            cacheRecords.remove(inx);
            recordFilePos.remove(cr.getIndex());
            for (int i = 0; i < cacheRecords.size(); i++) {
              if (cacheRecords.get(i).getIndex() > cr.getIndex()) {
                cacheRecords.get(i).setIndex(cacheRecords.get(i).getIndex() - 1);
              }
            }
            fireUpdateListener(UpdateListener.Event.AFTER_DELETE);
          }
        }
      }
    }
  }

  /**
   * <p>
   * Dodaje rekord na koñcu listy w pamiêci.
   * <p>
   * Musi byæ ustawiona w³aœciwoœæ cacheData inaczej nic siê nie stanie.
   * <p>
   * <b>Wykonanie tego polecenia nie zmienia danych w bazie danych.</b>
   * 
   * @param cr
   * @throws UseDBException
   */
  public void append(CacheRecord cr) throws UseDBException {
    if (!cacheData || recordFilePos.size() == 0) {
      checkOpened();
    }
    if (cacheData) {
      fireUpdateListener(UpdateListener.Event.BEFORE_APPEND);
      cr.applyUpdates();
      WriteRecord rec = new WriteRecord(cr);
      synchronized (recordFilePos) {
        try {
          recordFilePos.add(Long.valueOf(rec.write(cacheRAF)));
          cr.setIndex(recordFilePos.size() - 1);
          synchronized (cacheRecords) {
            cacheRecords.add(cr);
          }
          cr.updateTime();
        } catch (VariantException e) {
          fireQueryListener(Event.ERROR);
        } catch (IOException e) {
          fireQueryListener(Event.ERROR);
        }
      }
      fireUpdateListener(UpdateListener.Event.AFTER_APPEND);
      fireQueryListener(Event.FLUSHED);
    }
  }

  /**
   * <p>
   * Aktualizuje wybrany rekord w pamiêci.
   * <p>
   * Musi byæ ustawiona w³aœciwoœæ cacheData inaczej nic siê nie stanie.
   * <p>
   * <b>Wykonanie tego polecenia nie zmienia danych w bazie danych.</b>
   * 
   * @param cr
   * @throws UseDBException
   */
  public void update(CacheRecord cr) throws UseDBException {
    if (!cacheData || recordFilePos.size() == 0) {
      checkOpened();
    }
    if (cacheData) {
      fireUpdateListener(UpdateListener.Event.BEFORE_UPDATE);
      cr.applyUpdates();
      WriteRecord rec = new WriteRecord(cr);
      synchronized (recordFilePos) {
        try {
          synchronized (cacheRecords) {
            int inx = findRecordCache(cr.getIndex());
            if (inx != -1) {
              cacheRecords.set(inx, cr);
              cr.updateTime();
            }
          }
          recordFilePos.set(cr.getIndex(), Long.valueOf(rec.write(cacheRAF)));
        } catch (VariantException e) {
          fireQueryListener(Event.ERROR);
        } catch (IOException e) {
          fireQueryListener(Event.ERROR);
        }
      }
      fireUpdateListener(UpdateListener.Event.AFTER_UPDATE);
    }
  }

  /**
   * <p>
   * Anuluje zmiany dokonane na tym rekordzie.
   * <p>
   * <b>Wykonanie tego polecenia nie zmienia danych w bazie danych.</b>
   * 
   * @param cr
   * @throws UseDBException
   */
  public void cancel(CacheRecord cr) throws UseDBException {
    checkOpened();
    if (cacheData) {
      fireUpdateListener(UpdateListener.Event.BEFORE_CANCEL);
      cr.cancelUpdates();
      fireUpdateListener(UpdateListener.Event.AFTER_CANCEL);
    }
  }
  
  private Class<?> classByFieldInternal(int type) {
    switch (type) {
      case Types.LONGVARBINARY:
      case Types.VARBINARY:
      case Types.LONGVARCHAR:
      case Types.BLOB:
      case Types.CLOB:
      case Types.BINARY:
      case Types.NCHAR:
      case Types.CHAR:
      case Types.NVARCHAR:
      case Types.VARCHAR:
      case Types.LONGNVARCHAR:
        return String.class;
      case Types.BIGINT:
        return BigInteger.class;
      case Types.NUMERIC:
      case Types.DECIMAL:
        return BigDecimal.class;
      case Types.BOOLEAN:
        return Boolean.class;
      case Types.DATE:
        return Date.class;
      case Types.TIME:
        return Time.class;
      case Types.TIMESTAMP:
        return Timestamp.class;
      case Types.DOUBLE:
        return Double.class;
      case Types.FLOAT:
      case Types.REAL:
        return Float.class;
      case Types.INTEGER:
        return Integer.class;
      case Types.SMALLINT:
        return Short.class;
      case Types.TINYINT:
        return Byte.class;
      default:
        return String.class;
    }
  }

  /**
   * Zwraca domyœln¹ klasê kolumny wg typu pola z otwartego query
   * 
   * @param index
   * @return
   */
  public Class<?> classByField(int index) {
    try {
      return classByFieldInternal(getMetaData().getColumnType(index));
    } catch (SQLException e) {
      return Object.class;
    } catch (UseDBException e) {
      return Object.class;
    } catch (java.lang.IllegalStateException e) {
      return classByFieldInternal(getField(index).getDataType());
    }
  }
  
  public void variantByType(int type, int index, Variant value) throws UseDBException, SQLException, IOException {
    Object ro = null;
    switch (type) {
      case Types.LONGVARBINARY:
      case Types.BLOB: {
        try {
          InputStream stream = resultSet.getBinaryStream(index);
          if (stream == null) {
            value.clear();
          } else {
            value.setBinary(stream);
          }
        } catch (Exception e) {
          try {
            value.setBinary(resultSet.getBytes(index));
          }
          catch (Throwable ex) {
            try {
              value.setObject(resultSet.getObject(index, resultSet.getStatement().getConnection().getTypeMap()));
            } catch (Throwable ex2) {
              value.setObject(resultSet.getObject(index));
            }
          }
        }
        break;
      }
      case Types.VARBINARY: {
        if (resultSet.getObject(index) == null) {
          value.clear();
        } else {
          value.setBinary(resultSet.getBytes(index));
        }
        break;
      }
      case Types.BINARY: {
        if (resultSet.getObject(index) == null) {
          value.clear();
        } else {
          value.setBinary(resultSet.getBytes(index));
        }
        break;
      }
      case Types.LONGVARCHAR: {
        Reader stream = resultSet.getCharacterStream(index);
        if (stream == null) {
          value.clear();
        } else {
          StringBuilder sb = new StringBuilder();
          int ch;
          while ((ch = stream.read()) != -1) {
            sb.append((char)ch);
          }
          value.setString(sb.toString());
        }
        break;
      }
        // bo tak Oracle dobrze konwertuje znaki
      case Types.CLOB: {
        if (resultSet.getObject(index) == null) {
          value.clear();
        } else {
//          Clob clob = resultSet.getClob(index);
//          value.setString(StreamUtil.stream2String(clob.getAsciiStream()));
          value.setString(resultSet.getString(index));
        }
        break;
      }
      case Types.BIGINT:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof BigInteger) {
          value.setBigInteger((BigInteger)ro);
        } else if (ro instanceof Long) {
          value.setLong((Long)ro);
        } else if (ro instanceof Number) {
          value.setBigInteger(((Number)ro).longValue()); 
        } else {
          value.setBigInteger(resultSet.getLong(index));
        }
        break;
      case Types.NUMERIC:
      case Types.DECIMAL:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof BigDecimal) {
          value.setBigDecimal((BigDecimal)ro);
        } else if (ro instanceof Number) {
          value.setBigDecimal(new BigDecimal(((Number)ro).doubleValue())); 
        } else {
          value.setBigDecimal(resultSet.getBigDecimal(index));
        }
        break;
      case Types.BIT:
      case Types.BOOLEAN:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Boolean) {
          value.setBoolean((Boolean)ro);
        } else if (ro instanceof Number) {
          value.setBoolean(((Number)ro).intValue() != 0);
        } else {
          value.setBoolean(resultSet.getBoolean(index));
        }
        break;
      case Types.NCHAR:
      case Types.CHAR:
        if (isFirebirdDBKeyField(index)) {
          // special action for Firebird database
          // in DB_KEY field CHAR(8) contains 2 int displaing as HEX STRING
          ro = resultSet.getBytes(index);
          if (resultSet.wasNull() || ro == null) {
            value.clear();
          } else {
            value.setObject(new FirebirdDBKeyType((byte[])ro));
          }
        }
        else {
          ro = resultSet.getString(index);
          if (resultSet.wasNull() || ro == null) {
            value.clear();
          } else {
            value.setString((String)ro);
          }
        }
        break;
      case Types.LONGNVARCHAR:
      case Types.NVARCHAR:
      case Types.VARCHAR:
        ro = resultSet.getString(index);
        if (resultSet.wasNull() || ro == null) {
          value.clear();
        } else {
          value.setString((String)ro);
        }
        break;
      case Types.DATE:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else {
          // niektóre jdbc zwracaj¹ Date, a w niej datê i godzinê (oracle)
          try {
            value.setDate(resultSet.getTimestamp(index));
          }
          catch (Exception ex) {
            // obs³uga b³êdu dla tego, ¿e niektóre jdbc (odbc) nie potrafi¹ ponownie pobraæ pola
            if (ro instanceof Date) {
              value.setDate((Date)ro);
            }
            else {
              value.setValue(ro);
            }
          }
        }
        break;
      case Types.TIME:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Time) {
          value.setDate((Time)ro);
        } else {
          value.setDate(resultSet.getTime(index));
        }
        break;
      case -101: // Oracle.TIMESTAMPTZ
      case -102: // Oracle.TIMESTAMPTZ
      case Types.TIMESTAMP:
        ro = resultSet.getTimestamp(index);
        if (resultSet.wasNull() || ro == null) {
          value.clear();
        } else if (ro instanceof Timestamp) {
          value.setTimestamp((Timestamp)ro);
        } else {
          value.setTimestamp(resultSet.getTimestamp(index));
        }
        break;
      case Types.DOUBLE:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Double) {
          value.setDouble((Double)ro);
        } else if (ro instanceof Number) {
          value.setDouble(((Number)ro).doubleValue()); 
        } else {
          value.setDouble(resultSet.getDouble(index));
        }
        break;
      case Types.FLOAT:
      case Types.REAL:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Float) {
          value.setFloat((Float)ro);
        } else if (ro instanceof Number) {
          value.setFloat(((Number)ro).floatValue()); 
        } else {
          value.setFloat(resultSet.getFloat(index));
        }
        break;
      case Types.INTEGER:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Integer) {
          value.setInteger((Integer)ro);
        } else if (ro instanceof Number) {
          value.setInteger(((Number)ro).intValue()); 
        } else {
          value.setInteger(resultSet.getInt(index));
        }
        break;
      case Types.SMALLINT:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Short) {
          value.setShort((Short)ro);
        } else if (ro instanceof Number) {
          value.setShort(((Number)ro).shortValue()); 
        } else {
          value.setShort(resultSet.getShort(index));
        }
        break;
      case Types.TINYINT:
        if ((ro = resultSet.getObject(index)) == null) {
          value.clear();
        } else if (ro instanceof Byte) {
          value.setByte((Byte)ro);
        } else if (ro instanceof Number) {
          value.setByte(((Number)ro).byteValue()); 
        } else {
          value.setByte(resultSet.getByte(index));
        }
        break;
      case Types.JAVA_OBJECT:
      case Types.OTHER:
        try {
          value.setObject(resultSet.getObject(index, resultSet.getStatement().getConnection().getTypeMap()));
        } catch (Throwable ex) {
          value.setObject(resultSet.getObject(index));
        }
        break;
      case Types.ROWID:
        try {
          if (resultSet.getObject(index) == null) {
            value.clear();
          } else {
            value.setBinary(resultSet.getBytes(index));
            //value.setObject(resultSet.getRowId(index));
          }
        } catch (Throwable ex) {
          value.setBinary(resultSet.getBytes(index));
        }
        break;
      case Types.ARRAY:
        try {
          if (resultSet.getObject(index) == null) {
            value.clear();
          } else {
            Object[] array = (Object[])resultSet.getArray(index).getArray(database.getConnection().getTypeMap());
            ArrayList<Object> list = new ArrayList<Object>();
            for (Object o : array) {
              if (o instanceof Struct) {
                o = new SerialStruct((Struct) o, database.getConnection().getTypeMap());
              }
              list.add(o);
            }
            value.setList(list);
          }
        } catch (Throwable ex) {
          value.setBinary(resultSet.getBytes(index));
        }
        break;
      case Types.STRUCT:
        try {
          if (resultSet.getObject(index) == null) {
            value.clear();
          } else {
            value.setObject(new SerialStruct((Struct)resultSet.getObject(index), database.getConnection().getTypeMap()));
          }
        } catch (Throwable ex) {
          value.setBinary(resultSet.getBytes(index));
        }
        break;
      case Types.NULL:
        checkOpened();
        value.clear();
        break;
      default:
        QueryColumnTypeWrapper wrapper = QueryColumnTypeWrapper.wrapperMap.get(resultSet.getMetaData().getColumnClassName(index));
        if (wrapper != null) {
          value.setObject(wrapper.convert(resultSet.getObject(index)));
        }
        else {
          byte[] array = resultSet.getBytes(index);
          if (array == null) {
            value.clear();
          } else {
            value.setBinary(array);
          }
        }
    }
  }

//  public void variantByField(int index, Variant value) throws UseDBException, SQLException, IOException {
//    variantByType(getMetaData().getColumnType(index), index, value);
//  }
//
  private void fillRecord(WriteRecord rec) throws UseDBException, SQLException, IOException {
    int fldc = fields.getFieldCount();
    for (int i = 0; i < fldc; i++) {
      QueryField field = fields.getField(i);
      variantByType(field.getDataType(), field.getIndex(), rec.getField(i));
    }
  }

  /**
   * Jeœli nie ma ¿adnego rekordu ta funkcja nie mo¿e byæ wywo³ana Przed
   * wywo³aniem tej funkcji musi byæ wywo³ana funkcja first()
   * 
   * @throws IOException
   * @throws SQLException
   * @throws VariantException
   * @throws UseDBException
   */
  private void readCacheRecords() throws UseDBException, VariantException, SQLException, IOException {
    int cnt = 0;

    WriteRecord rec = new WriteRecord(fields.getFieldCount());
    synchronized (recordFilePos) {
      while (!isAfterLast() && cnt < getDatabase().getFetchRecordCount()) {
        fillRecord(rec);
        recordFilePos.add(rec.write(cacheRAF));
        internalNext();
        cnt++;
      }
    }

    rec.clear();
    rec = null;

    if (isAfterLast()) {
      flushed = true;
    }
  }

  private int getOldestCacheRecord() {
    int result = -1;
    long min = System.currentTimeMillis() + 1;
    for (int i = cacheRecords.size(); --i >= 0; ) {
      if (cacheRecords.get(i).getTime() < min) {
        min = cacheRecords.get(i).getTime();
        result = i;
      }
    }
    return result;
  }

  private int findRecordCache(int index) {
    for (int i = cacheRecords.size(); --i >= 0; ) {
      if (cacheRecords.get(i).getIndex() == index) {
        return i;
      }
    }
    return -1;
  }

  /**
   * <p>Pozwala pobraæ rekord o numerze index. Jeœli nie ma go w buforze to bufor
   * zostanie uzupe³niony. Funkcji nie nale¿y wywo³ywaæ dla query które nie s¹ buforowane.
   * 
   * @param index
   * @return rekord lub null jeœli nie ma rekordu o podanym indeksie lub jeœli Query zosta³o wczeœniej zamkniête
   * @throws IOException
   * @throws SQLException
   * @throws VariantException
   * @throws UseDBException
   */
  public CacheRecord getRecord(int index) throws UseDBException, VariantException, SQLException, IOException {
    if (cacheRecords != null) {
      synchronized (cacheRecords) {
        boolean flushed = false;
        int inx = findRecordCache(index);
        if (inx != -1) {
          CacheRecord result = cacheRecords.get(inx);
          result.updateTime();
          currentCacheRecord = inx;
          return result;
        } else {
          if (cacheRecords.size() > getDatabase().getCacheRecordCount()) {
            inx = getOldestCacheRecord();
            cacheRecords.remove(inx);
          }
          CacheRecord result = new CacheRecord(this, index);
          if (index > getRecordCount() - 1 && !isAfterLast()) {
            while (index > getRecordCount() - 1 && !isAfterLast()) {
              readCacheRecords();
            }
            flushed = true;
          }
          synchronized (recordFilePos) {
            if (index >= recordFilePos.size()) {
              currentCacheRecord = -1;
              return null;
            }
            // change context for Variant.read and ObjectInputStream.readObject
            // for specyfic, serialized database driver types
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getDatabase().getDriver().getClass().getClassLoader());
            try {
              result.read(cacheRAF, recordFilePos.get(index).longValue());
            }
            finally {
              Thread.currentThread().setContextClassLoader(cl);
            }
          }
          cacheRecords.add(result);
          currentCacheRecord = cacheRecords.size() - 1;
          result.updateTime();
          if (flushed) {
            fireQueryListener(Event.FLUSHED);
          }
          return result;
        }
      }
    }
    return null;
  }

  /**
   * Pozwala pobraæ aktualny rekord
   * 
   * @return
   * @throws IOException
   * @throws SQLException
   * @throws VariantException
   * @throws UseDBException
   */
  public CacheRecord getCurrentRecord() throws UseDBException, VariantException, SQLException, IOException {
    checkOpened();

    synchronized (cacheRecords) {
      if (currentCacheRecord != -1) {
        CacheRecord result = cacheRecords.get(currentCacheRecord);
        result.updateTime();
        return result;
      } else {
        currentCacheRecord = findRecordCache(currentRecordIndex);
        if (currentCacheRecord == -1) {
          return getRecord(currentRecordIndex);
        }
        CacheRecord result = cacheRecords.get(currentCacheRecord);
        result.updateTime();
        return cacheRecords.get(currentCacheRecord);
      }
    }
  }

  private void readAllRecords() throws SQLException, UseDBException, VariantException, IOException {
    flushing = true;
    disableScrolls();
    try {
      if (!isAfterLast()) {
        try {
          while (!isAfterLast() && state != State.CLOSING) {
            readCacheRecords();
          }
          fireQueryListener(Event.FLUSHED);
        } catch (UseDBException e) {
          fireQueryListener(Event.ERROR);
          throw e;
        } catch (VariantException e) {
          fireQueryListener(Event.ERROR);
          throw e;
        } catch (SQLException e) {
          fireQueryListener(Event.ERROR);
          throw e;
        } catch (IOException e) {
          fireQueryListener(Event.ERROR);
          throw e;
        }
      }
    } finally {
      flushing = false;
      enableScrolls();
    }
  }

  /**
   * Pozwala wype³niæ bufor wszystkimi rekordami zapytania Aby wype³nianie
   * zadzia³a³o musi byæ okreœlony FlushMode
   * 
   * @throws SQLException
   * @throws IOException
   * @throws VariantException
   * @throws UseDBException
   */
  public void flushAll() throws SQLException, UseDBException, VariantException, IOException {
    if (flushing || flushed) {
      return;
    }
    if (flushMode == FlushMode.fmSynch || flushMode == FlushMode.fmNone) {
      disableScrolls();
      try {
        readAllRecords();
      }
      finally {
        enableScrolls();
      }
    } else if (flushMode == FlushMode.fmAsynch) {
      getTaskPool().addTask(new Task() {
        public void run() {
          try {
            readAllRecords();
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
      });
    }
  }

  public ResultSetMetaData getMetaData() throws SQLException, UseDBException {
    checkOpened();
    return resultSet.getMetaData();
  }

  public void setDatabase(Database database) {
    if (this.database != database) {
      try {
        close();
      } catch (Exception e) {
        ExceptionUtil.processException(e);
      }
      if (this.database == null && database != null && !databaseSet) {
        database.statCreatedQueries++;
      }
      super.setDatabase(database);
    }
  }

  public final boolean isActive() {
    return state == State.OPENED; 
  }

  public final boolean isClosed() {
    if (isClosedExists) {
      try {
        if (resultSet != null && resultSet.isClosed()) {
          return false;
        }
      } catch (SQLException e) {
        ExceptionUtil.processException(e);
      }
      catch (java.lang.AbstractMethodError e) {
        isClosedExists = false;
      }
    }
    return state == State.CLOSE; 
  }

  public void setActive(boolean value) throws Exception {
    if (value) {
      open();
    } else {
      close();
    }
  }

  public void setCacheData(boolean cacheData) throws UseDBException {
    checkClose();
    if (!cacheData && closeResultAfterOpen) {
      throw new UseDBException(language.getString("err_db_closeResultAfterOpen", new Object[] {"cacheData"}));
    }
    this.cacheData = cacheData;
  }

  public boolean getCacheData() {
    return cacheData;
  }

  public Statement getStatement() {
    return statement;
  }

  public ResultSet getResultSet() {
    return resultSet;
  }

  public void setResultSet(ResultSet result) throws Exception {
    if (state == State.OPENING) {
      return;
    }
    if (isActive()) {
      close();
    }
    setSqlText("");
    flushed = false;
    checkDatabase();
    state = State.OPENING;
    openTime = System.currentTimeMillis();
    try {
      getDatabase().addQuery(this);
      synchronized (this) {
        fireQueryListener(Event.BEFORE_OPEN);
        hasNext = true;
        if (result.getStatement() instanceof PreparedStatement) {
          statement = (PreparedStatement) result.getStatement();
        }
        openingTime = 0;
        resultSet = result;
        checkIsClosedExists();
        fields.createFields(resultSet);
        state = State.OPENED;
        if (cacheData) {
          openCacheBuffer();
          writeFields();
          first();
          if (flushMode != FlushMode.fmNone && !eof()) {
            flushAll();
          }
          empty = recordFilePos.size() == 0;
        } else {
          first();
        }
        fireQueryListener(Event.AFTER_OPEN);
        if (closeResultAfterOpen) {
          closeStatement();
        }
      }
    } catch (Exception e) {
      fireQueryListener(Event.ERROR);
      getDatabase().removeQuery(this);
      state = State.NONE;
      close();
      throw e;
    }
  }
  
  public void openQueryFile(String fileName) throws Exception {
    close();
    setSqlText("");
    cacheData = true;
    flushed = false;
    afterLast = false;
    checkDatabase();
    state = State.OPENING;
    openTime = System.currentTimeMillis();
    try {
      getDatabase().addQuery(this);
      synchronized (this) {
        fireQueryListener(Event.BEFORE_OPEN);
        openingTime = System.nanoTime();
        openCacheBuffer(fileName);
        readFields();
        synchronized (recordFilePos) {
          long pos = cacheRAF.getFilePointer();
          int recSize = cacheRAF.readInt();
          while (pos < cacheRAF.length()) {
            recordFilePos.add(pos);
            pos += recSize +4;
            if (pos >= cacheRAF.length()) {
              break;
            }
            cacheRAF.seek(pos);
            recSize = cacheRAF.readInt();
          };
        }
        flushed = true;
        afterLast = true;
        empty = recordFilePos.size() == 0;
        openingTime = System.nanoTime() - openingTime;
        state = State.OPENED;
        fireQueryListener(Event.AFTER_OPEN);
      }
    } catch (Exception e) {
      fireQueryListener(Event.ERROR);
      getDatabase().removeQuery(this);
      openingTime = 0;
      state = State.NONE;
      close();
      throw e;
    }
  }
  
  public void saveQueryFile(String fileName) throws IOException, UseDBException {
    if (cacheRAF != null) {
      synchronized (recordFilePos) {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        try {
          cacheRAF.seek(0);
          byte[] buffer = new byte[1024 *8];
          int len;
          while ((len = cacheRAF.read(buffer)) > 0) {
            raf.write(buffer, 0, len);
          }
        }
        finally {
          raf.close();
        }
      }
    }
    else {
      throw new UseDBException(language.getString("err_db_NoCache"));
    }
  }

  public int getRecordCount() {
    if (recordFilePos != null) {
      synchronized (recordFilePos) {
        return recordFilePos.size();
      }
    } else {
      return -1;
    }
  }

  public void sortByColumn(int modelIndex, Order order) throws UseDBException {
    // jeœli by³o najpierw sortowanie przez zamianê ORDER BY to sortuj wg tej zasady
    if (orygPreparedSqlTextOrderByAction != null && preparedSqlText != null) {
      orderByColumn(modelIndex, order);
    }
    else {
      checkOpened();
      if (cacheData) {
        synchronized (cacheRecords) {
          cacheRecords.clear();
          QueryComparator comparator = new QueryComparator(modelIndex, order);
          try {
            synchronized (recordFilePos) {
              Collections.sort(recordFilePos, comparator);
            }
          } finally {
            comparator.dispose();
          }
        }
      }
    }
  }
  
  private void refreshOnOrderByAction() throws UseDBException {
    try {
      refresh();
    } catch (Exception e) {
      if (orygPreparedSqlTextOrderByAction != null) {
        preparedSqlText = orygPreparedSqlTextOrderByAction;
        orygPreparedSqlTextOrderByAction = null;
        try {
          refresh();
        } catch (Exception ex) {
        }
      }
      throw new UseDBException(e);
    }
  }

  public void orderByColumn(int modelIndex, Order order) throws UseDBException {
    //System.out.println("P:" +preparedSqlText);
    //System.out.println("O:" +orygPreparedSqlTextOrderByAction);
    if (StringUtil.isEmpty(preparedSqlText)) {
      sortByColumn(modelIndex, order);
    }
    else {
      checkOpened();
      if (order == Order.NONE) {
        if (orygPreparedSqlTextOrderByAction != null) {
          preparedSqlText = orygPreparedSqlTextOrderByAction;
          try {
            refresh();
          } catch (Exception e) {
            throw new UseDBException(e);
          } finally {
            orygPreparedSqlTextOrderByAction = null;
          }
        }
      }
      else {
        int orderByPos = SQLUtil.indexOf(preparedSqlText, "ORDER BY", 0, true);
        int lastOrderByPos = -1;
        while (orderByPos != -1) {
          lastOrderByPos = orderByPos;
          orderByPos = SQLUtil.indexOf(preparedSqlText, "ORDER BY", orderByPos +8, true);
        }
        QueryField field = fields.getField(modelIndex);
        if (lastOrderByPos != -1) {
          if (preparedSqlText.charAt(preparedSqlText.length() -1) != ')') {
            if (orygPreparedSqlTextOrderByAction == null) {
              orygPreparedSqlTextOrderByAction = preparedSqlText;
            }
            preparedSqlText = preparedSqlText.substring(0, lastOrderByPos +8);
            preparedSqlText = preparedSqlText +" " +SQLUtil.createSqlName(field.getFieldName(), database);
            if (order == Order.DESCENDING) {
              preparedSqlText = preparedSqlText +" DESC";
            }
            refreshOnOrderByAction();
          }
        }
        else {
          if (orygPreparedSqlTextOrderByAction == null) {
            orygPreparedSqlTextOrderByAction = preparedSqlText;
          }
          preparedSqlText = preparedSqlText +"\n ORDER BY " +SQLUtil.createSqlName(field.getFieldName(), database);
          if (order == Order.DESCENDING) {
            preparedSqlText = preparedSqlText +" DESC";
          }
          refreshOnOrderByAction();
        }
      }
      //System.out.println(preparedSqlText);
    }
  }
  
  /**
   * <p>Flaga oznaczaj¹ca, ¿e close() i open() jest wymuszone przez akcje ORDER BY
   * <p>Oznacza to, ¿e obiekt QueryTable nie mo¿e wywo³aæ tableHeader.resetOrder()
   * @return
   */
  public boolean isOnOrderByAction() {
    return orygPreparedSqlTextOrderByAction != null;
  }

  private void setCacheFile(File cacheFile) {
    this.cacheFile = cacheFile;
  }

  public File getCacheFile() {
    return cacheFile;
  }

  public boolean isFlushed() {
    return flushed;
  }

  public boolean isAfterLast() {
    return afterLast;
  }

  public TaskPool getTaskPool() {
    return database.getTaskPool();
  }

  /**
   * <p>
   * Pozwala ustawiæ na wyszukanym po keyField rekordzie zgodnie z wartoœci¹.
   * <p>
   * Aby locate dzia³a³ Query.setCacheData() musi byæ ustawione na true
   * 
   * @param keyField
   * @param value
   *          Variant, nie mo¿e byæ null
   * @return zwraca true jeœli (pierwszy) rekord zosta³ znaleziony
   * @throws UseDBException
   * @throws VariantException
   * @throws SQLException
   * @throws IOException
   */
  public boolean locate(String keyField, Variant value) throws UseDBException, VariantException, SQLException, IOException {
    if (cacheData && !isEmpty()) {
      first();
      while (!eof()) {
        if (value.equals(fieldByName(keyField).getValue())) {
          return true;
        }
        next();
      }
    }
    return false;
  }

  /**
   * <p>
   * @param keyField
   * @param value
   * @param caseInsensitive dzia³a jedynie gdy wartoœæ pola jest konwertowalna do ci¹gu znaków String
   * @param partial dzia³a jedynie dla caseInsensitive = false 
   * @return
   * @throws UseDBException
   * @throws VariantException
   * @throws SQLException
   * @throws IOException
   */
  public boolean locate(String keyField, Variant value, boolean caseInsensitive, boolean partial) throws UseDBException,
      VariantException, SQLException, IOException {
    if (cacheData && !isEmpty()) {
      first();
      while (!eof()) {
        if (!fieldByName(keyField).isNull()) {
          if (caseInsensitive) {
            if (partial) {
              if (fieldByName(keyField).getString().toUpperCase().indexOf(value.getString().toUpperCase()) >= 0) {
                return true;
              }
            }
            else if (value.getString().equalsIgnoreCase(fieldByName(keyField).getString())) {
              return true;
            }
          }
          else {
            if (partial) {
              if (fieldByName(keyField).getString().indexOf(value.getString()) >= 0) {
                return true;
              }
            }
            else if (value.equals(fieldByName(keyField).getValue())) {
              return true;
            }
          }
        }
        next();
      }
    }
    return false;
  }
  
  public void disableScrolls() {
    disableScrollsFlag++;
  }
  
  public void enableScrolls() {
    disableScrollsFlag--;
  }
  
  public Object clone() {
    Query result = getDatabase().createQuery();
    try {
      result.setSqlText(getSqlText());
      result.setCacheData(getCacheData());
      result.setFlushMode(getFlushMode());
      result.setParamCheck(getParamCheck());
      for (int i=0; i<getParameterCount(); i++) {
        Parameter param = getParameter(i);
        result.paramByName(param.getParamName()).setValue(param.getValue());
      }
      return result;
    }
    catch (Exception ex) {
      return null;
    }
  }
  
  public boolean isCloseResultAfterOpen() {
    return closeResultAfterOpen;
  }

  /**
   * <p>Ustawienie na true przed otwarciem kursora spowoduje automatyczne zbuforowanie danych
   * i zamkniêcie obiektu Statement czyli kursora.
   * <p>Przydatne przy ustawieniach bazy danych CLOSE_CURSOR_ON_COMMIT.
   * <p>Ustawienie na true automatycznie przestawia cacheData na true oraz flushMode na fmSynch
   * @param closeResultAfterOpen
   * @throws UseDBException 
   */
  public void setCloseResultAfterOpen(boolean closeResultAfterOpen) throws UseDBException {
    this.closeResultAfterOpen = closeResultAfterOpen;
    if (this.closeResultAfterOpen) {
      setFlushMode(FlushMode.fmSynch);
    }
  }

  public int getResultSetConcurrency() {
    return resultSetConcurrency;
  }

  public void setResultSetConcurrency(int resultSetConcurrency) {
    this.resultSetConcurrency = resultSetConcurrency;
  }

  public int getResultSetType() {
    return resultSetType;
  }

  public void setResultSetType(int resultSetType) {
    this.resultSetType = resultSetType;
  }

  private final class QueryComparator implements Comparator<Long> {
    private int column = -1;
    private Order order = Order.NONE;
    private HashMap<Long, Variant> cache = new HashMap<Long, Variant>();
    private CacheRecord cacheRecord = new CacheRecord();

    QueryComparator(int column, Order order) {
      super();
      this.column = column;
      this.order = order;
    }

    public void dispose() {
      cache.clear();
      cache = null;
    }

    public Variant getValue(Long o) throws Exception {
      Variant result = cache.get(o);
      if (result == null) {
        cacheRecord.clear();
        cacheRecord.read(cacheRAF, o);
        result = cacheRecord.getField(column).value;
        cache.put(o, result);
      }
      return result;
    }

    public int compare(Long o1, Long o2) {
      if (order != Order.NONE) {
        try {
          if (order == Order.ASCENDING) {
            return getValue(o1.longValue()).compareTo(getValue(o2.longValue()));
          } else { // DESCENDING
            return getValue(o2.longValue()).compareTo(getValue(o1.longValue()));
          }
        } catch (Exception e) {
          //ExceptionUtil.processException(e);
          return 0;
        }
      } else {
        return o1.compareTo(o2);
      }
    }
  }
  
}

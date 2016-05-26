package pl.mpak.usedb.core;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import pl.mpak.usedb.UseDBException;
import pl.mpak.util.Languages;
import pl.mpak.util.StringUtil;

public class Command extends ParametrizedCommand {
  private static final long serialVersionUID = 2760793526568164758L;
  
  private static Languages language = new Languages(Command.class);

  public enum Event {
    BEFORE_EXECUTE,
    AFTER_EXECUTE,
    ERROR
  }

  /**
   * statusy obiektu Query
   */
  public enum State {
    NONE, EXECUTEING, EXECUTED
  };

  private volatile State state = State.NONE;
  
  private transient Statement statement;
  private int updateCount = -1;
  private long executionTime = 0;
  private long executedTime = 0;
  private final EventListenerList commandListenerList = new EventListenerList();
  private Boolean excapeProcessing = null;

  public Command() {
    super();
  }
  
  public Command(Database database) {
    super(database);
  }
  
  protected void finalize() throws Throwable {
    super.finalize();
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
  
  public void fireCommandListener(Event event) {
    synchronized (commandListenerList) {
      CommandListener[] listeners = commandListenerList.getListeners(CommandListener.class);
      EventObject eo = new EventObject(this);
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
  
  public void execute(String sqlCommand) throws Exception {
    setSqlText(sqlCommand);
    execute();
  }
  
  /**
   * <p>Wykonanie polecenia UPDATE, INSERT, DELETE lub ka¿dego
   * które nie zwraca kursora jak polecenie DDL
   * getUpdateCount() zwraca iloœæ zmienionych wierszy w przypadku
   * wykonania UPDATE, INSERT lub DELETE 
   * 
   * @throws Exception 
   */
  public boolean execute() throws Exception {
    if (database == null) {
      throw new UseDBException(language.getString("no_db_assigned"));
    }
    if (!getDatabase().fireExecutableListener(this)) {
      return false;
    }
    getDatabase().addCommand(this);
    state = State.EXECUTEING;
    executedTime = System.currentTimeMillis();
    try {
      database.fireCommandListener(Event.BEFORE_EXECUTE, this);
      fireCommandListener(Event.BEFORE_EXECUTE);
      String executingSqlText = preparedSqlText;
      if (paramCheck && parameterList.parameterCount() > 0) {
        executingSqlText = parameterList.replaceParameters(executingSqlText);
        if (StringUtil.toBoolean(database.getUserProperties().getProperty(Database.useDbCallPrepareStatement, "false"))) {
          statement = database.getConnection().prepareStatement(executingSqlText);
        }
        else {
          statement = database.getConnection().prepareCall(executingSqlText);
        }
        parameterList.bindParameters((PreparedStatement)statement);
      }
      else {
        statement = database.getConnection().createStatement();
        if (excapeProcessing != null) {
          statement.setEscapeProcessing(excapeProcessing);
        }
        else if (database.getUserProperties().getProperty(Database.useDbEscapeProcessing) != null) {
          statement.setEscapeProcessing(StringUtil.toBoolean(database.getUserProperties().getProperty(Database.useDbEscapeProcessing)));
        }
      }
      executionTime = System.nanoTime();
      boolean executed = false;
      try {
        updateCount = 0;
        if (statement instanceof PreparedStatement) {
          executed = ((PreparedStatement)statement).execute();
          if (statement instanceof CallableStatement) {
            parameterList.assigOutParameters((CallableStatement)statement);
          }
        }
        else {
          executed = statement.execute(executingSqlText);
        }
        if (!executed) {
          updateCount = statement.getUpdateCount();
        }
        executionTime = System.nanoTime() -executionTime;
      }
      catch(Exception e) {
        executionTime = 0;
        throw e;
      }
      database.fireCommandListener(Event.AFTER_EXECUTE, this);
      fireCommandListener(Event.AFTER_EXECUTE);
      if (!executed) {
        statement.close();
        statement = null;
      }
    }
    catch(Throwable e) {
      if (statement != null) {
        statement.close();
        statement = null;
      }
      database.fireCommandListener(Event.ERROR, this);
      fireCommandListener(Event.ERROR);
      if (e instanceof Exception) {
        throw (Exception)e;
      }
      throw new Exception(e);
    }
    finally {
      state = State.EXECUTED;
      getDatabase().removeCommand(this);
    }
    return true;
  }
  
  public void cancel() throws SQLException {
    if (statement != null) {
      statement.cancel();
    }
  }

  /**
   * Zwraca czas wykonania polecenia w nano secundach
   * @return
   */
  public long getExecutionTime() {
    return executionTime;
  }

  /**
   * Zwraca datê i godzinê rozpoczêcia wykonywania polecenia
   * @return
   */
  public long getExecutedTime() {
    return executedTime;
  }

  public int getUpdateCount() {
    return updateCount;
  }

  public Statement getStatement() {
    return statement;
  }

  public State getState() {
    return state;
  }

  public Boolean getExcapeProcessing() {
    return excapeProcessing;
  }

  public void setExcapeProcessing(Boolean excapeProcessing) {
    this.excapeProcessing = excapeProcessing;
  }
  
}

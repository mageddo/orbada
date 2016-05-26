package pl.mpak.usedb.script;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.Assert;
import pl.mpak.util.array.StringList;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskExecutor;

public class SQLScript {

  private Database database;
  private long scriptTime = 0;
  private long errorCount = 0;

  enum ListenerEvent {
    BEFORE_SCRIPT,
    AFTER_SCRIPT,
    BEFORE_COMMAND,
    AFTER_COMMAND,
    BEFORE_QUERY,
    AFTER_QUERY,
    ERROR_OCCURED
  }
  private EventListenerList scriptListenerList = new EventListenerList();
  
  public SQLScript() {
  }

  public SQLScript(Database database) {
    this.database = database;
  }
  
  public void addScriptListener(ScriptListener listener) {
    synchronized (scriptListenerList) {
      scriptListenerList.add(ScriptListener.class, listener);
    }
  }
  
  public void removeScriptListener(ScriptListener listener) {
    synchronized (scriptListenerList) {
      scriptListenerList.remove(ScriptListener.class, listener);
    }
  }
  
  public boolean fireScriptListener(ListenerEvent event, Object object, String sqlText) {
    synchronized (scriptListenerList) {
      ScriptListener[] listeners = scriptListenerList.getListeners(ScriptListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case BEFORE_SCRIPT: 
            listeners[i].beforeScript(new EventObject(object));
            break;
          case AFTER_SCRIPT: 
            listeners[i].afterScript(new EventObject(object));
            break;
          case BEFORE_COMMAND: 
            listeners[i].beforeCommand(new EventObject(object));
            break;
          case AFTER_COMMAND: 
            listeners[i].afterCommand(new EventObject(object));
            break;
          case BEFORE_QUERY: 
            listeners[i].beforeQuery(new EventObject(object));
            break;
          case AFTER_QUERY: 
            listeners[i].afterQuery(new EventObject(object));
            break;
          case ERROR_OCCURED: 
            return listeners[i].errorOccured(new ErrorScriptEventObject(object, sqlText));
        }
      }
      return false;
    }  
  }
  
  private boolean executeCmd(StringList cmd) {
    String sql = cmd.getText();
    if (sql.length() > 0 && sql.charAt(sql.length() -1) == ';') {
      sql = sql.substring(0, sql.length() -1);
    }
    if (SQLUtil.isSelect(sql)) {
      Query query = database.createQuery();
      try {
        query.setParamCheck(false);
        query.setSqlText(sql);
        fireScriptListener(ListenerEvent.BEFORE_QUERY, query, null);
        query.open();
        fireScriptListener(ListenerEvent.AFTER_QUERY, query, null);
      }
      catch(Exception ex) {
        errorCount++;
        if (fireScriptListener(ListenerEvent.ERROR_OCCURED, ex, sql)) {
          cmd.clear();
          return false;
        }
      }
      finally {
        query.close();
      }
    }
    else {
      Command command = database.createCommand();
      try {
        command.setParamCheck(false);
        command.setSqlText(cmd.getText());
        fireScriptListener(ListenerEvent.BEFORE_COMMAND, command, null);
        command.execute();
        fireScriptListener(ListenerEvent.AFTER_COMMAND, command, null);
      }
      catch(Exception ex) {
        errorCount++;
        if (fireScriptListener(ListenerEvent.ERROR_OCCURED, ex, cmd.getText())) {
          cmd.clear();
          return false;
        }
      }
    }
    cmd.clear();
    return true;
  }
  
  /**
   * <p>Wykonywanie skryptu mo¿e zostaæ przerwane jeœli wykonywane jest przez pl.mpak.util.task.Task.setCancel(true)
   * @param reader
   * @throws IOException
   */
  public void execute(BufferedReader reader) throws IOException {
    try {
      Assert.notNull(database);
      Assert.notNull(reader);
      
      errorCount = 0;
      scriptTime = System.nanoTime();
      fireScriptListener(ListenerEvent.BEFORE_SCRIPT, this, null);
      StringList cmd = new StringList();
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 1 && line.trim().charAt(0) == '/') {
          if (!executeCmd(cmd)) {
            break;
          }
        }
        else if (line.length() > 0 && line.charAt(line.length() -1) == ';') {
          cmd.add(line);
          if (SQLUtil.isSelect(cmd.getText()) || SQLUtil.isDml(cmd.getText())) {
            if (!executeCmd(cmd)) {
              break;
            }
          }
        }
        else {
          cmd.add(line);
        }
        if (Thread.currentThread() instanceof TaskExecutor) {
          Task task = ((TaskExecutor)Thread.currentThread()).getCurrentTask();
          if (task != null && task.isCanceled()) {
            break;
          }
        }
      }
      if (cmd.size() > 0) {
        executeCmd(cmd);
      }
      scriptTime = System.nanoTime() - scriptTime;
      fireScriptListener(ListenerEvent.AFTER_SCRIPT, this, null);
    }
    finally {
      reader.close();
    }
  }
  
  public void execute(InputStream inputStream) throws IOException {
    execute(new BufferedReader(new InputStreamReader(inputStream)));
  }
  
  public void execute(String fileName) throws FileNotFoundException, IOException {
    execute(new FileInputStream(fileName));
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  public long getScriptTime() {
    return scriptTime;
  }

  public long getErrorCount() {
    return errorCount;
  }

}

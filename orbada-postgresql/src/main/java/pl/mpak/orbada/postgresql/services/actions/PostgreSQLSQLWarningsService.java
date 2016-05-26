/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.services.actions;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.EventObject;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.CommandListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.TaskUtil;

/**
 *
 * @author akaluza
 */
public class PostgreSQLSQLWarningsService extends UniversalActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");
  
  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public boolean addToolButton() {
    return false;
  }

  @Override
  public boolean addMenuItem() {
    return false;
  }

  @Override
  public boolean addToEditor() {
    return false;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLSQLWarningsService-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
  
  private void statementWarnings(Statement stmt) throws SQLException {
    boolean found = false;
    if (stmt != null) {
      SQLWarning warn = stmt.getWarnings();
      while (warn != null) {
        scriptResultPanel.append(warn.toString() +"\n");
        warn = warn.getNextWarning();
        found = true;
      }
      if (found) {
        stmt.clearWarnings();
      }
    }
  }
  
  @Override
  public void beforeOpenQuery(final Query query) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          int waitCount = 10;
          while (true) {
            if (query.getState() == Query.State.OPENING && query.getStatement() != null) {
              break;
            }
            TaskUtil.sleep(100);
            if (--waitCount == 0) {
              return;
            }
            if (Thread.currentThread().getState() == Thread.State.TERMINATED) {
              break;
            }
          }
          while (true) {
            if (query.getState() == Query.State.OPENING) {
              statementWarnings(query.getStatement());
            }
            else {
              break;
            }
            if (Thread.currentThread().getState() == Thread.State.TERMINATED) {
              break;
            }
            TaskUtil.sleep(1000);
          }
        } catch (SQLException ex) {
          ExceptionUtil.processException(ex);
        }
      }
    }).start();
  }
  
  @Override
  public void beforeExecuteCommand(final Command command) {
    command.addCommandListener(new CommandListener() {
      @Override
      public void beforeExecute(EventObject e) {
      }
      @Override
      public void afterExecute(EventObject e) {
        if (((Command)e.getSource()).getStatement() != null) {
          try {
            statementWarnings(((Command)e.getSource()).getStatement());
          } catch (SQLException ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
      @Override
      public void errorPerformed(EventObject e) {
      }
    });
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          int waitCount = 10;
          while (true) {
            if (command.getState() == Command.State.EXECUTEING && command.getStatement() != null) {
              break;
            }
            TaskUtil.sleep(100);
            if (--waitCount == 0) {
              return;
            }
            if (Thread.currentThread().getState() == Thread.State.TERMINATED) {
              break;
            }
          }
          while (true) {
            if (command.getState() == Command.State.EXECUTEING) {
              statementWarnings(command.getStatement());
            }
            else {
              break;
            }
            if (Thread.currentThread().getState() == Thread.State.TERMINATED) {
              break;
            }
            TaskUtil.sleep(1000);
          }
        } catch (SQLException ex) {
          ExceptionUtil.processException(ex);
        }
      }
    }).start();
  }

  @Override
  public void afterOpenQuery(Query query) {
    if (query.getStatement() != null) {
      try {
        statementWarnings(query.getStatement());
      } catch (SQLException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
}

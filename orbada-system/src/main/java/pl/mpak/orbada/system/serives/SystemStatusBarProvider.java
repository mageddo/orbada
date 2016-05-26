/*
 * SystemStatusBarProvider.java
 *
 * Created on 2007-10-26, 22:38:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.system.serives;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import pl.mpak.orbada.OrbadaCancelCloseException;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.StatusBarProvider;
import pl.mpak.orbada.system.OrbadaSystemPlugin;
import pl.mpak.orbada.system.gui.QueryExecutionDialog;
import pl.mpak.orbada.system.gui.TaskExecutionDialog;
import pl.mpak.orbada.universal.gui.ErrorBox;
import pl.mpak.sky.gui.swing.CursorChanger;
import pl.mpak.sky.gui.swing.comp.StatusPanel;
import pl.mpak.sky.gui.swing.comp.SystemStatusBar;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.CommandListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;
import pl.mpak.usedb.core.DatabaseManagerEvent;
import pl.mpak.usedb.core.DatabaseManagerListener;
import pl.mpak.usedb.core.DatabaseQueryEvent;
import pl.mpak.usedb.core.DatabaseQueryListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.ProcessExceptionListener;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPoolContainerEvent;
import pl.mpak.util.task.TaskPoolContainerListener;
import pl.mpak.util.task.TaskPoolEvent;
import pl.mpak.util.task.TaskPoolListener;
import pl.mpak.util.task.TaskPoolManager;

/**
 *
 * @author akaluza
 */
public class SystemStatusBarProvider extends StatusBarProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("system");
  public static SystemStatusBarProvider instance;
  //private PanelmPakLogo panelmPakLogo;
  private javax.swing.JPanel panelSystemStatus;
  private SystemStatusBar systemStatusBar;
  private StatusPanel panelQuery;
  private StatusPanel panelTask;
  private JButton buttonError;
  private int openingQueries = 0;
  private int openedQueries = 0;
  private int openedQueryCount = 0;
  private int openedDatabases = 0;
  private int runingTasks = 0;
  private int waitingTasks = 0;
  private DatabaseQueryListener databaseQueryListener;
  private QueryListener queryListener;
  private CommandListener commandListener;
  private Throwable exception;
  private static final ArrayList<Task> taskList = new ArrayList<Task>();
  private int statusTextEnabled = 0;
  private boolean requestUpdateStatusBarQuery = true;

  public SystemStatusBarProvider() {
    instance = this;
  }

  public static ArrayList<Task> getTaskList() {
    return taskList;
  }

  public String getDescription() {
    return stringManager.getString("system-status-bar-provider");
  }

  private void updateStatusBarQuery() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        updateOpenedQueries();
        if (openingQueries > 0) {
          panelQuery.setForeground(Color.RED);
        } else {
          panelQuery.setForeground(null);
        }
        panelQuery.setText(String.format("%d, %d/%d/%d ", new Object[]{openedDatabases, openingQueries, openedQueries, openedQueryCount}));
        panelQuery.revalidate();
      }
    });
  }

  private void updateStatusBarTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (runingTasks > 0) {
          panelTask.setForeground(Color.RED);
        } else {
          panelTask.setForeground(null);
        }
        int count = 0;
        int percentage = 0;
        synchronized (taskList) {
          if (taskList.size() > 0 && runingTasks > 0) {
            for (Task task : taskList) {
              if (task.getPercenExecution() > 0) {
                percentage += task.getPercenExecution(); 
                count++;
              }
            }
            percentage = (int)((double)percentage /count);
          }
        }
        if (percentage > 0) {
          panelTask.setText(String.format("%d/%d [%d%%] ", new Object[]{runingTasks, waitingTasks, percentage}));
        }
        else {
          panelTask.setText(String.format("%d/%d ", new Object[]{runingTasks, waitingTasks}));
        }
        panelTask.revalidate();
      }
    });
  }

  private void initStatusBarTask() {
    TaskPoolManager.addTaskPoolListener(new TaskPoolListener() {

      public void beginPool(TaskPoolEvent e) {
      }

      public void endPool(TaskPoolEvent e) {
      }

      public void beginTask(TaskPoolEvent e) {
        waitingTasks--;
        runingTasks++;
        updateStatusBarTask();
      }

      public void endTask(TaskPoolEvent e) {
        runingTasks--;
        updateStatusBarTask();
        synchronized (taskList) {
          taskList.remove(e.getTask());
        }
      }
    });
    TaskPoolManager.addTaskPoolContainerListener(new TaskPoolContainerListener() {
      public void addTask(TaskPoolContainerEvent e) {
        synchronized (taskList) {
          taskList.add(e.getTask());
        }
        waitingTasks++;
        updateStatusBarTask();
      }
      public void removeTask(TaskPoolContainerEvent arg0) {
      }
    });
    panelTask = systemStatusBar.addPanel("task-panel");
    panelTask.setToolTipText(stringManager.getString("background-tasks"));
    panelTask.setIcon(new ImageIcon(getClass().getResource("/pl/mpak/res/icons/runing-tasks18.gif")));
    panelTask.setDisplayActivation(true);
    panelTask.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (!TaskExecutionDialog.showed) {
          TaskExecutionDialog.showDialog();
        }
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
    });
    updateStatusBarTask();
  }
  
  private void updateOpenedQueries() {
    openingQueries = 0;
    openedQueries = 0;
    for (int i = 0; i < DatabaseManager.getManager().getDatabaseCount(); i++) {
      Database database = DatabaseManager.getManager().getDatabase(i);
      synchronized (database.getQueryListLocker()) {
        for (int q = 0; q < database.getQueryCount(); q++) {
          if (database.getQuery(q).getState() == Query.State.OPENING) {
            openingQueries++;
          }
          if (database.getQuery(q).getState() == Query.State.OPENED) {
            openedQueries++;
          }
        }
      }
      synchronized (database.getCommandListLocker()) {
        for (int q = 0; q < database.getCommandCount(); q++) {
          if (database.getCommand(q).getState() == Command.State.EXECUTEING) {
            openingQueries++;
          }
        }
      }
    }
  }

  private void initStatusBarQuery() {
    queryListener = new QueryListener() {
      CursorChanger workCursor;
      boolean waitCount;
      private void workCursorSet() {
        if (Thread.currentThread().getName() != null && Thread.currentThread().getName().startsWith("AWT-")) {
          if (!waitCount) {
            if (workCursor == null) {
              workCursor = new CursorChanger(Application.get().getMainFrame().getGlassPane(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
            workCursor.show();
            Application.get().getMainFrame().getGlassPane().setVisible(true);
          }
          waitCount = true;
        }
      }
      private void workCursorReset() {
        if (waitCount) {
          if (workCursor != null) {
            Application.get().getMainFrame().getGlassPane().setVisible(false);
            workCursor.restore();
          }
          waitCount = false;
        }
      }
      public void beforeScroll(EventObject event) {
      }
      public void afterScroll(EventObject event) {
      }
      public void beforeOpen(EventObject event) {
        requestUpdateStatusBarQuery = true;
        workCursorSet();
      }
      public void afterOpen(EventObject event) {
        openedQueryCount++;
        requestUpdateStatusBarQuery = true;
        workCursorReset();
      }
      public void beforeClose(EventObject event) {
      }
      public void afterClose(EventObject event) {
        requestUpdateStatusBarQuery = true;
      }
      public void flushedPerformed(EventObject event) {
      }
      public void errorPerformed(EventObject event) {
        workCursorReset();
        requestUpdateStatusBarQuery = true;
      }
    };
    commandListener = new CommandListener() {
      public void beforeExecute(EventObject event) {
        requestUpdateStatusBarQuery = true;
      }
      public void afterExecute(EventObject event) {
        openedQueryCount++;
        requestUpdateStatusBarQuery = true;
      }
      public void errorPerformed(EventObject event) {
        requestUpdateStatusBarQuery = true;
      }
    };
    databaseQueryListener = new DatabaseQueryListener() {
      public void queryAdded(DatabaseQueryEvent event) {
        event.getQuery().addQueryListener(queryListener);
      }
      public void queryRemoved(DatabaseQueryEvent event) {
        event.getQuery().removeQueryListener(queryListener);
      }
    };
    DatabaseManager.getManager().addDatabaseManagerListener(new DatabaseManagerListener() {
      public void databaseAdded(DatabaseManagerEvent event) {
        openedDatabases++;
        event.getDatabase().addDatabaseQueryListener(databaseQueryListener);
        event.getDatabase().addCommandListener(commandListener);
        requestUpdateStatusBarQuery = true;
      }
      public void databaseRemoved(DatabaseManagerEvent event) {
        event.getDatabase().removeDatabaseQueryListener(databaseQueryListener);
        event.getDatabase().removeCommandListener(commandListener);
        openedDatabases--;
        requestUpdateStatusBarQuery = true;
      }
    });
    for (int i = 0; i < DatabaseManager.getManager().getDatabaseCount(); i++) {
      DatabaseManager.getManager().getDatabase(i).addDatabaseQueryListener(databaseQueryListener);
      DatabaseManager.getManager().getDatabase(i).addCommandListener(commandListener);
    }

    panelQuery = systemStatusBar.addPanel("query-panel");
    panelQuery.setToolTipText(stringManager.getString("usedb-lib-status"));
    panelQuery.setIcon(new ImageIcon(getClass().getResource("/pl/mpak/res/icons/runing-queries18.gif")));
    panelQuery.setDisplayActivation(true);
    panelQuery.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (!QueryExecutionDialog.showed) {
          QueryExecutionDialog.showDialog();
        }
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
    });
    systemStatusBar.getTimer().schedule(new TimerTask() {
      public void run() {
        if (requestUpdateStatusBarQuery) {
          updateStatusBarQuery();
          requestUpdateStatusBarQuery = false;
        }
        if (taskList.size() > 0) {
          updateStatusBarTask();
        }
      }
    }, 1000, 1000);
  }

  public void initButtonError() {
    buttonError.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonError.setToolTipText("");
        if (exception != null) {
          ErrorBox.show(exception);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            buttonError.setEnabled(false);
          }
        });
      }
    });
    ExceptionUtil.addProcessExceptionListener(new ProcessExceptionListener() {
      public void processException(EventObject evt) {
        exception = (Throwable) evt.getSource();
        if (exception instanceof OrbadaCancelCloseException) {
          return;
        }
        if (exception != null) {
          String message = exception.getMessage();
          buttonError.setToolTipText(message);
          setStatusText(message);
        } else {
          buttonError.setToolTipText("");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            buttonError.setEnabled(true);
          }
        });
      }
    });
  }

  public void initStatusTextPanel() {
    systemStatusBar.addPanel("status-text");
    systemStatusBar.getPanel("status-text").setText(" ");
    systemStatusBar.getPanel("status-text").setIcon(new ImageIcon(getClass().getResource("/pl/mpak/res/icons/status_text.gif")));
    systemStatusBar.getTimer().schedule(new TimerTask() {
      public void run() {
        if (statusTextEnabled == 0) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              systemStatusBar.getPanel("status-text").setText("");
            }
          });
        } else {
          statusTextEnabled--;
        }
      }
    }, 1000, 1000);
  }

  public void applySettings() {
    ISettings settings = application.getSettings(SystemSettingsProvider.settingsName);
    systemStatusBar.setTimeStatus(!settings.getValue(SystemSettingsProvider.hideTime, false));
    systemStatusBar.setRunTimeStatus(!settings.getValue(SystemSettingsProvider.hideRunTime, false));
  }

  public Component getComponent() {
    if (panelSystemStatus == null) {
      panelSystemStatus = new javax.swing.JPanel(new java.awt.BorderLayout());
      //panelmPakLogo = new PanelmPakLogo();

      systemStatusBar = new SystemStatusBar();
      buttonError = new JButton(new ImageIcon(getClass().getResource("/pl/mpak/res/icons/error_square16.gif")));
      buttonError.setMargin(new Insets(1, 1, 1, 1));
      buttonError.setPreferredSize(new Dimension(20, 20));
      buttonError.setEnabled(false);
      buttonError.setFocusable(false);
      systemStatusBar.addBefore(systemStatusBar.getPanel("memory-panel"), buttonError);

      initButtonError();
      initStatusBarQuery();
      initStatusBarTask();
      initStatusTextPanel();
      panelSystemStatus.add(systemStatusBar, java.awt.BorderLayout.WEST);
      //panelSystemStatus.add(panelmPakLogo, java.awt.BorderLayout.EAST);
      applySettings();
    }
    return panelSystemStatus;
  }

  public void setStatusText(final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (text != null) {
          systemStatusBar.getPanel("status-text").setText(text.substring(0, Math.min(90, text.length())) +(text.length() > 90 ? "... " : " "));
        }
      }
    });
    statusTextEnabled = 10;
  }

  public String getGroupName() {
    return OrbadaSystemPlugin.systemGroupName;
  }
}

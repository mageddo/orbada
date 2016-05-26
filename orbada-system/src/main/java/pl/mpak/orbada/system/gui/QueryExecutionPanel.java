/*
 * TaskExecutionPanel.java
 *
 * Created on 12 listopad 2007, 18:27
 */

package pl.mpak.orbada.system.gui;

import java.awt.Component;
import java.util.EventObject;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.CommandListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;
import pl.mpak.usedb.core.DatabaseManagerEvent;
import pl.mpak.usedb.core.DatabaseManagerListener;
import pl.mpak.usedb.core.DatabaseQueryEvent;
import pl.mpak.usedb.core.DatabaseQueryListener;
import pl.mpak.usedb.core.QueryListener;

/**
 *
 * @author  akaluza
 */
public class QueryExecutionPanel extends javax.swing.JPanel {
  
  private DatabaseManagerListener databaseManagerListener;
  private DatabaseQueryListener databaseQueryListener;
  private CommandListener commandListener;
  private QueryListener queryListener;
  
  /** Creates new form TaskExecutionPanel */
  public QueryExecutionPanel() {
    initComponents();
    init();
  }
  
  private void init() {
    queryListener = new QueryListener() {

      public void beforeScroll(EventObject event) {
      }

      public void afterScroll(EventObject event) {
      }

      public void beforeOpen(EventObject event) {
        refreshList();
      }

      public void afterOpen(EventObject event) {
        refreshList();
      }

      public void beforeClose(EventObject event) {
        refreshList();
      }

      public void afterClose(EventObject event) {
        refreshList();
      }

      public void flushedPerformed(EventObject event) {
        refreshList();
      }

      public void errorPerformed(EventObject event) {
        refreshList();
      }
    };
    commandListener = new CommandListener() {

      private void remove(Command command) {
        synchronized (panelQueries) {
          for (int i = 0; i < panelQueries.getComponentCount(); i++) {
            Component c = panelQueries.getComponent(i);
            if (c instanceof CommandPanel && command.equals(((CommandPanel)c).getCommand())) {
              panelQueries.remove(c);
            }
          }
        }
        refreshList();
      }
      
      public void beforeExecute(EventObject event) {
        synchronized (panelQueries) {
          panelQueries.add(new CommandPanel((Command)event.getSource()));
        }
        refreshList();
      }

      public void afterExecute(EventObject event) {
        remove((Command)event.getSource());
      }

      public void errorPerformed(EventObject event) {
        remove((Command)event.getSource());
      }
    };
    
    databaseQueryListener = new DatabaseQueryListener() {

      public void queryAdded(DatabaseQueryEvent event) {
        event.getQuery().addQueryListener(queryListener);
        synchronized (panelQueries) {
          panelQueries.add(new QueryPanel(event.getQuery()));
        }
        refreshList();
      }

      public void queryRemoved(DatabaseQueryEvent event) {
        synchronized (panelQueries) {
          for (int i = 0; i < panelQueries.getComponentCount(); i++) {
            Component c = panelQueries.getComponent(i);
            if (c instanceof QueryPanel && event.getQuery().equals(((QueryPanel)c).getQuery())) {
              panelQueries.remove(c);
            }
          }
        }
        event.getQuery().removeQueryListener(queryListener);
        refreshList();
      }
    };
    DatabaseManager.getManager().addDatabaseManagerListener(databaseManagerListener = new DatabaseManagerListener() {

      public void databaseAdded(DatabaseManagerEvent event) {
        event.getDatabase().addDatabaseQueryListener(databaseQueryListener);
        event.getDatabase().addCommandListener(commandListener);
        refreshList();
      }

      public void databaseRemoved(DatabaseManagerEvent event) {
        event.getDatabase().removeDatabaseQueryListener(databaseQueryListener);
        event.getDatabase().removeCommandListener(commandListener);
        refreshList();
      }
    });

    for (int i = 0; i < DatabaseManager.getManager().getDatabaseCount(); i++) {
      Database database = DatabaseManager.getManager().getDatabase(i);
      synchronized (database.getQueryListLocker()) {
        for (int q = 0; q < database.getQueryCount(); q++) {
          database.getQuery(q).addQueryListener(queryListener);
          panelQueries.add(new QueryPanel(database.getQuery(q)));
        }
      }
      synchronized (database.getCommandListLocker()) {
        for (int q = 0; q < database.getCommandCount(); q++) {
          database.getCommand(q).addCommandListener(commandListener);
          panelQueries.add(new CommandPanel(database.getCommand(q)));
        }
      }
      database.addDatabaseQueryListener(databaseQueryListener);
      database.addCommandListener(commandListener);
    }

    refreshList();
  }
  
  private void refreshList() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (panelQueries) {
          for (int i = 0; i < panelQueries.getComponentCount(); i++) {
            Component c = panelQueries.getComponent(i);
            if (c instanceof QueryPanel) {
              ((QueryPanel)c).update();
            }
            else if (c instanceof CommandPanel) {
              ((CommandPanel)c).update();
            }
          }
        }
        panelQueries.revalidate();
        panelQueries.repaint();
      }
    });
  }
  
  public void close() {
    DatabaseManager.getManager().removeDatabaseManagerListener(databaseManagerListener);
    for (int i = 0; i < DatabaseManager.getManager().getDatabaseCount(); i++) {
      Database database = DatabaseManager.getManager().getDatabase(i);
      for (int q = 0; q < database.getQueryCount(); q++) {
        database.getQuery(q).removeQueryListener(queryListener);
      }
      database.removeDatabaseQueryListener(databaseQueryListener);
      database.removeCommandListener(commandListener);
    }
    panelQueries.removeAll();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    panelQueries = new javax.swing.JPanel();

    panelQueries.setLayout(new javax.swing.BoxLayout(panelQueries, javax.swing.BoxLayout.Y_AXIS));
    jScrollPane1.setViewportView(panelQueries);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel panelQueries;
  // End of variables declaration//GEN-END:variables
  
}

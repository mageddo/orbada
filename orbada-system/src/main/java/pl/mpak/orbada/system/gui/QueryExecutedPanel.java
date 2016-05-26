/*
 * TaskExecutionPanel.java
 *
 * Created on 12 listopad 2007, 18:27
 */

package pl.mpak.orbada.system.gui;

import java.util.Date;
import java.util.EventObject;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.system.OrbadaSystemPlugin;
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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class QueryExecutedPanel extends javax.swing.JPanel {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSystemPlugin.class);
  
  private DatabaseManagerListener databaseManagerListener;
  private DatabaseQueryListener databaseQueryListener;
  private CommandListener commandListener;
  private QueryListener queryListener;
  
  /** Creates new form TaskExecutionPanel */
  public QueryExecutedPanel() {
    initComponents();
    init();
  }
  
  private void checkTextLength() {
    if (textExecuted.getDocument().getLength() > 100000) {
      try {
        textExecuted.getDocument().remove(0, textExecuted.getDocument().getLength() -100000);
      } catch (BadLocationException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  private void appendText(final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textExecuted.append(text);
        checkTextLength();
      }
    });
  }
  
  private void appendQuery(Query query) {
    StringBuffer sb = new StringBuffer();
    sb.append("--- DATABASE ---\n");
    if (query.getDatabase().getPublicName() != null) {
      sb.append(query.getDatabase().getPublicName() +"\n");
    }
    sb.append(query.getDatabase().getUrl() +"\n");
    sb.append("--- SQL QUERY ---\n");
    sb.append(query.getSqlText() +"\n");
    sb.append(String.format(stringManager.getString("opened-records"), new Object[] {query.getRecordCount(), new Variant(new Date(query.getOpenTime())), StringUtil.formatTime(query.getOpeningTime())}) +"\n");
    if (query.getParameterCount() > 0) {
      sb.append("--- PARAMETERS ---\n");
      for (int i=0; i<query.getParameterCount(); i++) {
        sb.append(query.getParameter(i) +"\n");
      }
    }
    sb.append("\n");
    appendText(sb.toString());
  }
  
  private void appendCommand(Command command) {
    StringBuffer sb = new StringBuffer();
    sb.append("--- DATABASE ---\n");
    if (command.getDatabase().getPublicName() != null) {
      sb.append(command.getDatabase().getPublicName() +"\n");
    }
    sb.append(command.getDatabase().getUrl() +"\n");
    sb.append("--- SQL COMMAND ---\n");
    sb.append(command.getSqlText() +"\n");
    sb.append(String.format(stringManager.getString("execution-time"), new Object[] {new Variant(new Date(command.getExecutedTime())), StringUtil.formatTime(command.getExecutionTime())}) +"\n");
    if (command.getParameterCount() > 0) {
      sb.append("--- PARAMETERS ---\n");
      for (int i=0; i<command.getParameterCount(); i++) {
        sb.append(command.getParameter(i) +"\n");
      }
    }
    sb.append("\n");
    appendText(sb.toString());
  }
  
  private void init() {
    queryListener = new QueryListener() {
      public void beforeScroll(EventObject event) {
      }
      public void afterScroll(EventObject event) {
      }
      public void beforeOpen(EventObject event) {
      }
      public void afterOpen(EventObject event) {
        appendQuery((Query)event.getSource());
      }
      public void beforeClose(EventObject event) {
      }
      public void afterClose(EventObject event) {
      }
      public void flushedPerformed(EventObject event) {
      }
      public void errorPerformed(EventObject event) {
        textExecuted.append("--- ERROR ---\n");
        appendQuery((Query)event.getSource());
      }
    };
    commandListener = new CommandListener() {
      public void beforeExecute(EventObject event) {
      }
      public void afterExecute(EventObject event) {
        appendCommand((Command)event.getSource());
      }
      public void errorPerformed(EventObject event) {
        textExecuted.append("--- ERROR ---\n");
        appendCommand((Command)event.getSource());
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
    DatabaseManager.getManager().addDatabaseManagerListener(databaseManagerListener = new DatabaseManagerListener() {
      public void databaseAdded(DatabaseManagerEvent event) {
        event.getDatabase().addDatabaseQueryListener(databaseQueryListener);
        event.getDatabase().addCommandListener(commandListener);
      }
      public void databaseRemoved(DatabaseManagerEvent event) {
        event.getDatabase().removeDatabaseQueryListener(databaseQueryListener);
        event.getDatabase().removeCommandListener(commandListener);
      }
    });
    
    for (int i = 0; i < DatabaseManager.getManager().getDatabaseCount(); i++) {
      Database database = DatabaseManager.getManager().getDatabase(i);
      for (int q = 0; q < database.getQueryCount(); q++) {
        database.getQuery(q).addQueryListener(queryListener);
      }
      database.addDatabaseQueryListener(databaseQueryListener);
      database.addCommandListener(commandListener);
    }
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
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    textExecuted = new pl.mpak.sky.gui.swing.comp.TextArea();

    textExecuted.setColumns(20);
    textExecuted.setEditable(false);
    textExecuted.setRows(5);
    textExecuted.setFont(new java.awt.Font("Courier New", 0, 11));
    jScrollPane1.setViewportView(textExecuted);

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
  private pl.mpak.sky.gui.swing.comp.TextArea textExecuted;
  // End of variables declaration//GEN-END:variables
  
}

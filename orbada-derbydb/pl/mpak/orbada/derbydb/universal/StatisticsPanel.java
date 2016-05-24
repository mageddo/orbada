/*
 * ExplainPlanPanel.java
 *
 * Created on 28 luty 2008, 19:22
 */
package pl.mpak.orbada.derbydb.universal;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author  akaluza
 */
public class StatisticsPanel extends javax.swing.JPanel implements Closeable {

  private Database database;

  /** Creates new form ExplainPlanPanel */
  public StatisticsPanel(Database database) {
    this.database = database;
    initComponents();
    init();
  }

  private void init() {
    try {
      database.executeCommand("CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1)");
      database.executeCommand("CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(1)");
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public void close() throws IOException {
    try {
      database.executeCommand("CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(0)");
      database.executeCommand("CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(0)");
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void beforeSql() {
  }
  
  public void afterSql() {
    Command command = database.createCommand();
    try {
      command.execute("VALUES SYSCS_UTIL.SYSCS_GET_RUNTIMESTATISTICS()");
      if (command.getStatement() != null) {
        ResultSet rs = command.getStatement().getResultSet();
        if (rs != null) {
          Query query = database.createQuery();
          try {
            query.setResultSet(rs);
            textStatistics.setText(query.getField(0).getString());
          }
          finally {
            query.close();
          }
        }
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
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
    textStatistics = new pl.mpak.sky.gui.swing.comp.TextArea();

    setLayout(new java.awt.BorderLayout());

    textStatistics.setColumns(20);
    textStatistics.setEditable(false);
    textStatistics.setRows(5);
    textStatistics.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
    jScrollPane1.setViewportView(textStatistics);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.sky.gui.swing.comp.TextArea textStatistics;
  // End of variables declaration//GEN-END:variables
}

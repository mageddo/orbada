/*
 * ExplainPlanPanel.java
 *
 * Created on 28 luty 2008, 19:22
 */
package pl.mpak.orbada.mysql.gui;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import orbada.gui.comps.table.Table;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class AutotracePanel extends javax.swing.JPanel implements Closeable {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  private Database database;
  private HashMap<String, String> sessionMap;
  private ArrayList<String[]> globalList;
  private ArrayList<String[]> sessionList;
  private ArrayList<String[]> diffList;

  /** Creates new form ExplainPlanPanel */
  public AutotracePanel(Database database) {
    this.database = database;
    sessionMap = new HashMap<String, String>();
    sessionList = new ArrayList<String[]>();
    globalList = new ArrayList<String[]>();
    diffList = new ArrayList<String[]>();
    initComponents();
    init();
  }

  private void init() {
    tableLastSql.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableLastSql.setModel(new AbstractTableModel() {
      @Override
      public String getColumnName(int column) {
        if (column == 0) {
          return stringManager.getString("name");
        }
        else if (column == 1) {
          return stringManager.getString("value");
        }
        return "???";
      }
      public int getRowCount() {
        return diffList.size();
      }
      public int getColumnCount() {
        return 2;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        return diffList.get(rowIndex)[columnIndex];
      }
    });
    tableSession.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableSession.setModel(new AbstractTableModel() {
      @Override
      public String getColumnName(int column) {
        if (column == 0) {
          return stringManager.getString("name");
        }
        else if (column == 1) {
          return stringManager.getString("value");
        }
        return "???";
      }
      public int getRowCount() {
        return sessionList.size();
      }
      public int getColumnCount() {
        return 2;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        return sessionList.get(rowIndex)[columnIndex];
      }
    });
    tableGlobal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableGlobal.setModel(new AbstractTableModel() {
      @Override
      public String getColumnName(int column) {
        if (column == 0) {
          return stringManager.getString("name");
        }
        else if (column == 1) {
          return stringManager.getString("value");
        }
        return "???";
      }
      public int getRowCount() {
        return globalList.size();
      }
      public int getColumnCount() {
        return 2;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        return globalList.get(rowIndex)[columnIndex];
      }
    });
  }

  public void close() throws IOException {
  }
  
  public void beforeSql() {
    diffList.clear();
    sessionList.clear();
    Query query = database.createQuery();
    try {
      query.open(Sql.getStatsList());
      while (!query.eof()) {
        sessionMap.put(query.fieldByName("variable_name").getString(), query.fieldByName("variable_value").getString());
        sessionList.add(new String[] {query.fieldByName("variable_name").getString(), query.fieldByName("variable_value").getString()});
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    tableLastSql.revalidate();
    tableSession.revalidate();
    tableGlobal.revalidate();
  }
  
  public void afterSql() {
    Query query = database.createQuery();
    try {
      query.open(Sql.getStatsList());
      while (!query.eof()) {
        String value = sessionMap.get(query.fieldByName("variable_name").getString());
        if (value != null) {
          if (!value.equals(query.fieldByName("variable_value").getString())) {
            try {
              value = String.valueOf(query.fieldByName("variable_value").getLong() -Long.parseLong(value));
            }
            catch (NumberFormatException e) {
              value = query.fieldByName("variable_value").getString();
            }
            diffList.add(new String[] {query.fieldByName("variable_name").getString(), value});
          }
        }
        sessionMap.put(query.fieldByName("variable_name").getString(), query.fieldByName("variable_value").getString());
        sessionList.add(new String[] {query.fieldByName("variable_name").getString(), query.fieldByName("variable_value").getString()});
        query.next();
      }
      query.open(Sql.getGlobalStatsList());
      while (!query.eof()) {
        globalList.add(new String[] {query.fieldByName("variable_name").getString(), query.fieldByName("variable_value").getString()});
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    tableLastSql.revalidate();
    tableSession.revalidate();
    tableGlobal.revalidate();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jTabbedPane1 = new javax.swing.JTabbedPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableLastSql = new Table();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableSession = new Table();
    jScrollPane3 = new javax.swing.JScrollPane();
    tableGlobal = new Table();

    setLayout(new java.awt.BorderLayout());

    jTabbedPane1.setFocusable(false);

    tableLastSql.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    jScrollPane1.setViewportView(tableLastSql);

    jTabbedPane1.addTab(stringManager.getString("sql-command"), jScrollPane1); // NOI18N

    tableSession.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    jScrollPane2.setViewportView(tableSession);

    jTabbedPane1.addTab(stringManager.getString("session-status"), jScrollPane2); // NOI18N

    tableGlobal.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    jScrollPane3.setViewportView(tableGlobal);

    jTabbedPane1.addTab(stringManager.getString("global-status"), jScrollPane3); // NOI18N

    add(jTabbedPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JTabbedPane jTabbedPane1;
  private Table tableGlobal;
  private Table tableLastSql;
  private Table tableSession;
  // End of variables declaration//GEN-END:variables
}

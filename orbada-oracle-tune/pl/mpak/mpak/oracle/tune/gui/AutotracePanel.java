/*
 * ExplainPlanPanel.java
 *
 * Created on 28 luty 2008, 19:22
 */
package pl.mpak.mpak.oracle.tune.gui;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
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

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleTunePlugin.class);

  private Database database;
  private HashMap<String, String> fullMap;
  private ArrayList<String[]> fullList;
  private ArrayList<String[]> diffList;

  /** Creates new form ExplainPlanPanel */
  public AutotracePanel(Database database) {
    this.database = database;
    fullMap = new HashMap<String, String>();
    fullList = new ArrayList<String[]>();
    diffList = new ArrayList<String[]>();
    initComponents();
    init();
  }

  private void init() {
    tableLastSql.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableLastSql.setModel(new AbstractTableModel() {
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
        return fullList.size();
      }
      public int getColumnCount() {
        return 2;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        return fullList.get(rowIndex)[columnIndex];
      }
    });
  }

  public void close() throws IOException {
  }
  
  public void beforeSql() {
    diffList.clear();
    fullList.clear();
    Query query = database.createQuery();
    try {
      query.open(Sql.getStatsList());
      while (!query.eof()) {
        fullMap.put(query.fieldByName("name").getString(), query.fieldByName("value").getString());
        query.next();
      }
      Iterator<Map.Entry<String,String>> i = fullMap.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry<String,String> e = i.next();
        fullList.add(new String[] {e.getKey(), e.getValue()});
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
  }
  
  public void afterSql() {
    Query query = database.createQuery();
    try {
      query.open(Sql.getStatsList());
      while (!query.eof()) {
        String value = fullMap.get(query.fieldByName("name").getString());
        if (value != null) {
          if (!value.equals(query.fieldByName("value").getString())) {
            value = String.valueOf(query.fieldByName("value").getLong() -Long.parseLong(value));
            diffList.add(new String[] {query.fieldByName("name").getString(), value});
          }
        }
        fullMap.put(query.fieldByName("name").getString(), query.fieldByName("value").getString());
        query.next();
      }
      Iterator<Map.Entry<String,String>> i = fullMap.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry<String,String> e = i.next();
        fullList.add(new String[] {e.getKey(), e.getValue()});
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
    tableLastSql = new pl.mpak.orbada.gui.comps.table.Table();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableSession = new pl.mpak.orbada.gui.comps.table.Table();

    setLayout(new java.awt.BorderLayout());

    jTabbedPane1.setFocusable(false);

    tableLastSql.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Nazwa parametru", "Warto��"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }
    });
    tableLastSql.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    jScrollPane1.setViewportView(tableLastSql);

    jTabbedPane1.addTab(stringManager.getString("sql-command"), jScrollPane1); // NOI18N

    tableSession.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Nazwa parametru", "Warto��"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }
    });
    tableSession.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    jScrollPane2.setViewportView(tableSession);

    jTabbedPane1.addTab(stringManager.getString("session"), jScrollPane2); // NOI18N

    add(jTabbedPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTabbedPane jTabbedPane1;
  private pl.mpak.orbada.gui.comps.table.Table tableLastSql;
  private pl.mpak.orbada.gui.comps.table.Table tableSession;
  // End of variables declaration//GEN-END:variables
}

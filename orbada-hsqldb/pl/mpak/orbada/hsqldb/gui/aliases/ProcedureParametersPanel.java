/*
 * TableColumns.java
 *
 * Created on 28 paŸdziernik 2007, 17:43
 */

package pl.mpak.orbada.hsqldb.gui.aliases;

import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ProcedureParametersPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaHSqlDbPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  
  private Timer timer;
  
  /** Creates new form TableColumns
   * @param accesibilities
   */
  public ProcedureParametersPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refresh();
      }
    };
    OrbadaHSqlDbPlugin.getRefreshQueue().add(timer);
    
    tableParameters.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableParameters.getSelectedRow();
        if (rowIndex >= 0 && tableParameters.getQuery().isActive()) {
          try {
            tableParameters.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableParameters.getQuery().setDatabase(getDatabase());
    try {
      tableParameters.addColumn(new QueryTableColumn("column_name", stringManager.getString("column-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableParameters.addColumn(new QueryTableColumn("display_type", stringManager.getString("display-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableParameters.addColumn(new QueryTableColumn("nullable", "Null?", 40));
      tableParameters.addColumn(new QueryTableColumn("column_type", stringManager.getString("column-type"), 50));
      tableParameters.addColumn(new QueryTableColumn("remarks", stringManager.getString("comment"), 300));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("ProcedureParametersPanel-title");
  }
  
  public void refresh() {
    try {
      String columnName = null;
      requestRefresh = false;
      if (tableParameters.getQuery().isActive() && tableParameters.getSelectedRow() >= 0) {
        tableParameters.getQuery().getRecord(tableParameters.getSelectedRow());
        columnName = tableParameters.getQuery().fieldByName("COLUMN_NAME").getString();
      }
      tableParameters.getQuery().close();
      tableParameters.getQuery().setSqlText(Sql.getProcedureParameterList(null));
      tableParameters.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableParameters.getQuery().paramByName("procedure_name").setString(currentTableName);
      tableParameters.getQuery().open();
      if (!tableParameters.getQuery().isEmpty()) {
        if (columnName != null && tableParameters.getQuery().locate("column_name", new Variant(columnName))) {
          tableParameters.changeSelection(tableParameters.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableParameters.changeSelection(0, 0);
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentTableName = objectName;
      if (isVisible()) {
        timer.restart();
      }
      else {
        requestRefresh = true;
      }
    }
  }
  
  @Override
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    timer.cancel();
    tableParameters.getQuery().close();
    accesibilities = null;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    tableParameters = new pl.mpak.orbada.gui.comps.table.ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableParameters);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableParameters);
    add(statusBar, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    jPanel1.add(toolBar);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  refresh(null, currentSchemaName, currentTableName);
}//GEN-LAST:event_formComponentShown


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableParameters;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

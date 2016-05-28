package pl.mpak.orbada.oracle.gui.tables;

import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.table.EnableTableRelatedConstraintWizard;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableReferencedFromPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  
  /** Creates new form TableIndexesPanel 
   * @param accesibilities 
   */
  public TableReferencedFromPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableReferencedFrom.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableReferencedFrom.getSelectedRow();
        if (rowIndex >= 0 && tableReferencedFrom.getQuery().isActive()) {
          try {
            tableReferencedFrom.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableReferencedFrom.getQuery().setDatabase(getDatabase());
    try {
      tableReferencedFrom.addColumn(new QueryTableColumn("r_constraint_name", stringManager.getString("ref-constraint-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableReferencedFrom.addColumn(new QueryTableColumn("r_schema_name", stringManager.getString("ref-schema-name"), 120));
      tableReferencedFrom.addColumn(new QueryTableColumn("r_table_name", stringManager.getString("ref-table-name"), 120));
      tableReferencedFrom.addColumn(new QueryTableColumn("r_column_name", stringManager.getString("ref-column-name"), 120));
      tableReferencedFrom.addColumn(new QueryTableColumn("constraint_name", stringManager.getString("foreign-key"), 150));
      tableReferencedFrom.addColumn(new QueryTableColumn("column_name", stringManager.getString("column-name"), 120));
      tableReferencedFrom.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 70));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("d1.column_name", stringManager.getString("column-name"), (String[])null));
      def.add(new SqlFilterDefComponent("a1.owner", stringManager.getString("schema"), (String[])null));
      def.add(new SqlFilterDefComponent("a1.table_name", stringManager.getString("table-name"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-table-referenced-from-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableReferencedFrom, buttonActions, menuActions, "oracle-table-referenced-from-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableReferencedFromPanel-title");
  }
  
  private void refreshTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableReferencedFrom.getQuery().close();
      tableReferencedFrom.getQuery().setSqlText(Sql.getTableReferencedFromList(filter.getSqlText()));
      tableReferencedFrom.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableReferencedFrom.getQuery().paramByName("table_name").setString(currentTableName);
      tableReferencedFrom.getQuery().open();
      if (!tableReferencedFrom.getQuery().isEmpty()) {
        tableReferencedFrom.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentTableName = objectName;
      if (isVisible()) {
        refresh();
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
    closing = true;
    tableReferencedFrom.getQuery().close();
    accesibilities = null;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmFilter = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuEnableDisableRelated = new javax.swing.JMenuItem();
    cmEnableDisableRelated = new pl.mpak.sky.gui.swing.Action();
    statusBarIndexes = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarIndexes = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableReferencedFrom = new ViewTable();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    menuEnableDisableRelated.setAction(cmEnableDisableRelated);
    menuActions.add(menuEnableDisableRelated);

    cmEnableDisableRelated.setActionCommandKey("cmEnableDisableRelated");
    cmEnableDisableRelated.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/enable_all.gif")); // NOI18N
    cmEnableDisableRelated.setText(stringManager.getString("cmEnableDisableRelated-text")); // NOI18N
    cmEnableDisableRelated.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEnableDisableRelatedActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    statusBarIndexes.setShowFieldType(false);
    statusBarIndexes.setShowOpenTime(false);
    statusBarIndexes.setTable(tableReferencedFrom);
    add(statusBarIndexes, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarIndexes.setFloatable(false);
    toolBarIndexes.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(buttonFilter);
    toolBarIndexes.add(jSeparator1);
    toolBarIndexes.add(buttonActions);

    jPanel1.add(toolBarIndexes);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableReferencedFrom);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmEnableDisableRelatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEnableDisableRelatedActionPerformed
  if (SqlCodeWizardDialog.show(new EnableTableRelatedConstraintWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmEnableDisableRelatedActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmEnableDisableRelated;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuEnableDisableRelated;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarIndexes;
  private ViewTable tableReferencedFrom;
  private javax.swing.JToolBar toolBarIndexes;
  // End of variables declaration//GEN-END:variables
  
}

package pl.mpak.orbada.derbydb.tables;

import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.AlterTableNullWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintCheckWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
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
public class TableColumnsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean isView;
  private boolean requestRefresh = false;
  private SqlFilter filter;
  
  private Timer timer;
  
  /** Creates new form TableColumns
   * @param accesibilities
   */
  public TableColumnsPanel(IViewAccesibilities accesibilities) {
    this(accesibilities, false);
  }
  
  public TableColumnsPanel(IViewAccesibilities accesibilities, boolean isView) {
    this.accesibilities = accesibilities;
    this.isView = isView;
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
    OrbadaDerbyDbPlugin.getRefreshQueue().add(timer);
    
    tableColumns.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableColumns.getSelectedRow();
        if (rowIndex >= 0 && tableColumns.getQuery().isActive()) {
          try {
            tableColumns.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableColumns.getQuery().setDatabase(getDatabase());
    try {
      tableColumns.addColumn(new QueryTableColumn("columnnumber", stringManager.getString("TableColumnsPanel-lp"), 30));
      tableColumns.addColumn(new QueryTableColumn("columnname", stringManager.getString("TableColumnsPanel-column-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableColumns.addColumn(new QueryTableColumn("columndatatype", stringManager.getString("TableColumnsPanel-column-type"), 150, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      if (!isView) {
        tableColumns.addColumn(new QueryTableColumn("columndefault", stringManager.getString("TableColumnsPanel-default-value"), 300));
        tableColumns.addColumn(new QueryTableColumn("autoincrementvalue", stringManager.getString("TableColumnsPanel-autoinc-value"), 30));
        tableColumns.addColumn(new QueryTableColumn("autoincrementstart", stringManager.getString("TableColumnsPanel-autoinc-start"), 30));
        tableColumns.addColumn(new QueryTableColumn("autoincrementinc", stringManager.getString("TableColumnsPanel-autoinc-next"), 30));
      }
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("c.columnname", stringManager.getString("TableColumnsPanel-column-name"), (String[])null));
      def.add(new SqlFilterDefComponent("varchar( c.columndatatype, 512 )", stringManager.getString("TableColumnsPanel-column-type"), new String[] {"", "'VARCHAR%'", "'NUMERIC%'", "'INTEGER%'", "'TIMESTAMP%'"}));
      def.add(new SqlFilterDefComponent("varchar( c.columndefault, 512 )", stringManager.getString("TableColumnsPanel-default-value"), (String[])null));
      def.add(new SqlFilterDefComponent("c.autoincrementvalue is not null", stringManager.getString("TableColumnsPanel-autoincremental")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-" +(isView ? "view" : "table") +"-columns-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableColumns, buttonActions, menuActions, "derbydb-table-columns-actions");
    if (isView) {
      cmColumnNotNull.setEnabled(false);
      cmCreateConstraintCheck.setEnabled(false);
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableColumnsPanel-title");
  }
  
  public void refresh() {
    try {
      String columnName = null;
      requestRefresh = false;
      if (tableColumns.getQuery().isActive() && tableColumns.getSelectedRow() >= 0) {
        tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
        columnName = tableColumns.getQuery().fieldByName("COLUMNNAME").getString();
      }
      tableColumns.getQuery().close();
      tableColumns.getQuery().setSqlText(DerbyDbSql.getTableColumnList(filter.getSqlText()));
      tableColumns.getQuery().paramByName("schemaname").setString(currentSchemaName);
      tableColumns.getQuery().paramByName("tablename").setString(currentTableName);
      tableColumns.getQuery().open();
      if (!tableColumns.getQuery().isEmpty()) {
        if (columnName != null && tableColumns.getQuery().locate("columnname", new Variant(columnName))) {
          tableColumns.changeSelection(tableColumns.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableColumns.changeSelection(0, 0);
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
    tableColumns.getQuery().close();
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
    cmColumnNotNull = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintCheck = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableColumns = new ViewTable();
    statusBarColumns = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarColumns = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonColumnNotNull = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonCreateConstraintCheck = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/derbydb/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmColumnNotNull.setActionCommandKey("cmColumnNotNull");
    cmColumnNotNull.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/lock16.gif")); // NOI18N
    cmColumnNotNull.setText(stringManager.getString("TableColumnsPanel-cmColumnNotNull-text")); // NOI18N
    cmColumnNotNull.setTooltip(stringManager.getString("TableColumnsPanel-cmColumnNotNull-hint")); // NOI18N
    cmColumnNotNull.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColumnNotNullActionPerformed(evt);
      }
    });

    cmCreateConstraintCheck.setActionCommandKey("cmCreateConstraintCheck");
    cmCreateConstraintCheck.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/verify_document_16.gif")); // NOI18N
    cmCreateConstraintCheck.setText(stringManager.getString("TableColumnsPanel-cmCreateConstraintCheck-text")); // NOI18N
    cmCreateConstraintCheck.setTooltip(stringManager.getString("TableColumnsPanel-cmCreateConstraintCheck-hint")); // NOI18N
    cmCreateConstraintCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintCheckActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableColumns);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarColumns.setShowFieldType(false);
    statusBarColumns.setShowOpenTime(false);
    statusBarColumns.setTable(tableColumns);
    add(statusBarColumns, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarColumns.setFloatable(false);
    toolBarColumns.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonFilter);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarColumns.add(jSeparator1);

    buttonColumnNotNull.setAction(cmColumnNotNull);
    buttonColumnNotNull.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonColumnNotNull.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonColumnNotNull);

    buttonCreateConstraintCheck.setAction(cmCreateConstraintCheck);
    buttonCreateConstraintCheck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintCheck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonCreateConstraintCheck);
    toolBarColumns.add(jSeparator2);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonActions);

    jPanel1.add(toolBarColumns);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmCreateConstraintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintCheckActionPerformed
  if (tableColumns.getSelectedRow() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
      String columnName = tableColumns.getQuery().fieldByName("COLUMNNAME").getString();
      SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmCreateConstraintCheckActionPerformed

private void cmColumnNotNullActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColumnNotNullActionPerformed
  if (tableColumns.getSelectedRow() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
      String columnName = tableColumns.getQuery().fieldByName("COLUMNNAME").getString();
      SqlCodeWizardDialog.show(new AlterTableNullWizardPanel(getDatabase(), currentSchemaName, currentTableName, columnName, "ALTER"), true);
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmColumnNotNullActionPerformed

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

  private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
    refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  refresh(null, currentSchemaName, currentTableName);
}//GEN-LAST:event_formComponentShown


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonColumnNotNull;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmColumnNotNull;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarColumns;
  private ViewTable tableColumns;
  private javax.swing.JToolBar toolBarColumns;
  // End of variables declaration//GEN-END:variables
  
}

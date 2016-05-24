package pl.mpak.orbada.firebird.gui.tables;

import java.awt.Dialog;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.freezing.TableFreezeViewService;
import pl.mpak.orbada.firebird.gui.wizards.CommentWizard;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class TablesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  
  public TablesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
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
        refreshTabbedPanes();
      }
    };
    OrbadaFirebirdPlugin.getRefreshQueue().add(timer);

    tabbedPane = new TableTabbedPane(accesibilities);
    splitTables.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-tables-panel");
    splitTables.setDividerLocation(settings.getValue("split-location", (long)splitTables.getDividerLocation()).intValue());
    currentSchemaName = "USER";
    
    tableTables.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
      }
    });
    
    tableTables.getQuery().setDatabase(getDatabase());
    tableTables.addColumn(new QueryTableColumn("TABLE_NAME", stringManager.getString("TablesPanelView-table-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableTables.addColumn(new QueryTableColumn("DESCRIPTION", stringManager.getString("comment"), 200));
    tableTables.addColumn(new QueryTableColumn("OWNER_NAME", stringManager.getString("TablesPanelView-owner"), 100));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("TABLE_NAME", stringManager.getString("TablesPanelView-table-name"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-tables-filter"),
      cmFilter, buttonFilter, 
      def);
    
    new ComponentActionsAction(getDatabase(), tableTables, buttonActions, menuActions, "firebird-tables-actions");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableTables.requestFocusInWindow();
      }
    });
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase("USER")) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
        buttonSelectSchema.setSelected(true);
      }
      else {
        buttonSelectSchema.setSelected(false);
        accesibilities.setTabTitle(tabTitle);
      }
    }
  }
  
  private void refreshTabbedPanes() {
    String tableName = "";
    int rowIndex = tableTables.getSelectedRow();
    if (rowIndex >= 0) {
      try {
        tableTables.getQuery().getRecord(rowIndex);
        tableName = tableTables.getQuery().fieldByName("table_name").getString();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    tabbedPane.refresh(null, null, tableName);
  }
  
  private void refreshTableListTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    if (!isVisible() || viewClosing) {
      return;
    }
    try {
      String tableName = null;
      if (tableTables.getQuery().isActive() && tableTables.getSelectedRow() >= 0) {
        tableTables.getQuery().getRecord(tableTables.getSelectedRow());
        tableName = tableTables.getQuery().fieldByName("TABLE_NAME").getString();
      }
      refresh(tableName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableTables.getSelectedColumn();
      int index = Math.max(0, tableTables.getSelectedRow());
      tableTables.getQuery().close();
      tableTables.getQuery().setSqlText(Sql.getTableList(filter.getSqlText()));
      tableTables.getQuery().paramByName("SYSTEM_FLAG").setString(currentSchemaName);
      tableTables.getQuery().open();
      if (objectName != null && tableTables.getQuery().locate("TABLE_NAME", new Variant(objectName))) {
        tableTables.changeSelection(tableTables.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableTables.getQuery().isEmpty()) {
        tableTables.changeSelection(Math.min(index, tableTables.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      refreshTabbedPanes();
    }
  }

  public boolean canClose() {
    return tabbedPane.canClose();
  }

  public void close() throws IOException {
    timer.cancel();
    settings.setValue("split-location", (long)splitTables.getDividerLocation());
    viewClosing = true;
    tabbedPane.close();
    tableTables.getQuery().close();
    accesibilities = null;
    settings.store();
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    cmFilter = new pl.mpak.sky.gui.swing.Action();
    cmDropTable = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCommentTable = new javax.swing.JMenuItem();
    menuRecordCount = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropTable = new javax.swing.JMenuItem();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmRecordCount = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    splitTables = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTables = new pl.mpak.orbada.gui.comps.table.ViewTable();
    jPanel1 = new javax.swing.JPanel();
    toolBarTables = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFreezeObject = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel2 = new javax.swing.JPanel();
    statusBarTables = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmDropTable.setActionCommandKey("cmDropTable");
    cmDropTable.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTable.setText(stringManager.getString("TablesPanelView-cmDropTable-text")); // NOI18N
    cmDropTable.setTooltip(stringManager.getString("TablesPanelView-cmDropTable-hint")); // NOI18N
    cmDropTable.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTableActionPerformed(evt);
      }
    });

    menuCommentTable.setAction(cmComment);
    menuActions.add(menuCommentTable);

    menuRecordCount.setAction(cmRecordCount);
    menuActions.add(menuRecordCount);
    menuActions.add(jSeparator2);

    menuDropTable.setAction(cmDropTable);
    menuActions.add(menuDropTable);

    cmComment.setActionCommandKey("cmComment");
    cmComment.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif")); // NOI18N
    cmComment.setText(stringManager.getString("cmComment-text")); // NOI18N
    cmComment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentActionPerformed(evt);
      }
    });

    cmRecordCount.setActionCommandKey("cmRecordCount");
    cmRecordCount.setText(stringManager.getString("TablesPanelView-cmRecordCount-text")); // NOI18N
    cmRecordCount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRecordCountActionPerformed(evt);
      }
    });

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    splitTables.setBorder(null);
    splitTables.setDividerLocation(200);
    splitTables.setContinuousLayout(true);
    splitTables.setOneTouchExpandable(true);

    panelTables.setPreferredSize(new java.awt.Dimension(250, 100));
    panelTables.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTables);

    panelTables.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarTables.setFloatable(false);
    toolBarTables.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonRefresh);

    buttonSelectSchema.setAction(cmSelectSchema);
    buttonSelectSchema.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSelectSchema.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonSelectSchema);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonFilter);

    buttonFreezeObject.setAction(cmFreezeObject);
    buttonFreezeObject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFreezeObject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonFreezeObject);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarTables.add(jSeparator1);
    toolBarTables.add(buttonActions);

    jPanel1.add(toolBarTables);

    panelTables.add(jPanel1, java.awt.BorderLayout.NORTH);

    jPanel2.setLayout(new java.awt.BorderLayout());

    statusBarTables.setShowFieldType(false);
    statusBarTables.setShowFieldValue(false);
    statusBarTables.setShowOpenTime(false);
    statusBarTables.setTable(tableTables);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    splitTables.setLeftComponent(panelTables);

    add(splitTables, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmDropTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTableActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TablesPanelView-drop-table-q"), new Object[] {tableTables.getQuery().fieldByName("TABLE_NAME").getString()}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop table " +SQLUtil.createSqlName(tableTables.getQuery().fieldByName("TABLE_NAME").getString()), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropTableActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableTables.getQuery().isActive()) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void cmSelectSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectSchemaActionPerformed
  if (StringUtil.equalsIgnoreCase(currentSchemaName, "USER")) {
    setCurrentSchemaName("SYSTEM");
  }
  else {
    setCurrentSchemaName("USER");
  }
  refresh();
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableListTask();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableTables.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectionModel().getLeadSelectionIndex());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new CommentWizard(getDatabase(), tableName, "RDB$RELATIONS"), true) != null) {
        refresh(tableName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmRecordCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRecordCountActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    Query query = getDatabase().createQuery();
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      query.open("select count( 0 ) cnt from " +SQLUtil.createSqlName(tableName));
      MessageBox.show(this, stringManager.getString("TablesPanelView-record-count"), String.format(stringManager.getString("TablesPanelView-record-count-info"), new Object[] {SQLUtil.createSqlName(tableName, query.fieldByName("cnt").getString())}), ModalResult.OK, MessageBox.INFORMATION);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    } finally {
      query.close();
    }
  }
}//GEN-LAST:event_cmRecordCountActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      final String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      accesibilities.createView(new TableFreezeViewService(accesibilities, null, tableName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmDropTable;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRecordCount;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCommentTable;
  private javax.swing.JMenuItem menuDropTable;
  private javax.swing.JMenuItem menuRecordCount;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane splitTables;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableTables;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables
  
}

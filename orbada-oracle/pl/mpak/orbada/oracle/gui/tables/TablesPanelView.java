package pl.mpak.orbada.oracle.gui.tables;

import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.table.CommentTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.CopyTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateTriggerPKColumnWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.DropTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.LockTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.RenameTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.TruncateTableWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.gui.freezing.TableFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateExternalTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateNormalTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateTemporaryTableWizard;
import pl.mpak.orbada.oracle.services.OracleSettingsProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class TablesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private ISettings connectionSettings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  private boolean refreshing = false;
  
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
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    tabbedPane = new TableTabbedPane(accesibilities);
    splitTables.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tables-panel");
    connectionSettings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), OracleSettingsProvider.settingsName);
    splitTables.setDividerLocation(settings.getValue("split-location", (long)splitTables.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableTables.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          timer.restart();
        }
      }
    });
    
    tableTables.getQuery().setDatabase(getDatabase());
    tableTables.addColumn(new QueryTableColumn("table_name", stringManager.getString("table-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableTables.addColumn(new QueryTableColumn("remarks", stringManager.getString("comment"), 200));
    tableTables.addColumn(new QueryTableColumn("cluster_name", stringManager.getString("cluster-name"), 100));
    tableTables.addColumn(new QueryTableColumn("tablespace_name", stringManager.getString("tablespace-name"), 100));
    tableTables.addColumn(new QueryTableColumn("temporary", stringManager.getString("temporary"), 50));
    tableTables.addColumn(new QueryTableColumn("cache", stringManager.getString("is-cached"), 50));
    tableTables.addColumn(new QueryTableColumn("table_type", stringManager.getString("table-type"), 70));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("tbl.table_name", stringManager.getString("table-name"), (String[])null));
    def.add(new SqlFilterDefComponent("tbl.cluster_name", stringManager.getString("cluster-name"), (String[])null));
    def.add(new SqlFilterDefComponent("tbl.tablespace_name", stringManager.getString("tablespace-name"), (String[])null));
    def.add(new SqlFilterDefComponent("tcm.comments", stringManager.getString("comment"), (String[])null));
    def.add(new SqlFilterDefComponent("tbl.temporary = 'Y'", stringManager.getString("is-temporary")));
    def.add(new SqlFilterDefComponent("trim(tbl.cache) = 'Y'", stringManager.getString("is-cached")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tables-filter"),
      cmFilter, buttonFilter, 
      def);
    
    new ComponentActionsAction(getDatabase(), tableTables, buttonActions, menuActions, "oracle-tables-actions");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableTables.requestFocusInWindow();
      }
    });
  }

  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(OracleDbInfoProvider.getCurrentSchema(getDatabase()))) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
      }
      else {
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
        final String remarks = tableTables.getQuery().fieldByName("remarks").getString();
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            textRemarks.setText(remarks);
          }
        });
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else {
      textRemarks.setText("");
    }
    tabbedPane.refresh(null, currentSchemaName, tableName);
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
  
  public void refresh(String tableName) {
    refreshing = true;
    try {
      int column = tableTables.getSelectedColumn();
      int index = Math.max(0, tableTables.getSelectedRow());
      tableTables.getQuery().close();
      tableTables.getQuery().setSqlText(Sql.getTableList(filter.getSqlText(), connectionSettings.getValue(OracleSettingsProvider.setSolveTableMetadataProblem, false)));
      tableTables.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableTables.getQuery().open();
      if (tableName != null && tableTables.getQuery().locate("TABLE_NAME", new Variant(tableName))) {
        tableTables.changeSelection(tableTables.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableTables.getQuery().isEmpty()) {
        tableTables.changeSelection(Math.min(index, tableTables.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      refreshing = false;
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
    connectionSettings.store();
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
        menuCreateTable = new javax.swing.JMenuItem();
        menuCreateExternalTable = new javax.swing.JMenuItem();
        menuCreateTemporaryTable = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        menuCommentTable = new javax.swing.JMenuItem();
        menuRecordCount = new javax.swing.JMenuItem();
        menuCreatePKColumnTrigger = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        menuLockTable = new javax.swing.JMenuItem();
        menuRenameTable = new javax.swing.JMenuItem();
        menuCopyTable = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        menuTruncateTable = new javax.swing.JMenuItem();
        menuDropTable = new javax.swing.JMenuItem();
        cmComment = new pl.mpak.sky.gui.swing.Action();
        cmRecordCount = new pl.mpak.sky.gui.swing.Action();
        cmCreatePKColumnTrigger = new pl.mpak.sky.gui.swing.Action();
        cmTruncateTable = new pl.mpak.sky.gui.swing.Action();
        cmLockTable = new pl.mpak.sky.gui.swing.Action();
        cmRenameTable = new pl.mpak.sky.gui.swing.Action();
        cmCopyTable = new pl.mpak.sky.gui.swing.Action();
        cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
        cmCreateTable = new pl.mpak.sky.gui.swing.Action();
        cmCreateExternalTable = new pl.mpak.sky.gui.swing.Action();
        cmCreateTemporaryTable = new pl.mpak.sky.gui.swing.Action();
        splitTables = new javax.swing.JSplitPane();
        panelTables = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        toolBarTables = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFreezeObject = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JSeparator();
        buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableTables = new pl.mpak.orbada.gui.comps.table.ViewTable();
        jPanel2 = new javax.swing.JPanel();
        statusBarTables = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        textRemarks = new pl.mpak.sky.gui.swing.comp.TextArea();

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
        cmDropTable.setText(stringManager.getString("cmDropTable-text")); // NOI18N
        cmDropTable.setTooltip(stringManager.getString("cmDropTable-hint")); // NOI18N
        cmDropTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDropTableActionPerformed(evt);
            }
        });

        menuCreateTable.setAction(cmCreateTable);
        menuActions.add(menuCreateTable);

        menuCreateExternalTable.setAction(cmCreateExternalTable);
        menuActions.add(menuCreateExternalTable);

        menuCreateTemporaryTable.setAction(cmCreateTemporaryTable);
        menuActions.add(menuCreateTemporaryTable);
        menuActions.add(jSeparator5);

        menuCommentTable.setAction(cmComment);
        menuActions.add(menuCommentTable);

        menuRecordCount.setAction(cmRecordCount);
        menuActions.add(menuRecordCount);

        menuCreatePKColumnTrigger.setAction(cmCreatePKColumnTrigger);
        menuActions.add(menuCreatePKColumnTrigger);
        menuActions.add(jSeparator3);

        menuLockTable.setAction(cmLockTable);
        menuActions.add(menuLockTable);

        menuRenameTable.setAction(cmRenameTable);
        menuActions.add(menuRenameTable);

        menuCopyTable.setAction(cmCopyTable);
        menuActions.add(menuCopyTable);
        menuActions.add(jSeparator4);

        menuTruncateTable.setAction(cmTruncateTable);
        menuActions.add(menuTruncateTable);

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
        cmRecordCount.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/counts.gif")); // NOI18N
        cmRecordCount.setText(stringManager.getString("cmRecordCount-text")); // NOI18N
        cmRecordCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmRecordCountActionPerformed(evt);
            }
        });

        cmCreatePKColumnTrigger.setActionCommandKey("cmCreatePKColumnTrigger");
        cmCreatePKColumnTrigger.setText(stringManager.getString("cmCreatePKColumnTrigger-text")); // NOI18N
        cmCreatePKColumnTrigger.setTooltip(stringManager.getString("cmCreatePKColumnTrigger-hint")); // NOI18N
        cmCreatePKColumnTrigger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCreatePKColumnTriggerActionPerformed(evt);
            }
        });

        cmTruncateTable.setActionCommandKey("cmTruncateTable");
        cmTruncateTable.setText(stringManager.getString("cmTruncateTable-text")); // NOI18N
        cmTruncateTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmTruncateTableActionPerformed(evt);
            }
        });

        cmLockTable.setActionCommandKey("cmLockTable");
        cmLockTable.setText(stringManager.getString("cmLockTable-text")); // NOI18N
        cmLockTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmLockTableActionPerformed(evt);
            }
        });

        cmRenameTable.setActionCommandKey("cmRenameTable");
        cmRenameTable.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/rename.gif")); // NOI18N
        cmRenameTable.setText(stringManager.getString("cmRenameTable-text")); // NOI18N
        cmRenameTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmRenameTableActionPerformed(evt);
            }
        });

        cmCopyTable.setActionCommandKey("cmCopyTable");
        cmCopyTable.setText(stringManager.getString("cmCopyTable-text")); // NOI18N
        cmCopyTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCopyTableActionPerformed(evt);
            }
        });

        cmFreezeObject.setActionCommandKey("cmFreezeObject");
        cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
        cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
        cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmFreezeObjectActionPerformed(evt);
            }
        });

        cmCreateTable.setActionCommandKey("cmCreateTable");
        cmCreateTable.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/table.gif")); // NOI18N
        cmCreateTable.setText(stringManager.getString("cmCreateTable-text")); // NOI18N
        cmCreateTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCreateTableActionPerformed(evt);
            }
        });

        cmCreateExternalTable.setActionCommandKey("cmCreateExternalTable");
        cmCreateExternalTable.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/table.gif")); // NOI18N
        cmCreateExternalTable.setText(stringManager.getString("cmCreateExternalTable-text")); // NOI18N
        cmCreateExternalTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCreateExternalTableActionPerformed(evt);
            }
        });

        cmCreateTemporaryTable.setActionCommandKey("cmCreateTemporaryTable");
        cmCreateTemporaryTable.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/table.gif")); // NOI18N
        cmCreateTemporaryTable.setText(stringManager.getString("cmCreateTemporaryTable-text")); // NOI18N
        cmCreateTemporaryTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCreateTemporaryTableActionPerformed(evt);
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

        jScrollPane1.setViewportView(tableTables);

        panelTables.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        statusBarTables.setShowFieldType(false);
        statusBarTables.setShowFieldValue(false);
        statusBarTables.setShowOpenTime(false);
        statusBarTables.setTable(tableTables);
        jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(166, 50));

        textRemarks.setColumns(20);
        textRemarks.setEditable(false);
        textRemarks.setLineWrap(true);
        textRemarks.setRows(5);
        textRemarks.setFocusable(false);
        textRemarks.setFont(new java.awt.Font("Courier New", 0, 11));
        jScrollPane2.setViewportView(textRemarks);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

        splitTables.setLeftComponent(panelTables);

        add(splitTables, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void cmDropTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTableActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      if (SqlCodeWizardDialog.show(new DropTableWizard(getDatabase(), currentSchemaName, tableTables.getQuery().fieldByName("table_name").getString()), true) != null) {
        refresh(null);
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
  Query query = getDatabase().createQuery();
  try {
    query.open(Sql.getSchemaList());
    Vector<String> vl = QueryUtil.staticData("{schema_name}", query);
    Point point = buttonSelectSchema.getLocationOnScreen();
    point.y+= buttonSelectSchema.getHeight();
    SimpleSelectDialog.select((Window)SwingUtil.getWindowComponent(this), point.x, point.y, vl, vl.indexOf(currentSchemaName), new SimpleSelectDialog.CallBack() {
      public void selected(Object o) {
        setCurrentSchemaName(o.toString());
        refresh();
      }
    });
  }
  catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
  finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableListTask();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableTables.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectionModel().getLeadSelectionIndex());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new CommentTableWizard(getDatabase(), currentSchemaName, tableName), true) != null) {
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
      String schemaName = tableTables.getQuery().fieldByName("schema_name").getString();
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      query.open("select /*+ INDEX_FFS( " +tableName +" ) */ count( 0 ) cnt from " +SQLUtil.createSqlName(schemaName, tableName));
      MessageBox.show(this, stringManager.getString("record-count"), String.format(stringManager.getString("TablesPanelView-record-count-info"), new Object[] {SQLUtil.createSqlName(schemaName, tableName), query.fieldByName("cnt").getString()}), ModalResult.OK, MessageBox.INFORMATION);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    } finally {
      query.close();
    }
  }
}//GEN-LAST:event_cmRecordCountActionPerformed

private void cmCreatePKColumnTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreatePKColumnTriggerActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      SqlCodeWizardDialog.show(new CreateTriggerPKColumnWizard(getDatabase(), currentSchemaName, tableName), true);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCreatePKColumnTriggerActionPerformed

private void cmTruncateTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmTruncateTableActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new TruncateTableWizard(getDatabase(), currentSchemaName, tableName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmTruncateTableActionPerformed

private void cmLockTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmLockTableActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      SqlCodeWizardDialog.show(new LockTableWizard(getDatabase(), currentSchemaName, tableName), true);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmLockTableActionPerformed

private void cmRenameTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameTableActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new RenameTableWizard(getDatabase(), currentSchemaName, tableName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmRenameTableActionPerformed

private void cmCopyTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCopyTableActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new CopyTableWizard(getDatabase(), currentSchemaName, tableName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCopyTableActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableTables.getSelectedRow() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectedRow());
      final String tableName = tableTables.getQuery().fieldByName("table_name").getString();
      accesibilities.createView(new TableFreezeViewService(accesibilities, currentSchemaName, tableName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmCreateTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTableActionPerformed
  if (SqlCodeWizardDialog.show(new CreateNormalTableWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateTableActionPerformed

private void cmCreateExternalTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateExternalTableActionPerformed
  if (SqlCodeWizardDialog.show(new CreateExternalTableWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateExternalTableActionPerformed

private void cmCreateTemporaryTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTemporaryTableActionPerformed
  if (SqlCodeWizardDialog.show(new CreateTemporaryTableWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateTemporaryTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
    private pl.mpak.sky.gui.swing.Action cmComment;
    private pl.mpak.sky.gui.swing.Action cmCopyTable;
    private pl.mpak.sky.gui.swing.Action cmCreateExternalTable;
    private pl.mpak.sky.gui.swing.Action cmCreatePKColumnTrigger;
    private pl.mpak.sky.gui.swing.Action cmCreateTable;
    private pl.mpak.sky.gui.swing.Action cmCreateTemporaryTable;
    private pl.mpak.sky.gui.swing.Action cmDropTable;
    private pl.mpak.sky.gui.swing.Action cmFilter;
    private pl.mpak.sky.gui.swing.Action cmFreezeObject;
    private pl.mpak.sky.gui.swing.Action cmLockTable;
    private pl.mpak.sky.gui.swing.Action cmRecordCount;
    private pl.mpak.sky.gui.swing.Action cmRefresh;
    private pl.mpak.sky.gui.swing.Action cmRenameTable;
    private pl.mpak.sky.gui.swing.Action cmSelectSchema;
    private pl.mpak.sky.gui.swing.Action cmTruncateTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JPopupMenu menuActions;
    private javax.swing.JMenuItem menuCommentTable;
    private javax.swing.JMenuItem menuCopyTable;
    private javax.swing.JMenuItem menuCreateExternalTable;
    private javax.swing.JMenuItem menuCreatePKColumnTrigger;
    private javax.swing.JMenuItem menuCreateTable;
    private javax.swing.JMenuItem menuCreateTemporaryTable;
    private javax.swing.JMenuItem menuDropTable;
    private javax.swing.JMenuItem menuLockTable;
    private javax.swing.JMenuItem menuRecordCount;
    private javax.swing.JMenuItem menuRenameTable;
    private javax.swing.JMenuItem menuTruncateTable;
    private javax.swing.JPanel panelTables;
    private javax.swing.JSplitPane splitTables;
    private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
    private pl.mpak.orbada.gui.comps.table.ViewTable tableTables;
    private pl.mpak.sky.gui.swing.comp.TextArea textRemarks;
    private javax.swing.JToolBar toolBarTables;
    // End of variables declaration//GEN-END:variables
  
}

package pl.mpak.orbada.oracle.gui.indexes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.CoalasceIndexWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropIndexWizard;
import pl.mpak.orbada.oracle.gui.wizards.RenameIndexWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.CreateIndexWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
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
public class IndexesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  private boolean refreshing = false;
  
  public IndexesPanelView(IViewAccesibilities accesibilities) {
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

    tabbedPane = new OrbadaTabbedPane("INDEX",
      new Component[] {
        new IndexColumnsPanel(accesibilities),
        new IndexDetailsPanel(accesibilities),
        new IndexSizePanel(accesibilities),
        new IndexPartitionsPanel(accesibilities),
        new IndexSourcePanel(accesibilities)
    });
    splitTables.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-indexes-panel");
    splitTables.setDividerLocation(settings.getValue("split-location", (long)splitTables.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableIndexes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          timer.restart();
        }
      }
    });
    
    tableIndexes.getQuery().setDatabase(getDatabase());
    tableIndexes.addColumn(new QueryTableColumn("index_name", stringManager.getString("index-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableIndexes.addColumn(new QueryTableColumn("table_name", stringManager.getString("table-name"), 150));
    tableIndexes.addColumn(new QueryTableColumn("uniqueness", stringManager.getString("uniqueness"), 50));
    tableIndexes.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
      public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
        if (StringUtil.nvl((String)value, "").equals("VALID")) {
          ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
        }
        else if (StringUtil.nvl((String)value, "").equals("INVALID")) {
          ((JLabel)renderer).setForeground(Color.RED);
        }
      }
    })));
    tableIndexes.addColumn(new QueryTableColumn("index_type", stringManager.getString("index-type"), 60));
    tableIndexes.addColumn(new QueryTableColumn("constraint", stringManager.getString("constraint"), 60));
    tableIndexes.addColumn(new QueryTableColumn("initial_extent", stringManager.getString("initial-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("next_extent", stringManager.getString("next-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("min_extents", stringManager.getString("min-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("max_extents", stringManager.getString("max-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("pct_increase", stringManager.getString("pct-increase"), 50));
    tableIndexes.addColumn(new QueryTableColumn("pct_free", stringManager.getString("pct-free"), 50));
    tableIndexes.addColumn(new QueryTableColumn("tablespace_name", stringManager.getString("tablespace-name"), 100));
    SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("i.index_name", stringManager.getString("index-name"), (String[])null));
      def.add(new SqlFilterDefComponent("i.tablespace_name", stringManager.getString("tablespace-name"), (String[])null));
      def.add(new SqlFilterDefComponent("i.index_type", stringManager.getString("index-type"), new String[] {"", "'NORMAL'"}));
      def.add(new SqlFilterDefComponent("i.uniqueness = 'UNIQUE'", stringManager.getString("unique")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-indexes-filter"),
      cmFilter, buttonFilter, 
      def);
    
    new ComponentActionsAction(getDatabase(), tableIndexes, buttonActions, menuActions, "oracle-indexes-actions");

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableIndexes.requestFocusInWindow();
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
    String indexName = "";
    int rowIndex = tableIndexes.getSelectedRow();
    if (rowIndex >= 0 && tableIndexes.getQuery().isActive()) {
      try {
        tableIndexes.getQuery().getRecord(rowIndex);
        indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    tabbedPane.refresh(null, currentSchemaName, indexName);
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
      String indexName = null;
      if (tableIndexes.getQuery().isActive() && tableIndexes.getSelectedRow() >= 0) {
        tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
        indexName = tableIndexes.getQuery().fieldByName("INDEX_NAME").getString();
      }
      refresh(indexName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    refreshing = true;
    try {
      int column = tableIndexes.getSelectedColumn();
      int index = Math.max(0, tableIndexes.getSelectedRow());
      tableIndexes.getQuery().close();
      tableIndexes.getQuery().setSqlText(Sql.getAllIndexList(filter.getSqlText()));
      tableIndexes.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableIndexes.getQuery().open();
      if (objectName != null && tableIndexes.getQuery().locate("INDEX_NAME", new Variant(objectName))) {
        tableIndexes.changeSelection(tableIndexes.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableIndexes.getQuery().isEmpty()) {
        tableIndexes.changeSelection(Math.min(index, tableIndexes.getRowCount() -1), column);
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
    tableIndexes.getQuery().close();
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
    menuActions = new javax.swing.JPopupMenu();
    menuCreateIndex = new javax.swing.JMenuItem();
    menuCoalasceIndex = new javax.swing.JMenuItem();
    menuRenameIndex = new javax.swing.JMenuItem();
    menuSimpleCreateIndex = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropIndex = new javax.swing.JMenuItem();
    cmDropIndex = new pl.mpak.sky.gui.swing.Action();
    cmSimpleCreateIndex = new pl.mpak.sky.gui.swing.Action();
    cmCreateIndex = new pl.mpak.sky.gui.swing.Action();
    cmCoalasceIndex = new pl.mpak.sky.gui.swing.Action();
    cmRenameIndex = new pl.mpak.sky.gui.swing.Action();
    splitTables = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableIndexes = new ViewTable();
    jPanel1 = new javax.swing.JPanel();
    toolBarTables = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
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

    menuCreateIndex.setAction(cmCreateIndex);
    menuActions.add(menuCreateIndex);

    menuCoalasceIndex.setAction(cmCoalasceIndex);
    menuActions.add(menuCoalasceIndex);

    menuRenameIndex.setAction(cmRenameIndex);
    menuActions.add(menuRenameIndex);

    menuSimpleCreateIndex.setAction(cmSimpleCreateIndex);
    menuActions.add(menuSimpleCreateIndex);
    menuActions.add(jSeparator3);

    menuDropIndex.setAction(cmDropIndex);
    menuActions.add(menuDropIndex);

    cmDropIndex.setActionCommandKey("cmDropIndex");
    cmDropIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropIndex.setText(stringManager.getString("cmDropIndex-text")); // NOI18N
    cmDropIndex.setTooltip(stringManager.getString("cmDropIndex-hint")); // NOI18N
    cmDropIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropIndexActionPerformed(evt);
      }
    });

    cmSimpleCreateIndex.setActionCommandKey("cmSimpleCreateIndex");
    cmSimpleCreateIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/index.gif")); // NOI18N
    cmSimpleCreateIndex.setText(stringManager.getString("cmSimpleCreateIndex-text")); // NOI18N
    cmSimpleCreateIndex.setTooltip(stringManager.getString("cmSimpleCreateIndex-hint")); // NOI18N
    cmSimpleCreateIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSimpleCreateIndexActionPerformed(evt);
      }
    });

    cmCreateIndex.setActionCommandKey("cmCreateIndex");
    cmCreateIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/index.gif")); // NOI18N
    cmCreateIndex.setText(stringManager.getString("cmCreateIndex-text")); // NOI18N
    cmCreateIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateIndexActionPerformed(evt);
      }
    });

    cmCoalasceIndex.setActionCommandKey("cmCoalasceIndex");
    cmCoalasceIndex.setText(stringManager.getString("cmCoalasceIndex-text")); // NOI18N
    cmCoalasceIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCoalasceIndexActionPerformed(evt);
      }
    });

    cmRenameIndex.setActionCommandKey("cmCoalasceIndex");
    cmRenameIndex.setText(stringManager.getString("cmRenameIndex-text")); // NOI18N
    cmRenameIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRenameIndexActionPerformed(evt);
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

    jScrollPane1.setViewportView(tableIndexes);

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

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarTables.add(jSeparator1);
    toolBarTables.add(buttonActions);

    jPanel1.add(toolBarTables);

    panelTables.add(jPanel1, java.awt.BorderLayout.NORTH);

    jPanel2.setLayout(new java.awt.BorderLayout());

    statusBarTables.setShowFieldType(false);
    statusBarTables.setShowFieldValue(false);
    statusBarTables.setShowOpenTime(false);
    statusBarTables.setTable(tableIndexes);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    splitTables.setLeftComponent(panelTables);

    add(splitTables, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableIndexes.getQuery().isActive()) {
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

private void cmDropIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      String tableName = tableIndexes.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new DropIndexWizard(getDatabase(), currentSchemaName, tableName, indexName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropIndexActionPerformed

private void cmSimpleCreateIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSimpleCreateIndexActionPerformed
  if (SqlCodeWizardDialog.show(new CreateIndexWizardPanel(getDatabase(), currentSchemaName, null), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmSimpleCreateIndexActionPerformed

private void cmCreateIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateIndexActionPerformed
  if (SqlCodeWizardDialog.show(new pl.mpak.orbada.oracle.gui.wizards.CreateIndexWizard(getDatabase(), currentSchemaName, null), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateIndexActionPerformed

private void cmCoalasceIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCoalasceIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      String tableName = tableIndexes.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new CoalasceIndexWizard(getDatabase(), currentSchemaName, tableName, indexName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCoalasceIndexActionPerformed

private void cmRenameIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      String tableName = tableIndexes.getQuery().fieldByName("table_name").getString();
      if (SqlCodeWizardDialog.show(new RenameIndexWizard(getDatabase(), currentSchemaName, tableName, indexName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmRenameIndexActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCoalasceIndex;
  private pl.mpak.sky.gui.swing.Action cmCreateIndex;
  private pl.mpak.sky.gui.swing.Action cmDropIndex;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRenameIndex;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmSimpleCreateIndex;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCoalasceIndex;
  private javax.swing.JMenuItem menuCreateIndex;
  private javax.swing.JMenuItem menuDropIndex;
  private javax.swing.JMenuItem menuRenameIndex;
  private javax.swing.JMenuItem menuSimpleCreateIndex;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane splitTables;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private ViewTable tableIndexes;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables
  
}

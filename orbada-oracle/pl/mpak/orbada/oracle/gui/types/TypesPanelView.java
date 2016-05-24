package pl.mpak.orbada.oracle.gui.types;

import java.awt.Color;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.gui.freezing.TypeFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.CompileAllObjectsWizard;
import pl.mpak.orbada.oracle.gui.wizards.type.CreateObjectTypeWizard;
import pl.mpak.orbada.oracle.gui.wizards.type.CreateTableTypeWizard;
import pl.mpak.orbada.oracle.gui.wizards.type.CreateVarrayTypeWizard;
import pl.mpak.orbada.oracle.gui.wizards.type.DropTypeWizard;
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
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
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
public class TypesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  private boolean refreshing = false;
  
  public TypesPanelView(IViewAccesibilities accesibilities) {
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

    tabbedPane = new TypeTabbedPane(accesibilities);
    split.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-types-panel");
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableTypes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          if (lastIndex != tableTypes.getSelectedRow() && !tabbedPane.canClose()) {
            tableTypes.changeSelection(lastIndex, tableTypes.getSelectedColumn());
            return;
          }
          lastIndex = tableTypes.getSelectedRow();
          timer.restart();
        }
      }
    });
    
    tableTypes.getQuery().setDatabase(getDatabase());
    tableTypes.addColumn(new QueryTableColumn("type_name", stringManager.getString("type-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableTypes.addColumn(new QueryTableColumn("TYPECODE", stringManager.getString("type-code"), 70));
    QueryTableCellRendererFilter rf = new QueryTableCellRendererFilter() {
      public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
        if (StringUtil.nvl((String)value, "").equals("VALID")) {
          ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
        }
        else if (StringUtil.nvl((String)value, "").equals("INVALID")) {
          ((JLabel)renderer).setForeground(Color.RED);
        }
        else if (StringUtil.nvl((String)value, "").equals("DEBUG")) {
          ((JLabel)renderer).setForeground(SwingUtil.Color.NAVY);
        }
      }
    };
    tableTypes.addColumn(new QueryTableColumn("HEAD_STATUS", stringManager.getString("head-status"), 60, new QueryTableCellRenderer(rf)));
    tableTypes.addColumn(new QueryTableColumn("BODY_STATUS", stringManager.getString("body-status"), 60, new QueryTableCellRenderer(rf)));
    tableTypes.addColumn(new QueryTableColumn("HEAD_CREATED", stringManager.getString("head-created"), 110));
    tableTypes.addColumn(new QueryTableColumn("BODY_CREATED", stringManager.getString("body-created"), 110));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("type_name", stringManager.getString("type-name"), (String[])null));
    def.add(new SqlFilterDefComponent("TYPECODE", stringManager.getString("type-code"), (String[])null));
    def.add(new SqlFilterDefComponent("(HEAD_STATUS = 'INVALID' or BODY_STATUS = 'INVALID')", stringManager.getString("invalid")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-types-filter"),
      cmFilter, buttonFilter, 
      def);
    
    SwingUtil.addAction(tableTypes, cmCompile);
    new ComponentActionsAction(getDatabase(), tableTypes, buttonActions, menuActions, "oracle-types-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableTypes.requestFocusInWindow();
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
    String typeName = "";
    int rowIndex = tableTypes.getSelectedRow();
    if (rowIndex >= 0 && tableTypes.getQuery().isActive()) {
      try {
        tableTypes.getQuery().getRecord(rowIndex);
        typeName = tableTypes.getQuery().fieldByName("type_name").getString();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    tabbedPane.refresh(null, currentSchemaName, typeName);
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
      String typeName = null;
      if (tableTypes.getQuery().isActive() && tableTypes.getSelectedRow() >= 0) {
        tableTypes.getQuery().getRecord(tableTypes.getSelectedRow());
        typeName = tableTypes.getQuery().fieldByName("TYPE_NAME").getString();
      }
      refresh(typeName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    refreshing = true;
    try {
      int column = tableTypes.getSelectedColumn();
      int index = Math.max(0, tableTypes.getSelectedRow());
      tableTypes.getQuery().close();
      tableTypes.getQuery().setSqlText(Sql.getTypeList(filter.getSqlText(), OracleDbInfoProvider.instance.getMajorVersion(getDatabase())));
      tableTypes.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableTypes.getQuery().open();
      if (objectName != null && tableTypes.getQuery().locate("TYPE_NAME", new Variant( objectName))) {
        tableTypes.changeSelection(tableTypes.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableTypes.getQuery().isEmpty()) {
        tableTypes.changeSelection(Math.min(index, tableTypes.getRowCount() -1), column);
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
    settings.setValue("split-location", (long)split.getDividerLocation());
    viewClosing = true;
    tabbedPane.close();
    tableTypes.getQuery().close();
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
    menuCreateObjectType = new javax.swing.JMenuItem();
    menuCreateTableType = new javax.swing.JMenuItem();
    menuCreateVarrayType = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuCompileAll = new javax.swing.JMenuItem();
    menuCompileAllInvalid = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropType = new javax.swing.JMenuItem();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmDropType = new pl.mpak.sky.gui.swing.Action();
    cmCreateObjectType = new pl.mpak.sky.gui.swing.Action();
    cmCreateTableType = new pl.mpak.sky.gui.swing.Action();
    cmCreateVarrayType = new pl.mpak.sky.gui.swing.Action();
    cmCompileAll = new pl.mpak.sky.gui.swing.Action();
    cmCompileAllInvalid = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTypes = new pl.mpak.orbada.gui.comps.table.ViewTable();
    jPanel1 = new javax.swing.JPanel();
    toolBarTables = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFreezeObject = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonCompile = new pl.mpak.sky.gui.swing.comp.ToolButton();
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

    menuCreateObjectType.setAction(cmCreateObjectType);
    menuActions.add(menuCreateObjectType);

    menuCreateTableType.setAction(cmCreateTableType);
    menuActions.add(menuCreateTableType);

    menuCreateVarrayType.setAction(cmCreateVarrayType);
    menuActions.add(menuCreateVarrayType);
    menuActions.add(jSeparator3);

    menuCompileAll.setAction(cmCompileAll);
    menuActions.add(menuCompileAll);

    menuCompileAllInvalid.setAction(cmCompileAllInvalid);
    menuActions.add(menuCompileAllInvalid);
    menuActions.add(jSeparator2);

    menuDropType.setAction(cmDropType);
    menuActions.add(menuDropType);

    cmCompile.setActionCommandKey("cmCompile");
    cmCompile.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmCompile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute16.gif")); // NOI18N
    cmCompile.setText(stringManager.getString("cmCompile-text")); // NOI18N
    cmCompile.setTooltip(stringManager.getString("cmCompile-hint")); // NOI18N
    cmCompile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileActionPerformed(evt);
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

    cmDropType.setActionCommandKey("cmDropType");
    cmDropType.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropType.setText(stringManager.getString("cmDropType-text")); // NOI18N
    cmDropType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTypeActionPerformed(evt);
      }
    });

    cmCreateObjectType.setActionCommandKey("cmCreateObjectType");
    cmCreateObjectType.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/type.gif")); // NOI18N
    cmCreateObjectType.setText(stringManager.getString("cmCreateObjectType-text")); // NOI18N
    cmCreateObjectType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateObjectTypeActionPerformed(evt);
      }
    });

    cmCreateTableType.setActionCommandKey("cmCreateTableType");
    cmCreateTableType.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/type.gif")); // NOI18N
    cmCreateTableType.setText(stringManager.getString("cmCreateTableType-text")); // NOI18N
    cmCreateTableType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateTableTypeActionPerformed(evt);
      }
    });

    cmCreateVarrayType.setActionCommandKey("cmCreateVarrayType");
    cmCreateVarrayType.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/type.gif")); // NOI18N
    cmCreateVarrayType.setText(stringManager.getString("cmCreateVarrayType-text")); // NOI18N
    cmCreateVarrayType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateVarrayTypeActionPerformed(evt);
      }
    });

    cmCompileAll.setActionCommandKey("cmCompileAll");
    cmCompileAll.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute_all.gif")); // NOI18N
    cmCompileAll.setText(stringManager.getString("cmCompileAll-text")); // NOI18N
    cmCompileAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileAllActionPerformed(evt);
      }
    });

    cmCompileAllInvalid.setActionCommandKey("cmCompileAllInvalid");
    cmCompileAllInvalid.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute_all_invalid.gif")); // NOI18N
    cmCompileAllInvalid.setText(stringManager.getString("cmCompileAllInvalid-text")); // NOI18N
    cmCompileAllInvalid.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileAllInvalidActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    split.setBorder(null);
    split.setDividerLocation(200);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    panelTables.setPreferredSize(new java.awt.Dimension(250, 100));
    panelTables.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTypes);

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

    buttonCompile.setAction(cmCompile);
    buttonCompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonCompile);
    toolBarTables.add(buttonActions);

    jPanel1.add(toolBarTables);

    panelTables.add(jPanel1, java.awt.BorderLayout.NORTH);

    jPanel2.setLayout(new java.awt.BorderLayout());

    statusBarTables.setShowFieldType(false);
    statusBarTables.setShowFieldValue(false);
    statusBarTables.setShowOpenTime(false);
    statusBarTables.setTable(tableTypes);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    split.setLeftComponent(panelTables);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableTypes.getQuery().isActive()) {
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
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileActionPerformed
  if (tableTypes.getSelectedRow() >= 0) {
    try {
      tableTypes.getQuery().getRecord(tableTypes.getSelectedRow());
      String typeName = tableTypes.getQuery().fieldByName("type_name").getString();
      getDatabase().executeCommand(
        "ALTER TYPE " +SQLUtil.createSqlName(currentSchemaName, typeName) +" COMPILE" +
        (OracleDbInfoProvider.instance.isDebugClauseNeeded(getDatabase()) ? " DEBUG" : "")
      );
      refresh();
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableTypes.getSelectedRow() >= 0) {
    try {
      tableTypes.getQuery().getRecord(tableTypes.getSelectedRow());
      final String typeName = tableTypes.getQuery().fieldByName("type_name").getString();
      accesibilities.createView(new TypeFreezeViewService(accesibilities, currentSchemaName, typeName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmDropTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTypeActionPerformed
  if (tableTypes.getSelectedRow() >= 0) {
    try {
      tableTypes.getQuery().getRecord(tableTypes.getSelectedRow());
      String typeName = tableTypes.getQuery().fieldByName("type_name").getString();
      if (SqlCodeWizardDialog.show(new DropTypeWizard(getDatabase(), currentSchemaName, typeName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropTypeActionPerformed

private void cmCreateObjectTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateObjectTypeActionPerformed
  if (SqlCodeWizardDialog.show(new CreateObjectTypeWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateObjectTypeActionPerformed

private void cmCreateTableTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTableTypeActionPerformed
  if (SqlCodeWizardDialog.show(new CreateTableTypeWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateTableTypeActionPerformed

private void cmCreateVarrayTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateVarrayTypeActionPerformed
  if (SqlCodeWizardDialog.show(new CreateVarrayTypeWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateVarrayTypeActionPerformed

private void cmCompileAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllActionPerformed
SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "TYPE", null), true);
}//GEN-LAST:event_cmCompileAllActionPerformed

private void cmCompileAllInvalidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllInvalidActionPerformed
SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "TYPE", "INVALID"), true);
}//GEN-LAST:event_cmCompileAllInvalidActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCompile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCompile;
  private pl.mpak.sky.gui.swing.Action cmCompileAll;
  private pl.mpak.sky.gui.swing.Action cmCompileAllInvalid;
  private pl.mpak.sky.gui.swing.Action cmCreateObjectType;
  private pl.mpak.sky.gui.swing.Action cmCreateTableType;
  private pl.mpak.sky.gui.swing.Action cmCreateVarrayType;
  private pl.mpak.sky.gui.swing.Action cmDropType;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCompileAll;
  private javax.swing.JMenuItem menuCompileAllInvalid;
  private javax.swing.JMenuItem menuCreateObjectType;
  private javax.swing.JMenuItem menuCreateTableType;
  private javax.swing.JMenuItem menuCreateVarrayType;
  private javax.swing.JMenuItem menuDropType;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableTypes;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables
  
}

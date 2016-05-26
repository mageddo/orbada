package pl.mpak.orbada.oracle.gui.triggers;

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

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateTableTriggerWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateTriggerPKColumnWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropTriggerWizard;
import pl.mpak.orbada.oracle.gui.wizards.EnableTriggerWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.gui.freezing.TriggerFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.CompileAllObjectsWizard;
import pl.mpak.orbada.oracle.gui.wizards.view.CreateViewTriggerWizard;
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
public class TriggersPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
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
  
  public TriggersPanelView(IViewAccesibilities accesibilities) {
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

    tabbedPane = new TriggerTabbedPane(accesibilities);
    splitTables.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-triggers-panel");
    splitTables.setDividerLocation(settings.getValue("split-location", (long)splitTables.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableTriggers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          TriggerSourcePanel panel = (TriggerSourcePanel)tabbedPane.getComponent(TriggerSourcePanel.class);
          if (panel != null && lastIndex != tableTriggers.getSelectedRow() && !panel.canClose()) {
            tableTriggers.changeSelection(lastIndex, tableTriggers.getSelectedColumn());
            return;
          }
          lastIndex = tableTriggers.getSelectedRow();
          timer.restart();
        }
      }
    });
    
    tableTriggers.getQuery().setDatabase(getDatabase());
    tableTriggers.addColumn(new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableTriggers.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
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
    })));
    tableTriggers.addColumn(new QueryTableColumn("enabled", stringManager.getString("enabled"), 70, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
      public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
        if (StringUtil.nvl((String)value, "").equals("DISABLED")) {
          ((JLabel)renderer).setForeground(Color.DARK_GRAY);
        }
      }
    })));
    tableTriggers.addColumn(new QueryTableColumn("table_owner", stringManager.getString("table-schema"), 100));
    tableTriggers.addColumn(new QueryTableColumn("table_name", stringManager.getString("table-name"), 100));
    tableTriggers.addColumn(new QueryTableColumn("trigger_type", stringManager.getString("trigger-type"), 120));
    tableTriggers.addColumn(new QueryTableColumn("triggering_event", stringManager.getString("triggeriing-event"), 120));
    tableTriggers.addColumn(new QueryTableColumn("created", stringManager.getString("created"), 110));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("allt.trigger_name", stringManager.getString("trigger-name"), (String[])null));
    def.add(new SqlFilterDefComponent("allt.status = 'DISABLED'", stringManager.getString("are-disabled")));
    def.add(new SqlFilterDefComponent("allo.status = 'INVALID'", stringManager.getString("invalid")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-triggers-filter"),
      cmFilter, buttonFilter, 
      def);
    
    SwingUtil.addAction(tableTriggers, cmCompile);
    new ComponentActionsAction(getDatabase(), tableTriggers, buttonActions, menuActions, "oracle-triggers-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableTriggers.requestFocusInWindow();
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
    String triggerName = "";
    int rowIndex = tableTriggers.getSelectedRow();
    if (rowIndex >= 0 && tableTriggers.getQuery().isActive()) {
      try {
        tableTriggers.getQuery().getRecord(rowIndex);
        triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    tabbedPane.refresh(null, currentSchemaName, triggerName);
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
      String triggerName = null;
      if (tableTriggers.getQuery().isActive() && tableTriggers.getSelectedRow() >= 0) {
        tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
        triggerName = tableTriggers.getQuery().fieldByName("TRIGGER_NAME").getString();
      }
      refresh(triggerName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String triggerName) {
    refreshing = true;
    try {
      int column = tableTriggers.getSelectedColumn();
      int index = Math.max(0, tableTriggers.getSelectedRow());
      tableTriggers.getQuery().close();
      tableTriggers.getQuery().setSqlText(Sql.getAllTriggerList(filter.getSqlText(), OracleDbInfoProvider.instance.getMajorVersion(getDatabase())));
      tableTriggers.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableTriggers.getQuery().open();
      if ( triggerName != null && tableTriggers.getQuery().locate("TRIGGER_NAME", new Variant( triggerName))) {
        tableTriggers.changeSelection(tableTriggers.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableTriggers.getQuery().isEmpty()) {
        tableTriggers.changeSelection(Math.min(index, tableTriggers.getRowCount() -1), column);
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
    tableTriggers.getQuery().close();
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
    menuEnable = new javax.swing.JMenuItem();
    menuCreatePKColumnTrigger = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuCreateTableTrigger = new javax.swing.JMenuItem();
    menuCreateViewTrigger = new javax.swing.JMenuItem();
    jSeparator4 = new javax.swing.JSeparator();
    menuCompileAll = new javax.swing.JMenuItem();
    menuCompileAllInvalid = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropTrigger = new javax.swing.JMenuItem();
    cmDropTrigger = new pl.mpak.sky.gui.swing.Action();
    cmCreatePKColumnTrigger = new pl.mpak.sky.gui.swing.Action();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    cmEnableTrigger = new pl.mpak.sky.gui.swing.Action();
    cmCreateTableTrigger = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmCreateViewTrigger = new pl.mpak.sky.gui.swing.Action();
    cmCompileAll = new pl.mpak.sky.gui.swing.Action();
    cmCompileAllInvalid = new pl.mpak.sky.gui.swing.Action();
    splitTables = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTriggers = new ViewTable();
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

    menuEnable.setAction(cmEnableTrigger);
    menuActions.add(menuEnable);

    menuCreatePKColumnTrigger.setAction(cmCreatePKColumnTrigger);
    menuActions.add(menuCreatePKColumnTrigger);
    menuActions.add(jSeparator2);

    menuCreateTableTrigger.setAction(cmCreateTableTrigger);
    menuActions.add(menuCreateTableTrigger);

    menuCreateViewTrigger.setAction(cmCreateViewTrigger);
    menuActions.add(menuCreateViewTrigger);
    menuActions.add(jSeparator4);

    menuCompileAll.setAction(cmCompileAll);
    menuActions.add(menuCompileAll);

    menuCompileAllInvalid.setAction(cmCompileAllInvalid);
    menuActions.add(menuCompileAllInvalid);
    menuActions.add(jSeparator3);

    menuDropTrigger.setAction(cmDropTrigger);
    menuActions.add(menuDropTrigger);

    cmDropTrigger.setActionCommandKey("cmDropTrigger");
    cmDropTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTrigger.setText(stringManager.getString("cmDropTrigger-text")); // NOI18N
    cmDropTrigger.setTooltip(stringManager.getString("cmDropTrigger-hint")); // NOI18N
    cmDropTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTriggerActionPerformed(evt);
      }
    });

    cmCreatePKColumnTrigger.setActionCommandKey("cmCreatePKColumnTrigger");
    cmCreatePKColumnTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif")); // NOI18N
    cmCreatePKColumnTrigger.setText(stringManager.getString("cmCreatePKColumnTrigger-text")); // NOI18N
    cmCreatePKColumnTrigger.setTooltip(stringManager.getString("cmCreatePKColumnTrigger-hint")); // NOI18N
    cmCreatePKColumnTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreatePKColumnTriggerActionPerformed(evt);
      }
    });

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

    cmEnableTrigger.setActionCommandKey("cmEnableTrigger");
    cmEnableTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmEnableTrigger.setText(stringManager.getString("cmEnableTrigger-text")); // NOI18N
    cmEnableTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEnableTriggerActionPerformed(evt);
      }
    });

    cmCreateTableTrigger.setActionCommandKey("cmCreateTableTrigger");
    cmCreateTableTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif")); // NOI18N
    cmCreateTableTrigger.setText(stringManager.getString("cmCreateTableTrigger-text")); // NOI18N
    cmCreateTableTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateTableTriggerActionPerformed(evt);
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

    cmCreateViewTrigger.setActionCommandKey("cmCreateViewTrigger");
    cmCreateViewTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif")); // NOI18N
    cmCreateViewTrigger.setText(stringManager.getString("cmCreateViewTrigger-text")); // NOI18N
    cmCreateViewTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateViewTriggerActionPerformed(evt);
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

    splitTables.setBorder(null);
    splitTables.setDividerLocation(200);
    splitTables.setContinuousLayout(true);
    splitTables.setOneTouchExpandable(true);

    panelTables.setPreferredSize(new java.awt.Dimension(250, 100));
    panelTables.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTriggers);

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
    statusBarTables.setTable(tableTriggers);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    splitTables.setLeftComponent(panelTables);

    add(splitTables, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableTriggers.getQuery().isActive()) {
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

private void cmDropTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (SqlCodeWizardDialog.show(new DropTriggerWizard(getDatabase(), currentSchemaName, triggerName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropTriggerActionPerformed

private void cmCreatePKColumnTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreatePKColumnTriggerActionPerformed
  try {
    if (SqlCodeWizardDialog.show(new CreateTriggerPKColumnWizard(getDatabase(), currentSchemaName, null), true) != null) {
      refresh();
    }
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmCreatePKColumnTriggerActionPerformed

private void cmCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      getDatabase().executeCommand(
        "ALTER TRIGGER " +SQLUtil.createSqlName(currentSchemaName, triggerName) +" COMPILE" +
        (OracleDbInfoProvider.instance.isDebugClauseNeeded(getDatabase()) ? " DEBUG" : "")
      );
      refresh();
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed

private void cmEnableTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEnableTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (SqlCodeWizardDialog.show(new EnableTriggerWizard(getDatabase(), currentSchemaName, null, triggerName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmEnableTriggerActionPerformed

private void cmCreateTableTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTableTriggerActionPerformed
  if (SqlCodeWizardDialog.show(new CreateTableTriggerWizard(getDatabase(), currentSchemaName, null), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateTableTriggerActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      final String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      accesibilities.createView(new TriggerFreezeViewService(accesibilities, currentSchemaName, triggerName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmCreateViewTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateViewTriggerActionPerformed
  if (SqlCodeWizardDialog.show(new CreateViewTriggerWizard(getDatabase(), currentSchemaName, null), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateViewTriggerActionPerformed

private void cmCompileAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllActionPerformed
SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "TRIGGER", null), true);
}//GEN-LAST:event_cmCompileAllActionPerformed

private void cmCompileAllInvalidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllInvalidActionPerformed
SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "TRIGGER", "INVALID"), true);
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
  private pl.mpak.sky.gui.swing.Action cmCreatePKColumnTrigger;
  private pl.mpak.sky.gui.swing.Action cmCreateTableTrigger;
  private pl.mpak.sky.gui.swing.Action cmCreateViewTrigger;
  private pl.mpak.sky.gui.swing.Action cmDropTrigger;
  private pl.mpak.sky.gui.swing.Action cmEnableTrigger;
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
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCompileAll;
  private javax.swing.JMenuItem menuCompileAllInvalid;
  private javax.swing.JMenuItem menuCreatePKColumnTrigger;
  private javax.swing.JMenuItem menuCreateTableTrigger;
  private javax.swing.JMenuItem menuCreateViewTrigger;
  private javax.swing.JMenuItem menuDropTrigger;
  private javax.swing.JMenuItem menuEnable;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane splitTables;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private ViewTable tableTriggers;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables
  
}

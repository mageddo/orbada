package pl.mpak.orbada.firebird.gui.triggers;

import java.awt.Component;
import java.awt.Dialog;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.freezing.TriggerFreezeViewService;
import pl.mpak.orbada.firebird.gui.wizards.CreateTriggerWizard;
import pl.mpak.orbada.firebird.util.SourceCreator;
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
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author  akaluza
 */
public class TriggersPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  
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
    OrbadaFirebirdPlugin.getRefreshQueue().add(timer);

    tabbedPane = new TriggerTabbedPane(accesibilities);
    splitTables.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-triggers-panel");
    splitTables.setDividerLocation(settings.getValue("split-location", (long)splitTables.getDividerLocation()).intValue());
    currentSchemaName = "USER";
    
    tableTriggers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        TriggerSourcePanel panel = (TriggerSourcePanel)tabbedPane.getComponent(TriggerSourcePanel.class);
        if (panel != null && lastIndex != tableTriggers.getSelectedRow() && !panel.canClose()) {
          tableTriggers.changeSelection(lastIndex, tableTriggers.getSelectedColumn());
          return;
        }
        lastIndex = tableTriggers.getSelectedRow();
        timer.restart();
      }
    });
    
    tableTriggers.getQuery().setDatabase(getDatabase());
    tableTriggers.addColumn(new QueryTableColumn("TRIGGER_NAME", stringManager.getString("TriggersPanelView-trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableTriggers.addColumn(new QueryTableColumn("RDB$TRIGGER_TYPE", stringManager.getString("TriggersPanelView-when"), 120, new QueryTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setForeground(tableTriggers.getForeground());
        label.setHorizontalAlignment(JLabel.LEADING);
        if (value instanceof Variant) {
          try {
            label.setText(SourceCreator.decodeTriggerType(null, ((Variant)value).getInteger()));
          } catch (VariantException ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          label.setText(SourceCreator.decodeTriggerType(null, (Short)value));
        }
        return label;
      }
    }));
    tableTriggers.addColumn(new QueryTableColumn("TABLE_NAME", stringManager.getString("TriggersPanelView-table-name"), 150));
    tableTriggers.addColumn(new QueryTableColumn("TRIGGER_SEQUENCE", stringManager.getString("TriggersPanelView-sequence"), 60));
    tableTriggers.addColumn(new QueryTableColumn("TRIGGER_ACTIVE", stringManager.getString("TriggersPanelView-active"), 60));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("TRIGGER_NAME", stringManager.getString("TriggersPanelView-trigger-name"), (String[])null));
    def.add(new SqlFilterDefComponent("TABLE_NAME", stringManager.getString("TriggersPanelView-table-name"), (String[])null));
    def.add(new SqlFilterDefComponent("TRIGGER_ACTIVE <> 'Y'", stringManager.getString("TriggersPanelView-inactived")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-triggers-filter"),
      cmFilter, buttonFilter, 
      def);
    
    new ComponentActionsAction(getDatabase(), tableTriggers, buttonActions, menuActions, "firebird-triggers-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableTriggers.requestFocusInWindow();
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
  
  public void refresh(String objectName) {
    try {
      int column = tableTriggers.getSelectedColumn();
      int index = Math.max(0, tableTriggers.getSelectedRow());
      tableTriggers.getQuery().close();
      tableTriggers.getQuery().setSqlText(Sql.getAllTriggerList(filter.getSqlText()));
      tableTriggers.getQuery().paramByName("SYSTEM_FLAG").setString(currentSchemaName);
      tableTriggers.getQuery().open();
      if (objectName != null && tableTriggers.getQuery().locate("TRIGGER_NAME", new Variant( objectName))) {
        tableTriggers.changeSelection(tableTriggers.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableTriggers.getQuery().isEmpty()) {
        tableTriggers.changeSelection(Math.min(index, tableTriggers.getRowCount() -1), column);
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
    jMenuItem1 = new javax.swing.JMenuItem();
    menuCreateTrigger = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropTrigger = new javax.swing.JMenuItem();
    cmDropTrigger = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmCreateTrigger = new pl.mpak.sky.gui.swing.Action();
    cmActiveTrigger = new pl.mpak.sky.gui.swing.Action();
    splitTables = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTriggers = new pl.mpak.orbada.gui.comps.table.ViewTable();
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

    jMenuItem1.setAction(cmActiveTrigger);
    menuActions.add(jMenuItem1);

    menuCreateTrigger.setAction(cmCreateTrigger);
    menuActions.add(menuCreateTrigger);
    menuActions.add(jSeparator2);

    menuDropTrigger.setAction(cmDropTrigger);
    menuActions.add(menuDropTrigger);

    cmDropTrigger.setActionCommandKey("cmDropTrigger");
    cmDropTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTrigger.setText(stringManager.getString("TriggersPanelView-cmDropTrigger-text")); // NOI18N
    cmDropTrigger.setTooltip(stringManager.getString("TriggersPanelView-cmDropTrigger-hint")); // NOI18N
    cmDropTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTriggerActionPerformed(evt);
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

    cmCreateTrigger.setActionCommandKey("cmCreateTrigger");
    cmCreateTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif")); // NOI18N
    cmCreateTrigger.setText(stringManager.getString("TriggersPanelView-cmCreateTrigger-text")); // NOI18N
    cmCreateTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateTriggerActionPerformed(evt);
      }
    });

    cmActiveTrigger.setActionCommandKey("cmActiveTrigger");
    cmActiveTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmActiveTrigger.setText(stringManager.getString("TriggersPanelView-cmActiveTrigger-text")); // NOI18N
    cmActiveTrigger.setTooltip(stringManager.getString("TriggersPanelView-cmActiveTrigger-hint")); // NOI18N
    cmActiveTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmActiveTriggerActionPerformed(evt);
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
  if (StringUtil.equalsIgnoreCase(currentSchemaName, "USER")) {
    setCurrentSchemaName("SYSTEM");
  }
  else {
    setCurrentSchemaName("USER");
  }
  refresh();
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmDropTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TriggersPanelView-drop-trigger-q"), new Object[] {triggerName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop trigger " +SQLUtil.createSqlName(triggerName), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropTriggerActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      final String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      accesibilities.createView(new TriggerFreezeViewService(accesibilities, null, triggerName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmCreateTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTriggerActionPerformed
  SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new CreateTriggerWizard(getDatabase(), null), true);
  if (result != null) {
    refresh(result.getResultMap().get("object_name"));
  }
}//GEN-LAST:event_cmCreateTriggerActionPerformed

private void cmActiveTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmActiveTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (StringUtil.toBoolean(tableTriggers.getQuery().fieldByName("trigger_active").getString())) {
        if (MessageBox.show((Dialog)null, stringManager.getString("TriggersPanelView-trigger"), String.format(stringManager.getString("TriggersPanelView-inactive-trigger-q"), new Object[] {triggerName}), ModalResult.YESNO) == ModalResult.YES) {
          getDatabase().createCommand("alter trigger " +SQLUtil.createSqlName(triggerName) +" INACTIVE", true);
          refresh();
        }
      }
      else {
        if (MessageBox.show((Dialog)null, stringManager.getString("TriggersPanelView-trigger"), String.format(stringManager.getString("TriggersPanelView-active-trigger-q"), new Object[] {triggerName}), ModalResult.YESNO) == ModalResult.YES) {
          getDatabase().createCommand("alter trigger " +SQLUtil.createSqlName(triggerName) +" ACTIVE", true);
          refresh();
        }
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmActiveTriggerActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmActiveTrigger;
  private pl.mpak.sky.gui.swing.Action cmCreateTrigger;
  private pl.mpak.sky.gui.swing.Action cmDropTrigger;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCreateTrigger;
  private javax.swing.JMenuItem menuDropTrigger;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane splitTables;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableTriggers;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables
  
}

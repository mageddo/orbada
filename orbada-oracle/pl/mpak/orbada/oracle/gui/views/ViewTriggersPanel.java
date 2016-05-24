package pl.mpak.orbada.oracle.gui.views;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.util.SourceCreator;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.freezing.TriggerFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.view.CreateViewTriggerWizard;
import pl.mpak.orbada.oracle.gui.wizards.view.DropViewTriggerWizard;
import pl.mpak.orbada.oracle.gui.wizards.view.EnableViewTriggerWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ViewTriggersPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentViewName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private ISettings settings;
  
  /** Creates new form TableTriggersPanel
   * @param accesibilities
   */
  public ViewTriggersPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-view-triggers-panel");
    splitTriggers.setDividerLocation(settings.getValue("split-location", (long)splitTriggers.getDividerLocation()).intValue());
    
    textTrigger.setDatabase(getDatabase());
    textTrigger.setEditable(false);
    tableTriggers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      String triggerName;
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableTriggers.getSelectedRow();
        if (rowIndex >= 0 && tableTriggers.getQuery().isActive()) {
          try {
            tableTriggers.getQuery().getRecord(rowIndex);
            if (triggerName == null || !triggerName.equals(tableTriggers.getQuery().fieldByName("trigger_name").getString())) {
              updateTriggerBody();
              triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          textTrigger.setText("");
        }
      }
    });
    
    tableTriggers.getQuery().setDatabase(getDatabase());
    try {
      tableTriggers.addColumn(new QueryTableColumn("schema_name", stringManager.getString("schema"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableTriggers.addColumn(new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableTriggers.addColumn(new QueryTableColumn("trigger_type", stringManager.getString("trigger-type"), 120));
      tableTriggers.addColumn(new QueryTableColumn("triggering_event", stringManager.getString("triggering-event"), 120));
      tableTriggers.addColumn(new QueryTableColumn("enabled", stringManager.getString("enabled"), 80, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("DISABLED")) {
            ((JLabel)renderer).setForeground(Color.DARK_GRAY);
          }
        }
      })));
      tableTriggers.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 80, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
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
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("allt.trigger_name", stringManager.getString("trigger-name"), (String[])null));
      def.add(new SqlFilterDefComponent("allt.status = 'DISABLED'", stringManager.getString("disabled")));
      def.add(new SqlFilterDefComponent("allo.status = 'INVALID'", stringManager.getString("invalid")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-view-triggers-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableTriggers, buttonActions, menuActions, "oracle-view-triggers-actions");
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
      int column = tableTriggers.getSelectedColumn();
      int index = Math.max(0, tableTriggers.getSelectedRow());
      String triggerName = null;
      if (tableTriggers.getQuery().isActive() && tableTriggers.getSelectedRow() >= 0) {
        tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
        triggerName = tableTriggers.getQuery().fieldByName("TRIGGER_NAME").getString();
      }
      tableTriggers.getQuery().close();
      tableTriggers.getQuery().setSqlText(Sql.getTableTriggerList(filter.getSqlText()));
      tableTriggers.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableTriggers.getQuery().paramByName("table_name").setString(currentViewName);
      tableTriggers.getQuery().open();
      if (triggerName != null && tableTriggers.getQuery().locate("TRIGGER_NAME", new Variant( triggerName))) {
        tableTriggers.changeSelection(tableTriggers.getQuery().getCurrentRecord().getIndex(), column);
        updateTriggerBody();
      } else if (!tableTriggers.getQuery().isEmpty()) {
        tableTriggers.changeSelection(Math.min(index, tableTriggers.getRowCount() -1), column);
        updateTriggerBody();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void updateTriggerBody() {
    try {
      new SourceCreator(getDatabase(), textTrigger).getSource(currentSchemaName, "TRIGGER", tableTriggers.getQuery().fieldByName("trigger_name").getString());
    }
    catch (Exception ex) {
      textTrigger.setDatabaseObject(null, null, null, "");
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("ViewTriggersPanel-title");
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentViewName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentViewName = objectName;
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
    settings.setValue("split-location", (long)splitTriggers.getDividerLocation());
    settings.store();
    tableTriggers.getQuery().close();
    textTrigger.setDatabase(null);
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
    cmDropTrigger = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuEnable = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuCreateTrigger = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropTrigger = new javax.swing.JMenuItem();
    cmEnableTrigger = new pl.mpak.sky.gui.swing.Action();
    cmCreateTrigger = new pl.mpak.sky.gui.swing.Action();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    jPanel2 = new javax.swing.JPanel();
    toolBarTriggers = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonCompile = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton4 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator5 = new javax.swing.JToolBar.Separator();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator6 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    splitTriggers = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTriggers = new pl.mpak.orbada.gui.comps.table.ViewTable();
    statusBarTriggers = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    textTrigger = new pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
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

    cmDropTrigger.setActionCommandKey("cmDropTrigger");
    cmDropTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTrigger.setText(stringManager.getString("cmDropTrigger-text")); // NOI18N
    cmDropTrigger.setTooltip(stringManager.getString("cmDropTrigger-hint")); // NOI18N
    cmDropTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTriggerActionPerformed(evt);
      }
    });

    menuEnable.setAction(cmEnableTrigger);
    menuActions.add(menuEnable);
    menuActions.add(jSeparator2);

    menuCreateTrigger.setAction(cmCreateTrigger);
    menuActions.add(menuCreateTrigger);
    menuActions.add(jSeparator3);

    menuDropTrigger.setAction(cmDropTrigger);
    menuActions.add(menuDropTrigger);

    cmEnableTrigger.setActionCommandKey("cmEnableTrigger");
    cmEnableTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmEnableTrigger.setText(stringManager.getString("cmEnableTrigger-text")); // NOI18N
    cmEnableTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEnableTriggerActionPerformed(evt);
      }
    });

    cmCreateTrigger.setActionCommandKey("cmCreateTrigger");
    cmCreateTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif")); // NOI18N
    cmCreateTrigger.setText(stringManager.getString("cmCreateTrigger-text")); // NOI18N
    cmCreateTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateTriggerActionPerformed(evt);
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

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
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

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarTriggers.setFloatable(false);
    toolBarTriggers.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonFilter);
    toolBarTriggers.add(jSeparator1);

    buttonCompile.setAction(cmCompile);
    buttonCompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonCompile);

    toolButton4.setAction(cmFreezeObject);
    toolButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton4);
    toolBarTriggers.add(jSeparator5);

    toolButton2.setAction(cmEnableTrigger);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton2);

    toolButton1.setAction(cmCreateTrigger);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton1);
    toolBarTriggers.add(jSeparator4);

    toolButton3.setAction(cmDropTrigger);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton3);
    toolBarTriggers.add(jSeparator6);
    toolBarTriggers.add(buttonActions);

    jPanel2.add(toolBarTriggers);

    add(jPanel2, java.awt.BorderLayout.NORTH);

    splitTriggers.setBorder(null);
    splitTriggers.setDividerLocation(350);
    splitTriggers.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splitTriggers.setContinuousLayout(true);
    splitTriggers.setOneTouchExpandable(true);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTriggers);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarTriggers.setShowFieldType(false);
    statusBarTriggers.setShowOpenTime(false);
    statusBarTriggers.setTable(tableTriggers);
    jPanel1.add(statusBarTriggers, java.awt.BorderLayout.SOUTH);

    splitTriggers.setLeftComponent(jPanel1);

    textTrigger.setPreferredSize(new java.awt.Dimension(81, 150));
    splitTriggers.setRightComponent(textTrigger);

    add(splitTriggers, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (SqlCodeWizardDialog.show(new DropViewTriggerWizard(getDatabase(), currentSchemaName, currentViewName, triggerName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropTriggerActionPerformed

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

private void cmEnableTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEnableTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (SqlCodeWizardDialog.show(new EnableViewTriggerWizard(getDatabase(), currentSchemaName, currentViewName, triggerName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmEnableTriggerActionPerformed

private void cmCreateTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTriggerActionPerformed
  if (SqlCodeWizardDialog.show(new CreateViewTriggerWizard(getDatabase(), currentSchemaName, currentViewName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateTriggerActionPerformed

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


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCompile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmCompile;
  private pl.mpak.sky.gui.swing.Action cmCreateTrigger;
  private pl.mpak.sky.gui.swing.Action cmDropTrigger;
  private pl.mpak.sky.gui.swing.Action cmEnableTrigger;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JToolBar.Separator jSeparator5;
  private javax.swing.JToolBar.Separator jSeparator6;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCreateTrigger;
  private javax.swing.JMenuItem menuDropTrigger;
  private javax.swing.JMenuItem menuEnable;
  private javax.swing.JSplitPane splitTriggers;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTriggers;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableTriggers;
  private pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea textTrigger;
  private javax.swing.JToolBar toolBarTriggers;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton4;
  // End of variables declaration//GEN-END:variables
  
}

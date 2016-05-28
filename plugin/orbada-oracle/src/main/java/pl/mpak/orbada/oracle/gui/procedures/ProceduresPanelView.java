package pl.mpak.orbada.oracle.gui.procedures;

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
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.gui.freezing.ProcedureFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.CallObjectWizard;
import pl.mpak.orbada.oracle.gui.wizards.CompileAllObjectsWizard;
import pl.mpak.orbada.oracle.gui.wizards.CreateProcedureWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropProcedureWizard;
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
public class ProceduresPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
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
  
  public ProceduresPanelView(IViewAccesibilities accesibilities) {
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

    tabbedPane = new ProcedureTabbedPane(accesibilities);
    split.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-procedures-panel");
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableProcedures.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          ProcedureSourcePanel panel = (ProcedureSourcePanel)tabbedPane.getComponent(ProcedureSourcePanel.class);
          if (panel != null && lastIndex != tableProcedures.getSelectedRow() && !panel.canClose()) {
            tableProcedures.changeSelection(lastIndex, tableProcedures.getSelectedColumn());
            return;
          }
          lastIndex = tableProcedures.getSelectedRow();
          timer.restart();
        }
      }
    });
    
    tableProcedures.getQuery().setDatabase(getDatabase());
    tableProcedures.addColumn(new QueryTableColumn("object_name", stringManager.getString("name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableProcedures.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
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
    tableProcedures.addColumn(new QueryTableColumn("created", stringManager.getString("she-created"), 110));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("object_name", stringManager.getString("name"), (String[])null));
    def.add(new SqlFilterDefComponent("status = 'INVALID'", stringManager.getString("invalid")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-procedures-filter"),
      cmFilter, buttonFilter, 
      def);
    
    SwingUtil.addAction(tableProcedures, cmCompile);
    SwingUtil.addAction(tableProcedures, cmCallProcedure);
    new ComponentActionsAction(getDatabase(), tableProcedures, buttonActions, menuActions, "oracle-procedures-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableProcedures.requestFocusInWindow();
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
    String objectName = "";
    int rowIndex = tableProcedures.getSelectedRow();
    if (rowIndex >= 0 && tableProcedures.getQuery().isActive()) {
      try {
        tableProcedures.getQuery().getRecord(rowIndex);
        objectName = tableProcedures.getQuery().fieldByName("object_name").getString();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    tabbedPane.refresh(null, currentSchemaName, objectName);
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
      String objectName = null;
      if (tableProcedures.getQuery().isActive() && tableProcedures.getSelectedRow() >= 0) {
        tableProcedures.getQuery().getRecord(tableProcedures.getSelectedRow());
        objectName = tableProcedures.getQuery().fieldByName("object_name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    refreshing = true;
    try {
      int column = tableProcedures.getSelectedColumn();
      int index = Math.max(0, tableProcedures.getSelectedRow());
      tableProcedures.getQuery().close();
      tableProcedures.getQuery().setSqlText(Sql.getFunctionList(filter.getSqlText(), OracleDbInfoProvider.instance.getMajorVersion(getDatabase())));
      tableProcedures.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableProcedures.getQuery().paramByName("function_type").setString("PROCEDURE");
      tableProcedures.getQuery().open();
      if (objectName != null && tableProcedures.getQuery().locate("object_name", new Variant( objectName))) {
        tableProcedures.changeSelection(tableProcedures.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableProcedures.getQuery().isEmpty()) {
        tableProcedures.changeSelection(Math.min(index, tableProcedures.getRowCount() -1), column);
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
    tableProcedures.getQuery().close();
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
    menuCreateProcedure = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuCompileAll = new javax.swing.JMenuItem();
    menuCompileAllInvalid = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropProcedure = new javax.swing.JMenuItem();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmDropProcedure = new pl.mpak.sky.gui.swing.Action();
    cmCreateProcedure = new pl.mpak.sky.gui.swing.Action();
    cmCompileAll = new pl.mpak.sky.gui.swing.Action();
    cmCompileAllInvalid = new pl.mpak.sky.gui.swing.Action();
    cmCallProcedure = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableProcedures = new ViewTable();
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

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    jMenuItem1.setAction(cmCallProcedure);
    menuActions.add(jMenuItem1);

    menuCreateProcedure.setAction(cmCreateProcedure);
    menuActions.add(menuCreateProcedure);
    menuActions.add(jSeparator3);

    menuCompileAll.setAction(cmCompileAll);
    menuActions.add(menuCompileAll);

    menuCompileAllInvalid.setAction(cmCompileAllInvalid);
    menuActions.add(menuCompileAllInvalid);
    menuActions.add(jSeparator2);

    menuDropProcedure.setAction(cmDropProcedure);
    menuActions.add(menuDropProcedure);

    cmCompile.setActionCommandKey("cmCompile");
    cmCompile.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmCompile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/db_sql_execute16.gif")); // NOI18N
    cmCompile.setText(stringManager.getString("cmCompile-text")); // NOI18N
    cmCompile.setTooltip(stringManager.getString("cmCompile-hint")); // NOI18N
    cmCompile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileActionPerformed(evt);
      }
    });

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    cmDropProcedure.setActionCommandKey("cmDropProcedure");
    cmDropProcedure.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif")); // NOI18N
    cmDropProcedure.setText(stringManager.getString("cmDropProcedure-text")); // NOI18N
    cmDropProcedure.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropProcedureActionPerformed(evt);
      }
    });

    cmCreateProcedure.setActionCommandKey("cmCreateProcedure");
    cmCreateProcedure.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/procedure.gif")); // NOI18N
    cmCreateProcedure.setText(stringManager.getString("cmCreateProcedure-text")); // NOI18N
    cmCreateProcedure.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateProcedureActionPerformed(evt);
      }
    });

    cmCompileAll.setActionCommandKey("cmCompileAll");
    cmCompileAll.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/db_sql_execute_all.gif")); // NOI18N
    cmCompileAll.setText(stringManager.getString("cmCompileAll-text")); // NOI18N
    cmCompileAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileAllActionPerformed(evt);
      }
    });

    cmCompileAllInvalid.setActionCommandKey("cmCompileAllInvalid");
    cmCompileAllInvalid.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/db_sql_execute_all_invalid.gif")); // NOI18N
    cmCompileAllInvalid.setText(stringManager.getString("cmCompileAllInvalid-text")); // NOI18N
    cmCompileAllInvalid.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileAllInvalidActionPerformed(evt);
      }
    });

    cmCallProcedure.setActionCommandKey("cmCallProcedure");
    cmCallProcedure.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
    cmCallProcedure.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/call.gif")); // NOI18N
    cmCallProcedure.setText(stringManager.getString("cmCallProcedure-text")); // NOI18N
    cmCallProcedure.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCallProcedureActionPerformed(evt);
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

    jScrollPane1.setViewportView(tableProcedures);

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
    statusBarTables.setTable(tableProcedures);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    split.setLeftComponent(panelTables);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableProcedures.getQuery().isActive()) {
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
  if (tableProcedures.getSelectedRow() >= 0) {
    try {
      tableProcedures.getQuery().getRecord(tableProcedures.getSelectedRow());
      String objectName = tableProcedures.getQuery().fieldByName("object_name").getString();
      getDatabase().executeCommand(
        "ALTER PROCEDURE " +SQLUtil.createSqlName(currentSchemaName, objectName) +" COMPILE" +
        (OracleDbInfoProvider.instance.isDebugClauseNeeded(getDatabase()) ? " DEBUG" : "")
      );
      refresh();
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableProcedures.getSelectedRow() >= 0) {
    try {
      tableProcedures.getQuery().getRecord(tableProcedures.getSelectedRow());
      final String objectName = tableProcedures.getQuery().fieldByName("object_name").getString();
      accesibilities.createView(new ProcedureFreezeViewService(accesibilities, currentSchemaName, objectName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmDropProcedureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropProcedureActionPerformed
  if (tableProcedures.getSelectedRow() >= 0) {
    try {
      tableProcedures.getQuery().getRecord(tableProcedures.getSelectedRow());
      String procedureName = tableProcedures.getQuery().fieldByName("object_name").getString();
      if (SqlCodeWizardDialog.show(new DropProcedureWizard(getDatabase(), currentSchemaName, procedureName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropProcedureActionPerformed

private void cmCreateProcedureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateProcedureActionPerformed
  SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new CreateProcedureWizard(getDatabase(), currentSchemaName), true);
  if (result != null) {
    refresh(result.getResultMap().get("object_name"));
  }
}//GEN-LAST:event_cmCreateProcedureActionPerformed

private void cmCompileAllInvalidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllInvalidActionPerformed
SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "PROCEDURE", "INVALID"), true);
}//GEN-LAST:event_cmCompileAllInvalidActionPerformed

private void cmCompileAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllActionPerformed
SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "PROCEDURE", null), true);
}//GEN-LAST:event_cmCompileAllActionPerformed

private void cmCallProcedureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCallProcedureActionPerformed
  if (tableProcedures.getSelectedRow() >= 0) {
    try {
      tableProcedures.getQuery().getRecord(tableProcedures.getSelectedRow());
      SqlCodeWizardDialog.show(new CallObjectWizard(getDatabase(), currentSchemaName, tableProcedures.getQuery().fieldByName("object_name").getString(), null), true);
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCallProcedureActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCompile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCallProcedure;
  private pl.mpak.sky.gui.swing.Action cmCompile;
  private pl.mpak.sky.gui.swing.Action cmCompileAll;
  private pl.mpak.sky.gui.swing.Action cmCompileAllInvalid;
  private pl.mpak.sky.gui.swing.Action cmCreateProcedure;
  private pl.mpak.sky.gui.swing.Action cmDropProcedure;
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
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCompileAll;
  private javax.swing.JMenuItem menuCompileAllInvalid;
  private javax.swing.JMenuItem menuCreateProcedure;
  private javax.swing.JMenuItem menuDropProcedure;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private ViewTable tableProcedures;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables

}

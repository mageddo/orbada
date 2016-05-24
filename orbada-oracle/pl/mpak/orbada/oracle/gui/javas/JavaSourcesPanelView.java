package pl.mpak.orbada.oracle.gui.javas;

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
import pl.mpak.orbada.oracle.gui.freezing.JavaSourceFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.CompileAllObjectsWizard;
import pl.mpak.orbada.oracle.gui.wizards.java.CreateJavaSourceWizard;
import pl.mpak.orbada.oracle.gui.wizards.java.DropJavaSourceWizard;
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
public class JavaSourcesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
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
  
  public JavaSourcesPanelView(IViewAccesibilities accesibilities) {
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

    tabbedPane = new JavaSourceTabbedPane(accesibilities);
    split.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-sources-panel");
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableSources.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          JavaSourcePanel panel = (JavaSourcePanel)tabbedPane.getComponent(JavaSourcePanel.class);
          if (panel != null && lastIndex != tableSources.getSelectedRow() && !panel.canClose()) {
            tableSources.changeSelection(lastIndex, tableSources.getSelectedColumn());
            return;
          }
          lastIndex = tableSources.getSelectedRow();
          timer.restart();
        }
      }
    });
    
    tableSources.getQuery().setDatabase(getDatabase());
    tableSources.addColumn(new QueryTableColumn("name", stringManager.getString("name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableSources.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
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
    tableSources.addColumn(new QueryTableColumn("created", stringManager.getString("she-created"), 110));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null));
    def.add(new SqlFilterDefComponent("status = 'INVALID'", stringManager.getString("invalid")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-sources-filter"),
      cmFilter, buttonFilter, 
      def);
    
    SwingUtil.addAction(tableSources, cmCompile);
    new ComponentActionsAction(getDatabase(), tableSources, buttonActions, menuActions, "oracle-java-sources-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableSources.requestFocusInWindow();
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
    int rowIndex = tableSources.getSelectedRow();
    if (rowIndex >= 0 && tableSources.getQuery().isActive()) {
      try {
        tableSources.getQuery().getRecord(rowIndex);
        objectName = tableSources.getQuery().fieldByName("name").getString();
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
      if (tableSources.getQuery().isActive() && tableSources.getSelectedRow() >= 0) {
        tableSources.getQuery().getRecord(tableSources.getSelectedRow());
        objectName = tableSources.getQuery().fieldByName("name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    refreshing = true;
    try {
      int column = tableSources.getSelectedColumn();
      int index = Math.max(0, tableSources.getSelectedRow());
      tableSources.getQuery().close();
      tableSources.getQuery().setSqlText(Sql.getJavaList(filter.getSqlText()));
      tableSources.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableSources.getQuery().paramByName("object_type").setString("JAVA SOURCE");
      tableSources.getQuery().open();
      if (objectName != null && tableSources.getQuery().locate("name", new Variant( objectName))) {
        tableSources.changeSelection(tableSources.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableSources.getQuery().isEmpty()) {
        tableSources.changeSelection(Math.min(index, tableSources.getRowCount() -1), column);
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
    tableSources.getQuery().close();
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
    menuCreateJavaSource = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuCompileAll = new javax.swing.JMenuItem();
    menuCompileAllInvalid = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropJavaSource = new javax.swing.JMenuItem();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmDropJavaSource = new pl.mpak.sky.gui.swing.Action();
    cmCreateJavaSource = new pl.mpak.sky.gui.swing.Action();
    cmCompileAll = new pl.mpak.sky.gui.swing.Action();
    cmCompileAllInvalid = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSources = new pl.mpak.orbada.gui.comps.table.ViewTable();
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

    menuCreateJavaSource.setAction(cmCreateJavaSource);
    menuActions.add(menuCreateJavaSource);
    menuActions.add(jSeparator3);

    menuCompileAll.setAction(cmCompileAll);
    menuActions.add(menuCompileAll);

    menuCompileAllInvalid.setAction(cmCompileAllInvalid);
    menuActions.add(menuCompileAllInvalid);
    menuActions.add(jSeparator2);

    menuDropJavaSource.setAction(cmDropJavaSource);
    menuActions.add(menuDropJavaSource);

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

    cmDropJavaSource.setActionCommandKey("cmDropJavaSource");
    cmDropJavaSource.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropJavaSource.setText(stringManager.getString("cmDropJavaSource-text")); // NOI18N
    cmDropJavaSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropJavaSourceActionPerformed(evt);
      }
    });

    cmCreateJavaSource.setActionCommandKey("cmCreateJavaSource");
    cmCreateJavaSource.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/java_source.gif")); // NOI18N
    cmCreateJavaSource.setText(stringManager.getString("cmCreateJavaSource-text")); // NOI18N
    cmCreateJavaSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateJavaSourceActionPerformed(evt);
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

    jScrollPane1.setViewportView(tableSources);

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
    statusBarTables.setTable(tableSources);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    split.setLeftComponent(panelTables);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableSources.getQuery().isActive()) {
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
  if (tableSources.getSelectedRow() >= 0) {
    try {
      tableSources.getQuery().getRecord(tableSources.getSelectedRow());
      String objectName = tableSources.getQuery().fieldByName("name").getString();
      getDatabase().executeCommand(
        "ALTER JAVA SOURCE " +SQLUtil.createSqlName(currentSchemaName, objectName) +" COMPILE" +
        (OracleDbInfoProvider.instance.isDebugClauseNeeded(getDatabase()) ? " DEBUG" : "")
      );
      refresh();
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableSources.getSelectedRow() >= 0) {
    try {
      tableSources.getQuery().getRecord(tableSources.getSelectedRow());
      final String objectName = tableSources.getQuery().fieldByName("name").getString();
      accesibilities.createView(new JavaSourceFreezeViewService(accesibilities, currentSchemaName, objectName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmDropJavaSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropJavaSourceActionPerformed
  if (tableSources.getSelectedRow() >= 0) {
    try {
      tableSources.getQuery().getRecord(tableSources.getSelectedRow());
      if (SqlCodeWizardDialog.show(new DropJavaSourceWizard(getDatabase(), currentSchemaName, tableSources.getQuery().fieldByName("name").getString()), true) != null) {
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropJavaSourceActionPerformed

private void cmCreateJavaSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateJavaSourceActionPerformed
  if (SqlCodeWizardDialog.show(new CreateJavaSourceWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateJavaSourceActionPerformed

private void cmCompileAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllActionPerformed
  SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "JAVA SOURCE", null), true); 
}//GEN-LAST:event_cmCompileAllActionPerformed

private void cmCompileAllInvalidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileAllInvalidActionPerformed
  SqlCodeWizardDialog.show(new CompileAllObjectsWizard(getDatabase(), currentSchemaName, "JAVA SOURCE", "INVALID"), true);
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
  private pl.mpak.sky.gui.swing.Action cmCreateJavaSource;
  private pl.mpak.sky.gui.swing.Action cmDropJavaSource;
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
  private javax.swing.JMenuItem menuCreateJavaSource;
  private javax.swing.JMenuItem menuDropJavaSource;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableSources;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables
  
}

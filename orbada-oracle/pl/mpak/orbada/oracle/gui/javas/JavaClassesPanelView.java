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
import pl.mpak.orbada.oracle.gui.freezing.JavaClassFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.java.DropJavaClassWizard;
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
public class JavaClassesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  
  public JavaClassesPanelView(IViewAccesibilities accesibilities) {
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

    tabbedPane = new JavaClassTabbedPane(accesibilities);
    split.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-classes-panel");
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableClasses.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
      }
    });
    
    tableClasses.getQuery().setDatabase(getDatabase());
    tableClasses.addColumn(new QueryTableColumn("name", stringManager.getString("name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    //tableSources.addColumn(new QueryTableColumn("accessibility", "Zakres", 80));
    tableClasses.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
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
    tableClasses.addColumn(new QueryTableColumn("created", stringManager.getString("she-created"), 110));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null));
    def.add(new SqlFilterDefComponent("status = 'INVALID'", stringManager.getString("invalid")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-classes-filter"),
      cmFilter, buttonFilter, 
      def);
    
    SwingUtil.addAction(tableClasses, cmCompile);
    new ComponentActionsAction(getDatabase(), tableClasses, buttonActions, menuActions, "oracle-java-classes-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableClasses.requestFocusInWindow();
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
    int rowIndex = tableClasses.getSelectedRow();
    if (rowIndex >= 0 && tableClasses.getQuery().isActive()) {
      try {
        tableClasses.getQuery().getRecord(rowIndex);
        objectName = tableClasses.getQuery().fieldByName("name").getString();
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
      if (tableClasses.getQuery().isActive() && tableClasses.getSelectedRow() >= 0) {
        tableClasses.getQuery().getRecord(tableClasses.getSelectedRow());
        objectName = tableClasses.getQuery().fieldByName("name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String javaName) {
    try {
      int column = tableClasses.getSelectedColumn();
      int index = Math.max(0, tableClasses.getSelectedRow());
      tableClasses.getQuery().close();
      tableClasses.getQuery().setSqlText(Sql.getJavaList(filter.getSqlText()));
      tableClasses.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableClasses.getQuery().paramByName("object_type").setString("JAVA CLASS");
      tableClasses.getQuery().open();
      if (javaName != null && tableClasses.getQuery().locate("name", new Variant( javaName))) {
        tableClasses.changeSelection(tableClasses.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableClasses.getQuery().isEmpty()) {
        tableClasses.changeSelection(Math.min(index, tableClasses.getRowCount() -1), column);
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
    settings.setValue("split-location", (long)split.getDividerLocation());
    viewClosing = true;
    tabbedPane.close();
    tableClasses.getQuery().close();
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
    menuDropJavaClass = new javax.swing.JMenuItem();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmDropJavaClass = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableClasses = new pl.mpak.orbada.gui.comps.table.ViewTable();
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

    menuDropJavaClass.setAction(cmDropJavaClass);
    menuActions.add(menuDropJavaClass);

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
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    cmDropJavaClass.setActionCommandKey("cmDropJavaClass");
    cmDropJavaClass.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropJavaClass.setText(stringManager.getString("cmDropJavaClass-text")); // NOI18N
    cmDropJavaClass.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropJavaClassActionPerformed(evt);
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

    jScrollPane1.setViewportView(tableClasses);

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
    statusBarTables.setTable(tableClasses);
    jPanel2.add(statusBarTables, java.awt.BorderLayout.SOUTH);

    panelTables.add(jPanel2, java.awt.BorderLayout.SOUTH);

    split.setLeftComponent(panelTables);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableClasses.getQuery().isActive()) {
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
  if (tableClasses.getSelectedRow() >= 0) {
    try {
      tableClasses.getQuery().getRecord(tableClasses.getSelectedRow());
      String objectName = tableClasses.getQuery().fieldByName("name").getString();
      getDatabase().executeCommand("ALTER JAVA CLASS " +SQLUtil.createSqlName(currentSchemaName, objectName) +" COMPILE");
      refresh();
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableClasses.getSelectedRow() >= 0) {
    try {
      tableClasses.getQuery().getRecord(tableClasses.getSelectedRow());
      final String objectName = tableClasses.getQuery().fieldByName("name").getString();
      accesibilities.createView(new JavaClassFreezeViewService(accesibilities, currentSchemaName, objectName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmDropJavaClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropJavaClassActionPerformed
  if (tableClasses.getSelectedRow() >= 0) {
    try {
      tableClasses.getQuery().getRecord(tableClasses.getSelectedRow());
      if (SqlCodeWizardDialog.show(new DropJavaClassWizard(getDatabase(), currentSchemaName, tableClasses.getQuery().fieldByName("name").getString()), true) != null) {
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropJavaClassActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCompile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCompile;
  private pl.mpak.sky.gui.swing.Action cmDropJavaClass;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuDropJavaClass;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableClasses;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables

}

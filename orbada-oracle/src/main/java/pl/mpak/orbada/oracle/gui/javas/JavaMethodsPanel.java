package pl.mpak.orbada.oracle.gui.javas;

import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class JavaMethodsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentObjectName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private Timer timer;
  private ISettings settings;
  
  public JavaMethodsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
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
        refreshMethodInfo();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-source-methods-panel");
    splitMethods.setDividerLocation(settings.getValue("split-methods-location", (long)splitMethods.getDividerLocation()).intValue());
    splitArguments.setDividerLocation(settings.getValue("split-arguments-location", (long)splitArguments.getDividerLocation()).intValue());

    tableMethods.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
      }
    });
    
    tableMethods.getQuery().setDatabase(getDatabase());
    try {
      tableMethods.addColumn(new QueryTableColumn("method_index", stringManager.getString("pos"), 30));
      tableMethods.addColumn(new QueryTableColumn("accessibility", stringManager.getString("accessibility"), 80, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableMethods.addColumn(new QueryTableColumn("method_name", stringManager.getString("method-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableMethods.addColumn(new QueryTableColumn("return_type", stringManager.getString("return-type"), 150));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("method_name", stringManager.getString("method-name"), (String[])null));
      def.add(new SqlFilterDefComponent("accessibility", stringManager.getString("accessibility"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-source-methods-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    tableArguments.getQuery().setDatabase(getDatabase());
    try {
      tableArguments.getQuery().setSqlText(Sql.getJavaMethodArgumentList(null));
      tableArguments.addColumn(new QueryTableColumn("argument_position", stringManager.getString("pos"), 30));
      tableArguments.addColumn(new QueryTableColumn("argument_type", stringManager.getString("argument-type"), 250));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    tableThrows.getQuery().setDatabase(getDatabase());
    try {
      tableThrows.getQuery().setSqlText(Sql.getJavaMethodThrowList(null));
      tableThrows.addColumn(new QueryTableColumn("exception_index", stringManager.getString("pos"), 30));
      tableThrows.addColumn(new QueryTableColumn("exception_class", stringManager.getString("exception-class"), 250));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableMethods, buttonActions, menuActions, "oracle-java-methods-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("JavaMethodsPanel-title");
  }
  
  private void refreshMethodInfo() {
    int rowIndex = tableMethods.getSelectedRow();
    if (rowIndex >= 0 && tableMethods.getQuery().isActive()) {
      try {
        tableMethods.getQuery().getRecord(rowIndex);
        tableArguments.getQuery().close();
        tableArguments.getQuery().paramByName("method_index").setString(tableMethods.getQuery().fieldByName("method_index").getString());
        tableArguments.getQuery().open();
        tableThrows.getQuery().close();
        tableThrows.getQuery().paramByName("method_index").setString(tableMethods.getQuery().fieldByName("method_index").getString());
        tableThrows.getQuery().open();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
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
      String attrName = null;
      requestRefresh = false;
      if (tableMethods.getQuery().isActive() && tableMethods.getSelectedRow() >= 0) {
        tableMethods.getQuery().getRecord(tableMethods.getSelectedRow());
        attrName = tableMethods.getQuery().fieldByName("method_name").getString();
      }
      tableArguments.getQuery().close();
      tableMethods.getQuery().close();
      tableMethods.getQuery().setSqlText(Sql.getJavaMethodList(filter.getSqlText()));
      tableMethods.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableMethods.getQuery().paramByName("object_name").setString(currentObjectName);
      tableMethods.getQuery().open();
      tableArguments.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableArguments.getQuery().paramByName("object_name").setString(currentObjectName);
      tableThrows.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableThrows.getQuery().paramByName("object_name").setString(currentObjectName);
      if (!tableMethods.getQuery().isEmpty()) {
        if (attrName != null && tableMethods.getQuery().locate("method_name", new Variant(attrName))) {
          tableMethods.changeSelection(tableMethods.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableMethods.changeSelection(0, 0);
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentObjectName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentObjectName = objectName;
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
    settings.setValue("split-methods-location", (long)splitMethods.getDividerLocation());
    settings.setValue("split-arguments-location", (long)splitArguments.getDividerLocation());
    settings.store();
    timer.cancel();
    closing = true;
    tableMethods.getQuery().close();
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
    menuActions = new javax.swing.JPopupMenu();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarColumns = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    splitMethods = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableMethods = new ViewTable();
    splitArguments = new javax.swing.JSplitPane();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableArguments = new ViewTable();
    jScrollPane3 = new javax.swing.JScrollPane();
    tableThrows = new ViewTable();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
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

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableMethods);
    add(statusBar, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarColumns.setFloatable(false);
    toolBarColumns.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonFilter);
    toolBarColumns.add(jSeparator1);
    toolBarColumns.add(buttonActions);

    jPanel1.add(toolBarColumns);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    splitMethods.setBorder(null);
    splitMethods.setDividerLocation(250);
    splitMethods.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splitMethods.setContinuousLayout(true);
    splitMethods.setOneTouchExpandable(true);

    jScrollPane1.setViewportView(tableMethods);

    splitMethods.setLeftComponent(jScrollPane1);

    splitArguments.setBorder(null);
    splitArguments.setDividerLocation(300);
    splitArguments.setContinuousLayout(true);
    splitArguments.setOneTouchExpandable(true);

    jScrollPane2.setViewportView(tableArguments);

    splitArguments.setLeftComponent(jScrollPane2);

    jScrollPane3.setViewportView(tableThrows);

    splitArguments.setRightComponent(jScrollPane3);

    splitMethods.setRightComponent(splitArguments);

    add(splitMethods, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

  private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
    refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JSplitPane splitArguments;
  private javax.swing.JSplitPane splitMethods;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableArguments;
  private ViewTable tableMethods;
  private ViewTable tableThrows;
  private javax.swing.JToolBar toolBarColumns;
  // End of variables declaration//GEN-END:variables
  
}

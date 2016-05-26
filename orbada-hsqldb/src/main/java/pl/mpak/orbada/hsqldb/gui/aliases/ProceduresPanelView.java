package pl.mpak.orbada.hsqldb.gui.aliases;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.cm.GrantClassWizardAction;
import pl.mpak.orbada.hsqldb.cm.RevokeClassWizardAction;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.Titleable;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ProceduresPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** Creates new form DerbyDbObjectPanelView
   * @param accesibilities
   */
  public ProceduresPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = HSqlDbInfoProvider.getCurrentSchema(getDatabase());

    if (HSqlDbInfoProvider.getVersionTest(getDatabase()) == HSqlDbInfoProvider.hsqlDb18) {
      toolBar.add(new ToolButton(new GrantClassWizardAction(getDatabase()) {
        @Override
        public void actionPerformed(ActionEvent e) {
          super.actionPerformed(e);
          refresh();
        }
      }));
      toolBar.add(new ToolButton(new RevokeClassWizardAction(getDatabase()) {
        @Override
        public void actionPerformed(ActionEvent e) {
          super.actionPerformed(e);
          refresh();
        }
      }));
    }
    
    tableProcedures.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        String procedureName = "";
        int rowIndex = tableProcedures.getSelectedRow();
        if (rowIndex >= 0 && tableProcedures.getQuery().isActive()) {
          try {
            tableProcedures.getQuery().getRecord(rowIndex);
            procedureName = tableProcedures.getQuery().fieldByName("procedure_name").getString();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        for (int i=0; i<tabbedTableInfo.getTabCount(); i++) {
          Component c = tabbedTableInfo.getComponentAt(i);
          if (c instanceof ITabObjectInfo) {
            ((ITabObjectInfo)c).refresh(null, currentSchemaName, procedureName);
          }
        }
      }
    });
    addInfoPanel(new ProcedureParametersPanel(accesibilities));
    
    tableProcedures.getQuery().setDatabase(getDatabase());
    tableProcedures.addColumn(new QueryTableColumn("procedure_name", stringManager.getString("procedure-name"), 220, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableProcedures.addColumn(new QueryTableColumn("procedure_type", stringManager.getString("procedure-type"), 80));
    tableProcedures.addColumn(new QueryTableColumn("origin", stringManager.getString("origin"), 130));
    tableProcedures.addColumn(new QueryTableColumn("num_input_params", stringManager.getString("num-input-params"), 60));
    if (HSqlDbInfoProvider.getVersionTest(getDatabase()) == HSqlDbInfoProvider.hsqlDb18) {
      tableProcedures.addColumn(new QueryTableColumn("num_result_sets", stringManager.getString("num-result-sets"), 60));
    }
    tableProcedures.addColumn(new QueryTableColumn("remarks", stringManager.getString("comment"), 300));
    if (HSqlDbInfoProvider.getVersionTest(getDatabase()) == HSqlDbInfoProvider.hsqlDb18) {
      tableProcedures.addColumn(new QueryTableColumn("specific_name", stringManager.getString("specification"), 200));
    }
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("procedure_name", stringManager.getString("procedure-name"), (String[])null));
    def.add(new SqlFilterDefComponent("procedure_type", stringManager.getString("procedure-type"), new String[] {"'PROCEDURE'", "'FUNCTION'"}));
    def.add(new SqlFilterDefComponent("origin", stringManager.getString("origin"), new String[] {"'ALIAS'", "'BUILTIN ROUTINE'", "'USER DEFINED ROUTINE'"}));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "hsqldb-procedures-filter"),
      cmFilter, buttonFilter, 
      def);
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableProcedures.requestFocusInWindow();
      }
    });
    new ComponentActionsAction(getDatabase(), tableProcedures, buttonActions, menuActions, "hsqldb-procedures-actions");
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(HSqlDbInfoProvider.getCurrentSchema(getDatabase()))) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
      }
      else {
        accesibilities.setTabTitle(tabTitle);
      }
    }
  }
  
  public void refresh() {
    if (!isVisible() || viewClosing) {
      return;
    }
    try {
      String tableName = null;
      if (tableProcedures.getQuery().isActive() && tableProcedures.getSelectedRow() >= 0) {
        tableProcedures.getQuery().getRecord(tableProcedures.getSelectedRow());
        tableName = tableProcedures.getQuery().fieldByName("PROCEDURE_NAME").getString();
      }
      refresh(tableName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int index = Math.max(0, tableProcedures.getSelectedRow());
      tableProcedures.getQuery().close();
      tableProcedures.getQuery().setSqlText(Sql.getProcedureList(filter.getSqlText(), HSqlDbInfoProvider.getVersionTest(getDatabase())));
      tableProcedures.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableProcedures.getQuery().open();
      if (objectName != null && tableProcedures.getQuery().locate("PROCEDURE_NAME", new Variant(objectName))) {
        tableProcedures.changeSelection(tableProcedures.getQuery().getCurrentRecord().getIndex(), tableProcedures.getSelectedColumn());
      } else if (!tableProcedures.getQuery().isEmpty()) {
        tableProcedures.changeSelection(Math.min(index, tableProcedures.getRowCount() -1), tableProcedures.getSelectedColumn());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void addInfoPanel(JPanel panel) {
    tabbedTableInfo.addTab(((Titleable)panel).getTitle(), panel);
    tabbedTableInfo.setTabComponentAt(tabbedTableInfo.indexOfComponent(panel), new TabCloseComponent(((Titleable)panel).getTitle()));
  }
  
  @Override
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    viewClosing = true;
    int i = 0;
    while (i<tabbedTableInfo.getTabCount()) {
      Component c = tabbedTableInfo.getComponentAt(i);
      if (c instanceof Closeable) {
        try {
          ((Closeable)c).close();
        } catch (IOException ex) {
        }
      } else {
        i++;
      }
      tabbedTableInfo.remove(c);
    }
    tableProcedures.getQuery().close();
    accesibilities = null;
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
    splinPane = new javax.swing.JSplitPane();
    panelProcedures = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableProcedures = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    tabbedTableInfo = new javax.swing.JTabbedPane();

    cmRefresh.setActionCommandKey("cmRefresh");
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

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    splinPane.setBorder(null);
    splinPane.setDividerLocation(400);
    splinPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splinPane.setContinuousLayout(true);
    splinPane.setOneTouchExpandable(true);

    panelProcedures.setPreferredSize(new java.awt.Dimension(250, 100));
    panelProcedures.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableProcedures);

    panelProcedures.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowFieldValue(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableProcedures);
    panelProcedures.add(statusBar, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);

    buttonSelectSchema.setAction(cmSelectSchema);
    buttonSelectSchema.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSelectSchema.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonSelectSchema);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonFilter);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);

    jPanel1.add(toolBar);

    panelProcedures.add(jPanel1, java.awt.BorderLayout.NORTH);

    splinPane.setLeftComponent(panelProcedures);

    tabbedTableInfo.setFocusable(false);
    splinPane.setRightComponent(tabbedTableInfo);

    add(splinPane, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableProcedures.getQuery().isActive()) {
    refresh();
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


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JPanel panelProcedures;
  private javax.swing.JSplitPane splinPane;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private javax.swing.JTabbedPane tabbedTableInfo;
  private ViewTable tableProcedures;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

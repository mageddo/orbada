package pl.mpak.orbada.derbydb.tables;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.io.Closeable;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.gui.ContentPanel;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class TablesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** Creates new form DerbyDbObjectPanelView
   * @param accesibilities
   */
  public TablesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = getDatabase().getUserName().toUpperCase();
    
    tableTables.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        String tableName = "";
        int rowIndex = tableTables.getSelectedRow();
        if (rowIndex >= 0 && tableTables.getQuery().isActive()) {
          try {
            tableTables.getQuery().getRecord(rowIndex);
            tableName = tableTables.getQuery().fieldByName("tablename").getString();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        for (int i=0; i<tabbedTableInfo.getTabCount(); i++) {
          Component c = tabbedTableInfo.getComponentAt(i);
          if (c instanceof ITabObjectInfo) {
            ((ITabObjectInfo)c).refresh(null, currentSchemaName, tableName);
          }
        }
      }
    });
    addInfoPanel(stringManager.getString("TablesPanelView-columns"), new TableColumnsPanel(accesibilities));
    addInfoPanel(stringManager.getString("TablesPanelView-indexes"), new TableIndexesPanel(accesibilities));
    addInfoPanel(stringManager.getString("TablesPanelView-constraints"), new TableConstraintsPanel(accesibilities));
    addInfoPanel(stringManager.getString("TablesPanelView-triggers"), new TableTriggersPanel(accesibilities));
    addInfoPanel(stringManager.getString("TablesPanelView-datas"), new ContentPanel(accesibilities));
    
    tableTables.getQuery().setDatabase(getDatabase());
    tableTables.addColumn(new QueryTableColumn("tablename", stringManager.getString("TablesPanelView-table-name"), 120, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableTables.addColumn(new QueryTableColumn("authorizationid", stringManager.getString("TablesPanelView-authorization"), 120));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("t.tablename", stringManager.getString("TablesPanelView-table-name"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-tables-filter"),
      cmFilter, buttonFilter, 
      def);
    new ComponentActionsAction(getDatabase(), tableTables, buttonActions, menuActions, "derbydb-tables-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableTables.requestFocusInWindow();
      }
    });
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(getDatabase().getUserName())) {
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
      if (tableTables.getQuery().isActive() && tableTables.getSelectedRow() >= 0) {
        tableTables.getQuery().getRecord(tableTables.getSelectedRow());
        tableName = tableTables.getQuery().fieldByName("TABLENAME").getString();
      }
      refresh(tableName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int index = Math.max(0, tableTables.getSelectedRow());
      tableTables.getQuery().close();
      tableTables.getQuery().setSqlText(DerbyDbSql.getTableList(filter.getSqlText()));
      tableTables.getQuery().paramByName("schemaname").setString(currentSchemaName);
      tableTables.getQuery().open();
      if (objectName != null && tableTables.getQuery().locate("TABLENAME", new Variant(objectName))) {
        tableTables.changeSelection(tableTables.getQuery().getCurrentRecord().getIndex(), tableTables.getSelectedColumn());
      } else if (!tableTables.getQuery().isEmpty()) {
        tableTables.changeSelection(Math.min(index, tableTables.getRowCount() -1), tableTables.getSelectedColumn());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void addInfoPanel(String title, JPanel panel) {
    tabbedTableInfo.addTab(title, panel);
    tabbedTableInfo.setTabComponentAt(tabbedTableInfo.indexOfComponent(panel), new TabCloseComponent(title));
  }
  
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
    tableTables.getQuery().close();
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
    cmDropTable = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    splinPane = new javax.swing.JSplitPane();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTables = new ViewTable();
    statusBarTables = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarTables = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonDtopTable = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    tabbedTableInfo = new javax.swing.JTabbedPane();

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

    cmDropTable.setActionCommandKey("cmDropTable");
    cmDropTable.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTable.setText(stringManager.getString("TablesPanelView-cmDropTable-text")); // NOI18N
    cmDropTable.setTooltip(stringManager.getString("TablesPanelView-cmDropTable-hint")); // NOI18N
    cmDropTable.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTableActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    splinPane.setBorder(null);
    splinPane.setDividerLocation(200);
    splinPane.setContinuousLayout(true);
    splinPane.setOneTouchExpandable(true);

    panelTables.setPreferredSize(new java.awt.Dimension(250, 100));
    panelTables.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTables);

    panelTables.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarTables.setShowFieldType(false);
    statusBarTables.setShowFieldValue(false);
    statusBarTables.setShowOpenTime(false);
    statusBarTables.setTable(tableTables);
    panelTables.add(statusBarTables, java.awt.BorderLayout.PAGE_END);

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

    buttonDtopTable.setAction(cmDropTable);
    buttonDtopTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDtopTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonDtopTable);
    toolBarTables.add(jSeparator2);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonActions);

    jPanel1.add(toolBarTables);

    panelTables.add(jPanel1, java.awt.BorderLayout.NORTH);

    splinPane.setLeftComponent(panelTables);

    tabbedTableInfo.setFocusable(false);
    splinPane.setRightComponent(tabbedTableInfo);

    add(splinPane, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmDropTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTableActionPerformed
  if (tableTables.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableTables.getQuery().getRecord(tableTables.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableTables.getQuery().fieldByName("tablename").getString(), getDatabase());
      if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("TablesPanelView-delete-table-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        getDatabase().createCommand("drop table " +objectName, true);
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmDropTableActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableTables.getQuery().isActive()) {
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
    query.open("select schemaname from sys.sysschemas order by schemaname");
    Vector<String> vl = QueryUtil.staticData("{schemaname}", query);
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
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDtopTable;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmDropTable;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JPanel panelTables;
  private javax.swing.JSplitPane splinPane;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private javax.swing.JTabbedPane tabbedTableInfo;
  private ViewTable tableTables;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables

}

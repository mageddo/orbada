package pl.mpak.orbada.derbydb.views;

import java.awt.Component;
import java.awt.Dialog;
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
import pl.mpak.orbada.derbydb.tables.TableColumnsPanel;
import pl.mpak.orbada.gui.ContentPanel;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectInfo;
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
public class ViewsPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaDerbyDbPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** Creates new form ViewsPanelView 
   * @param accesibilities 
   */
  public ViewsPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = getDatabase().getUserName().toUpperCase();
    
    tableViews.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        String viewName = "";
        int rowIndex = tableViews.getSelectedRow();
        if (rowIndex >= 0 && tableViews.getQuery().isActive()) {
          try {
            tableViews.getQuery().getRecord(rowIndex);
            viewName = tableViews.getQuery().fieldByName("viewname").getString();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        for (int i=0; i<tabbedViewInfo.getTabCount(); i++) {
          Component c = tabbedViewInfo.getComponentAt(i);
          if (c instanceof ITabObjectInfo) {
            ((ITabObjectInfo)c).refresh(null, currentSchemaName, viewName);
          }
        }
      }
    });
    addInfoPanel(stringManager.getString("ViewsPanelView-columns"), new TableColumnsPanel(accesibilities, true));
    addInfoPanel(stringManager.getString("ViewsPanelView-source"), new ViewSourcePanel(accesibilities));
    addInfoPanel(stringManager.getString("ViewsPanelView-datas"), new ContentPanel(accesibilities));
    
    tableViews.getQuery().setDatabase(getDatabase());
    tableViews.addColumn(new QueryTableColumn("viewname", stringManager.getString("ViewsPanelView-view-name"), 120, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableViews.addColumn(new QueryTableColumn("authorizationid", stringManager.getString("ViewsPanelView-authorization"), 120));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("t.tablename", stringManager.getString("ViewsPanelView-view-name"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-views-filter"),
      cmFilter, buttonFilter, 
      def);
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableViews.requestFocusInWindow();
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
      String viewName = null;
      if (tableViews.getQuery().isActive() && tableViews.getSelectedRow() >= 0) {
        tableViews.getQuery().getRecord(tableViews.getSelectedRow());
        viewName = tableViews.getQuery().fieldByName("VIEWNAME").getString();
      }
      refresh(viewName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int index = Math.max(0, tableViews.getSelectedRow());
      tableViews.getQuery().close();
      tableViews.getQuery().setSqlText(DerbyDbSql.getViewList(filter.getSqlText()));
      tableViews.getQuery().paramByName("schemaname").setString(currentSchemaName);
      tableViews.getQuery().open();
      if (objectName != null && tableViews.getQuery().locate("VIEWNAME", new Variant(objectName))) {
        tableViews.changeSelection(tableViews.getQuery().getCurrentRecord().getIndex(), tableViews.getSelectedColumn());
      } else if (!tableViews.getQuery().isEmpty()) {
        tableViews.changeSelection(Math.min(index, tableViews.getRowCount() -1), tableViews.getSelectedColumn());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void addInfoPanel(String title, JPanel panel) {
    tabbedViewInfo.addTab(title, panel);
    tabbedViewInfo.setTabComponentAt(tabbedViewInfo.indexOfComponent(panel), new TabCloseComponent(title));
  }
  
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    viewClosing = true;
    int i = 0;
    while (i<tabbedViewInfo.getTabCount()) {
      Component c = tabbedViewInfo.getComponentAt(i);
      if (c instanceof Closeable) {
        try {
          ((Closeable)c).close();
        } catch (IOException ex) {
        }
      } else {
        i++;
      }
      tabbedViewInfo.remove(c);
    }
    tableViews.getQuery().close();
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
    cmDropView = new pl.mpak.sky.gui.swing.Action();
    splinPane = new javax.swing.JSplitPane();
    panelViews = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableViews = new ViewTable();
    statusBarViews = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarViews = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonDtopView = new pl.mpak.sky.gui.swing.comp.ToolButton();
    tabbedViewInfo = new javax.swing.JTabbedPane();

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
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/derbydb/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmDropView.setActionCommandKey("cmDropTable");
    cmDropView.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropView.setText(stringManager.getString("ViewsPanelView-cmDropView-text")); // NOI18N
    cmDropView.setTooltip(stringManager.getString("ViewsPanelView-cmDropView-hint")); // NOI18N
    cmDropView.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropViewActionPerformed(evt);
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

    panelViews.setPreferredSize(new java.awt.Dimension(250, 100));
    panelViews.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableViews);

    panelViews.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarViews.setShowFieldType(false);
    statusBarViews.setShowFieldValue(false);
    statusBarViews.setShowOpenTime(false);
    panelViews.add(statusBarViews, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarViews.setFloatable(false);
    toolBarViews.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarViews.add(buttonRefresh);

    buttonSelectSchema.setAction(cmSelectSchema);
    buttonSelectSchema.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSelectSchema.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarViews.add(buttonSelectSchema);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarViews.add(buttonFilter);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarViews.add(jSeparator1);

    buttonDtopView.setAction(cmDropView);
    buttonDtopView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDtopView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarViews.add(buttonDtopView);

    jPanel1.add(toolBarViews);

    panelViews.add(jPanel1, java.awt.BorderLayout.NORTH);

    splinPane.setLeftComponent(panelViews);

    tabbedViewInfo.setFocusable(false);
    splinPane.setRightComponent(tabbedViewInfo);

    add(splinPane, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropViewActionPerformed
  if (tableViews.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableViews.getQuery().fieldByName("viewname").getString(), getDatabase());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), stringManager.getString("ViewsPanelView-delete-view-q"), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop view " +objectName, true);
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmDropViewActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableViews.getQuery().isActive()) {
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
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  } finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDtopView;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmDropView;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JPanel panelViews;
  private javax.swing.JSplitPane splinPane;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarViews;
  private javax.swing.JTabbedPane tabbedViewInfo;
  private ViewTable tableViews;
  private javax.swing.JToolBar toolBarViews;
  // End of variables declaration//GEN-END:variables

}

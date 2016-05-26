package pl.mpak.orbada.sqlite.gui.databases;

import java.io.File;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.DataTable;
import orbada.gui.comps.table.ViewTable;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.Sql;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class DatabasesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  private IViewAccesibilities accesibilities;
  private String tabTitle;
  private boolean viewClosing = false;
  private ISettings settings;
  
  public DatabasesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "sqlite-database-panel");

    tableDetails.getQuery().setDatabase(getDatabase());
    tableDatabases.getQuery().setDatabase(getDatabase());
    try {
      tableDatabases.getQuery().setCloseResultAfterOpen(true);
      tableDatabases.addColumn(new QueryTableColumn("seq", stringManager.getString("pos"), 50));
      tableDatabases.addColumn(new QueryTableColumn("name", stringManager.getString("database-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableDatabases.addColumn(new QueryTableColumn("file", stringManager.getString("file"), 450));
      tableDatabases.addColumn(new QueryTableColumn("file_size", stringManager.getString("file-size"), 70));
      tableDatabases.addColumn(new QueryTableColumn("cache_size", stringManager.getString("cache-size"), 70));
      tableDatabases.addColumn(new QueryTableColumn("page_count", stringManager.getString("page-count"), 70));
      tableDatabases.addColumn(new QueryTableColumn("max_page_count", stringManager.getString("max-page-count"), 80));
      tableDatabases.addColumn(new QueryTableColumn("locking_mode", stringManager.getString("locking-mode"), 100));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    tableDatabases.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        tableDetails.getQuery().close();
      }
    });
    new ComponentActionsAction(getDatabase(), tableDatabases, buttonActions, menuActions, "sqlite-databases-actions");

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        split.setDividerLocation(settings.getValue("split-location", (long)(split.getHeight() *0.8)).intValue());
      }
    });
  }
  
  public void refresh() {
    if (!isVisible() || viewClosing) {
      return;
    }
    try {
      String objectName = null;
      if (tableDatabases.getQuery().isActive() && tableDatabases.getSelectedRow() >= 0) {
        tableDatabases.getQuery().getRecord(tableDatabases.getSelectedRow());
        objectName = tableDatabases.getQuery().fieldByName("name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private String createDatabasesSQL() throws Exception {
    Query query = getDatabase().createQuery();
    Query pragma = getDatabase().createQuery();
    StringBuilder sb = new StringBuilder();
    try {
      query.open(Sql.getCatalogList());
      while (!query.eof()) {
        if (sb.length() > 0) {
          sb.append("UNION ALL\n");
        }
        sb.append("SELECT ");
        sb.append("'").append(query.fieldByName("seq").getString()).append("' seq");
        sb.append(", '").append(query.fieldByName("name").getString()).append("' name");
        sb.append(", '").append(query.fieldByName("file").getString()).append("' file");
        sb.append(", ").append(new File(query.fieldByName("file").getString()).length()).append(" file_size");
        sb.append(", ").append(pragma.open("pragma " +getDatabase().quoteName(query.fieldByName("name").getString()) +".cache_size").fieldByName("cache_size").getString()).append(" cache_size");
        sb.append(", ").append(pragma.open("pragma " +getDatabase().quoteName(query.fieldByName("name").getString()) +".page_count").fieldByName("page_count").getString()).append(" page_count");
        sb.append(", ").append(pragma.open("pragma " +getDatabase().quoteName(query.fieldByName("name").getString()) +".max_page_count").fieldByName("max_page_count").getString()).append(" max_page_count");
        sb.append(", '").append(pragma.open("pragma " +getDatabase().quoteName(query.fieldByName("name").getString()) +".locking_mode").fieldByName("locking_mode").getString()).append("' locking_mode");
        sb.append("\n");
        query.next();
      }
    }
    finally {
      pragma.close();
      query.close();
    }
    return sb.toString();
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableDatabases.getSelectedColumn();
      int index = Math.max(0, tableDatabases.getSelectedRow());
      tableDatabases.getQuery().close();
      tableDatabases.getQuery().setSqlText(createDatabasesSQL());
      tableDatabases.getQuery().open();
      if (objectName != null && tableDatabases.getQuery().locate("name", new Variant(objectName))) {
        tableDatabases.changeSelection(tableDatabases.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableDatabases.getQuery().isEmpty()) {
        tableDatabases.changeSelection(Math.min(index, tableDatabases.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    settings.setValue("split-location", (long)split.getDividerLocation());
    viewClosing = true;
    tableDatabases.getQuery().close();
    accesibilities = null;
    settings.store();
  }

  public Query getQueryDetails() {
    return tableDetails.getQuery();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jPanel1 = new javax.swing.JPanel();
    split = new javax.swing.JSplitPane();
    jPanel3 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableDatabases = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel4 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableDetails = new DataTable();
    statusBarDetails = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRefresh.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/refresh16.gif"))); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    split.setBorder(null);
    split.setDividerLocation(201);
    split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel3.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableDatabases);

    jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableDatabases);
    jPanel3.add(statusBar, java.awt.BorderLayout.SOUTH);

    split.setTopComponent(jPanel3);

    jPanel4.setLayout(new java.awt.BorderLayout());

    jScrollPane2.setViewportView(tableDetails);

    jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    statusBarDetails.setShowFieldType(false);
    statusBarDetails.setShowOpenTime(false);
    statusBarDetails.setTable(tableDetails);
    jPanel4.add(statusBarDetails, java.awt.BorderLayout.SOUTH);

    split.setBottomComponent(jPanel4);

    jPanel1.add(split, java.awt.BorderLayout.CENTER);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);

    jPanel2.add(toolBar);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableDatabases.getQuery().isActive()) {
    refresh();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarDetails;
  private ViewTable tableDatabases;
  private DataTable tableDetails;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

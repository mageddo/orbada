package pl.mpak.orbada.oracle.gui.dblinks;

import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.CreateDbLinkWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropDbLinkWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
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
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class DbLinksPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** 
   * @param accesibilities
   */
  public DbLinksPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    tableSynonyms.getQuery().setDatabase(getDatabase());
    try {
      tableSynonyms.addColumn(new QueryTableColumn("db_link", stringManager.getString("db-link-name"), 300, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableSynonyms.addColumn(new QueryTableColumn("username", stringManager.getString("user-name"), 150));
      tableSynonyms.addColumn(new QueryTableColumn("host", stringManager.getString("destination-host"), 180));
      tableSynonyms.addColumn(new QueryTableColumn("created", stringManager.getString("created"), 110));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("db_link", stringManager.getString("db-link-name"), (String[])null));
      def.add(new SqlFilterDefComponent("username", stringManager.getString("user-name"), (String[])null));
      def.add(new SqlFilterDefComponent("host", stringManager.getString("destination-host"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-dblinks-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableSynonyms, buttonActions, menuActions, "oracle-dblinks-actions");
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
      if (tableSynonyms.getQuery().isActive() && tableSynonyms.getSelectedRow() >= 0) {
        tableSynonyms.getQuery().getRecord(tableSynonyms.getSelectedRow());
        objectName = tableSynonyms.getQuery().fieldByName("db_link").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableSynonyms.getSelectedColumn();
      int index = Math.max(0, tableSynonyms.getSelectedRow());
      tableSynonyms.getQuery().close();
      tableSynonyms.getQuery().setSqlText(Sql.getDbLinkList(filter.getSqlText()));
      tableSynonyms.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableSynonyms.getQuery().open();
      if (objectName != null && tableSynonyms.getQuery().locate("db_link", new Variant(objectName))) {
        tableSynonyms.changeSelection(tableSynonyms.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableSynonyms.getQuery().isEmpty()) {
        tableSynonyms.changeSelection(Math.min(index, tableSynonyms.getRowCount() -1), column);
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
    viewClosing = true;
    tableSynonyms.getQuery().close();
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
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCreateDbLink = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropDbLink = new javax.swing.JMenuItem();
    cmDropDbLink = new pl.mpak.sky.gui.swing.Action();
    cmCreateDbLink = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSynonyms = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

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

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    menuCreateDbLink.setAction(cmCreateDbLink);
    menuActions.add(menuCreateDbLink);
    menuActions.add(jSeparator2);

    menuDropDbLink.setAction(cmDropDbLink);
    menuActions.add(menuDropDbLink);

    cmDropDbLink.setActionCommandKey("cmDropDbLink");
    cmDropDbLink.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif")); // NOI18N
    cmDropDbLink.setText(stringManager.getString("cmDropDbLink-text")); // NOI18N
    cmDropDbLink.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropDbLinkActionPerformed(evt);
      }
    });

    cmCreateDbLink.setActionCommandKey("cmCreateDbLink");
    cmCreateDbLink.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/db_link.gif")); // NOI18N
    cmCreateDbLink.setText(stringManager.getString("cmCreateDbLink-text")); // NOI18N
    cmCreateDbLink.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateDbLinkActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableSynonyms);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableSynonyms);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

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
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);

    jPanel2.add(toolBar);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableSynonyms.getQuery().isActive()) {
    refresh();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

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
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  } finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmDropDbLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropDbLinkActionPerformed
  if (tableSynonyms.getSelectedRow() >= 0) {
    try {
      tableSynonyms.getQuery().getRecord(tableSynonyms.getSelectedRow());
      String dbLinkName = tableSynonyms.getQuery().fieldByName("db_link").getString();
      if (SqlCodeWizardDialog.show(new DropDbLinkWizard(getDatabase(), currentSchemaName, dbLinkName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropDbLinkActionPerformed

private void cmCreateDbLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateDbLinkActionPerformed
  if (SqlCodeWizardDialog.show(new CreateDbLinkWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateDbLinkActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCreateDbLink;
  private pl.mpak.sky.gui.swing.Action cmDropDbLink;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCreateDbLink;
  private javax.swing.JMenuItem menuDropDbLink;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableSynonyms;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

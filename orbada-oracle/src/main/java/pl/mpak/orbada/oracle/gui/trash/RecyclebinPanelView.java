package pl.mpak.orbada.oracle.gui.trash;

import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.FlashbackObjectWizard;
import pl.mpak.orbada.oracle.gui.wizards.PurgeObjectWizard;
import pl.mpak.orbada.oracle.gui.wizards.PurgeRecyclebinWizard;
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
public class RecyclebinPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private boolean dbaRole;
  
  public RecyclebinPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    dbaRole = "TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false"));
    tableRecyclebin.getQuery().setDatabase(getDatabase());
    try {
      if (dbaRole) {
        tableRecyclebin.addColumn(new QueryTableColumn("owner", stringManager.getString("schema"), 150));
      }
      tableRecyclebin.addColumn(new QueryTableColumn("object_name", stringManager.getString("obejct-name"), 220));
      tableRecyclebin.addColumn(new QueryTableColumn("original_name", stringManager.getString("oryginal-name"), 180, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableRecyclebin.addColumn(new QueryTableColumn("type", stringManager.getString("object-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)));
      tableRecyclebin.addColumn(new QueryTableColumn("operation", stringManager.getString("operation"), 100));
      tableRecyclebin.addColumn(new QueryTableColumn("createtime", stringManager.getString("created"), 120));
      tableRecyclebin.addColumn(new QueryTableColumn("droptime", stringManager.getString("droptime"), 120));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("object_name", stringManager.getString("obejct-name"), (String[])null));
      def.add(new SqlFilterDefComponent("original_name", stringManager.getString("oryginal-name"), (String[])null));
      def.add(new SqlFilterDefComponent("type", stringManager.getString("object-type"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-recyclebin-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableRecyclebin, buttonActions, menuActions, "oracle-recyclebin-actions");
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
      if (tableRecyclebin.getQuery().isActive() && tableRecyclebin.getSelectedRow() >= 0) {
        tableRecyclebin.getQuery().getRecord(tableRecyclebin.getSelectedRow());
        objectName = tableRecyclebin.getQuery().fieldByName("object_name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableRecyclebin.getSelectedColumn();
      int index = Math.max(0, tableRecyclebin.getSelectedRow());
      tableRecyclebin.getQuery().close();
      if (dbaRole) {
        tableRecyclebin.getQuery().setSqlText(Sql.getDbaRecyclebinList(filter.getSqlText()));
      }
      else {
        tableRecyclebin.getQuery().setSqlText(Sql.getRecyclebinList(filter.getSqlText()));
      }
      tableRecyclebin.getQuery().open();
      if (objectName != null && tableRecyclebin.getQuery().locate("object_name", new Variant(objectName))) {
        tableRecyclebin.changeSelection(tableRecyclebin.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableRecyclebin.getQuery().isEmpty()) {
        tableRecyclebin.changeSelection(Math.min(index, tableRecyclebin.getRowCount() -1), column);
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
    tableRecyclebin.getQuery().close();
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
    menuFlashback = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuPurgeObject = new javax.swing.JMenuItem();
    menuPurgeRecyclebin = new javax.swing.JMenuItem();
    cmFlashbackObject = new pl.mpak.sky.gui.swing.Action();
    cmPurgeObject = new pl.mpak.sky.gui.swing.Action();
    cmPurgeRecyclebin = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableRecyclebin = new ViewTable();
    statusBarSequences = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

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

    menuFlashback.setAction(cmFlashbackObject);
    menuActions.add(menuFlashback);
    menuActions.add(jSeparator2);

    menuPurgeObject.setAction(cmPurgeObject);
    menuActions.add(menuPurgeObject);

    menuPurgeRecyclebin.setAction(cmPurgeRecyclebin);
    menuActions.add(menuPurgeRecyclebin);

    cmFlashbackObject.setActionCommandKey("cmFlashbackObject");
    cmFlashbackObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/undo.gif")); // NOI18N
    cmFlashbackObject.setText(stringManager.getString("cmFlashbackObject-text")); // NOI18N
    cmFlashbackObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFlashbackObjectActionPerformed(evt);
      }
    });

    cmPurgeObject.setActionCommandKey("cmPurgeObject");
    cmPurgeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmPurgeObject.setText(stringManager.getString("cmPurgeObject-text")); // NOI18N
    cmPurgeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmPurgeObjectActionPerformed(evt);
      }
    });

    cmPurgeRecyclebin.setActionCommandKey("cmPurgeRecyclebin");
    cmPurgeRecyclebin.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmPurgeRecyclebin.setText(stringManager.getString("cmPurgeRecyclebin-text")); // NOI18N
    cmPurgeRecyclebin.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmPurgeRecyclebinActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableRecyclebin);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarSequences.setShowFieldType(false);
    statusBarSequences.setShowOpenTime(false);
    statusBarSequences.setTable(tableRecyclebin);
    jPanel1.add(statusBarSequences, java.awt.BorderLayout.SOUTH);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);

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
  if (!tableRecyclebin.getQuery().isActive()) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmFlashbackObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFlashbackObjectActionPerformed
  if (tableRecyclebin.getSelectedRow() >= 0) {
    try {
      tableRecyclebin.getQuery().getRecord(tableRecyclebin.getSelectedRow());
      if (dbaRole) {
        if (SqlCodeWizardDialog.show(new FlashbackObjectWizard(getDatabase(), tableRecyclebin.getQuery().fieldByName("owner").getString(), tableRecyclebin.getQuery().fieldByName("object_name").getString()), true) != null) {
          refresh(null);
        }
      }
      else if (SqlCodeWizardDialog.show(new FlashbackObjectWizard(getDatabase(), tableRecyclebin.getQuery().fieldByName("object_name").getString()), true) != null) {
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFlashbackObjectActionPerformed

private void cmPurgeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPurgeObjectActionPerformed
  if (tableRecyclebin.getSelectedRow() >= 0) {
    try {
      tableRecyclebin.getQuery().getRecord(tableRecyclebin.getSelectedRow());
      if (dbaRole) {
        if (SqlCodeWizardDialog.show(new PurgeObjectWizard(getDatabase(), tableRecyclebin.getQuery().fieldByName("owner").getString(), tableRecyclebin.getQuery().fieldByName("object_name").getString()), true) != null) {
          refresh(null);
        }
      }
      else {
        if (SqlCodeWizardDialog.show(new PurgeObjectWizard(getDatabase(), tableRecyclebin.getQuery().fieldByName("object_name").getString()), true) != null) {
          refresh(null);
        }
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmPurgeObjectActionPerformed

private void cmPurgeRecyclebinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPurgeRecyclebinActionPerformed
  if (SqlCodeWizardDialog.show(new PurgeRecyclebinWizard(getDatabase()), true) != null) {
    refresh(null);
  }
}//GEN-LAST:event_cmPurgeRecyclebinActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFlashbackObject;
  private pl.mpak.sky.gui.swing.Action cmPurgeObject;
  private pl.mpak.sky.gui.swing.Action cmPurgeRecyclebin;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuFlashback;
  private javax.swing.JMenuItem menuPurgeObject;
  private javax.swing.JMenuItem menuPurgeRecyclebin;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarSequences;
  private ViewTable tableRecyclebin;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

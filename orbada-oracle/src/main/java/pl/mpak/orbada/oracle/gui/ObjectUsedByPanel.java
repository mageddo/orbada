package pl.mpak.orbada.oracle.gui;

import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.freezing.FreezeFactory;
import pl.mpak.orbada.oracle.gui.freezing.FreezeViewService;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class ObjectUsedByPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentObjectName = "";
  private String objectType;
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private FreezeFactory freezeFactory;
  
  /** Creates new form TableIndexesPanel 
   * @param accesibilities 
   */
  public ObjectUsedByPanel(IViewAccesibilities accesibilities, String objectType) {
    this.accesibilities = accesibilities;
    this.objectType = objectType;
    freezeFactory = new FreezeFactory(accesibilities);
    initComponents();
    init();
  }
  
  private void init() {
    tableUsedBy.getQuery().setDatabase(getDatabase());
    try {
      tableUsedBy.addColumn(new QueryTableColumn("schema_name", stringManager.getString("schema"), 120));
      tableUsedBy.addColumn(new QueryTableColumn("object_name", stringManager.getString("object-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableUsedBy.addColumn(new QueryTableColumn("object_type", stringManager.getString("object-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableUsedBy.addColumn(new QueryTableColumn("dependency_type", stringManager.getString("dependency-type"), 80));
      tableUsedBy.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 50));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("owner", stringManager.getString("schema"), (String[])null));
      def.add(new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null));
      def.add(new SqlFilterDefComponent("type", stringManager.getString("object-type"), new String[] {"", "'TABLE'", "'VIEW'", "'FUNCTION'", "'PROCEDURE'", "'PACKAGE'"}));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-" +objectType +"-used-by-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableUsedBy, buttonActions, menuActions, "oracle-used-by-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("ObjectUsedByPanel-title");
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
      cmRefresh.setEnabled(false);
      try {
        requestRefresh = false;
        tableUsedBy.getQuery().close();
        tableUsedBy.getQuery().setSqlText(Sql.getUsedByList(filter.getSqlText()));
        tableUsedBy.getQuery().paramByName("schema_name").setString(currentSchemaName);
        tableUsedBy.getQuery().paramByName("object_name").setString(currentObjectName);
        tableUsedBy.getQuery().paramByName("object_type").setString(objectType);
        tableUsedBy.getQuery().open();
        if (!tableUsedBy.getQuery().isEmpty()) {
          tableUsedBy.changeSelection(0, 0);
        }
      } catch (Exception ex) {
        MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
      }
    }
    finally {
      cmRefresh.setEnabled(true);
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
    closing = true;
    tableUsedBy.getQuery().close();
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
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    statusBarIndexes = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarIndexes = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonFreeze = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableUsedBy = new ViewTable();

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

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    statusBarIndexes.setShowFieldType(false);
    statusBarIndexes.setShowOpenTime(false);
    statusBarIndexes.setTable(tableUsedBy);
    add(statusBarIndexes, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarIndexes.setFloatable(false);
    toolBarIndexes.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(buttonFilter);
    toolBarIndexes.add(jSeparator1);

    buttonFreeze.setAction(cmFreezeObject);
    buttonFreeze.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFreeze.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(buttonFreeze);
    toolBarIndexes.add(buttonActions);

    jPanel1.add(toolBarIndexes);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableUsedBy);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableUsedBy.getSelectedRow() >= 0) {
    try {
      tableUsedBy.getQuery().getRecord(tableUsedBy.getSelectedRow());
      String schemaName = tableUsedBy.getQuery().fieldByName("schema_name").getString();
      String objectName = tableUsedBy.getQuery().fieldByName("object_name").getString();
      String objectType_ = tableUsedBy.getQuery().fieldByName("object_type").getString();
      FreezeViewService service = freezeFactory.createInstance(objectType_, schemaName, objectName);
      if (service != null) {
        accesibilities.createView(service);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreeze;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarIndexes;
  private ViewTable tableUsedBy;
  private javax.swing.JToolBar toolBarIndexes;
  // End of variables declaration//GEN-END:variables
  
}

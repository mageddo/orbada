package pl.mpak.orbada.oracle.gui.tables;

import java.awt.Component;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.table.GrantTablePrivilegesWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.RevokeTablePrivilegesWizard;
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
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class TableGrantsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  
  /** Creates new form TableIndexesPanel 
   * @param accesibilities 
   */
  public TableGrantsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableGrants.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableGrants.getSelectedRow();
        if (rowIndex >= 0 && tableGrants.getQuery().isActive()) {
          try {
            tableGrants.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableGrants.getQuery().setDatabase(getDatabase());
    try {
      tableGrants.addColumn(new QueryTableColumn("grantee", stringManager.getString("grantee"), 120));
      tableGrants.addColumn(new QueryTableColumn("privilege", stringManager.getString("privilege"), 160, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableGrants.addColumn(new QueryTableColumn("grantor", stringManager.getString("grantor"), 120));
      tableGrants.addColumn(new QueryTableColumn("grantable", stringManager.getString("grantable"), 70, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("YES")) {
            ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
          }
        }
      })));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("privilege", stringManager.getString("privilege"), (String[])null));
      def.add(new SqlFilterDefComponent("grantee", stringManager.getString("grantee"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-table-grants-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableGrants, buttonActions, menuActions, "oracle-table-grants-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableGrantsPanel-title");
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
      requestRefresh = false;
      tableGrants.getQuery().close();
      tableGrants.getQuery().setSqlText(Sql.getPrivilegeList(filter.getSqlText()));
      tableGrants.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableGrants.getQuery().paramByName("table_name").setString(currentTableName);
      tableGrants.getQuery().open();
      if (!tableGrants.getQuery().isEmpty()) {
        tableGrants.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentTableName = objectName;
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
    tableGrants.getQuery().close();
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
    menuGrants = new javax.swing.JMenuItem();
    menuRevoke = new javax.swing.JMenuItem();
    cmGrants = new pl.mpak.sky.gui.swing.Action();
    cmRevoke = new pl.mpak.sky.gui.swing.Action();
    statusBarIndexes = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarIndexes = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableGrants = new pl.mpak.orbada.gui.comps.table.ViewTable();

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

    menuGrants.setAction(cmGrants);
    menuActions.add(menuGrants);

    menuRevoke.setAction(cmRevoke);
    menuActions.add(menuRevoke);

    cmGrants.setActionCommandKey("cmGrant");
    cmGrants.setText(stringManager.getString("cmGrants-text")); // NOI18N
    cmGrants.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmGrantsActionPerformed(evt);
      }
    });

    cmRevoke.setActionCommandKey("cmRevoke");
    cmRevoke.setText(stringManager.getString("cmRevoke-text")); // NOI18N
    cmRevoke.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRevokeActionPerformed(evt);
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
    statusBarIndexes.setTable(tableGrants);
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
    toolBarIndexes.add(buttonActions);

    jPanel1.add(toolBarIndexes);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableGrants);

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

private void cmGrantsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGrantsActionPerformed
  if (SqlCodeWizardDialog.show(new GrantTablePrivilegesWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmGrantsActionPerformed

private void cmRevokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRevokeActionPerformed
  if (SqlCodeWizardDialog.show(new RevokeTablePrivilegesWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmRevokeActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmGrants;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRevoke;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuGrants;
  private javax.swing.JMenuItem menuRevoke;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarIndexes;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableGrants;
  private javax.swing.JToolBar toolBarIndexes;
  // End of variables declaration//GEN-END:variables
  
}

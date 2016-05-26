package pl.mpak.orbada.firebird.gui.procedures;

import java.awt.Component;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.wizards.GrantProcedurePrivilegesWizard;
import pl.mpak.orbada.firebird.gui.wizards.RevokeProcedurePrivilegesWizard;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ITabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
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
public class ProcedureGrantsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  
  /** Creates new form TableIndexesPanel 
   * @param accesibilities 
   */
  public ProcedureGrantsPanel(IViewAccesibilities accesibilities) {
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
      tableGrants.addColumn(new QueryTableColumn("TABLE_NAME", stringManager.getString("ProcedureGrantsPanel-object"), 120));
      tableGrants.addColumn(new QueryTableColumn("USER_NAME", stringManager.getString("ProcedureGrantsPanel-grant-for"), 120));
      tableGrants.addColumn(new QueryTableColumn("PRIVILEGE", stringManager.getString("ProcedureGrantsPanel-right"), 160, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableGrants.addColumn(new QueryTableColumn("GRANTOR", stringManager.getString("ProcedureGrantsPanel-grantor"), 120));
      tableGrants.addColumn(new QueryTableColumn("GRANT_OPTION", stringManager.getString("ProcedureGrantsPanel-grant-option"), 70, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("Y")) {
            ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
          }
        }
      })));
      tableGrants.addColumn(new QueryTableColumn("TYPE_NAME", stringManager.getString("ProcedureGrantsPanel-object-type"), 120));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("PRIVILEGE", stringManager.getString("ProcedureGrantsPanel-right"), (String[])null));
      def.add(new SqlFilterDefComponent("USER_NAME", stringManager.getString("ProcedureGrantsPanel-grant-for"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-view-grants-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableGrants, buttonActions, menuActions, "firebird-procedure-grants-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("ProcedureGrantsPanel-grants");
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
    if (!currentTableName.equals(objectName) || requestRefresh) {
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
    menuGrant = new javax.swing.JMenuItem();
    menuRevoke = new javax.swing.JMenuItem();
    cmGrant = new pl.mpak.sky.gui.swing.Action();
    cmRevoke = new pl.mpak.sky.gui.swing.Action();
    statusBarIndexes = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarIndexes = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableGrants = new ViewTable();

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

    menuGrant.setAction(cmGrant);
    menuActions.add(menuGrant);

    menuRevoke.setAction(cmRevoke);
    menuActions.add(menuRevoke);

    cmGrant.setActionCommandKey("cmGrant");
    cmGrant.setText(stringManager.getString("ProcedureGrantsPanel-cmGrant-text")); // NOI18N
    cmGrant.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmGrantActionPerformed(evt);
      }
    });

    cmRevoke.setActionCommandKey("cmRevoke");
    cmRevoke.setText(stringManager.getString("ProcedureGrantsPanel-cmRevoke-text")); // NOI18N
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

private void cmGrantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGrantActionPerformed
  if (SqlCodeWizardDialog.show(new GrantProcedurePrivilegesWizard(getDatabase(), currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmGrantActionPerformed

private void cmRevokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRevokeActionPerformed
  if (SqlCodeWizardDialog.show(new RevokeProcedurePrivilegesWizard(getDatabase(), currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmRevokeActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmGrant;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRevoke;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuGrant;
  private javax.swing.JMenuItem menuRevoke;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarIndexes;
  private ViewTable tableGrants;
  private javax.swing.JToolBar toolBarIndexes;
  // End of variables declaration//GEN-END:variables
  
}

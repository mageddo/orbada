package pl.mpak.orbada.firebird.gui.tables;

import java.awt.Dialog;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import orbada.gui.ITabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintCheckWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintForeignKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintPrimaryKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableConstraintsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentTableName = "";
  private boolean closing = false;
  private boolean requestRefresh = false;
  private SqlFilter filter;
  
  public TableConstraintsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableConstraints.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableConstraints.getSelectedRow();
        if (rowIndex >= 0 && tableConstraints.getQuery().isActive()) {
          try {
            tableConstraints.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableConstraints.getQuery().setDatabase(getDatabase());
    try {
      tableConstraints.addColumn(new QueryTableColumn("CONSTRAINT_NAME", stringManager.getString("TableConstraintsPanel-constraint-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableConstraints.addColumn(new QueryTableColumn("CONSTRAINT_TYPE", stringManager.getString("TableConstraintsPanel-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableConstraints.addColumn(new QueryTableColumn("COLUMNS", stringManager.getString("TableConstraintsPanel-columns"), 150));
      tableConstraints.addColumn(new QueryTableColumn("FK_RULE", stringManager.getString("TableConstraintsPanel-fk-rule"), 150));
      tableConstraints.addColumn(new QueryTableColumn("CHECK_CLAUSE", stringManager.getString("TableConstraintsPanel-check-clause"), 150));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("CONSTRAINT_NAME", stringManager.getString("TableConstraintsPanel-constraint-name"), (String[])null));
      def.add(new SqlFilterDefComponent("CONSTRAINT_TYPE", stringManager.getString("TableConstraintsPanel-constraint-type"), new String[] {"", "'PRIMARY KEY'", "'FOREIGN KEY'", "'CHECK'"}));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-table-constraints-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableConstraints, buttonActions, menuActions, "firebird-table-constraints-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableConstraints.getQuery().close();
      tableConstraints.getQuery().setSqlText(Sql.getConstraintList(filter.getSqlText()));
      tableConstraints.getQuery().paramByName("table_name").setString(currentTableName);
      tableConstraints.getQuery().open();
      if (!tableConstraints.getQuery().isEmpty()) {
        tableConstraints.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
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
    tableConstraints.getQuery().close();
    accesibilities = null;
  }
  
  public String getTitle() {
    return stringManager.getString("TableConstraintsPanel-constraints");
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
    cmDropConstraint = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintCheck = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintFK = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintPK = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableConstraints = new ViewTable();
    statusBarConstraints = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarConstraints = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JSeparator();
    buttonCreateConstraintCheck = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonCreateConstraintFK = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonCreateConstraintPrimaryKey = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonDtopConstraint = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
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

    cmDropConstraint.setActionCommandKey("cmDropConstraint");
    cmDropConstraint.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropConstraint.setText(stringManager.getString("TableConstraintsPanel-cmDropConstraint-text")); // NOI18N
    cmDropConstraint.setTooltip(stringManager.getString("TableConstraintsPanel-cmDropConstraint-hint")); // NOI18N
    cmDropConstraint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropConstraintActionPerformed(evt);
      }
    });

    cmCreateConstraintCheck.setActionCommandKey("cmCreateConstraintCheck");
    cmCreateConstraintCheck.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/verify_document_16.gif")); // NOI18N
    cmCreateConstraintCheck.setText(stringManager.getString("TableConstraintsPanel-cmCreateConstraintCheck-text")); // NOI18N
    cmCreateConstraintCheck.setTooltip(stringManager.getString("TableConstraintsPanel-cmCreateConstraintCheck-hint")); // NOI18N
    cmCreateConstraintCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintCheckActionPerformed(evt);
      }
    });

    cmCreateConstraintFK.setActionCommandKey("cmCreateConstraintFK");
    cmCreateConstraintFK.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/foreign_key16.gif")); // NOI18N
    cmCreateConstraintFK.setText(stringManager.getString("TableConstraintsPanel-cmCreateConstraintFK-text")); // NOI18N
    cmCreateConstraintFK.setTooltip(stringManager.getString("TableConstraintsPanel-cmCreateConstraintFK-hint")); // NOI18N
    cmCreateConstraintFK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintFKActionPerformed(evt);
      }
    });

    cmCreateConstraintPK.setActionCommandKey("cmCreateConstraintPrimaryKey");
    cmCreateConstraintPK.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/primary_key16.gif")); // NOI18N
    cmCreateConstraintPK.setText(stringManager.getString("TableConstraintsPanel-cmCreateConstraintPK-text")); // NOI18N
    cmCreateConstraintPK.setTooltip(stringManager.getString("TableConstraintsPanel-cmCreateConstraintPK-hint")); // NOI18N
    cmCreateConstraintPK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintPKActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableConstraints);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarConstraints.setShowFieldType(false);
    statusBarConstraints.setShowOpenTime(false);
    statusBarConstraints.setTable(tableConstraints);
    add(statusBarConstraints, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarConstraints.setFloatable(false);
    toolBarConstraints.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonFilter);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarConstraints.add(jSeparator2);

    buttonCreateConstraintCheck.setAction(cmCreateConstraintCheck);
    buttonCreateConstraintCheck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintCheck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonCreateConstraintCheck);

    buttonCreateConstraintFK.setAction(cmCreateConstraintFK);
    buttonCreateConstraintFK.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintFK.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonCreateConstraintFK);

    buttonCreateConstraintPrimaryKey.setAction(cmCreateConstraintPK);
    buttonCreateConstraintPrimaryKey.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintPrimaryKey.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonCreateConstraintPrimaryKey);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarConstraints.add(jSeparator1);

    buttonDtopConstraint.setAction(cmDropConstraint);
    buttonDtopConstraint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDtopConstraint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonDtopConstraint);
    toolBarConstraints.add(jSeparator3);
    toolBarConstraints.add(buttonActions);

    jPanel1.add(toolBarConstraints);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

  private void cmCreateConstraintPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintPKActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintPrimaryKeyWizardPanel(getDatabase(), null, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintPKActionPerformed

private void cmCreateConstraintFKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintFKActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintForeignKeyWizardPanel(getDatabase(), null, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintFKActionPerformed

private void cmCreateConstraintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintCheckActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(getDatabase(), null, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintCheckActionPerformed

  private void cmDropConstraintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropConstraintActionPerformed
  if (tableConstraints.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableConstraints.getQuery().getRecord(tableConstraints.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(tableConstraints.getQuery().fieldByName("constraint_name").getString());
      String tableName = SQLUtil.createSqlName(currentTableName);
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TableConstraintsPanel-drop-constraint-q"), new Object[] {objectName, tableName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("alter table " +tableName +" drop constraint " +objectName, true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmDropConstraintActionPerformed

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refresh(null, null, currentTableName);
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintFK;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintPrimaryKey;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDtopConstraint;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintFK;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintPK;
  private pl.mpak.sky.gui.swing.Action cmDropConstraint;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarConstraints;
  private ViewTable tableConstraints;
  private javax.swing.JToolBar toolBarConstraints;
  // End of variables declaration//GEN-END:variables
  
}

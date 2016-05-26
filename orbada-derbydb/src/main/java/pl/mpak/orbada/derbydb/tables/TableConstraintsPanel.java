package pl.mpak.orbada.derbydb.tables;

import java.awt.Component;
import java.awt.Dialog;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.AlterTableNullWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintCheckWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintForeignKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintPrimaryKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;

/**
 *
 * @author  akaluza
 */
public class TableConstraintsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private SqlFilter filter;
  
  private Timer timer;
  
  /** Creates new form TableConstraintsPanel 
   * @param accesibilities 
   */
  public TableConstraintsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refresh();
      }
    };
    OrbadaDerbyDbPlugin.getRefreshQueue().add(timer);
    
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
      tableConstraints.addColumn(new QueryTableColumn("constraintname", stringManager.getString("TableConstraintsPanel-constraint-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableConstraints.addColumn(new QueryTableColumn("type", stringManager.getString("TableConstraintsPanel-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableConstraints.addColumn(new QueryTableColumn("enabled", stringManager.getString("TableConstraintsPanel-enabled"), 50,new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          ((JLabel)renderer).setHorizontalAlignment(JLabel.CENTER);
          if (StringUtil.nvl(value, "").toString().equals("Y")) {
            ((JLabel)renderer).setText(stringManager.getString("yes"));
          }
          else {
            ((JLabel)renderer).setText("-");
          }
        }
      })));
      tableConstraints.addColumn(new QueryTableColumn("tablename_fk", stringManager.getString("TableConstraintsPanel-foreign-key-table"), 150));
      tableConstraints.addColumn(new QueryTableColumn("constraintname_fk", stringManager.getString("TableConstraintsPanel-foreign-key-constraint-name"), 150));
      tableConstraints.addColumn(new QueryTableColumn("deleterule_fk", stringManager.getString("TableConstraintsPanel-delete-rule"), 100));
      tableConstraints.addColumn(new QueryTableColumn("updaterule_fk", stringManager.getString("TableConstraintsPanel-update-rule"), 100));
      tableConstraints.addColumn(new QueryTableColumn("checkdefinition", stringManager.getString("TableConstraintsPanel-check-rule"), 200));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("cs.constraintname", stringManager.getString("TableConstraintsPanel-constraint-name"), (String[])null));
      def.add(new SqlFilterDefComponent("(case when cs.type = 'P' then 'Primary Key' when cs.type = 'F' then 'Foreign Key' when cs.type = 'C' then 'Check' when cs.type = 'U' then 'Unique' end)", stringManager.getString("TableConstraintsPanel-constraint-type"), new String[] {"", "'Primary Key'", "'Foreign Key'", "'Check'", "'Unique'"}));
      def.add(new SqlFilterDefComponent("tfk.tablename", stringManager.getString("TableConstraintsPanel-alien-table"), (String[])null));
      def.add(new SqlFilterDefComponent("csfk.constraintname", stringManager.getString("TableConstraintsPanel-alien-constraint-name"), (String[])null));
      def.add(new SqlFilterDefComponent("cs.state = 'D'", stringManager.getString("TableConstraintsPanel-constraint-enabled")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-table-constraints-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableConstraints, buttonActions, menuActions, "derbydb-table-constraints-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableConstraintsPanel-title");
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableConstraints.getQuery().close();
      tableConstraints.getQuery().setSqlText(DerbyDbSql.getTableConstraintList(filter.getSqlText()));
      tableConstraints.getQuery().paramByName("schemaname").setString(currentSchemaName);
      tableConstraints.getQuery().paramByName("tablename").setString(currentTableName);
      tableConstraints.getQuery().open();
      if (!tableConstraints.getQuery().isEmpty()) {
        tableConstraints.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentTableName = objectName;
      if (isVisible()) {
        timer.restart();
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
    timer.cancel();
    tableConstraints.getQuery().close();
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
    cmDropConstraint = new pl.mpak.sky.gui.swing.Action();
    cmSwitchEnabled = new pl.mpak.sky.gui.swing.Action();
    cmColumnNotNull = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintCheck = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintFK = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintPrimaryKey = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableConstraints = new ViewTable();
    statusBarConstraints = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarConstraints = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JSeparator();
    buttonSwitchEnabled = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JSeparator();
    buttonColumnNotNull = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonCreateConstraintCheck = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonCreateConstraintFK = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonCreateConstraintPrimaryKey = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonDtopConstraint = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
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
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/derbydb/res/icons/filter16.gif")); // NOI18N
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

    cmSwitchEnabled.setActionCommandKey("cmSwitchEnabled");
    cmSwitchEnabled.setEnabled(false);
    cmSwitchEnabled.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmSwitchEnabled.setText(stringManager.getString("TableConstraintsPanel-cmSwitchEnabled-text")); // NOI18N
    cmSwitchEnabled.setTooltip(stringManager.getString("TableConstraintsPanel-cmSwitchEnabled-hint")); // NOI18N

    cmColumnNotNull.setActionCommandKey("cmColumnNotNull");
    cmColumnNotNull.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/lock16.gif")); // NOI18N
    cmColumnNotNull.setText(stringManager.getString("TableConstraintsPanel-cmColumnNotNull-text")); // NOI18N
    cmColumnNotNull.setTooltip(stringManager.getString("TableConstraintsPanel-cmColumnNotNull-hint")); // NOI18N
    cmColumnNotNull.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColumnNotNullActionPerformed(evt);
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

    cmCreateConstraintPrimaryKey.setActionCommandKey("cmCreateConstraintPrimaryKey");
    cmCreateConstraintPrimaryKey.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/primary_key16.gif")); // NOI18N
    cmCreateConstraintPrimaryKey.setText(stringManager.getString("TableConstraintsPanel-cmCreateConstraintPrimaryKey-text")); // NOI18N
    cmCreateConstraintPrimaryKey.setTooltip(stringManager.getString("TableConstraintsPanel-cmCreateConstraintPrimaryKey-hint")); // NOI18N
    cmCreateConstraintPrimaryKey.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintPrimaryKeyActionPerformed(evt);
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

    buttonSwitchEnabled.setAction(cmSwitchEnabled);
    buttonSwitchEnabled.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSwitchEnabled.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonSwitchEnabled);

    jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarConstraints.add(jSeparator3);

    buttonColumnNotNull.setAction(cmColumnNotNull);
    buttonColumnNotNull.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonColumnNotNull.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonColumnNotNull);

    buttonCreateConstraintCheck.setAction(cmCreateConstraintCheck);
    buttonCreateConstraintCheck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintCheck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonCreateConstraintCheck);

    buttonCreateConstraintFK.setAction(cmCreateConstraintFK);
    buttonCreateConstraintFK.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintFK.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonCreateConstraintFK);

    buttonCreateConstraintPrimaryKey.setAction(cmCreateConstraintPrimaryKey);
    buttonCreateConstraintPrimaryKey.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCreateConstraintPrimaryKey.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonCreateConstraintPrimaryKey);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarConstraints.add(jSeparator1);

    buttonDtopConstraint.setAction(cmDropConstraint);
    buttonDtopConstraint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDtopConstraint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonDtopConstraint);
    toolBarConstraints.add(jSeparator4);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonActions);

    jPanel1.add(toolBarConstraints);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmCreateConstraintPrimaryKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintPrimaryKeyActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintPrimaryKeyWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintPrimaryKeyActionPerformed

private void cmCreateConstraintFKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintFKActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintForeignKeyWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintFKActionPerformed

private void cmCreateConstraintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintCheckActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintCheckActionPerformed

private void cmColumnNotNullActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColumnNotNullActionPerformed
  try {
    SqlCodeWizardDialog.show(new AlterTableNullWizardPanel(getDatabase(), currentSchemaName, currentTableName, null, "ALTER"), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmColumnNotNullActionPerformed

  private void cmDropConstraintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropConstraintActionPerformed
  if (tableConstraints.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableConstraints.getQuery().getRecord(tableConstraints.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableConstraints.getQuery().fieldByName("constraintname").getString(), getDatabase());
      String tableName = SQLUtil.createSqlName(currentSchemaName, currentTableName, getDatabase());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), stringManager.getString("TableConstraintsPanel-delete-constraint-q"), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("alter table " +tableName +" drop constraint " +objectName, true);
        timer.restart();
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
  refresh(null, currentSchemaName, currentTableName);
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonColumnNotNull;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintFK;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCreateConstraintPrimaryKey;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDtopConstraint;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSwitchEnabled;
  private pl.mpak.sky.gui.swing.Action cmColumnNotNull;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintFK;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintPrimaryKey;
  private pl.mpak.sky.gui.swing.Action cmDropConstraint;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSwitchEnabled;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarConstraints;
  private ViewTable tableConstraints;
  private javax.swing.JToolBar toolBarConstraints;
  // End of variables declaration//GEN-END:variables
  
}

package pl.mpak.orbada.oracle.gui.seq;

import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;

import orbada.gui.comps.table.ViewTable;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.AlterSequenceWizard;
import pl.mpak.orbada.oracle.gui.wizards.CreateSequenceWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropSequenceWizard;
import pl.mpak.orbada.oracle.gui.wizards.RecreateSequenceWizard;
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
public class SequencesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  public SequencesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    tableSequences.getQuery().setDatabase(getDatabase());
    try {
      tableSequences.addColumn(new QueryTableColumn("sequence_name", stringManager.getString("sequence-name"), 190, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableSequences.addColumn(new QueryTableColumn("last_number", stringManager.getString("last-value"), 120));
      tableSequences.addColumn(new QueryTableColumn("min_value", stringManager.getString("min-value"), 120));
      tableSequences.addColumn(new QueryTableColumn("max_value", stringManager.getString("max-value"), 180));
      tableSequences.addColumn(new QueryTableColumn("increment_by", stringManager.getString("increment-by"), 120));
      tableSequences.addColumn(new QueryTableColumn("cycle_flag", stringManager.getString("cycle-flag"), 30));
      tableSequences.addColumn(new QueryTableColumn("order_flag", stringManager.getString("order-flag"), 30));
      tableSequences.addColumn(new QueryTableColumn("cache_size", stringManager.getString("cache-size"), 80));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("sequence_name", stringManager.getString("sequence-name"), (String[])null));
      def.add(new SqlFilterDefComponent("cycle_flag = 'Y'", stringManager.getString("are-cycle-flag")));
      def.add(new SqlFilterDefComponent("order_flag = 'Y'", stringManager.getString("are-order-flag")));
      def.add(new SqlFilterDefComponent("nvl( cache_size, 0 ) > 0", stringManager.getString("are-cache-size")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-sequences-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableSequences, buttonActions, menuActions, "oracle-sequences-actions");
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
      if (tableSequences.getQuery().isActive() && tableSequences.getSelectedRow() >= 0) {
        tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
        objectName = tableSequences.getQuery().fieldByName("sequence_name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableSequences.getSelectedColumn();
      int index = Math.max(0, tableSequences.getSelectedRow());
      tableSequences.getQuery().close();
      tableSequences.getQuery().setSqlText(Sql.getSequenceList(filter.getSqlText()));
      tableSequences.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableSequences.getQuery().open();
      if (objectName != null && tableSequences.getQuery().locate("sequence_name", new Variant(objectName))) {
        tableSequences.changeSelection(tableSequences.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableSequences.getQuery().isEmpty()) {
        tableSequences.changeSelection(Math.min(index, tableSequences.getRowCount() -1), column);
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
    tableSequences.getQuery().close();
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
    menuCreateSequence = new javax.swing.JMenuItem();
    menuAlterSequence = new javax.swing.JMenuItem();
    menuRecreateSequence = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropSequence = new javax.swing.JMenuItem();
    cmCreateSequence = new pl.mpak.sky.gui.swing.Action();
    cmAlterSequence = new pl.mpak.sky.gui.swing.Action();
    cmRecreateSequence = new pl.mpak.sky.gui.swing.Action();
    cmDropSequence = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSequences = new ViewTable();
    statusBarSequences = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
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

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    menuCreateSequence.setAction(cmCreateSequence);
    menuActions.add(menuCreateSequence);

    menuAlterSequence.setAction(cmAlterSequence);
    menuActions.add(menuAlterSequence);

    menuRecreateSequence.setAction(cmRecreateSequence);
    menuRecreateSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuRecreateSequenceActionPerformed(evt);
      }
    });
    menuActions.add(menuRecreateSequence);
    menuActions.add(jSeparator2);

    menuDropSequence.setAction(cmDropSequence);
    menuActions.add(menuDropSequence);

    cmCreateSequence.setActionCommandKey("cmCreateSequence");
    cmCreateSequence.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/sequence.gif")); // NOI18N
    cmCreateSequence.setText(stringManager.getString("cmCreateSequence-text")); // NOI18N
    cmCreateSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateSequenceActionPerformed(evt);
      }
    });

    cmAlterSequence.setActionCommandKey("cmAlterSequence");
    cmAlterSequence.setText(stringManager.getString("cmAlterSequence-text")); // NOI18N
    cmAlterSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAlterSequenceActionPerformed(evt);
      }
    });

    cmRecreateSequence.setActionCommandKey("cmRecreateSequence");
    cmRecreateSequence.setText(stringManager.getString("cmRecreateSequence-text")); // NOI18N

    cmDropSequence.setActionCommandKey("cmDropSequence");
    cmDropSequence.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropSequence.setText(stringManager.getString("cmDropSequence-text")); // NOI18N
    cmDropSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropSequenceActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableSequences);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarSequences.setShowFieldType(false);
    statusBarSequences.setShowOpenTime(false);
    statusBarSequences.setTable(tableSequences);
    jPanel1.add(statusBarSequences, java.awt.BorderLayout.SOUTH);

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

    toolButton1.setAction(cmCreateSequence);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(toolButton1);
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
  if (!tableSequences.getQuery().isActive()) {
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

private void cmCreateSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateSequenceActionPerformed
  if (SqlCodeWizardDialog.show(new CreateSequenceWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateSequenceActionPerformed

private void cmAlterSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAlterSequenceActionPerformed
  if (tableSequences.getSelectedRow() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
      String sequenceName = tableSequences.getQuery().fieldByName("sequence_name").getString();
      if (SqlCodeWizardDialog.show(new AlterSequenceWizard(getDatabase(), currentSchemaName, sequenceName), true) != null) {
        refresh(sequenceName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmAlterSequenceActionPerformed

private void menuRecreateSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRecreateSequenceActionPerformed
  if (tableSequences.getSelectedRow() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
      String sequenceName = tableSequences.getQuery().fieldByName("sequence_name").getString();
      if (SqlCodeWizardDialog.show(new RecreateSequenceWizard(getDatabase(), currentSchemaName, sequenceName), true) != null) {
        refresh(sequenceName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_menuRecreateSequenceActionPerformed

private void cmDropSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropSequenceActionPerformed
  if (tableSequences.getSelectedRow() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
      String sequenceName = tableSequences.getQuery().fieldByName("sequence_name").getString();
      if (SqlCodeWizardDialog.show(new DropSequenceWizard(getDatabase(), currentSchemaName, sequenceName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropSequenceActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmAlterSequence;
  private pl.mpak.sky.gui.swing.Action cmCreateSequence;
  private pl.mpak.sky.gui.swing.Action cmDropSequence;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRecreateSequence;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuAlterSequence;
  private javax.swing.JMenuItem menuCreateSequence;
  private javax.swing.JMenuItem menuDropSequence;
  private javax.swing.JMenuItem menuRecreateSequence;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarSequences;
  private ViewTable tableSequences;
  private javax.swing.JToolBar toolBar;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  // End of variables declaration//GEN-END:variables

}

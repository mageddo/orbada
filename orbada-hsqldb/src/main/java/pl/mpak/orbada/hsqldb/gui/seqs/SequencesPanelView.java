package pl.mpak.orbada.hsqldb.gui.seqs;

import java.awt.Dialog;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.cm.CreateSequenceWizardAction;
import pl.mpak.orbada.hsqldb.gui.wizards.AlterSequenceWizardPanel;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.ToolButton;
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
public class SequencesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** Creates new form TableTriggersPanel
   * @param accesibilities
   */
  public SequencesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = HSqlDbInfoProvider.getCurrentSchema(getDatabase());
    
    toolBarTriggers.add(new ToolButton(new CreateSequenceWizardAction(getDatabase()) {
      @Override
      public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        refresh();
      }
    }));
    
    tableSequences.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableSequences.getSelectedRow();
        if (rowIndex >= 0 && tableSequences.getQuery().isActive()) {
          try {
            tableSequences.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableSequences.getQuery().setDatabase(getDatabase());
    try {
      tableSequences.addColumn(new QueryTableColumn("sequence_name", stringManager.getString("sequence-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableSequences.addColumn(new QueryTableColumn("data_type", stringManager.getString("dtd-identifier"), 100));
      tableSequences.addColumn(new QueryTableColumn("maximum_value", stringManager.getString("maximum-value"), 70));
      tableSequences.addColumn(new QueryTableColumn("minimum_value", stringManager.getString("minimum-value"), 70));
      tableSequences.addColumn(new QueryTableColumn("increment", stringManager.getString("increment"), 70));
      tableSequences.addColumn(new QueryTableColumn("cycle_option", stringManager.getString("cycle-option"), 40));
      tableSequences.addColumn(new QueryTableColumn("start_with", stringManager.getString("start-with"), 70));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("sequence_name", stringManager.getString("sequence-name"), (String[])null));
      def.add(new SqlFilterDefComponent("data_type", stringManager.getString("dtd-identifier"), new String[] {"INTEGER", "BIGINT"}));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-sequences-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableSequences, buttonActions, menuActions, "hsqldb-sequences-actions");
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(HSqlDbInfoProvider.getCurrentSchema(getDatabase()))) {
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
      String seqName = null;
      if (tableSequences.getQuery().isActive() && tableSequences.getSelectedRow() >= 0) {
        tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
        seqName = tableSequences.getQuery().fieldByName("sequence_name").getString();
      }
      refresh(seqName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int index = Math.max(0, tableSequences.getSelectedRow());
      tableSequences.getQuery().close();
      tableSequences.getQuery().setSqlText(Sql.getSequenceList(filter.getSqlText(), HSqlDbInfoProvider.getVersionTest(getDatabase())));
      tableSequences.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableSequences.getQuery().open();
      if (objectName != null && tableSequences.getQuery().locate("sequence_name", new Variant(objectName))) {
        tableSequences.changeSelection(tableSequences.getQuery().getCurrentRecord().getIndex(), tableSequences.getSelectedColumn());
      } else if (!tableSequences.getQuery().isEmpty()) {
        tableSequences.changeSelection(Math.min(index, tableSequences.getRowCount() -1), tableSequences.getSelectedColumn());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  @Override
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
    cmDropSequence = new pl.mpak.sky.gui.swing.Action();
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    cmAlterSequence = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSequences = new ViewTable();
    statusBarSequences = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBarTriggers = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JSeparator();
    buttonDropTrigger = new pl.mpak.sky.gui.swing.comp.ToolButton();
    separator = new javax.swing.JToolBar.Separator();
    buttonAlterSequence = new pl.mpak.sky.gui.swing.comp.ToolButton();
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

    cmDropSequence.setActionCommandKey("cmDropSequence");
    cmDropSequence.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropSequence.setText(stringManager.getString("cmDropSequence-text")); // NOI18N
    cmDropSequence.setTooltip(stringManager.getString("cmDropSequence-hint")); // NOI18N
    cmDropSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropSequenceActionPerformed(evt);
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

    cmAlterSequence.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit_sequence.gif")); // NOI18N
    cmAlterSequence.setText(stringManager.getString("cmAlterSequence-text")); // NOI18N
    cmAlterSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAlterSequenceActionPerformed(evt);
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

    toolBarTriggers.setFloatable(false);
    toolBarTriggers.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonRefresh);

    buttonSelectSchema.setAction(cmSelectSchema);
    buttonSelectSchema.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSelectSchema.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonSelectSchema);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonFilter);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarTriggers.add(jSeparator2);

    buttonDropTrigger.setAction(cmDropSequence);
    buttonDropTrigger.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDropTrigger.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonDropTrigger);
    toolBarTriggers.add(separator);

    buttonAlterSequence.setAction(cmAlterSequence);
    buttonAlterSequence.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonAlterSequence.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonAlterSequence);
    toolBarTriggers.add(buttonActions);

    jPanel2.add(toolBarTriggers);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropSequenceActionPerformed
  if (tableSequences.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableSequences.getQuery().fieldByName("sequence_name").getString(), getDatabase());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("SequencesPanelView-drop-sequence-q"), new Object[] {objectName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop sequence " +objectName, true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropSequenceActionPerformed

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

private void cmAlterSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAlterSequenceActionPerformed
  if (tableSequences.getSelectedRow() != -1) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
      if (SqlCodeWizardDialog.show(new AlterSequenceWizardPanel(getDatabase(), currentSchemaName, tableSequences.getQuery().fieldByName("sequence_name").getString()), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmAlterSequenceActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonAlterSequence;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDropTrigger;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmAlterSequence;
  private pl.mpak.sky.gui.swing.Action cmDropSequence;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JToolBar.Separator separator;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarSequences;
  private ViewTable tableSequences;
  private javax.swing.JToolBar toolBarTriggers;
  // End of variables declaration//GEN-END:variables

}

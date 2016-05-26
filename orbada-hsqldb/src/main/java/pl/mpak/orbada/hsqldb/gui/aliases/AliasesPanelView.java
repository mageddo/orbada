package pl.mpak.orbada.hsqldb.gui.aliases;

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
import pl.mpak.orbada.hsqldb.cm.CreateAliasWizardAction;
import pl.mpak.orbada.hsqldb.cm.GrantClassWizardAction;
import pl.mpak.orbada.hsqldb.cm.RevokeClassWizardAction;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
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
public class AliasesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** Creates new form TableTriggersPanel
   * @param accesibilities
   */
  public AliasesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = HSqlDbInfoProvider.getCurrentSchema(getDatabase());
    
    toolBar.add(new ToolButton(new CreateAliasWizardAction(getDatabase()) {
      @Override
      public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        refresh();
      }
    }));
    toolBar.add(new ToolButton(new GrantClassWizardAction(getDatabase()) {
      @Override
      public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        refresh();
      }
    }));
    toolBar.add(new ToolButton(new RevokeClassWizardAction(getDatabase()) {
      @Override
      public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        refresh();
      }
    }));
    
    tablealiasses.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tablealiasses.getSelectedRow();
        if (rowIndex >= 0 && tablealiasses.getQuery().isActive()) {
          try {
            tablealiasses.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tablealiasses.getQuery().setDatabase(getDatabase());
    try {
      tablealiasses.addColumn(new QueryTableColumn("alias_name", stringManager.getString("alias-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tablealiasses.addColumn(new QueryTableColumn("object_name", stringManager.getString("specification"), 200));
      tablealiasses.addColumn(new QueryTableColumn("num_input_params", stringManager.getString("num-input-parameters"), 30));
      tablealiasses.addColumn(new QueryTableColumn("num_result_sets", stringManager.getString("num-result-sets"), 30));
      tablealiasses.addColumn(new QueryTableColumn("alias_type", stringManager.getString("alias-type"), 70));
      tablealiasses.addColumn(new QueryTableColumn("procedure_type", stringManager.getString("procedure-type"), 70));
      tablealiasses.addColumn(new QueryTableColumn("remarks", stringManager.getString("comment"), 350));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("alias_name", stringManager.getString("alias-name"), (String[])null));
      def.add(new SqlFilterDefComponent("ifnull(p.specific_name, a.object_name)", stringManager.getString("specification"), (String[])null));
      def.add(new SqlFilterDefComponent("alias_type", stringManager.getString("alias-type"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-aliases-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tablealiasses, buttonActions, menuActions, "hsqldb-aliasses-actions");
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
      String aliasName = null;
      if (tablealiasses.getQuery().isActive() && tablealiasses.getSelectedRow() >= 0) {
        tablealiasses.getQuery().getRecord(tablealiasses.getSelectedRow());
        aliasName = tablealiasses.getQuery().fieldByName("ALIAS_NAME").getString();
      }
      refresh(aliasName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int index = Math.max(0, tablealiasses.getSelectedRow());
      tablealiasses.getQuery().close();
      tablealiasses.getQuery().setSqlText(Sql.getAliasList(filter.getSqlText()));
      tablealiasses.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tablealiasses.getQuery().open();
      if (objectName != null && tablealiasses.getQuery().locate("ALIAS_NAME", new Variant(objectName))) {
        tablealiasses.changeSelection(tablealiasses.getQuery().getCurrentRecord().getIndex(), tablealiasses.getSelectedColumn());
      } else if (!tablealiasses.getQuery().isEmpty()) {
        tablealiasses.changeSelection(Math.min(index, tablealiasses.getRowCount() -1), tablealiasses.getSelectedColumn());
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
    tablealiasses.getQuery().close();
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
    cmDropAlias = new pl.mpak.sky.gui.swing.Action();
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tablealiasses = new ViewTable();
    statusBarTriggers = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JSeparator();
    buttonDropTrigger = new pl.mpak.sky.gui.swing.comp.ToolButton();
    separator = new javax.swing.JToolBar.Separator();
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

    cmDropAlias.setActionCommandKey("cmDropTrigger");
    cmDropAlias.setEnabled(false);
    cmDropAlias.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropAlias.setText(stringManager.getString("cmDropAlias-text")); // NOI18N
    cmDropAlias.setTooltip(stringManager.getString("cmDropAlias-hint")); // NOI18N
    cmDropAlias.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropAliasActionPerformed(evt);
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

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tablealiasses);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarTriggers.setShowFieldType(false);
    statusBarTriggers.setShowOpenTime(false);
    statusBarTriggers.setTable(tablealiasses);
    jPanel1.add(statusBarTriggers, java.awt.BorderLayout.SOUTH);

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

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBar.add(jSeparator2);

    buttonDropTrigger.setAction(cmDropAlias);
    buttonDropTrigger.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDropTrigger.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonDropTrigger);
    toolBar.add(separator);
    toolBar.add(buttonActions);

    jPanel2.add(toolBar);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropAliasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropAliasActionPerformed
  if (tablealiasses.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tablealiasses.getQuery().getRecord(tablealiasses.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tablealiasses.getQuery().fieldByName("alias_name").getString(), getDatabase());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("AliasesPanelView-drop-selected-alias-q"), new Object[] {objectName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop alias " +objectName, true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropAliasActionPerformed

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tablealiasses.getQuery().isActive()) {
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


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDropTrigger;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmDropAlias;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JToolBar.Separator separator;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTriggers;
  private ViewTable tablealiasses;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

package pl.mpak.orbada.oracle.gui.javas;

import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.SwingUtil.Color;
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
public class JavaFieldsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentObjectName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  
  public JavaFieldsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableFields.getQuery().setDatabase(getDatabase());
    try {
      tableFields.addColumn(new QueryTableColumn("field_index", stringManager.getString("pos"), 30));
      tableFields.addColumn(new QueryTableColumn("field_name", stringManager.getString("field-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableFields.addColumn(new QueryTableColumn("accessibility", stringManager.getString("accessibility"), 80, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableFields.addColumn(new QueryTableColumn("field_class", stringManager.getString("field-class"), 250, new QueryTableCellRenderer(Color.NAVY)));
      tableFields.addColumn(new QueryTableColumn("is_static", stringManager.getString("is-static"), 30));
      tableFields.addColumn(new QueryTableColumn("is_final", stringManager.getString("is-final"), 30));
      tableFields.addColumn(new QueryTableColumn("is_volatile", stringManager.getString("is-volatile"), 30));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("field_name", stringManager.getString("field-name"), (String[])null));
      def.add(new SqlFilterDefComponent("accessibility", stringManager.getString("accessibility"), new String[] {"'PUBLIC'", "'PRIVATE'", "'PROTECTED'"}));
      def.add(new SqlFilterDefComponent("is_static = 'YES'", stringManager.getString("are-static")));
      def.add(new SqlFilterDefComponent("is_final = 'YES'", stringManager.getString("are-final")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-java-fields-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableFields, buttonActions, menuActions, "oracle-java-fields-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("JavaFieldsPanel-title");
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
      String metodName = null;
      requestRefresh = false;
      if (tableFields.getQuery().isActive() && tableFields.getSelectedRow() >= 0) {
        tableFields.getQuery().getRecord(tableFields.getSelectedRow());
        metodName = tableFields.getQuery().fieldByName("field_name").getString();
      }
      tableFields.getQuery().close();
      tableFields.getQuery().setSqlText(Sql.getJavaFieldList(filter.getSqlText()));
      tableFields.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableFields.getQuery().paramByName("object_name").setString(currentObjectName);
      tableFields.getQuery().open();
      if (!tableFields.getQuery().isEmpty()) {
        if (metodName != null && tableFields.getQuery().locate("field_name", new Variant(metodName))) {
          tableFields.changeSelection(tableFields.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableFields.changeSelection(0, 0);
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
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
    tableFields.getQuery().close();
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
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarColumns = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableFields = new ViewTable();

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

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableFields);
    add(statusBar, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarColumns.setFloatable(false);
    toolBarColumns.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonFilter);
    toolBarColumns.add(jSeparator1);
    toolBarColumns.add(buttonActions);

    jPanel1.add(toolBarColumns);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableFields);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

  private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
    refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableFields;
  private javax.swing.JToolBar toolBarColumns;
  // End of variables declaration//GEN-END:variables
  
}

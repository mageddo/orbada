package pl.mpak.orbada.oracle.gui.mviews;

import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import orbada.gui.comps.table.VerticalQueryTablePanel;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ITabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.mview.CreateMViewIndexWizard;
import pl.mpak.orbada.oracle.gui.wizards.mview.DropMViewIndexWizard;
import pl.mpak.orbada.oracle.gui.wizards.mview.RebuildMViewIndexWizard;
import pl.mpak.orbada.oracle.gui.wizards.mview.RenameMViewIndexWizard;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.QueryTableColumnModel;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class MViewIndexesPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private VerticalQueryTablePanel indexInfoPanel;
  private ISettings settings;
  
  public MViewIndexesPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-mview-index-panel");
    splitIndex.setBottomComponent(indexInfoPanel = new VerticalQueryTablePanel(getDatabase()));
    splitIndex.setDividerLocation(settings.getValue("split-location", (long)splitIndex.getDividerLocation()).intValue());
    buttonExpressionColumn.setSelected(settings.getValue("expression-column", buttonExpressionColumn.isSelected()));
    
    tableIndexes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      private String indexName;
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableIndexes.getSelectedRow();
        if (rowIndex >= 0 && tableIndexes.getQuery().isActive()) {
          try {
            tableIndexes.getQuery().getRecord(rowIndex);
            if (indexName == null || !tableIndexes.getQuery().fieldByName("index_name").getString().equals(indexName)) {
              indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
              indexInfoPanel.refresh(Sql.getIndexInfo(currentSchemaName, indexName));
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableIndexes.getQuery().setDatabase(getDatabase());
    try {
      prepareTableColumns();
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("uidx.index_name", stringManager.getString("index-name"), (String[])null));
      def.add(new SqlFilterDefComponent("uic.column_name", stringManager.getString("column-name"), (String[])null));
      def.add(new SqlFilterDefComponent("uidx.tablespace_name", stringManager.getString("tablespace-name"), (String[])null));
      def.add(new SqlFilterDefComponent("uidx.index_type", stringManager.getString("index-type"), new String[] {"", "'NORMAL'"}));
      def.add(new SqlFilterDefComponent("uidx.uniqueness = 'UNIQUE'", stringManager.getString("unique")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-mview-indexes-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableIndexes, buttonActions, menuActions, "oracle-mview-indexes-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("MViewIndexesPanel-title");
  }
  
  private void prepareTableColumns() {
    tableIndexes.addColumn(new QueryTableColumn("index_name", stringManager.getString("index-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableIndexes.addColumn(new QueryTableColumn("column_position", stringManager.getString("pos"), 30));
    tableIndexes.addColumn(new QueryTableColumn("column_name", stringManager.getString("column-name"), 100));
    if (buttonExpressionColumn.isSelected()) {
      tableIndexes.addColumn(new QueryTableColumn("column_expression", stringManager.getString("expression"), 100));
    }
    tableIndexes.addColumn(new QueryTableColumn("uniqueness", stringManager.getString("uniqueness"), 50));
    tableIndexes.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60));
    tableIndexes.addColumn(new QueryTableColumn("index_type", stringManager.getString("index-type"), 60));
    tableIndexes.addColumn(new QueryTableColumn("initial_extent", stringManager.getString("initial-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("next_extent", stringManager.getString("next-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("min_extents", stringManager.getString("min-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("max_extents", stringManager.getString("max-extent"), 70));
    tableIndexes.addColumn(new QueryTableColumn("pct_increase", stringManager.getString("pct-increase"), 50));
    tableIndexes.addColumn(new QueryTableColumn("pct_free", stringManager.getString("pct-free"), 50));
    tableIndexes.addColumn(new QueryTableColumn("tablespace_name", stringManager.getString("tablespace-name"), 100));
    tableIndexes.revalidate();
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
      tableIndexes.getQuery().close();
      tableIndexes.getQuery().setSqlText(Sql.getIndexList(filter.getSqlText(), buttonExpressionColumn.isSelected()));
      tableIndexes.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableIndexes.getQuery().paramByName("table_name").setString(currentTableName);
      tableIndexes.getQuery().open();
      if (!tableIndexes.getQuery().isEmpty()) {
        tableIndexes.changeSelection(0, 0);
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
    settings.setValue("split-location", (long)splitIndex.getDividerLocation());
    settings.setValue("expression-column", buttonExpressionColumn.isSelected());
    settings.store();
    tableIndexes.getQuery().close();
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
    cmDropIndex = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCreateIndex = new javax.swing.JMenuItem();
    menuRebuildIndex = new javax.swing.JMenuItem();
    menuRenameIndex = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropIndex = new javax.swing.JMenuItem();
    cmExpressionColumn = new pl.mpak.sky.gui.swing.Action();
    cmCreateIndex = new pl.mpak.sky.gui.swing.Action();
    cmRebuildIndex = new pl.mpak.sky.gui.swing.Action();
    cmRenameIndex = new pl.mpak.sky.gui.swing.Action();
    statusBarIndexes = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarIndexes = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonExpressionColumn = new javax.swing.JToggleButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    splitIndex = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableIndexes = new ViewTable();

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

    cmDropIndex.setActionCommandKey("cmDropIndex");
    cmDropIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropIndex.setText(stringManager.getString("cmDropIndex-text")); // NOI18N
    cmDropIndex.setTooltip(stringManager.getString("cmDropIndex-hint")); // NOI18N
    cmDropIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropIndexActionPerformed(evt);
      }
    });

    menuCreateIndex.setAction(cmCreateIndex);
    menuActions.add(menuCreateIndex);

    menuRebuildIndex.setAction(cmRebuildIndex);
    menuActions.add(menuRebuildIndex);

    menuRenameIndex.setAction(cmRenameIndex);
    menuActions.add(menuRenameIndex);
    menuActions.add(jSeparator3);

    menuDropIndex.setAction(cmDropIndex);
    menuActions.add(menuDropIndex);

    cmExpressionColumn.setActionCommandKey("cmExpressionColumn");
    cmExpressionColumn.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/expression.gif")); // NOI18N
    cmExpressionColumn.setText(stringManager.getString("cmExpressionColumn-text")); // NOI18N
    cmExpressionColumn.setTooltip(stringManager.getString("cmExpressionColumn-hint")); // NOI18N
    cmExpressionColumn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmExpressionColumnActionPerformed(evt);
      }
    });

    cmCreateIndex.setActionCommandKey("cmCreateIndex");
    cmCreateIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/create_index16.gif")); // NOI18N
    cmCreateIndex.setText(stringManager.getString("cmCreateIndex-text")); // NOI18N
    cmCreateIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateIndexActionPerformed(evt);
      }
    });

    cmRebuildIndex.setActionCommandKey("cmRebuildIndex");
    cmRebuildIndex.setText(stringManager.getString("cmRebuildIndex-text")); // NOI18N
    cmRebuildIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRebuildIndexActionPerformed(evt);
      }
    });

    cmRenameIndex.setActionCommandKey("cmRenameIndex");
    cmRenameIndex.setText(stringManager.getString("cmRenameIndex-text")); // NOI18N
    cmRenameIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRenameIndexActionPerformed(evt);
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
    statusBarIndexes.setTable(tableIndexes);
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

    buttonExpressionColumn.setAction(cmExpressionColumn);
    buttonExpressionColumn.setFocusable(false);
    buttonExpressionColumn.setHideActionText(true);
    buttonExpressionColumn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonExpressionColumn.setPreferredSize(new java.awt.Dimension(26, 26));
    buttonExpressionColumn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(buttonExpressionColumn);
    toolBarIndexes.add(jSeparator1);
    toolBarIndexes.add(buttonActions);

    jPanel1.add(toolBarIndexes);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    splitIndex.setBorder(null);
    splitIndex.setDividerLocation(350);
    splitIndex.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splitIndex.setContinuousLayout(true);
    splitIndex.setOneTouchExpandable(true);

    jScrollPane1.setViewportView(tableIndexes);

    splitIndex.setLeftComponent(jScrollPane1);

    add(splitIndex, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      if (SqlCodeWizardDialog.show(new DropMViewIndexWizard(getDatabase(), currentSchemaName, currentTableName, indexName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropIndexActionPerformed

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

private void cmExpressionColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExpressionColumnActionPerformed
  ((QueryTableColumnModel)tableIndexes.getColumnModel()).clearColumns();
  refresh();
  prepareTableColumns();
}//GEN-LAST:event_cmExpressionColumnActionPerformed

private void cmCreateIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateIndexActionPerformed
  if (SqlCodeWizardDialog.show(new CreateMViewIndexWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateIndexActionPerformed

private void cmRebuildIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRebuildIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      if (SqlCodeWizardDialog.show(new RebuildMViewIndexWizard(getDatabase(), currentSchemaName, currentTableName, indexName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmRebuildIndexActionPerformed

private void cmRenameIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      if (SqlCodeWizardDialog.show(new RenameMViewIndexWizard(getDatabase(), currentSchemaName, currentTableName, indexName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmRenameIndexActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private javax.swing.JToggleButton buttonExpressionColumn;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmCreateIndex;
  private pl.mpak.sky.gui.swing.Action cmDropIndex;
  private pl.mpak.sky.gui.swing.Action cmExpressionColumn;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRebuildIndex;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRenameIndex;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCreateIndex;
  private javax.swing.JMenuItem menuDropIndex;
  private javax.swing.JMenuItem menuRebuildIndex;
  private javax.swing.JMenuItem menuRenameIndex;
  private javax.swing.JSplitPane splitIndex;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarIndexes;
  private ViewTable tableIndexes;
  private javax.swing.JToolBar toolBarIndexes;
  // End of variables declaration//GEN-END:variables
  
}

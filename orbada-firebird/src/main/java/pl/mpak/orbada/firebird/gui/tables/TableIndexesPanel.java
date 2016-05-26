package pl.mpak.orbada.firebird.gui.tables;

import java.awt.Dialog;
import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.wizards.CommentWizard;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.CreateIndexWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
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
public class TableIndexesPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  private IViewAccesibilities accesibilities;
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;

  public TableIndexesPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableIndexes.getQuery().setDatabase(getDatabase());
    try {
      tableIndexes.addColumn(new QueryTableColumn("INDEX_NAME", stringManager.getString("TableIndexesPanel-index-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableIndexes.addColumn(new QueryTableColumn("COLUMNS", stringManager.getString("TableIndexesPanel-columns"), 150));
      tableIndexes.addColumn(new QueryTableColumn("UNIQUINES", stringManager.getString("TableIndexesPanel-unique"), 50));
      tableIndexes.addColumn(new QueryTableColumn("ORDERING", stringManager.getString("TableIndexesPanel-order"), 50));
      tableIndexes.addColumn(new QueryTableColumn("INDEX_ACTIVE", stringManager.getString("TableIndexesPanel-active"), 50));
      tableIndexes.addColumn(new QueryTableColumn("EXPRESSION_SOURCE", stringManager.getString("computed"), 100));
      tableIndexes.addColumn(new QueryTableColumn("DESCRIPTION", stringManager.getString("comment"), 250));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("INDEX_NAME", stringManager.getString("TableIndexesPanel-index-name"), (String[])null));
      def.add(new SqlFilterDefComponent("COLUMNS", stringManager.getString("TableIndexesPanel-columns"), (String[])null));
      def.add(new SqlFilterDefComponent("UNIQUINES = 'Y'", stringManager.getString("TableIndexesPanel-uniquenes")));
      def.add(new SqlFilterDefComponent("INDEX_ACTIVE = 'Y'", stringManager.getString("TableIndexesPanel-activated")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-table-indexes-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableIndexes, buttonActions, menuActions, "firebird-table-indexes-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableIndexesPanel-indexes");
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
      tableIndexes.getQuery().setSqlText(Sql.getIndexList(filter.getSqlText()));
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
    cmSimpleCreateIndex = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuSimpleCreateIndex = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropIndex = new javax.swing.JMenuItem();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    statusBarIndexes = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarIndexes = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableIndexes = new ViewTable();

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

    cmDropIndex.setActionCommandKey("cmDropIndex");
    cmDropIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif")); // NOI18N
    cmDropIndex.setText(stringManager.getString("TableIndexesPanel-cmDropIndex-text")); // NOI18N
    cmDropIndex.setTooltip(stringManager.getString("TableIndexesPanel-cmDropIndex-hint")); // NOI18N
    cmDropIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropIndexActionPerformed(evt);
      }
    });

    cmSimpleCreateIndex.setActionCommandKey("cmSimpleCreateIndex");
    cmSimpleCreateIndex.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/create_index16.gif")); // NOI18N
    cmSimpleCreateIndex.setText(stringManager.getString("TableIndexesPanel-cmSimpleCreateIndex-text")); // NOI18N
    cmSimpleCreateIndex.setTooltip(stringManager.getString("TableIndexesPanel-cmSimpleCreateIndex-hint")); // NOI18N
    cmSimpleCreateIndex.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSimpleCreateIndexActionPerformed(evt);
      }
    });

    menuSimpleCreateIndex.setAction(cmSimpleCreateIndex);
    menuActions.add(menuSimpleCreateIndex);

    jMenuItem1.setAction(cmComment);
    menuActions.add(jMenuItem1);
    menuActions.add(jSeparator3);

    menuDropIndex.setAction(cmDropIndex);
    menuActions.add(menuDropIndex);

    cmComment.setActionCommandKey("cmComment");
    cmComment.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/comment_edit.gif")); // NOI18N
    cmComment.setText(stringManager.getString("cmComment-text")); // NOI18N
    cmComment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentActionPerformed(evt);
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
    toolBarIndexes.add(jSeparator4);

    toolButton3.setAction(cmComment);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(toolButton3);

    toolButton1.setAction(cmSimpleCreateIndex);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(toolButton1);
    toolBarIndexes.add(jSeparator2);

    toolButton2.setAction(cmDropIndex);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarIndexes.add(toolButton2);
    toolBarIndexes.add(jSeparator1);
    toolBarIndexes.add(buttonActions);

    jPanel1.add(toolBarIndexes);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableIndexes);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmSimpleCreateIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSimpleCreateIndexActionPerformed
  if (SqlCodeWizardDialog.show(new CreateIndexWizardPanel(getDatabase(), null, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmSimpleCreateIndexActionPerformed

  private void cmDropIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropIndexActionPerformed
  if (tableIndexes.getSelectedRow() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectedRow());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TableIndexesPanel-drop-index-q"), new Object[] {indexName, currentTableName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop index " +SQLUtil.createSqlName(indexName), true);
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

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
if (tableIndexes.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableIndexes.getQuery().getRecord(tableIndexes.getSelectionModel().getLeadSelectionIndex());
      String indexName = tableIndexes.getQuery().fieldByName("index_name").getString();
      if (SqlCodeWizardDialog.show(new CommentWizard(getDatabase(), indexName, "RDB$INDICES"), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmDropIndex;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSimpleCreateIndex;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuDropIndex;
  private javax.swing.JMenuItem menuSimpleCreateIndex;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarIndexes;
  private ViewTable tableIndexes;
  private javax.swing.JToolBar toolBarIndexes;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  // End of variables declaration//GEN-END:variables
  
}

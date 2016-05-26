package pl.mpak.orbada.firebird.gui.tables;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.wizards.CommentWizard;
import pl.mpak.orbada.firebird.gui.wizards.RenameTableColumnWizard;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ITabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
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
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class TableColumnsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  
  /** Creates new form TableColumns
   * @param accesibilities
   */
  public TableColumnsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableColumns.getQuery().setDatabase(getDatabase());
    try {
      tableColumns.addColumn(new QueryTableColumn("FIELD_POSITION", stringManager.getString("TableColumnsPanel-no"), 30));
      tableColumns.addColumn(new QueryTableColumn("FIELD_NAME", stringManager.getString("TableColumnsPanel-column-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableColumns.addColumn(new QueryTableColumn("DISPLAY_TYPE", stringManager.getString("TableColumnsPanel-column-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableColumns.addColumn(new QueryTableColumn("DOMAIN_NAME", stringManager.getString("TableColumnsPanel-domain"), 120));
      tableColumns.addColumn(new QueryTableColumn("DEFAULT_SOURCE", stringManager.getString("TableColumnsPanel-default-value"), 200));
      tableColumns.addColumn(new QueryTableColumn("NULL_FLAG", stringManager.getString("TableColumnsPanel-null-short-q"), 30));
      tableColumns.addColumn(new QueryTableColumn("UPDATE_FLAG", stringManager.getString("TableColumnsPanel-update-short-q"), 30));
      tableColumns.addColumn(new QueryTableColumn("CHARACTER_SET_NAME", stringManager.getString("TableColumnsPanel-charset"), 200));
      tableColumns.addColumn(new QueryTableColumn("DESCRIPTION", stringManager.getString("comment"), 300));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("FIELD_NAME", stringManager.getString("TableColumnsPanel-column-name"), (String[])null));
      def.add(new SqlFilterDefComponent("DISPLAY_TYPE", stringManager.getString("TableColumnsPanel-column-type"), new String[] {"", "'VARYING%'", "'TEXT%'", "'SHORT%'", "'LONG%'", "'TIMESTAMP%'"}));
      def.add(new SqlFilterDefComponent("CHARACTER_SET_NAME", stringManager.getString("TableColumnsPanel-charset"), (String[])null));
      def.add(new SqlFilterDefComponent("NULL_FLAG = 'Y'", "NOT NULL"));
      def.add(new SqlFilterDefComponent("UPDATE_FLAG = 'Y'", stringManager.getString("TableColumnsPanel-can-update")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-table-columns-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableColumns, buttonActions, menuActions, "firebird-table-columns-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableColumnsPanel-columns");
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
      String columnName = null;
      requestRefresh = false;
      if (tableColumns.getQuery().isActive() && tableColumns.getSelectedRow() >= 0) {
        tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
        columnName = tableColumns.getQuery().fieldByName("FIELD_NAME").getString();
      }
      tableColumns.getQuery().close();
      tableColumns.getQuery().setSqlText(Sql.getColumnList(filter.getSqlText()));
      tableColumns.getQuery().paramByName("table_name").setString(currentTableName);
      tableColumns.getQuery().open();
      if (!tableColumns.getQuery().isEmpty()) {
        if (columnName != null && tableColumns.getQuery().locate("FIELD_NAME", new Variant(columnName))) {
          tableColumns.changeSelection(tableColumns.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableColumns.changeSelection(0, 0);
        }
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
    tableColumns.getQuery().close();
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
    cmCreateConstraintCheck = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCommentColumn = new javax.swing.JMenuItem();
    menuRenameColumn = new javax.swing.JMenuItem();
    menuColumnList = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JSeparator();
    menuCreateConstraintCheck = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropColumn = new javax.swing.JMenuItem();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmDropColumn = new pl.mpak.sky.gui.swing.Action();
    cmColumnList = new pl.mpak.sky.gui.swing.Action();
    cmRenameColumn = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintFK = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintPK = new pl.mpak.sky.gui.swing.Action();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableColumns = new ViewTable();
    statusBarColumns = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarColumns = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton4 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton5 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    toolButton6 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator6 = new javax.swing.JToolBar.Separator();
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

    cmCreateConstraintCheck.setActionCommandKey("cmCreateConstraintCheck");
    cmCreateConstraintCheck.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/verify_document_16.gif")); // NOI18N
    cmCreateConstraintCheck.setText(stringManager.getString("TableColumnsPanel-cmCreateConstraintCheck-text")); // NOI18N
    cmCreateConstraintCheck.setTooltip(stringManager.getString("TableColumnsPanel-cmCreateConstraintCheck-hint")); // NOI18N
    cmCreateConstraintCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintCheckActionPerformed(evt);
      }
    });

    menuCommentColumn.setAction(cmComment);
    menuActions.add(menuCommentColumn);

    menuRenameColumn.setAction(cmRenameColumn);
    menuActions.add(menuRenameColumn);

    menuColumnList.setAction(cmColumnList);
    menuActions.add(menuColumnList);
    menuActions.add(jSeparator5);

    menuCreateConstraintCheck.setAction(cmCreateConstraintCheck);
    menuActions.add(menuCreateConstraintCheck);

    jMenuItem1.setAction(cmCreateConstraintFK);
    menuActions.add(jMenuItem1);

    jMenuItem2.setAction(cmCreateConstraintPK);
    menuActions.add(jMenuItem2);
    menuActions.add(jSeparator3);

    menuDropColumn.setAction(cmDropColumn);
    menuActions.add(menuDropColumn);

    cmComment.setActionCommandKey("cmCommentTable");
    cmComment.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif")); // NOI18N
    cmComment.setText(stringManager.getString("cmComment-text")); // NOI18N
    cmComment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentActionPerformed(evt);
      }
    });

    cmDropColumn.setActionCommandKey("cmDropColumn");
    cmDropColumn.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropColumn.setText(stringManager.getString("TableColumnsPanel-cmDropColumn-text")); // NOI18N
    cmDropColumn.setTooltip(stringManager.getString("TableColumnsPanel-cmDropColumn-hint")); // NOI18N
    cmDropColumn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropColumnActionPerformed(evt);
      }
    });

    cmColumnList.setActionCommandKey("cmColumnList");
    cmColumnList.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/copy.gif")); // NOI18N
    cmColumnList.setText(stringManager.getString("TableColumnsPanel-cmColumnList-text")); // NOI18N
    cmColumnList.setTooltip(stringManager.getString("TableColumnsPanel-cmColumnList-hint")); // NOI18N
    cmColumnList.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColumnListActionPerformed(evt);
      }
    });

    cmRenameColumn.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/rename.gif")); // NOI18N
    cmRenameColumn.setText(stringManager.getString("cmRenameColumn-text")); // NOI18N
    cmRenameColumn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRenameColumnActionPerformed(evt);
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

    jScrollPane1.setViewportView(tableColumns);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarColumns.setShowFieldType(false);
    statusBarColumns.setShowOpenTime(false);
    statusBarColumns.setTable(tableColumns);
    add(statusBarColumns, java.awt.BorderLayout.PAGE_END);

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
    toolBarColumns.add(jSeparator2);

    toolButton1.setAction(cmComment);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton1);

    toolButton3.setAction(cmColumnList);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton3);
    toolBarColumns.add(jSeparator4);

    toolButton2.setAction(cmCreateConstraintCheck);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton2);

    toolButton4.setAction(cmCreateConstraintFK);
    toolButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton4);

    toolButton5.setAction(cmCreateConstraintPK);
    toolButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton5);
    toolBarColumns.add(jSeparator1);

    toolButton6.setAction(cmDropColumn);
    toolButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton6);
    toolBarColumns.add(jSeparator6);
    toolBarColumns.add(buttonActions);

    jPanel1.add(toolBarColumns);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmCreateConstraintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintCheckActionPerformed
  if (tableColumns.getSelectedRow() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
      SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(getDatabase(), null, currentTableName), true);
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmCreateConstraintCheckActionPerformed

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

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableColumns.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
      String columnName = tableColumns.getQuery().fieldByName("FIELD_NAME").getString();
      if (SqlCodeWizardDialog.show(new CommentWizard(getDatabase(), currentTableName, columnName, "RDB$RELATION_FIELDS"), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmDropColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropColumnActionPerformed
  if (tableColumns.getSelectedRow() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
      String columnName = tableColumns.getQuery().fieldByName("FIELD_NAME").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TableColumnsPanel-drop-column-q"), new Object[] {columnName, currentTableName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("alter table " +SQLUtil.createSqlName(currentTableName) +" DROP " +SQLUtil.createSqlName(columnName), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }

}//GEN-LAST:event_cmDropColumnActionPerformed

private void cmColumnListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColumnListActionPerformed
  if (tableColumns.getQuery().isActive()) {
    try {
      StringBuffer sb = new StringBuffer();
      int l = 0;
      tableColumns.getQuery().first();
      while (!tableColumns.getQuery().eof()) {
        if (sb.length() > 0) {
          sb.append(", ");
          l+= 2;
        }
        if (l > 100) {
          l = 0;
          sb.append('\n');
        }
        String column = SQLUtil.createSqlName(tableColumns.getQuery().fieldByName("FIELD_NAME").getString());
        sb.append(column);
        l+= column.length();
        tableColumns.getQuery().next();
      }
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection(sb.toString()), null);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmColumnListActionPerformed

private void cmRenameColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameColumnActionPerformed
  if (tableColumns.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
      String columnName = tableColumns.getQuery().fieldByName("FIELD_NAME").getString();
      if (SqlCodeWizardDialog.show(new RenameTableColumnWizard(getDatabase(), currentTableName, columnName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmRenameColumnActionPerformed

private void cmCreateConstraintFKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintFKActionPerformed
  try {
    tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
    String columnName = tableColumns.getQuery().fieldByName("FIELD_NAME").getString();
    SqlCodeWizardDialog.show(new CreateConstraintForeignKeyWizardPanel(getDatabase(), null, currentTableName, columnName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintFKActionPerformed

private void cmCreateConstraintPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintPKActionPerformed
  try {
    tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
    String columnName = tableColumns.getQuery().fieldByName("FIELD_NAME").getString();
    SqlCodeWizardDialog.show(new CreateConstraintPrimaryKeyWizardPanel(getDatabase(), null, currentTableName, columnName), true);
    refresh();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCreateConstraintPKActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmColumnList;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintFK;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintPK;
  private pl.mpak.sky.gui.swing.Action cmDropColumn;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRenameColumn;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JSeparator jSeparator5;
  private javax.swing.JToolBar.Separator jSeparator6;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuColumnList;
  private javax.swing.JMenuItem menuCommentColumn;
  private javax.swing.JMenuItem menuCreateConstraintCheck;
  private javax.swing.JMenuItem menuDropColumn;
  private javax.swing.JMenuItem menuRenameColumn;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarColumns;
  private ViewTable tableColumns;
  private javax.swing.JToolBar toolBarColumns;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton4;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton5;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton6;
  // End of variables declaration//GEN-END:variables
  
}

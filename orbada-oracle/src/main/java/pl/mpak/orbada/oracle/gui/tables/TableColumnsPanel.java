package pl.mpak.orbada.oracle.gui.tables;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.table.CommentTableColumnWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.DropTableColumnWizard;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.table.RenameTableColumnWizard;
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
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class TableColumnsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
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
    cmRenameColumn.setEnabled(StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("Ora9+")));
    tableColumns.getQuery().setDatabase(getDatabase());
    try {
      tableColumns.addColumn(new QueryTableColumn("column_id", stringManager.getString("pos"), 30));
      tableColumns.addColumn(new QueryTableColumn("column_name", stringManager.getString("column-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableColumns.addColumn(new QueryTableColumn("display_type", stringManager.getString("display-column-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableColumns.addColumn(new QueryTableColumn("data_default", stringManager.getString("default-value"), 200));
      tableColumns.addColumn(new QueryTableColumn("nullable", stringManager.getString("is-null"), 40));
      tableColumns.addColumn(new QueryTableColumn("pk_position", stringManager.getString("pk-pos"), 25));
      tableColumns.addColumn(new QueryTableColumn("remarks", stringManager.getString("comment"), 300));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("cols.column_name", stringManager.getString("column-name"), (String[])null));
      def.add(new SqlFilterDefComponent("cols.data_type", stringManager.getString("column-type"), new String[] {"", "'VARCHAR%'", "'NUMERIC%'", "'INTEGER%'", "'TIMESTAMP%'", "'DATE'"}));
      def.add(new SqlFilterDefComponent("cols.nullable = 'NO'", stringManager.getString("not-null")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-table-columns-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableColumns, buttonActions, menuActions, "oracle-table-columns-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableColumnsPanel-title");
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
      int column = tableColumns.getSelectedColumn();
      if (tableColumns.getQuery().isActive() && tableColumns.getSelectedRow() >= 0) {
        tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
        columnName = tableColumns.getQuery().fieldByName("COLUMN_NAME").getString();
      }
      tableColumns.getQuery().close();
      tableColumns.getQuery().setSqlText(Sql.getColumnList(filter.getSqlText()));
      tableColumns.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableColumns.getQuery().paramByName("table_name").setString(currentTableName);
      tableColumns.getQuery().open();
      if (!tableColumns.getQuery().isEmpty()) {
        if (columnName != null && tableColumns.getQuery().locate("column_name", new Variant(columnName))) {
          tableColumns.changeSelection(tableColumns.getQuery().getCurrentRecord().getIndex(), column);
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
    cmColumnNotNull = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintCheck = new pl.mpak.sky.gui.swing.Action();
    cmDropColumn = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCommentColumn = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jSeparator6 = new javax.swing.JSeparator();
    menuColumnNotNull = new javax.swing.JMenuItem();
    menuCreateConstraintCheck = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenuItem3 = new javax.swing.JMenuItem();
    jSeparator7 = new javax.swing.JSeparator();
    menuRenameColumn = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropColumn = new javax.swing.JMenuItem();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmColumnListCopy = new pl.mpak.sky.gui.swing.Action();
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
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton6 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator5 = new javax.swing.JToolBar.Separator();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton4 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton7 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton8 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    toolButton5 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
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

    cmColumnNotNull.setActionCommandKey("cmColumnNotNull");
    cmColumnNotNull.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/lock16.gif")); // NOI18N
    cmColumnNotNull.setText(stringManager.getString("cmColumnNotNull-text")); // NOI18N
    cmColumnNotNull.setTooltip(stringManager.getString("cmColumnNotNull-hint")); // NOI18N
    cmColumnNotNull.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColumnNotNullActionPerformed(evt);
      }
    });

    cmCreateConstraintCheck.setActionCommandKey("cmCreateConstraintCheck");
    cmCreateConstraintCheck.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/verify_document_16.gif")); // NOI18N
    cmCreateConstraintCheck.setText(stringManager.getString("cmCreateConstraintCheck-text")); // NOI18N
    cmCreateConstraintCheck.setTooltip(stringManager.getString("cmCreateConstraintCheck-hint")); // NOI18N
    cmCreateConstraintCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintCheckActionPerformed(evt);
      }
    });

    cmDropColumn.setActionCommandKey("cmDropColumn");
    cmDropColumn.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropColumn.setText(stringManager.getString("cmDropColumn-text")); // NOI18N
    cmDropColumn.setTooltip(stringManager.getString("cmDropColumn-hint")); // NOI18N
    cmDropColumn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropColumnActionPerformed(evt);
      }
    });

    menuCommentColumn.setAction(cmComment);
    menuActions.add(menuCommentColumn);

    jMenuItem1.setAction(cmColumnListCopy);
    menuActions.add(jMenuItem1);
    menuActions.add(jSeparator6);

    menuColumnNotNull.setAction(cmColumnNotNull);
    menuActions.add(menuColumnNotNull);

    menuCreateConstraintCheck.setAction(cmCreateConstraintCheck);
    menuActions.add(menuCreateConstraintCheck);

    jMenuItem2.setAction(cmCreateConstraintFK);
    menuActions.add(jMenuItem2);

    jMenuItem3.setAction(cmCreateConstraintPK);
    menuActions.add(jMenuItem3);
    menuActions.add(jSeparator7);

    menuRenameColumn.setAction(cmRenameColumn);
    menuRenameColumn.setText("jMenuItem2");
    menuActions.add(menuRenameColumn);
    menuActions.add(jSeparator3);

    menuDropColumn.setAction(cmDropColumn);
    menuActions.add(menuDropColumn);

    cmComment.setActionCommandKey("cmComment");
    cmComment.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif")); // NOI18N
    cmComment.setText(stringManager.getString("cmComment-text")); // NOI18N
    cmComment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentActionPerformed(evt);
      }
    });

    cmColumnListCopy.setActionCommandKey("cmColumnListCopy");
    cmColumnListCopy.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/copy.gif")); // NOI18N
    cmColumnListCopy.setText(stringManager.getString("cmColumnListCopy-text")); // NOI18N
    cmColumnListCopy.setTooltip(stringManager.getString("cmColumnListCopy-hint")); // NOI18N
    cmColumnListCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColumnListCopyActionPerformed(evt);
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
    cmCreateConstraintFK.setText(stringManager.getString("cmCreateConstraintFK-text")); // NOI18N
    cmCreateConstraintFK.setTooltip(stringManager.getString("cmCreateConstraintFK-hint")); // NOI18N
    cmCreateConstraintFK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintFKActionPerformed(evt);
      }
    });

    cmCreateConstraintPK.setActionCommandKey("cmCreateConstraintPK");
    cmCreateConstraintPK.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/primary_key16.gif")); // NOI18N
    cmCreateConstraintPK.setText(stringManager.getString("cmCreateConstraintPK-text")); // NOI18N
    cmCreateConstraintPK.setTooltip(stringManager.getString("cmCreateConstraintPK-hint")); // NOI18N
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

    toolButton2.setAction(cmColumnListCopy);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton2);

    toolButton6.setAction(cmRenameColumn);
    toolButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton6);
    toolBarColumns.add(jSeparator5);

    toolButton3.setAction(cmColumnNotNull);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton3);

    toolButton4.setAction(cmCreateConstraintCheck);
    toolButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton4);

    toolButton7.setAction(cmCreateConstraintFK);
    toolButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton7);

    toolButton8.setAction(cmCreateConstraintPK);
    toolButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton8);
    toolBarColumns.add(jSeparator4);

    toolButton5.setAction(cmDropColumn);
    toolButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton5);
    toolBarColumns.add(jSeparator1);
    toolBarColumns.add(buttonActions);

    jPanel1.add(toolBarColumns);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmCreateConstraintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintCheckActionPerformed
  if (tableColumns.getSelectedRow() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
      String columnName = tableColumns.getQuery().fieldByName("COLUMN_NAME").getString();
      SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmCreateConstraintCheckActionPerformed

private void cmColumnNotNullActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColumnNotNullActionPerformed
  if (tableColumns.getSelectedRow() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectedRow());
      String columnName = tableColumns.getQuery().fieldByName("COLUMN_NAME").getString();
      SqlCodeWizardDialog.show(new AlterTableNullWizardPanel(getDatabase(), currentSchemaName, currentTableName, columnName, "MODIFY", null), true);
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmColumnNotNullActionPerformed

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

private void cmDropColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropColumnActionPerformed
  if (tableColumns.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
      if (SqlCodeWizardDialog.show(new DropTableColumnWizard(getDatabase(), currentSchemaName, currentTableName, tableColumns.getQuery().fieldByName("column_name").getString()), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropColumnActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableColumns.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
      String columnName = tableColumns.getQuery().fieldByName("column_name").getString();
      if (SqlCodeWizardDialog.show(new CommentTableColumnWizard(getDatabase(), currentSchemaName, currentTableName, columnName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmColumnListCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColumnListCopyActionPerformed
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
        String column = SQLUtil.createSqlName(tableColumns.getQuery().fieldByName("COLUMN_NAME").getString());
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
}//GEN-LAST:event_cmColumnListCopyActionPerformed

private void cmRenameColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameColumnActionPerformed
  if (tableColumns.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
      String columnName = tableColumns.getQuery().fieldByName("column_name").getString();
      if (SqlCodeWizardDialog.show(new RenameTableColumnWizard(getDatabase(), currentSchemaName, currentTableName, columnName), true) != null) {
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
    String columnName = tableColumns.getQuery().fieldByName("column_name").getString();
    SqlCodeWizardDialog.show(new CreateConstraintForeignKeyWizardPanel(getDatabase(), currentSchemaName, currentTableName, columnName), true);
    refresh();
  } catch (Exception ex) {
    MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
  }
}//GEN-LAST:event_cmCreateConstraintFKActionPerformed

private void cmCreateConstraintPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintPKActionPerformed
  try {
    tableColumns.getQuery().getRecord(tableColumns.getSelectionModel().getLeadSelectionIndex());
    String columnName = tableColumns.getQuery().fieldByName("column_name").getString();
    SqlCodeWizardDialog.show(new CreateConstraintPrimaryKeyWizardPanel(getDatabase(), currentSchemaName, currentTableName, columnName), true);
    refresh();
  } catch (Exception ex) {
    MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
  }
}//GEN-LAST:event_cmCreateConstraintPKActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmColumnListCopy;
  private pl.mpak.sky.gui.swing.Action cmColumnNotNull;
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
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JToolBar.Separator jSeparator5;
  private javax.swing.JSeparator jSeparator6;
  private javax.swing.JSeparator jSeparator7;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuColumnNotNull;
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
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton7;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton8;
  // End of variables declaration//GEN-END:variables
  
}

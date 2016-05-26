package pl.mpak.orbada.firebird.gui.exceptions;

import java.awt.Dialog;
import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.wizards.AlterExceptionWizard;
import pl.mpak.orbada.firebird.gui.wizards.CommentWizard;
import pl.mpak.orbada.firebird.gui.wizards.CreateExceptionWizard;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
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
public class ExceptionsPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  public ExceptionsPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = "USER";
    tableExceptions.getQuery().setDatabase(getDatabase());
    try {
      tableExceptions.addColumn(new QueryTableColumn("EXCEPTION_NUMBER", stringManager.getString("ExceptionsPanelView-no"), 70));
      tableExceptions.addColumn(new QueryTableColumn("EXCEPTION_NAME", stringManager.getString("ExceptionsPanelView-exception-name"), 190, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableExceptions.addColumn(new QueryTableColumn("EXCEPTION_MESSAGE", stringManager.getString("ExceptionsPanelView-message"), 350));
      tableExceptions.addColumn(new QueryTableColumn("DESCRIPTION", stringManager.getString("comment"), 250));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("EXCEPTION_NAME", stringManager.getString("ExceptionsPanelView-exception-name"), (String[])null));
      def.add(new SqlFilterDefComponent("EXCEPTION_MESSAGE", stringManager.getString("ExceptionsPanelView-message"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-exceptions-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableExceptions, buttonActions, menuActions, "firebird-exceptions-actions");
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase("USER")) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
        buttonSelectSchema.setSelected(true);
      }
      else {
        buttonSelectSchema.setSelected(false);
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
      if (tableExceptions.getQuery().isActive() && tableExceptions.getSelectedRow() >= 0) {
        tableExceptions.getQuery().getRecord(tableExceptions.getSelectedRow());
        objectName = tableExceptions.getQuery().fieldByName("EXCEPTION_NAME").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableExceptions.getSelectedColumn();
      int index = Math.max(0, tableExceptions.getSelectedRow());
      tableExceptions.getQuery().close();
      tableExceptions.getQuery().setSqlText(Sql.getExceptionList(filter.getSqlText()));
      tableExceptions.getQuery().paramByName("SYSTEM_FLAG").setString(currentSchemaName);
      tableExceptions.getQuery().open();
      if (objectName != null && tableExceptions.getQuery().locate("EXCEPTION_NAME", new Variant(objectName))) {
        tableExceptions.changeSelection(tableExceptions.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableExceptions.getQuery().isEmpty()) {
        tableExceptions.changeSelection(Math.min(index, tableExceptions.getRowCount() -1), column);
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
    tableExceptions.getQuery().close();
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
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropSequence = new javax.swing.JMenuItem();
    cmDropException = new pl.mpak.sky.gui.swing.Action();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmCreateException = new pl.mpak.sky.gui.swing.Action();
    cmAlterException = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableExceptions = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
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

    jMenuItem1.setAction(cmCreateException);
    menuActions.add(jMenuItem1);

    jMenuItem2.setAction(cmAlterException);
    menuActions.add(jMenuItem2);
    menuActions.add(jSeparator2);

    menuDropSequence.setAction(cmDropException);
    menuActions.add(menuDropSequence);

    cmDropException.setActionCommandKey("cmDropException");
    cmDropException.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropException.setText(stringManager.getString("ExceptionsPanelView-cmDropException-text")); // NOI18N
    cmDropException.setTooltip(stringManager.getString("ExceptionsPanelView-cmDropException-hint")); // NOI18N
    cmDropException.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropExceptionActionPerformed(evt);
      }
    });

    cmComment.setActionCommandKey("cmComment");
    cmComment.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif")); // NOI18N
    cmComment.setText(stringManager.getString("cmComment-text")); // NOI18N
    cmComment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentActionPerformed(evt);
      }
    });

    cmCreateException.setActionCommandKey("cmCreateException");
    cmCreateException.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/exception.gif")); // NOI18N
    cmCreateException.setText(stringManager.getString("ExceptionsPanelView-cmCreateException-text")); // NOI18N
    cmCreateException.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateExceptionActionPerformed(evt);
      }
    });

    cmAlterException.setActionCommandKey("cmAlterException");
    cmAlterException.setText(stringManager.getString("ExceptionsPanelView-cmAlterException-text")); // NOI18N
    cmAlterException.setTooltip(stringManager.getString("ExceptionsPanelView-cmAlterException-hint")); // NOI18N
    cmAlterException.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAlterExceptionActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableExceptions);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableExceptions);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

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

    toolButton2.setAction(cmComment);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(toolButton2);

    toolButton3.setAction(cmCreateException);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(toolButton3);
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
  if (!tableExceptions.getQuery().isActive()) {
    refresh();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmSelectSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectSchemaActionPerformed
  if (StringUtil.equalsIgnoreCase(currentSchemaName, "USER")) {
    setCurrentSchemaName("SYSTEM");
  }
  else {
    setCurrentSchemaName("USER");
  }
  refresh();
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmDropExceptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropExceptionActionPerformed
  if (tableExceptions.getSelectedRow() >= 0) {
    try {
      tableExceptions.getQuery().getRecord(tableExceptions.getSelectedRow());
      String sequenceName = tableExceptions.getQuery().fieldByName("EXCEPTION_NAME").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("ExceptionsPanelView-dropexception-q"), new Object[] {sequenceName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop exception " +SQLUtil.createSqlName(sequenceName), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropExceptionActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableExceptions.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableExceptions.getQuery().getRecord(tableExceptions.getSelectionModel().getLeadSelectionIndex());
      String sequenceName = tableExceptions.getQuery().fieldByName("EXCEPTION_NAME").getString();
      if (SqlCodeWizardDialog.show(new CommentWizard(getDatabase(), sequenceName, "RDB$EXCEPTIONS"), true) != null) {
        refresh(sequenceName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmCreateExceptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateExceptionActionPerformed
  SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new CreateExceptionWizard(getDatabase()), true);
  if (result != null) {
    refresh(result.getResultMap().get("object_name"));
  }
}//GEN-LAST:event_cmCreateExceptionActionPerformed

private void cmAlterExceptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAlterExceptionActionPerformed
  if (tableExceptions.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableExceptions.getQuery().getRecord(tableExceptions.getSelectionModel().getLeadSelectionIndex());
      String exceptionName = tableExceptions.getQuery().fieldByName("EXCEPTION_NAME").getString();
      String message = tableExceptions.getQuery().fieldByName("EXCEPTION_MESSAGE").getString();
      SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new AlterExceptionWizard(getDatabase(), exceptionName, message), true);
      if (result != null) {
        refresh(result.getResultMap().get("object_name"));
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmAlterExceptionActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmAlterException;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmCreateException;
  private pl.mpak.sky.gui.swing.Action cmDropException;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuDropSequence;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableExceptions;
  private javax.swing.JToolBar toolBar;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  // End of variables declaration//GEN-END:variables

}

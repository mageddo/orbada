package pl.mpak.orbada.firebird.gui.sequences;

import java.awt.Dialog;
import java.io.IOException;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.wizards.AlterSequenceWizard;
import pl.mpak.orbada.firebird.gui.wizards.CommentWizard;
import pl.mpak.orbada.firebird.gui.wizards.CreateSequenceWizard;
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
import pl.mpak.usedb.core.Query;
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
public class SequencesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private boolean generatorDescription;
  
  public SequencesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    generatorDescription = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("generator-description", "false"));
    currentSchemaName = "USER";
    tableSequences.getQuery().setDatabase(getDatabase());
    try {
      tableSequences.addColumn(new QueryTableColumn("GENERATOR_ID", stringManager.getString("SequencesPanelView-id"), 70));
      tableSequences.addColumn(new QueryTableColumn("GENERATOR_NAME", stringManager.getString("SequencesPanelView-sequence-name"), 190, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      if (generatorDescription) {
        tableSequences.addColumn(new QueryTableColumn("DESCRIPTION", stringManager.getString("comment"), 250));
      }
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("GENERATOR_NAME", stringManager.getString("SequencesPanelView-sequence-name"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-sequences-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    cmComment.setEnabled(generatorDescription);
    new ComponentActionsAction(getDatabase(), tableSequences, buttonActions, menuActions, "firebird-sequences-actions");
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
      if (tableSequences.getQuery().isActive() && tableSequences.getSelectedRow() >= 0) {
        tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
        objectName = tableSequences.getQuery().fieldByName("GENERATOR_NAME").getString();
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
      tableSequences.getQuery().setSqlText(Sql.getSequenceList(filter.getSqlText(), generatorDescription));
      tableSequences.getQuery().paramByName("SYSTEM_FLAG").setString(currentSchemaName);
      tableSequences.getQuery().open();
      if (objectName != null && tableSequences.getQuery().locate("GENERATOR_NAME", new Variant(objectName))) {
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
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropSequence = new javax.swing.JMenuItem();
    cmDropSequence = new pl.mpak.sky.gui.swing.Action();
    cmGeneratorValue = new pl.mpak.sky.gui.swing.Action();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmCreateSequence = new pl.mpak.sky.gui.swing.Action();
    cmAlterSequence = new pl.mpak.sky.gui.swing.Action();
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

    jMenuItem1.setAction(cmCreateSequence);
    menuActions.add(jMenuItem1);

    jMenuItem2.setAction(cmAlterSequence);
    menuActions.add(jMenuItem2);
    menuActions.add(jSeparator2);

    menuDropSequence.setAction(cmDropSequence);
    menuActions.add(menuDropSequence);

    cmDropSequence.setActionCommandKey("cmDropSequence");
    cmDropSequence.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropSequence.setText(stringManager.getString("SequencesPanelView-cmDropSequence-text")); // NOI18N
    cmDropSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropSequenceActionPerformed(evt);
      }
    });

    cmGeneratorValue.setActionCommandKey("cmGeneratorValue");
    cmGeneratorValue.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/value.gif")); // NOI18N
    cmGeneratorValue.setText(stringManager.getString("SequencesPanelView-cmGeneratorValue-text")); // NOI18N
    cmGeneratorValue.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmGeneratorValueActionPerformed(evt);
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

    cmCreateSequence.setActionCommandKey("cmCreateSequence");
    cmCreateSequence.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/sequence.gif")); // NOI18N
    cmCreateSequence.setText(stringManager.getString("SequencesPanelView-cmCreateSequence-text")); // NOI18N
    cmCreateSequence.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateSequenceActionPerformed(evt);
      }
    });

    cmAlterSequence.setActionCommandKey("cmAlterSequence");
    cmAlterSequence.setText(stringManager.getString("SequencesPanelView-cmAlterSequence-text")); // NOI18N
    cmAlterSequence.setTooltip(stringManager.getString("SequencesPanelView-cmAlterSequence-hint")); // NOI18N
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

    toolButton1.setAction(cmGeneratorValue);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(toolButton1);

    toolButton2.setAction(cmComment);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(toolButton2);

    toolButton3.setAction(cmCreateSequence);
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
  if (!tableSequences.getQuery().isActive()) {
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

private void cmDropSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropSequenceActionPerformed
  if (tableSequences.getSelectedRow() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
      String sequenceName = tableSequences.getQuery().fieldByName("GENERATOR_NAME").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("SequencesPanelView-drop-sequence-q"), new Object[] {sequenceName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop generator " +SQLUtil.createSqlName(sequenceName), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropSequenceActionPerformed

private void cmGeneratorValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGeneratorValueActionPerformed
  if (tableSequences.getSelectedRow() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectedRow());
      String sequenceName = tableSequences.getQuery().fieldByName("GENERATOR_NAME").getString();
      Query query = getDatabase().createQuery();
      try {
        query.open("SELECT GEN_ID( " +SQLUtil.createSqlName(sequenceName) +", 0 ) VAL FROM RDB$DATABASE");
        MessageBox.show((Dialog)null, stringManager.getString("SequencesPanelView-value"), String.format(stringManager.getString("SequencesPanelView-sequence-value"), new Object[] {sequenceName, query.fieldByName("VAL").getLong()}), ModalResult.OK);
      }
      finally {
        query.close();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }

}//GEN-LAST:event_cmGeneratorValueActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableSequences.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectionModel().getLeadSelectionIndex());
      String sequenceName = tableSequences.getQuery().fieldByName("GENERATOR_NAME").getString();
      if (SqlCodeWizardDialog.show(new CommentWizard(getDatabase(), sequenceName, "RDB$GENERATORS"), true) != null) {
        refresh(sequenceName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmCreateSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateSequenceActionPerformed
  SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new CreateSequenceWizard(getDatabase()), true);
  if (result != null) {
    refresh(result.getResultMap().get("object_name"));
  }
}//GEN-LAST:event_cmCreateSequenceActionPerformed

private void cmAlterSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAlterSequenceActionPerformed
  if (tableSequences.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableSequences.getQuery().getRecord(tableSequences.getSelectionModel().getLeadSelectionIndex());
      String sequenceName = tableSequences.getQuery().fieldByName("GENERATOR_NAME").getString();
      SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new AlterSequenceWizard(getDatabase(), sequenceName), true);
      if (result != null) {
        refresh(result.getResultMap().get("object_name"));
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmAlterSequenceActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmAlterSequence;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmCreateSequence;
  private pl.mpak.sky.gui.swing.Action cmDropSequence;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmGeneratorValue;
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
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarSequences;
  private ViewTable tableSequences;
  private javax.swing.JToolBar toolBar;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  // End of variables declaration//GEN-END:variables

}

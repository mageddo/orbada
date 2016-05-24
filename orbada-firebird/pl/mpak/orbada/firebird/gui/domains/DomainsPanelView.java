package pl.mpak.orbada.firebird.gui.domains;

import java.awt.Dialog;
import java.io.IOException;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.wizards.CommentWizard;
import pl.mpak.orbada.firebird.gui.wizards.CreateDomainWizard;
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
public class DomainsPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  public DomainsPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = "USER";
    tableDomains.getQuery().setDatabase(getDatabase());
    try {
      tableDomains.addColumn(new QueryTableColumn("DOMAIN_NAME", stringManager.getString("DomainsPanelView-domain-name"), 190, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableDomains.addColumn(new QueryTableColumn("DISPLAY_TYPE", stringManager.getString("DomainsPanelView-type"), 150));
      tableDomains.addColumn(new QueryTableColumn("VALIDATION_SOURCE", stringManager.getString("DomainsPanelView-constraint"), 250));
      tableDomains.addColumn(new QueryTableColumn("COMPUTED_SOURCE", stringManager.getString("DomainsPanelView-computed-source"), 250));
      tableDomains.addColumn(new QueryTableColumn("DEFAULT_SOURCE", stringManager.getString("DomainsPanelView-default-value"), 150));
      tableDomains.addColumn(new QueryTableColumn("NULL_FLAG", stringManager.getString("DomainsPanelView-null-short-q"), 50));
      tableDomains.addColumn(new QueryTableColumn("CHARACTER_SET_NAME", stringManager.getString("DomainsPanelView-char-coding"), 100));
      tableDomains.addColumn(new QueryTableColumn("DESCRIPTION", stringManager.getString("comment"), 100));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("F.RDB$FIELD_NAME", stringManager.getString("DomainsPanelView-domain-name"), (String[])null));
      def.add(new SqlFilterDefComponent("(SELECT TRIM(RDB$TYPE_NAME) FROM RDB$TYPES T WHERE T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE' AND T.RDB$TYPE = F.RDB$FIELD_TYPE)", stringManager.getString("DomainsPanelView-type"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-domains-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableDomains, buttonActions, menuActions, "firebird-domains-actions");
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
      if (tableDomains.getQuery().isActive() && tableDomains.getSelectedRow() >= 0) {
        tableDomains.getQuery().getRecord(tableDomains.getSelectedRow());
        objectName = tableDomains.getQuery().fieldByName("DOMAIN_NAME").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableDomains.getSelectedColumn();
      int index = Math.max(0, tableDomains.getSelectedRow());
      tableDomains.getQuery().close();
      tableDomains.getQuery().setSqlText(Sql.getDomainList(filter.getSqlText()));
      tableDomains.getQuery().paramByName("SYSTEM_FLAG").setString(currentSchemaName);
      tableDomains.getQuery().open();
      if (objectName != null && tableDomains.getQuery().locate("DOMAIN_NAME", new Variant(objectName))) {
        tableDomains.changeSelection(tableDomains.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableDomains.getQuery().isEmpty()) {
        tableDomains.changeSelection(Math.min(index, tableDomains.getRowCount() -1), column);
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
    tableDomains.getQuery().close();
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
    jSeparator2 = new javax.swing.JSeparator();
    menuDropSequence = new javax.swing.JMenuItem();
    cmDropDomain = new pl.mpak.sky.gui.swing.Action();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmCreateDomain = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableDomains = new pl.mpak.orbada.gui.comps.table.ViewTable();
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

    jMenuItem1.setAction(cmCreateDomain);
    menuActions.add(jMenuItem1);
    menuActions.add(jSeparator2);

    menuDropSequence.setAction(cmDropDomain);
    menuActions.add(menuDropSequence);

    cmDropDomain.setActionCommandKey("cmDropDomain");
    cmDropDomain.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropDomain.setText(stringManager.getString("DomainsPanelView-cmDropDomain-text")); // NOI18N
    cmDropDomain.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropDomainActionPerformed(evt);
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

    cmCreateDomain.setActionCommandKey("cmCreateDomain");
    cmCreateDomain.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/domain.gif")); // NOI18N
    cmCreateDomain.setText(stringManager.getString("DomainsPanelView-cmCreateDomain-text")); // NOI18N
    cmCreateDomain.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateDomainActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableDomains);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableDomains);
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

    toolButton3.setAction(cmCreateDomain);
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
  if (!tableDomains.getQuery().isActive()) {
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

private void cmDropDomainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropDomainActionPerformed
  if (tableDomains.getSelectedRow() >= 0) {
    try {
      tableDomains.getQuery().getRecord(tableDomains.getSelectedRow());
      String domainName = tableDomains.getQuery().fieldByName("DOMAIN_NAME").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("DomainsPanelView-drop-domain-q"), new Object[] {domainName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop domain " +SQLUtil.createSqlName(domainName), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropDomainActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableDomains.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableDomains.getQuery().getRecord(tableDomains.getSelectionModel().getLeadSelectionIndex());
      String sequenceName = tableDomains.getQuery().fieldByName("DOMAIN_NAME").getString();
      if (SqlCodeWizardDialog.show(new CommentWizard(getDatabase(), sequenceName, "RDB$FIELDS"), true) != null) {
        refresh(sequenceName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmCreateDomainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateDomainActionPerformed
  SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new CreateDomainWizard(getDatabase()), true);
  if (result != null) {
    refresh(result.getResultMap().get("object_name"));
  }
}//GEN-LAST:event_cmCreateDomainActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmCreateDomain;
  private pl.mpak.sky.gui.swing.Action cmDropDomain;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuDropSequence;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableDomains;
  private javax.swing.JToolBar toolBar;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  // End of variables declaration//GEN-END:variables

}

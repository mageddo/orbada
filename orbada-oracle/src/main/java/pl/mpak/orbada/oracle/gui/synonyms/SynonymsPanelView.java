package pl.mpak.orbada.oracle.gui.synonyms;

import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;

import orbada.gui.comps.table.ViewTable;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.CreateSynonymWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropSynonymWizard;
import pl.mpak.orbada.oracle.gui.wizards.RenameSynonymWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class SynonymsPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  public SynonymsPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    tableSynonyms.getQuery().setDatabase(getDatabase());
    try {
      tableSynonyms.addColumn(new QueryTableColumn("synonym_name", stringManager.getString("synonym-name"), 190, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableSynonyms.addColumn(new QueryTableColumn("table_owner", stringManager.getString("object-schema"), 120));
      tableSynonyms.addColumn(new QueryTableColumn("table_name", stringManager.getString("object-name"), 150));
      tableSynonyms.addColumn(new QueryTableColumn("object_type", stringManager.getString("object-type"), 120));
      tableSynonyms.addColumn(new QueryTableColumn("db_link", stringManager.getString("db-link"), 150));
      tableSynonyms.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 50));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("syns.synonym_name", stringManager.getString("synonym-name"), (String[])null));
      def.add(new SqlFilterDefComponent("(select object_type from all_objects o where o.owner = syns.table_owner and o.object_name = syns.table_name and rownum = 1) is null", stringManager.getString("no-object")));
      def.add(new SqlFilterDefComponent("(select object_type from all_objects o where o.owner = syns.table_owner and o.object_name = syns.table_name and rownum = 1)", stringManager.getString("objects-type"), new String[] {"'TABLE'", "'VIEW'", "'FUNCTION'", "'PROCEDURE'", "'SEQUENCE'", "'PACKAGE'"}));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-synonyms-filter"),
        cmFilter, buttonFilter,
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableSynonyms, buttonActions, menuActions, "oracle-synonyms-actions");
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(OracleDbInfoProvider.getCurrentSchema(getDatabase()))) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
      }
      else {
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
      if (tableSynonyms.getQuery().isActive() && tableSynonyms.getSelectedRow() >= 0) {
        tableSynonyms.getQuery().getRecord(tableSynonyms.getSelectedRow());
        objectName = tableSynonyms.getQuery().fieldByName("synonym_name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableSynonyms.getSelectedColumn();
      int index = Math.max(0, tableSynonyms.getSelectedRow());
      tableSynonyms.getQuery().close();
      tableSynonyms.getQuery().setSqlText(Sql.getSynonymList(filter.getSqlText()));
      tableSynonyms.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableSynonyms.getQuery().open();
      if (objectName != null && tableSynonyms.getQuery().locate("synonym_name", new Variant(objectName))) {
        tableSynonyms.changeSelection(tableSynonyms.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableSynonyms.getQuery().isEmpty()) {
        tableSynonyms.changeSelection(Math.min(index, tableSynonyms.getRowCount() -1), column);
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
    tableSynonyms.getQuery().close();
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
    menuCreateSynonym = new javax.swing.JMenuItem();
    menuRenameSynonym = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropSynonym = new javax.swing.JMenuItem();
    cmDropSynonym = new pl.mpak.sky.gui.swing.Action();
    cmCreateSynonym = new pl.mpak.sky.gui.swing.Action();
    cmRenameSynonym = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSynonyms = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
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

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    menuCreateSynonym.setAction(cmCreateSynonym);
    menuActions.add(menuCreateSynonym);

    menuRenameSynonym.setAction(cmRenameSynonym);
    menuActions.add(menuRenameSynonym);
    menuActions.add(jSeparator2);

    menuDropSynonym.setAction(cmDropSynonym);
    menuActions.add(menuDropSynonym);

    cmDropSynonym.setActionCommandKey("cmDropSynonym");
    cmDropSynonym.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropSynonym.setText(stringManager.getString("cmDropSynonym-text")); // NOI18N
    cmDropSynonym.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropSynonymActionPerformed(evt);
      }
    });

    cmCreateSynonym.setActionCommandKey("cmCreateSynonym");
    cmCreateSynonym.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/synonym.gif")); // NOI18N
    cmCreateSynonym.setText(stringManager.getString("cmCreateSynonym-text")); // NOI18N
    cmCreateSynonym.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateSynonymActionPerformed(evt);
      }
    });

    cmRenameSynonym.setActionCommandKey("cmRenameSynonym");
    cmRenameSynonym.setText(stringManager.getString("cmRenameSynonym-text")); // NOI18N
    cmRenameSynonym.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRenameSynonymActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableSynonyms);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableSynonyms);
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
  if (!tableSynonyms.getQuery().isActive()) {
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

private void cmDropSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropSynonymActionPerformed
  if (tableSynonyms.getSelectedRow() >= 0) {
    try {
      tableSynonyms.getQuery().getRecord(tableSynonyms.getSelectedRow());
      String synonymName = tableSynonyms.getQuery().fieldByName("synonym_name").getString();
      if (SqlCodeWizardDialog.show(new DropSynonymWizard(getDatabase(), currentSchemaName, synonymName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropSynonymActionPerformed

private void cmCreateSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateSynonymActionPerformed
  if (SqlCodeWizardDialog.show(new CreateSynonymWizard(getDatabase(), currentSchemaName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateSynonymActionPerformed

private void cmRenameSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameSynonymActionPerformed
  if (tableSynonyms.getSelectedRow() >= 0) {
    try {
      tableSynonyms.getQuery().getRecord(tableSynonyms.getSelectedRow());
      String synonymName = tableSynonyms.getQuery().fieldByName("synonym_name").getString();
      if (SqlCodeWizardDialog.show(new RenameSynonymWizard(getDatabase(), currentSchemaName, synonymName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmRenameSynonymActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCreateSynonym;
  private pl.mpak.sky.gui.swing.Action cmDropSynonym;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRenameSynonym;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCreateSynonym;
  private javax.swing.JMenuItem menuDropSynonym;
  private javax.swing.JMenuItem menuRenameSynonym;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableSynonyms;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

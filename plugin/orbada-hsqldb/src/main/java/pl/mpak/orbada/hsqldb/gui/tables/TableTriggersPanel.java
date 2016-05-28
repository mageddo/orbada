package pl.mpak.orbada.hsqldb.gui.tables;

import java.awt.Dialog;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;

/**
 *
 * @author  akaluza
 */
public class TableTriggersPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private SqlFilter filter;
  
  private Timer timer;
  
  /** Creates new form TableTriggersPanel
   * @param accesibilities
   */
  public TableTriggersPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refresh();
      }
    };
    OrbadaHSqlDbPlugin.getRefreshQueue().add(timer);
    
    syntaxTrigger.setDatabase(getDatabase());
    syntaxTrigger.setEditable(false);
    tableTriggers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableTriggers.getSelectedRow();
        if (rowIndex >= 0 && tableTriggers.getQuery().isActive()) {
          try {
            tableTriggers.getQuery().getRecord(rowIndex);
            updateTriggerBody();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          syntaxTrigger.setText("");
        }
      }
    });
    
    tableTriggers.getQuery().setDatabase(getDatabase());
    try {
      tableTriggers.addColumn(new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableTriggers.addColumn(new QueryTableColumn("trigger_type", stringManager.getString("when"), 120));
      tableTriggers.addColumn(new QueryTableColumn("triggering_event", stringManager.getString("event"), 80));
      if (HSqlDbInfoProvider.getVersionTest(getDatabase()) == HSqlDbInfoProvider.hsqlDb18) {
        tableTriggers.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 80));
      }
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("trigger_name", stringManager.getString("trigger-name"), (String[])null));
      if (HSqlDbInfoProvider.getVersionTest(getDatabase()) == HSqlDbInfoProvider.hsqlDb18) {
        def.add(new SqlFilterDefComponent("status = 'DISABLED'", stringManager.getString("disabled")));
      }
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-table-triggers-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableTriggers, buttonActions, menuActions, "hsqldb-table-triggers-actions");
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableTriggers.getQuery().close();
      tableTriggers.getQuery().setSqlText(Sql.getTriggerList(filter.getSqlText(), HSqlDbInfoProvider.getVersionTest(getDatabase())));
      tableTriggers.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableTriggers.getQuery().paramByName("table_name").setString(currentTableName);
      tableTriggers.getQuery().open();
      if (!tableTriggers.getQuery().isEmpty()) {
        tableTriggers.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void updateTriggerBody() {
    try {
      syntaxTrigger.setText(tableTriggers.getQuery().fieldByName("source").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableTriggersPanel-title");
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentTableName = objectName;
      if (isVisible()) {
        timer.restart();
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
    timer.cancel();
    tableTriggers.getQuery().close();
    syntaxTrigger.setDatabase(null);
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
    cmDropTrigger = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTriggers = new ViewTable();
    statusBarTriggers = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    syntaxTrigger = new OrbadaSyntaxTextArea();
    jPanel2 = new javax.swing.JPanel();
    toolBarTriggers = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JSeparator();
    buttonDropTrigger = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

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

    cmDropTrigger.setActionCommandKey("cmDropTrigger");
    cmDropTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif")); // NOI18N
    cmDropTrigger.setText(stringManager.getString("cmDropTrigger-text")); // NOI18N
    cmDropTrigger.setTooltip(stringManager.getString("cmDropTrigger-hint")); // NOI18N
    cmDropTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTriggerActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTriggers);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarTriggers.setShowFieldType(false);
    statusBarTriggers.setShowOpenTime(false);
    statusBarTriggers.setTable(tableTriggers);
    jPanel1.add(statusBarTriggers, java.awt.BorderLayout.SOUTH);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    syntaxTrigger.setPreferredSize(new java.awt.Dimension(81, 150));
    add(syntaxTrigger, java.awt.BorderLayout.SOUTH);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarTriggers.setFloatable(false);
    toolBarTriggers.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonFilter);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarTriggers.add(jSeparator2);

    buttonDropTrigger.setAction(cmDropTrigger);
    buttonDropTrigger.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDropTrigger.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonDropTrigger);
    toolBarTriggers.add(jSeparator1);
    toolBarTriggers.add(buttonActions);

    jPanel2.add(toolBarTriggers);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTriggerActionPerformed
  if (tableTriggers.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableTriggers.getQuery().fieldByName("trigger_name").getString(), getDatabase());
      String tableName = SQLUtil.createSqlName(currentSchemaName, currentTableName, getDatabase());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TableTriggersPanel-drop-trigger-q"), new Object[] {objectName, tableName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop trigger " +objectName, true);
        timer.restart();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmDropTriggerActionPerformed

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  refresh(null, currentSchemaName, currentTableName);
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDropTrigger;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmDropTrigger;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTriggers;
  private OrbadaSyntaxTextArea syntaxTrigger;
  private ViewTable tableTriggers;
  private javax.swing.JToolBar toolBarTriggers;
  // End of variables declaration//GEN-END:variables
  
}

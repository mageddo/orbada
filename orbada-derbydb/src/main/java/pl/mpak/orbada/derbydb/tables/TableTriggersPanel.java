package pl.mpak.orbada.derbydb.tables;

import java.awt.Dialog;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
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
import pl.mpak.util.timer.Timer;

/**
 *
 * @author  akaluza
 */
public class TableTriggersPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

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
    OrbadaDerbyDbPlugin.getRefreshQueue().add(timer);
    
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
      tableTriggers.addColumn(new QueryTableColumn("triggername", stringManager.getString("TableTriggersPanel-trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableTriggers.addColumn(new QueryTableColumn("creationtimestamp", stringManager.getString("TableTriggersPanel-created"), 110));
      tableTriggers.addColumn(new QueryTableColumn("enabled", stringManager.getString("TableTriggersPanel-state"), 70));
      tableTriggers.addColumn(new QueryTableColumn("firingtime", stringManager.getString("TableTriggersPanel-when"), 120));
      tableTriggers.addColumn(new QueryTableColumn("event", stringManager.getString("TableTriggersPanel-event"), 80));
      tableTriggers.addColumn(new QueryTableColumn("type", stringManager.getString("TableTriggersPanel-for"), 150));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("trg.triggername", stringManager.getString("TableTriggersPanel-trigger-name"), (String[])null));
      def.add(new SqlFilterDefComponent("trg.state = 'D'", stringManager.getString("TableTriggersPanel-disabled")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-table-triggers-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableTriggers, buttonActions, menuActions, "derbydb-table-triggers-actions");
  }
  
  public String getTitle() {
    return stringManager.getString("TableTriggersPanel-title");
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableTriggers.getQuery().close();
      tableTriggers.getQuery().setSqlText(DerbyDbSql.getTableTriggerList(filter.getSqlText()));
      tableTriggers.getQuery().paramByName("schemaname").setString(currentSchemaName);
      tableTriggers.getQuery().paramByName("tablename").setString(currentTableName);
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
      String columnList = "";
      String body = "CREATE TRIGGER " +tableTriggers.getQuery().fieldByName("triggername").getString() +"\n";
      if (!tableTriggers.getQuery().fieldByName("referencedcolumns").isNull()) {
        Query query = getDatabase().createQuery();
        try {
          query.setSqlText("select columnname from sys.syscolumns where referenceid = :tableid and columnnumber in " +tableTriggers.getQuery().fieldByName("referencedcolumns").getString());
          query.paramByName("tableid").setString(tableTriggers.getQuery().fieldByName("tableid").getString());
          query.open();
          while (!query.eof()) {
            if (columnList.length() > 0) {
              columnList = columnList +", ";
            }
            columnList = columnList +query.fieldByName("columnname").getString();
            query.next();
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
          syntaxTrigger.setText("");
        } finally {
          query.close();
        }
      }
      body = body +
        tableTriggers.getQuery().fieldByName("firingtime").getString() +
        " " +tableTriggers.getQuery().fieldByName("event").getString() +
        (columnList.length() > 0 ? (" OF " +columnList) : "") +
        " ON " +SQLUtil.createSqlName(currentSchemaName, currentTableName, getDatabase()) +"\n";
      if (!tableTriggers.getQuery().fieldByName("referencingold").isNull() ||
          !tableTriggers.getQuery().fieldByName("referencingnew").isNull()) {
        body = body +"REFERENCING";
        if (!tableTriggers.getQuery().fieldByName("referencingold").isNull()) {
          body = body +" " +tableTriggers.getQuery().fieldByName("referencingold").getString();
        }
        if (!tableTriggers.getQuery().fieldByName("referencingnew").isNull()) {
          body = body +" " +tableTriggers.getQuery().fieldByName("referencingnew").getString();
        }
        body = body +"\n";
      }
      body = body +tableTriggers.getQuery().fieldByName("type").getString() +"\n";
      body = body +tableTriggers.getQuery().fieldByName("triggerdefinition").getString();
      syntaxTrigger.setText(body);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      syntaxTrigger.setText(ex.getMessage());
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
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
    cmSwitchEnabled = new pl.mpak.sky.gui.swing.Action();
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
    buttonSwitchEnabled = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonDropTrigger = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
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
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/derbydb/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmDropTrigger.setActionCommandKey("cmDropTrigger");
    cmDropTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTrigger.setText(stringManager.getString("TableTriggersPanel-cmDropTrigger-text")); // NOI18N
    cmDropTrigger.setTooltip(stringManager.getString("TableTriggersPanel-cmDropTrigger-hint")); // NOI18N
    cmDropTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTriggerActionPerformed(evt);
      }
    });

    cmSwitchEnabled.setActionCommandKey("cmSwitchEnabled");
    cmSwitchEnabled.setEnabled(false);
    cmSwitchEnabled.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmSwitchEnabled.setText(stringManager.getString("TableTriggersPanel-cmSwitchEnabled-text")); // NOI18N
    cmSwitchEnabled.setTooltip(stringManager.getString("TableTriggersPanel-cmSwitchEnabled-hint")); // NOI18N

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

    buttonSwitchEnabled.setAction(cmSwitchEnabled);
    buttonSwitchEnabled.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSwitchEnabled.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonSwitchEnabled);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarTriggers.add(jSeparator1);

    buttonDropTrigger.setAction(cmDropTrigger);
    buttonDropTrigger.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDropTrigger.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonDropTrigger);
    toolBarTriggers.add(jSeparator3);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonActions);

    jPanel2.add(toolBarTriggers);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTriggerActionPerformed
  if (tableTriggers.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableTriggers.getQuery().fieldByName("triggername").getString(), getDatabase());
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), stringManager.getString("TableTriggersPanel-delete-trigger-q"), ModalResult.YESNO) == ModalResult.YES) {
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
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSwitchEnabled;
  private pl.mpak.sky.gui.swing.Action cmDropTrigger;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSwitchEnabled;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTriggers;
  private OrbadaSyntaxTextArea syntaxTrigger;
  private ViewTable tableTriggers;
  private javax.swing.JToolBar toolBarTriggers;
  // End of variables declaration//GEN-END:variables
  
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DatasPanel.java
 *
 * Created on 2009-03-27, 19:53:10
 */
package pl.mpak.mpak.oracle.tune.gui.profiler;

import java.io.Closeable;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.ViewTable;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DatasPanel extends javax.swing.JPanel implements Closeable {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleTunePlugin.class);

  private IViewAccesibilities accesibilities;
  private Timer timer;
  private SqlFilter filter;
  private SourcePanel sourcePanel;
  private RunsPanel runsPanel;
  private long runid;
  private String schemaName;
  private String objectType;
  private String objectName;

  /** Creates new form DatasPanel */
  public DatasPanel(IViewAccesibilities accesibilities, RunsPanel runsPanel) {
    this.accesibilities = accesibilities;
    this.runsPanel = runsPanel;
    initComponents();
    init();
  }

  private void init() {
    sourcePanel = new SourcePanel(accesibilities);
    split.setBottomComponent(sourcePanel);
    
    timer = new Timer(200) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refreshLine();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    tableDatas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
      }
    });

    tableDatas.getQuery().setDatabase(getDatabase());
    tableDatas.addColumn(new QueryTableColumn("line#", stringManager.getString("line"), 70));
    tableDatas.addColumn(new QueryTableColumn("total_occur", stringManager.getString("total-occur"), 70));
    tableDatas.addColumn(new QueryTableColumn("total_time", stringManager.getString("total-time"), 120));
    tableDatas.addColumn(new QueryTableColumn("min_time", stringManager.getString("min-time"), 120));
    tableDatas.addColumn(new QueryTableColumn("max_time", stringManager.getString("max-time"), 120));
    tableDatas.addColumn(new QueryTableColumn("avg_time", stringManager.getString("avg-time"), 120));
    tableDatas.addColumn(new QueryTableColumn("perc_total_time", stringManager.getString("perc-total-time"), 70));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("line#", stringManager.getString("line"), (String[])null));
    def.add(new SqlFilterDefComponent("total_occur", stringManager.getString("total-occur"), (String[])null));
    def.add(new SqlFilterDefComponent("total_occur > 0", stringManager.getString("any-occur")));
    def.add(new SqlFilterDefComponent("total_time", stringManager.getString("total-time"), (String[])null));
    def.add(new SqlFilterDefComponent("min_time", stringManager.getString("min-time"), (String[])null));
    def.add(new SqlFilterDefComponent("max_time", stringManager.getString("max-time"), (String[])null));
    def.add(new SqlFilterDefComponent("avg_time", stringManager.getString("avg-time"), (String[])null));
    def.add(new SqlFilterDefComponent("perc_total_time", stringManager.getString("perc-total-time"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tune-profiler-datas-filter"),
      cmFilter, buttonFilter,
      def);

    new ComponentActionsAction(getDatabase(), tableDatas, buttonActions, menuActions, "oracle-tune-profiler-datas-actions");

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        split.setDividerLocation(0.5f);
      }
    });
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }

  private void refreshTableList(String lineNo) {
    try {
      int column = tableDatas.getSelectedColumn();
      int index = Math.max(0, tableDatas.getSelectedRow());
      if (lineNo == null & tableDatas.getQuery().isActive() && tableDatas.getSelectedRow() >= 0) {
        if (tableDatas.getQuery().getRecord(tableDatas.getSelectedRow()) != null) {
          lineNo = tableDatas.getQuery().fieldByName("line#").getString();
        }
      }
      tableDatas.getQuery().close();
      tableDatas.getQuery().setSqlText(Sql.getProfilerDataList(filter.getSqlText()));
      tableDatas.getQuery().paramByName("runid").setLong(runid);
      tableDatas.getQuery().paramByName("unit_owner").setString(schemaName);
      tableDatas.getQuery().paramByName("unit_type").setString(objectType);
      tableDatas.getQuery().paramByName("unit_name").setString(objectName);
      tableDatas.getQuery().paramByName("prec").setLong(runsPanel.getUnitPrecision());
      tableDatas.getQuery().open();
      tableDatas.getQuery().flushAll();
      if (!tableDatas.getQuery().isEmpty()) {
        if ("-1".equals(lineNo)) {
          tableDatas.changeSelection(0, column);
        } else if (lineNo != null && tableDatas.getQuery().locate("line#", new Variant(lineNo))) {
          tableDatas.changeSelection(tableDatas.getQuery().getCurrentRecord().getIndex(), column);
        } else {
          tableDatas.changeSelection(Math.min(index, tableDatas.getRowCount() -1), column);
        }
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      timer.restart();
    }
  }

  public void refresh(long runid, String schemaName, String objectType, String objectName) {
    this.runid = runid;
    this.schemaName = schemaName;
    this.objectType = objectType;
    this.objectName = objectName;
    refreshTableList("-1");
    sourcePanel.refresh(schemaName, objectType, objectName);
  }

  private void refreshLine() {
    if (tableDatas.getSelectedRow() >= 0) {
      try {
        if (tableDatas.getQuery().getRecord(tableDatas.getSelectedRow()) != null) {
          sourcePanel.gotoLine(tableDatas.getQuery().fieldByName("line#").getInteger());
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  public void close() throws IOException {
    timer.cancel();
    tableDatas.getQuery().close();
    sourcePanel.close();
    sourcePanel = null;
    runsPanel = null;
    accesibilities = null;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmFilter = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jPanel1 = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    split = new javax.swing.JSplitPane();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableDatas = new ViewTable();
    queryTableStatusBar1 = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();

    cmRefresh.setActionCommandKey("cmRefresh");
    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonFilter);
    jToolBar1.add(jSeparator1);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonActions);

    jPanel1.add(jToolBar1);

    add(jPanel1, java.awt.BorderLayout.PAGE_START);

    split.setBorder(null);
    split.setDividerLocation(200);
    split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel2.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableDatas);

    jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    queryTableStatusBar1.setTable(tableDatas);
    jPanel2.add(queryTableStatusBar1, java.awt.BorderLayout.SOUTH);

    split.setTopComponent(jPanel2);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
    refreshTableList(null);
}//GEN-LAST:event_cmRefreshActionPerformed

  private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
    if (SqlFilterDialog.show(filter)) {
      refreshTableList(null);
    }
}//GEN-LAST:event_cmFilterActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar queryTableStatusBar1;
  private javax.swing.JSplitPane split;
  private ViewTable tableDatas;
  // End of variables declaration//GEN-END:variables
}

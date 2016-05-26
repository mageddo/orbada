/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.mpak.oracle.tune.gui.profiler;

import java.awt.Font;
import java.io.Closeable;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.freezing.FreezeFactory;
import pl.mpak.orbada.oracle.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
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
public class UnitsPanel extends javax.swing.JPanel implements Closeable {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-tune");

  private IViewAccesibilities accesibilities;
  private Timer timer;
  private SqlFilter filter;
  private DatasPanel datasPanel;
  private FreezeFactory freezeFactory;
  private RunsPanel runsPanel;
  private long runid;

  /** Creates new form DatasPanel */
  public UnitsPanel(IViewAccesibilities accesibilities, RunsPanel runsPanel) {
    this.accesibilities = accesibilities;
    this.runsPanel = runsPanel;
    freezeFactory = new FreezeFactory(accesibilities);
    initComponents();
    init();
  }

  private void init() {
    datasPanel = new DatasPanel(accesibilities, runsPanel);
    split.setBottomComponent(datasPanel);
    textFind.addKeyListener(new TableRowChangeKeyListener(tableUnits));
    
    timer = new Timer(200) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refreshDatas();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    tableUnits.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
        if (tableUnits.getSelectedRow() >= 0) {
          try {
            if (tableUnits.getQuery().getRecord(tableUnits.getSelectedRow()) != null) {
              cmFreezeObject.setEnabled(freezeFactory.canCreate(tableUnits.getQuery().fieldByName("unit_type").getString()));
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });

    tableUnits.getQuery().setDatabase(getDatabase());
    tableUnits.addColumn(new QueryTableColumn("unit_type", stringManager.getString("object-type"), 150));
    tableUnits.addColumn(new QueryTableColumn("unit_owner", stringManager.getString("schema"), 150));
    tableUnits.addColumn(new QueryTableColumn("unit_name", stringManager.getString("obejct-name"), 250, new QueryTableCellRenderer(Font.BOLD)));
    tableUnits.addColumn(new QueryTableColumn("total_time", stringManager.getString("total-time"), 120));
    tableUnits.addColumn(new QueryTableColumn("perc_total_time", stringManager.getString("perc-total-time"), 70));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("unit_type", stringManager.getString("object-type"), (String[])null));
    def.add(new SqlFilterDefComponent("unit_owner", stringManager.getString("schema"), (String[])null));
    def.add(new SqlFilterDefComponent("unit_name", stringManager.getString("obejct-name"), (String[])null));
    def.add(new SqlFilterDefComponent("total_time", stringManager.getString("total-time"), (String[])null));
    def.add(new SqlFilterDefComponent("perc_total_time", stringManager.getString("perc-total-time"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tune-profiler-units-filter"),
      cmFilter, buttonFilter,
      def);

    new ComponentActionsAction(getDatabase(), tableUnits, buttonActions, menuActions, "oracle-tune-profiler-units-actions");
    SwingUtil.addAction(textFind, cmFind);
    SwingUtil.addAction(tableUnits, cmFreezeObject);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        split.setDividerLocation(0.4f);
      }
    });
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }

  private void refreshTableList(String objectName) {
    try {
      String findFilter = filter.getSqlText();
      String find = textFind.getText();
      if (!"".equals(find)) {
        if (findFilter != null) {
          findFilter = findFilter +"\n  and ";
        }
        else {
          findFilter = "";
        }
        findFilter = findFilter +"(unit_name like '%'||upper(:FIND)||'%' or unit_type = '%'||upper(:FIND)||'%')";
      }
      int column = tableUnits.getSelectedColumn();
      int index = Math.max(0, tableUnits.getSelectedRow());
      if (objectName == null & tableUnits.getQuery().isActive() && tableUnits.getSelectedRow() >= 0) {
        if (tableUnits.getQuery().getRecord(tableUnits.getSelectedRow()) != null) {
          objectName = tableUnits.getQuery().fieldByName("unit_name").getString();
        }
      }
      tableUnits.getQuery().close();
      tableUnits.getQuery().setSqlText(Sql.getProfilerUnitList(findFilter));
      tableUnits.getQuery().paramByName("runid").setLong(runid);
      tableUnits.getQuery().paramByName("prec").setLong(runsPanel.getUnitPrecision());
      if (!"".equals(find)) {
        tableUnits.getQuery().paramByName("FIND").setString(find);
      }
      tableUnits.getQuery().open();
      tableUnits.getQuery().flushAll();
      if (!tableUnits.getQuery().isEmpty()) {
        if ("".equals(objectName)) {
          tableUnits.changeSelection(0, column);
        } else if (objectName != null && tableUnits.getQuery().locate("unit_name", new Variant(objectName))) {
          tableUnits.changeSelection(tableUnits.getQuery().getCurrentRecord().getIndex(), column);
        } else {
          tableUnits.changeSelection(Math.min(index, tableUnits.getRowCount() -1), column);
        }
      }
      else {
        refreshDatas();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      timer.restart();
    }
  }

  public void refresh(long runid) {
    this.runid = runid;
    refreshTableList("");
  }

  private void refreshDatas() {
    if (tableUnits.getSelectedRow() >= 0) {
      try {
        tableUnits.getQuery().getRecord(tableUnits.getSelectedRow());
        datasPanel.refresh(runid,
          tableUnits.getQuery().fieldByName("unit_owner").getString(),
          tableUnits.getQuery().fieldByName("unit_type").getString(),
          tableUnits.getQuery().fieldByName("unit_name").getString());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else {
      datasPanel.refresh(runid, null, null, null);
    }
  }

  public void close() throws IOException {
    timer.cancel();
    tableUnits.getQuery().close();
    datasPanel.close();
    datasPanel = null;
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
    cmFind = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonFreeze = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel5 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    textFind = new pl.mpak.sky.gui.swing.comp.TextField();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    split = new javax.swing.JSplitPane();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableUnits = new ViewTable();
    queryTableStatusBar1 = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();

    cmRefresh.setActionCommandKey("cmRefresh");
    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmFind.setActionCommandKey("cmFind");
    cmFind.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmFind.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/find_source.gif")); // NOI18N
    cmFind.setText(stringManager.getString("cmFind-text")); // NOI18N
    cmFind.setTooltip(stringManager.getString("cmFind-hint")); // NOI18N
    cmFind.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFindActionPerformed(evt);
      }
    });

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
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

    buttonFreeze.setAction(cmFreezeObject);
    buttonFreeze.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFreeze.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonFreeze);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonActions);

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 3));

    jLabel2.setDisplayedMnemonic('S');
    jLabel2.setLabelFor(textFind);
    jLabel2.setText(stringManager.getString("search-dd")); // NOI18N
    jPanel5.add(jLabel2);

    textFind.setPreferredSize(new java.awt.Dimension(120, 20));
    jPanel5.add(textFind);

    jToolBar1.add(jPanel5);

    toolButton1.setAction(cmFind);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(toolButton1);

    jPanel1.add(jToolBar1);

    add(jPanel1, java.awt.BorderLayout.PAGE_START);

    split.setBorder(null);
    split.setDividerLocation(200);
    split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel2.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableUnits);

    jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    queryTableStatusBar1.setTable(tableUnits);
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

  private void cmFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFindActionPerformed
    refreshTableList(null);
}//GEN-LAST:event_cmFindActionPerformed

  private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
    if (tableUnits.getSelectedRow() >= 0) {
      try {
        tableUnits.getQuery().getRecord(tableUnits.getSelectedRow());
        String objectName = tableUnits.getQuery().fieldByName("unit_name").getString();
        String objectType = tableUnits.getQuery().fieldByName("unit_type").getString();
        String schemaName = tableUnits.getQuery().fieldByName("unit_owner").getString();
        FreezeViewService service = freezeFactory.createInstance(objectType, schemaName, objectName);
        if (service != null) {
          accesibilities.createView(service);
        }
      } catch (Exception ex) {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreeze;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFind;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar queryTableStatusBar1;
  private javax.swing.JSplitPane split;
  private ViewTable tableUnits;
  private pl.mpak.sky.gui.swing.comp.TextField textFind;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  // End of variables declaration//GEN-END:variables
}

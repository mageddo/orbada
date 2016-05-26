/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.mpak.oracle.tune.gui.profiler;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ParameterMetaData;
import java.sql.Types;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
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
public class RunsPanel extends javax.swing.JPanel implements Closeable {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleTunePlugin.class);

  private enum ProfileState {
    Started,
    Stopped,
    Paused
  }

  private IViewAccesibilities accesibilities;
  private Timer timer;
  private SqlFilter filter;
  private UnitsPanel unitsPanel;
  private boolean viewClosing;
  private ProfileState profileState = ProfileState.Stopped;
  private long runidProfile;
  private long unitPrecision;

  public RunsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }

  private void init() {
    unitPrecision = 1;
    Query query = getDatabase().createQuery();
    try {
       query.open(Sql.getProfilerUnitPrecision());
      if (!query.eof()) {
        unitPrecision = query.fieldByName("prec").getLong();
      }
    }
    catch (Exception ex) {
      try {
        query.open("select 'LINUX' platform from v$version where upper(banner) like '%LINUX:%' union all select 'WINDOWS' platform from v$version where upper(banner) like '%WINDOWS:%'");
        if (!query.eof()) {
          if ("LINUX".equals(query.fieldByName("platform").getString())) {
            unitPrecision = 1000000L;
          }
          else if ("WINDOWS".equals(query.fieldByName("platform").getString())) {
            unitPrecision = 1000000000L;
          }
        }
      } catch (Exception ex2) {
        unitPrecision = 1000000000L;
      }
    }
    finally {
      query.close();
    }
    accesibilities.addMenu(menuProfiler);
    unitsPanel = new UnitsPanel(accesibilities, this);
    split.setBottomComponent(unitsPanel);
    
    timer = new Timer(200) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refreshUnits();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    tableRuns.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
      }
    });

    tableRuns.getQuery().setDatabase(getDatabase());
    tableRuns.addColumn(new QueryTableColumn("runid", stringManager.getString("id"), 60));
    tableRuns.addColumn(new QueryTableColumn("run_date", stringManager.getString("date"), 120));
    tableRuns.addColumn(new QueryTableColumn("run_total_time", stringManager.getString("total-time"), 120));
    tableRuns.addColumn(new QueryTableColumn("run_comment1", stringManager.getString("comment"), 250));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("runid", stringManager.getString("id"), (String[])null));
    def.add(new SqlFilterDefComponent("run_date", stringManager.getString("date"), (String[])null));
    def.add(new SqlFilterDefComponent("run_total_time", stringManager.getString("total-time"), (String[])null));
    def.add(new SqlFilterDefComponent("run_comment1", stringManager.getString("comment"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tune-profiler-runs-filter"),
      cmFilter, buttonFilter,
      def);

    new ComponentActionsAction(getDatabase(), tableRuns, buttonActions, menuActions, "oracle-tune-profiler-runs-actions");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        split.setDividerLocation(0.3f);
      }
    });
    enableControls();
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }

  private void refreshTableList(String runId) {
    try {
      String findFilter = filter.getSqlText();
      if (!buttonAllRuns.isSelected()) {
        if (findFilter != null) {
          findFilter = findFilter +"\n  and ";
        }
        else {
          findFilter = "";
        }
        findFilter = findFilter +"run_comment1 = 'SESSIONID:'||userenv('SESSIONID')||' TERMINA:'||userenv('TERMINAL')||' USER:'||USER";
      }
      int column = tableRuns.getSelectedColumn();
      int index = Math.max(0, tableRuns.getSelectedRow());
      if (runId == null & tableRuns.getQuery().isActive() && tableRuns.getSelectedRow() >= 0) {
        if (tableRuns.getQuery().getRecord(tableRuns.getSelectedRow()) != null) {
          runId = tableRuns.getQuery().fieldByName("runid").getString();
        }
      }
      tableRuns.getQuery().close();
      tableRuns.getQuery().setSqlText(Sql.getProfilerRunList(findFilter));
      tableRuns.getQuery().paramByName("prec").setLong(getUnitPrecision());
      tableRuns.getQuery().open();
      tableRuns.getQuery().flushAll();
      if (!tableRuns.getQuery().isEmpty()) {
        if ("".equals(runId)) {
          tableRuns.changeSelection(0, column);
        } else if (runId != null && tableRuns.getQuery().locate("runid", new Variant(runId))) {
          tableRuns.changeSelection(tableRuns.getQuery().getCurrentRecord().getIndex(), column);
        } else {
          tableRuns.changeSelection(Math.min(index, tableRuns.getRowCount() -1), column);
        }
      }
      else {
        refreshUnits();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      timer.restart();
    }
  }

  private void refreshUnits() {
    if (tableRuns.getSelectedRow() >= 0) {
      try {
        tableRuns.getQuery().getRecord(tableRuns.getSelectedRow());
        unitsPanel.refresh(tableRuns.getQuery().fieldByName("runid").getLong());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else {
      unitsPanel.refresh(-1);
    }
  }

  public void close() throws IOException {
    viewClosing = true;
    if (profileState != ProfileState.Stopped) {
      cmStopProfile.performe();
    }
    timer.cancel();
    tableRuns.getQuery().close();
    unitsPanel.close();
    unitsPanel = null;
    accesibilities = null;
  }

  private void enableControls() {
    cmStartProfile.setEnabled(profileState == ProfileState.Stopped);
    cmStopProfile.setEnabled(profileState == ProfileState.Started || profileState == ProfileState.Paused);
    cmPauseProfile.setEnabled(profileState == ProfileState.Started);
    cmResumeProfile.setEnabled(profileState == ProfileState.Paused);
  }

  public long getUnitPrecision() {
    return buttonMilisecondUnit.isSelected() ? unitPrecision : 1L;
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
    cmAllRuns = new pl.mpak.sky.gui.swing.Action();
    cmDeleteRun = new pl.mpak.sky.gui.swing.Action();
    cmStartProfile = new pl.mpak.sky.gui.swing.Action();
    cmStopProfile = new pl.mpak.sky.gui.swing.Action();
    cmPauseProfile = new pl.mpak.sky.gui.swing.Action();
    cmResumeProfile = new pl.mpak.sky.gui.swing.Action();
    menuProfiler = new javax.swing.JMenu();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenuItem3 = new javax.swing.JMenuItem();
    jMenuItem4 = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    jMenuItem5 = new javax.swing.JMenuItem();
    jMenuItem7 = new javax.swing.JMenuItem();
    jSeparator4 = new javax.swing.JSeparator();
    jMenuItem6 = new javax.swing.JMenuItem();
    cmMilisecondUnit = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    jPanel2 = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    jToolBar2 = new javax.swing.JToolBar();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton4 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton5 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jToolBar1 = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonAllRuns = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonMilisecondUnit = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableRuns = new ViewTable();
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

    cmFind.setActionCommandKey("cmFind");
    cmFind.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmFind.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_source.gif")); // NOI18N
    cmFind.setText(stringManager.getString("cmFind-text")); // NOI18N
    cmFind.setTooltip(stringManager.getString("cmFind-hint")); // NOI18N
    cmFind.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFindActionPerformed(evt);
      }
    });

    cmAllRuns.setActionCommandKey("cmAllRuns");
    cmAllRuns.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/group.gif")); // NOI18N
    cmAllRuns.setText(stringManager.getString("cmAllRuns-text")); // NOI18N
    cmAllRuns.setTooltip(stringManager.getString("cmAllRuns-hint")); // NOI18N
    cmAllRuns.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAllRunsActionPerformed(evt);
      }
    });

    cmDeleteRun.setActionCommandKey("cmDeleteRun");
    cmDeleteRun.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.CTRL_MASK));
    cmDeleteRun.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDeleteRun.setText(stringManager.getString("cmDeleteRun-text")); // NOI18N
    cmDeleteRun.setTooltip(stringManager.getString("cmDeleteRun-hint")); // NOI18N
    cmDeleteRun.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteRunActionPerformed(evt);
      }
    });

    cmStartProfile.setActionCommandKey("cmStartProfile");
    cmStartProfile.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_MASK));
    cmStartProfile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute16.gif")); // NOI18N
    cmStartProfile.setText(stringManager.getString("cmStartProfile-text")); // NOI18N
    cmStartProfile.setTooltip(stringManager.getString("cmStartProfile-hint")); // NOI18N
    cmStartProfile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmStartProfileActionPerformed(evt);
      }
    });

    cmStopProfile.setActionCommandKey("cmStopProfile");
    cmStopProfile.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
    cmStopProfile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/stop16.gif")); // NOI18N
    cmStopProfile.setText(stringManager.getString("cmStopProfile-text")); // NOI18N
    cmStopProfile.setTooltip(stringManager.getString("cmStopProfile-hint")); // NOI18N
    cmStopProfile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmStopProfileActionPerformed(evt);
      }
    });

    cmPauseProfile.setActionCommandKey("cmPauseProfile");
    cmPauseProfile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/debug_pause.gif")); // NOI18N
    cmPauseProfile.setText(stringManager.getString("cmPauseProfile-text")); // NOI18N
    cmPauseProfile.setTooltip(stringManager.getString("cmPauseProfile-hint")); // NOI18N
    cmPauseProfile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmPauseProfileActionPerformed(evt);
      }
    });

    cmResumeProfile.setActionCommandKey("cmResumeProfile");
    cmResumeProfile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/debug_resume.gif")); // NOI18N
    cmResumeProfile.setText(stringManager.getString("cmResumeProfile-text")); // NOI18N
    cmResumeProfile.setTooltip(stringManager.getString("cmResumeProfile-hint")); // NOI18N
    cmResumeProfile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmResumeProfileActionPerformed(evt);
      }
    });

    menuProfiler.setText(stringManager.getString("menu-profiler")); // NOI18N

    jMenuItem1.setAction(cmStartProfile);
    menuProfiler.add(jMenuItem1);

    jMenuItem2.setAction(cmStopProfile);
    menuProfiler.add(jMenuItem2);

    jMenuItem3.setAction(cmPauseProfile);
    menuProfiler.add(jMenuItem3);

    jMenuItem4.setAction(cmResumeProfile);
    menuProfiler.add(jMenuItem4);
    menuProfiler.add(jSeparator3);

    jMenuItem5.setAction(cmAllRuns);
    menuProfiler.add(jMenuItem5);

    jMenuItem7.setAction(cmMilisecondUnit);
    menuProfiler.add(jMenuItem7);
    menuProfiler.add(jSeparator4);

    jMenuItem6.setAction(cmDeleteRun);
    menuProfiler.add(jMenuItem6);

    cmMilisecondUnit.setActionCommandKey("cmMilisecondUnit");
    cmMilisecondUnit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/ms.gif")); // NOI18N
    cmMilisecondUnit.setText(stringManager.getString("cmMilisecondUnit-text")); // NOI18N
    cmMilisecondUnit.setTooltip(stringManager.getString("cmMilisecondUnit-hint")); // NOI18N
    cmMilisecondUnit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMilisecondUnitActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    split.setBorder(null);
    split.setDividerLocation(200);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel2.setLayout(new java.awt.BorderLayout());

    pl.mpak.sky.gui.swing.VerticalFlowLayout verticalFlowLayout1 = new pl.mpak.sky.gui.swing.VerticalFlowLayout();
    verticalFlowLayout1.setVgap(0);
    verticalFlowLayout1.setHgap(0);
    jPanel1.setLayout(verticalFlowLayout1);

    jToolBar2.setFloatable(false);
    jToolBar2.setRollover(true);

    toolButton2.setAction(cmStartProfile);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar2.add(toolButton2);

    toolButton3.setAction(cmStopProfile);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar2.add(toolButton3);

    toolButton4.setAction(cmPauseProfile);
    toolButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar2.add(toolButton4);

    toolButton5.setAction(cmResumeProfile);
    toolButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar2.add(toolButton5);

    jPanel1.add(jToolBar2);

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

    buttonAllRuns.setAction(cmAllRuns);
    buttonAllRuns.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonAllRuns.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonAllRuns);

    buttonMilisecondUnit.setAction(cmMilisecondUnit);
    buttonMilisecondUnit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonMilisecondUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonMilisecondUnit);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonActions);

    toolButton1.setAction(cmDeleteRun);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(toolButton1);

    jPanel1.add(jToolBar1);

    jPanel2.add(jPanel1, java.awt.BorderLayout.PAGE_START);

    jScrollPane1.setViewportView(tableRuns);

    jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    queryTableStatusBar1.setTable(tableRuns);
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

  private void cmAllRunsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAllRunsActionPerformed
    buttonAllRuns.setSelected(!buttonAllRuns.isSelected());
    refreshTableList(null);
  }//GEN-LAST:event_cmAllRunsActionPerformed

  private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    if (isVisible() && !viewClosing && !tableRuns.getQuery().isActive()) {
      refreshTableList(null);
    }
  }//GEN-LAST:event_formComponentShown

  private void cmDeleteRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteRunActionPerformed
    if (tableRuns.getSelectedRow() >= 0) {
      try {
        tableRuns.getQuery().getRecord(tableRuns.getSelectedRow());
        long runid = tableRuns.getQuery().fieldByName("runid").getLong();
        if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("RunsPanel-delete-profile-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
          Command command = getDatabase().createCommand();
          command.setSqlText(Sql.getDeleteRunProfiler());
          command.paramByName("runid").setLong(runid);
          command.execute();
          refreshTableList(null);
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
  }//GEN-LAST:event_cmDeleteRunActionPerformed

  private void cmStartProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmStartProfileActionPerformed
    try {
      Command command = getDatabase().createCommand();
      command.setSqlText(Sql.getStartProfiler());
      command.paramByName("runid").setParamMode(ParameterMetaData.parameterModeOut, Types.BIGINT);
      command.execute();
      profileState = ProfileState.Started;
      runidProfile = command.paramByName("runid").getLong();
      refreshTableList(String.valueOf(runidProfile));
      refreshUnits();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    enableControls();
  }//GEN-LAST:event_cmStartProfileActionPerformed

  private void cmStopProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmStopProfileActionPerformed
    try {
      Command command = getDatabase().createCommand();
      command.setSqlText(Sql.getStopProfiler());
      command.paramByName("runid").setLong(runidProfile);
      command.execute();
      command.setSqlText(Sql.getCorrectTriggerLinesProfiler());
      command.paramByName("runid").setLong(runidProfile);
      command.execute();
      profileState = ProfileState.Stopped;
      refreshTableList(String.valueOf(runidProfile));
      refreshUnits();
      runidProfile = -1;
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    enableControls();
  }//GEN-LAST:event_cmStopProfileActionPerformed

  private void cmPauseProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPauseProfileActionPerformed
    try {
      Command command = getDatabase().createCommand();
      command.setSqlText(Sql.getPauseProfiler());
      command.paramByName("runid").setLong(runidProfile);
      command.execute();
      profileState = ProfileState.Paused;
      refreshTableList(String.valueOf(runidProfile));
      refreshUnits();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    enableControls();
  }//GEN-LAST:event_cmPauseProfileActionPerformed

  private void cmResumeProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmResumeProfileActionPerformed
    try {
      Command command = getDatabase().createCommand();
      command.setSqlText(Sql.getResumeProfiler());
      command.execute();
      profileState = ProfileState.Started;
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    enableControls();
  }//GEN-LAST:event_cmResumeProfileActionPerformed

  private void cmMilisecondUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMilisecondUnitActionPerformed
    buttonMilisecondUnit.setSelected(!buttonMilisecondUnit.isSelected());
    refreshTableList(null);
    refreshUnits();
  }//GEN-LAST:event_cmMilisecondUnitActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonAllRuns;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonMilisecondUnit;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmAllRuns;
  private pl.mpak.sky.gui.swing.Action cmDeleteRun;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFind;
  private pl.mpak.sky.gui.swing.Action cmMilisecondUnit;
  private pl.mpak.sky.gui.swing.Action cmPauseProfile;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmResumeProfile;
  private pl.mpak.sky.gui.swing.Action cmStartProfile;
  private pl.mpak.sky.gui.swing.Action cmStopProfile;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JMenuItem jMenuItem4;
  private javax.swing.JMenuItem jMenuItem5;
  private javax.swing.JMenuItem jMenuItem6;
  private javax.swing.JMenuItem jMenuItem7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JToolBar jToolBar2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenu menuProfiler;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar queryTableStatusBar1;
  private javax.swing.JSplitPane split;
  private ViewTable tableRuns;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton4;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton5;
  // End of variables declaration//GEN-END:variables
}

/*
 * TableReportPanel.java
 *
 * Created on 27 paŸdziernik 2008, 20:44
 */

package pl.mpak.orbada.reports.gui;

import java.awt.Component;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.DataTable;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.reports.Sql;
import pl.mpak.orbada.reports.db.ReportRecord;
import pl.mpak.orbada.universal.gui.CommandParametersDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Parameter;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableColumnModel;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.Titleable;
import pl.mpak.util.task.Task;

/**
 *
 * @author  akaluza
 */
public class TableReportPanel extends javax.swing.JPanel implements Titleable, Closeable {

  private IViewAccesibilities accessibilities;
  private ReportRecord report;
  private boolean viewClosing = false;
  private boolean refreshing = false;
  
  /** Creates new form TableReportPanel */
  public TableReportPanel(IViewAccesibilities accessibilities, ReportRecord report) {
    this.accessibilities = accessibilities;
    this.report = report;
    initComponents();
    init();
  }
  
  private void init() {
    tableReport.getQuery().setDatabase(getDatabase());
    statusBar.setTable(tableReport);
    
    prepareDetails();
    if (report.getOrepId() == null) {
      runReportTask(null);
    }
    else {
      buttonRun.setVisible(false);
      cmRun.setEnabled(false);
    }
  }

  public String getTitle() {
    return report.getName();
  }

  public Database getDatabase() {
    return accessibilities.getDatabase();
  }
  
  private void prepareDetails() {
    Query query = report.getDatabase().createQuery();
    try {
      query.setSqlText(Sql.getDetailReportList(null));
      query.paramByName("OREP_ID").setString(report.getId());
      query.open();
      if (!query.eof()) {
        split.setDividerLocation(0.6f);
        while (!query.eof()) {
          ReportRecord r = new ReportRecord(report.getDatabase());
          r.updateFrom(query);
          tabbedDetails.addTab(r.getName(), new TableReportPanel(accessibilities, r));
          query.next();
        }
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    if (tabbedDetails.getTabCount() > 0) {
      tableReport.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        int lastSelected = -1;
        public void valueChanged(ListSelectionEvent e) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              if (tableReport.getSelectedRow() >= 0 && lastSelected != tableReport.getSelectedRow()) {
                lastSelected = tableReport.getSelectedRow();
                try {
                  tableReport.getQuery().getRecord(tableReport.getSelectedRow());
                  for (int i = 0; i < tabbedDetails.getTabCount(); i++) {
                    Component c = tabbedDetails.getComponentAt(i);
                    if (c instanceof TableReportPanel) {
                      ((TableReportPanel) c).refreshTableList(tableReport.getQuery());
                    }
                  }
                } catch (Exception ex) {
                  ExceptionUtil.processException(ex);
                }
              }
            }
          });
        }
      });
    }
    else {
      split.setBottomComponent(null);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          split.setDividerLocation(1f);
        }
      });
    }
  }
  
  public void refreshTableList(Query query) {
    runReport(query);
  }
  
  private void runReportTask(final Query query) {
    getDatabase().getTaskPool().addTask(new Task() {
      public void run() {
        runReport(query);
      }
    });
  }
  
  private void refreshTableListTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refreshTableList();
      }
    });
  }
  
  private void refreshTableList() {
    if (!SwingUtil.isVisible(this) || viewClosing || !tableReport.getQuery().isActive() || refreshing) {
      return;
    }
    setRefreshing(true);
    try {
      int rowIndex = tableReport.getSelectedRow();
      int columnIndex = tableReport.getSelectedColumn();
      tableReport.getQuery().refresh();
      if (!tableReport.getQuery().eof()) {
        tableReport.changeSelection(rowIndex, columnIndex);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      setRefreshing(false);
    }
  }
  
  private void setRefreshing(final boolean value) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        cmRefresh.setEnabled(!value);
        cmRun.setEnabled(!value);
        cmCancelReport.setEnabled(value);
      }
    });
  }
  
  private void runReport(Query query) {
    setRefreshing(true);
    try {
      tableReport.getQuery().close();
      ((QueryTableColumnModel)tableReport.getColumnModel()).setAutoColumns(true);
      tableReport.setAutoFitWidth(true);
      tableReport.getQuery().setSqlText(report.getSql());
      if (tableReport.getQuery().getParameterCount() > 0) {
        if (query == null) {
          if (CommandParametersDialog.showDialog(tableReport.getQuery())) {
            tableReport.getQuery().open();
          }
        }
        else {
          for (int i=0; i<tableReport.getQuery().getParameterCount(); i++) {
            Parameter p = tableReport.getQuery().getParameter(i);
            if (p.getParamName().startsWith("&")) {
              p.setValue(query.fieldByName(p.getParamName().substring(1)).getValue());
            }
            else {
              p.setValue(query.fieldByName(p.getParamName()).getValue());
            }
          }
          tableReport.getQuery().open();
        }
      }
      else {
        tableReport.getQuery().open();
      }
      tableReport.setAutoFitWidth(false);
      ((QueryTableColumnModel)tableReport.getColumnModel()).setAutoColumns(false);
      if (tableReport.getQuery().isActive() && !tableReport.getQuery().eof()) {
        tableReport.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    finally {
      setRefreshing(false);
    }
  }
  
  public void close() throws IOException {
    viewClosing = true;
    while (tabbedDetails.getTabCount() > 0) {
      Component c = tabbedDetails.getComponentAt(0);
      if (c instanceof Closeable) {
        try {
          ((Closeable) c).close();
        } catch (IOException ex) {
          ExceptionUtil.processException(ex);
        }
      }
      tabbedDetails.remove(c);
    }
    tableReport.getQuery().close();
    accessibilities = null;
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
        cmRun = new pl.mpak.sky.gui.swing.Action();
        cmCancelReport = new pl.mpak.sky.gui.swing.Action();
        jPanel2 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonRun = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonCancelReport = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        refreshPanel = new pl.mpak.sky.gui.swing.comp.RefreshPanel();
        split = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReport = new DataTable();
        tabbedDetails = new javax.swing.JTabbedPane();

        cmRefresh.setActionCommandKey("cmRefresh");
        cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports"); // NOI18N
        cmRefresh.setText(bundle.getString("refresh")); // NOI18N
        cmRefresh.setTooltip(bundle.getString("refresh_tooltip")); // NOI18N
        cmRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmRefreshActionPerformed(evt);
            }
        });

        cmRun.setActionCommandKey("cmRun");
        cmRun.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/db_sql_execute16.gif")); // NOI18N
        cmRun.setText(bundle.getString("run_report")); // NOI18N
        cmRun.setTooltip(bundle.getString("run_report_tooltip")); // NOI18N
        cmRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmRunActionPerformed(evt);
            }
        });

        cmCancelReport.setActionCommandKey("cmCancelReport");
        cmCancelReport.setEnabled(false);
        cmCancelReport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/cancel.gif")); // NOI18N
        cmCancelReport.setText("Anulowanie wykonania");
        cmCancelReport.setTooltip("Pozwala anulowaæ wykonywanie raportu");
        cmCancelReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCancelReportActionPerformed(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        buttonRefresh.setAction(cmRefresh);
        buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonRefresh);

        buttonRun.setAction(cmRun);
        buttonRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonRun);

        buttonCancelReport.setAction(cmCancelReport);
        buttonCancelReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCancelReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonCancelReport);
        toolBar.add(jSeparator1);

        refreshPanel.addRefreshListener(new pl.mpak.sky.gui.swing.comp.RefreshListener() {
            public void refresh(pl.mpak.sky.gui.swing.comp.RefreshEvent evt) {
                refreshPanelRefresh(evt);
            }
        });
        toolBar.add(refreshPanel);

        jPanel2.add(toolBar);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        split.setBorder(null);
        split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(2147483647, 2147483647));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setViewportView(tableReport);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        split.setLeftComponent(jPanel1);

        tabbedDetails.setFocusable(false);
        tabbedDetails.setPreferredSize(new java.awt.Dimension(0, 300));
        split.setRightComponent(tabbedDetails);

        add(split, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableListTask();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRunActionPerformed
  runReportTask(null);
}//GEN-LAST:event_cmRunActionPerformed

  private void refreshPanelRefresh(pl.mpak.sky.gui.swing.comp.RefreshEvent evt) {//GEN-FIRST:event_refreshPanelRefresh
    refreshTableList();
  }//GEN-LAST:event_refreshPanelRefresh

private void cmCancelReportActionPerformed(java.awt.event.ActionEvent evt) {                                               
  try {
    tableReport.getQuery().cancel();
  } catch (SQLException ex) {
    ExceptionUtil.processException(ex);
  }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonCancelReport;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRun;
    private pl.mpak.sky.gui.swing.Action cmCancelReport;
    private pl.mpak.sky.gui.swing.Action cmRefresh;
    private pl.mpak.sky.gui.swing.Action cmRun;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private pl.mpak.sky.gui.swing.comp.RefreshPanel refreshPanel;
    private javax.swing.JSplitPane split;
    private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
    private javax.swing.JTabbedPane tabbedDetails;
    private DataTable tableReport;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

}

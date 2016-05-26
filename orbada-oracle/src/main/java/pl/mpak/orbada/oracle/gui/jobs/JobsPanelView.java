package pl.mpak.orbada.oracle.gui.jobs;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class JobsPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private Timer timer;
  private Query sysdate;
  
  public JobsPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    timer = new Timer(1000) {
      {
        setEnabled(false);
      }
      public void run() {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            refresh();
          }
        });
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    tableJobs.getQuery().setDatabase(getDatabase());
    try {
      sysdate = getDatabase().createQuery("select sysdate from dual", false);
      final QueryTableCellRenderer renderer = new QueryTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
          if (!isSelected) {
            try {
              if (!tableJobs.getQuery().fieldByName("this_date").isNull()) {
                label.setBackground(new Color(200, 255, 200));
              }
              else if ("Y".equals(tableJobs.getQuery().fieldByName("broken").getString())) {
                label.setBackground(new Color(255, 200, 200));
              }
            } catch (Exception ex) {
              label.setText(label.getText() +" [" +ex.getMessage() +"]");
            }
          }
          return label;
        }
      };
      tableJobs.addColumn(new QueryTableColumn("job", stringManager.getString("job-no"), 60, renderer));
      tableJobs.addColumn(new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 100, renderer));
      tableJobs.addColumn(new QueryTableColumn("last_date", stringManager.getString("last-date"), 120, renderer));
      tableJobs.addColumn(new QueryTableColumn("this_date", stringManager.getString("this-date"), 120, renderer));
      tableJobs.addColumn(new QueryTableColumn("next_date", stringManager.getString("next-date"), 120, renderer));
      tableJobs.addColumn(new QueryTableColumn("total_time", stringManager.getString("total-time"), 70, renderer));
      tableJobs.addColumn(new QueryTableColumn("broken", stringManager.getString("broken"), 50, new QueryTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          JLabel label = (JLabel)renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
          try {
            label.setHorizontalAlignment(JLabel.CENTER);
            if ("Y".equals(tableJobs.getQuery().fieldByName("broken").getString())) {
              label.setText(stringManager.getString("yes"));
            }
            else {
              label.setText("-");
            }
          } catch (Exception ex) {
            label.setText(label.getText() +" [" +ex.getMessage() +"]");
          }
          return label;
        }
      }));
      tableJobs.addColumn(new QueryTableColumn("failures", stringManager.getString("failures"), 50, renderer));
      tableJobs.addColumn(new QueryTableColumn("what", stringManager.getString("job-what"), 350, renderer));
      tableJobs.addColumn(new QueryTableColumn("interval", stringManager.getString("interval"), 350, renderer));
      tableJobs.addColumn(new QueryTableColumn("nls_env", stringManager.getString("nls-env"), 350, renderer));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("broken = 'Y'", stringManager.getString("are-broken")));
      def.add(new SqlFilterDefComponent("schema_name", stringManager.getString("schema-name"), (String[])null));
      def.add(new SqlFilterDefComponent("what", stringManager.getString("job-what"), (String[])null));
      def.add(new SqlFilterDefComponent("this_date is not null", stringManager.getString("are-this-date")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-jobs-view-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableJobs, buttonActions, menuActions, "oracle-jobs-actions");
  }
  
  private void refreshListTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    if (!SwingUtil.isVisible(this) || viewClosing) {
      return;
    }
    try {
      String objectName = null;
      if (tableJobs.getQuery().isActive() && tableJobs.getSelectedRow() >= 0) {
        tableJobs.getQuery().getRecord(tableJobs.getSelectedRow());
        objectName = tableJobs.getQuery().fieldByName("job").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        labelRefresh.setEnabled(true);
      }
    });
    try {
      sysdate.refresh();
      labelSysdate.setText(" " +sysdate.fieldByName("sysdate").getString());
      
      int column = tableJobs.getSelectedColumn();
      int index = Math.max(0, tableJobs.getSelectedRow());
      tableJobs.getQuery().close();
      tableJobs.getQuery().setSqlText(Sql.getJobList(filter.getSqlText(), "true".equalsIgnoreCase(accesibilities.getDatabase().getUserProperties().getProperty("dba-role"))));
      tableJobs.getQuery().open();
      if (tableJobs.getQuery().getOpeningTime() > getRefreshTime() *1000000) {
        comboRefresh.setSelectedIndex(comboRefresh.getSelectedIndex() +1);
      }
      if (objectName != null && tableJobs.getQuery().locate("job", new Variant(objectName))) {
        tableJobs.changeSelection(tableJobs.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableJobs.getQuery().isEmpty()) {
        tableJobs.changeSelection(Math.min(index, tableJobs.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          labelRefresh.setEnabled(false);
        }
      });
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  private long getRefreshTime() {
    if (comboRefresh.getSelectedIndex() == 0) {
      return 1000 *1000;
    }
    return (long)(Double.parseDouble(comboRefresh.getSelectedItem().toString()) *1000);
  }
  
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    timer.cancel();
    viewClosing = true;
    sysdate.close();
    tableJobs.getQuery().close();
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
    menuActions = new javax.swing.JPopupMenu();
    cmSubmitJob = new pl.mpak.sky.gui.swing.Action();
    cmEditJob = new pl.mpak.sky.gui.swing.Action();
    cmRemoveJob = new pl.mpak.sky.gui.swing.Action();
    cmRunJob = new pl.mpak.sky.gui.swing.Action();
    cmBrokenJob = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableJobs = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
    buttonSubmitJob = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonEditJob = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonBroken = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonRunJob = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    buttonRemoveJob = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    jLabel1 = new javax.swing.JLabel();
    comboRefresh = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    labelRefresh = new javax.swing.JLabel();
    labelSysdate = new javax.swing.JLabel();

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

    cmSubmitJob.setActionCommandKey("cmSubmitJob");
    cmSubmitJob.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmSubmitJob.setText(stringManager.getString("cmSubmitJob-text")); // NOI18N
    cmSubmitJob.setTooltip(stringManager.getString("cmSubmitJob-hint")); // NOI18N
    cmSubmitJob.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSubmitJobActionPerformed(evt);
      }
    });

    cmEditJob.setActionCommandKey("cmEditJob");
    cmEditJob.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEditJob.setText(stringManager.getString("cmEditJob-text")); // NOI18N
    cmEditJob.setTooltip(stringManager.getString("cmEditJob-hint")); // NOI18N
    cmEditJob.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditJobActionPerformed(evt);
      }
    });

    cmRemoveJob.setActionCommandKey("cmRemoveJob");
    cmRemoveJob.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmRemoveJob.setText(stringManager.getString("cmRemoveJob-text")); // NOI18N
    cmRemoveJob.setTooltip(stringManager.getString("cmRemoveJob-hint")); // NOI18N
    cmRemoveJob.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRemoveJobActionPerformed(evt);
      }
    });

    cmRunJob.setActionCommandKey("cmRunJob");
    cmRunJob.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute16.gif")); // NOI18N
    cmRunJob.setText(stringManager.getString("cmRunJob-text")); // NOI18N
    cmRunJob.setTooltip(stringManager.getString("cmRunJob-hint")); // NOI18N
    cmRunJob.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRunJobActionPerformed(evt);
      }
    });

    cmBrokenJob.setActionCommandKey("cmBrokenJob");
    cmBrokenJob.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/green_bdot.gif")); // NOI18N
    cmBrokenJob.setText(stringManager.getString("cmBrokenJob-text")); // NOI18N
    cmBrokenJob.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmBrokenJobActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableJobs);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableJobs);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonFilter);
    toolBar.add(jSeparator3);

    buttonSubmitJob.setAction(cmSubmitJob);
    buttonSubmitJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSubmitJob.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonSubmitJob);

    buttonEditJob.setAction(cmEditJob);
    buttonEditJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonEditJob.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonEditJob);

    buttonBroken.setAction(cmBrokenJob);
    buttonBroken.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonBroken.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonBroken);

    buttonRunJob.setAction(cmRunJob);
    buttonRunJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRunJob.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRunJob);
    toolBar.add(jSeparator4);

    buttonRemoveJob.setAction(cmRemoveJob);
    buttonRemoveJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRemoveJob.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRemoveJob);
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);
    toolBar.add(jSeparator2);

    jLabel1.setText(stringManager.getString("refresh-dd")); // NOI18N
    toolBar.add(jLabel1);

    comboRefresh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "off", "0.5", "1", "5", "10", "30", "60", "120" }));
    comboRefresh.setPreferredSize(new java.awt.Dimension(60, 22));
    comboRefresh.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboRefreshItemStateChanged(evt);
      }
    });
    toolBar.add(comboRefresh);

    jLabel2.setText(" s ");
    toolBar.add(jLabel2);

    labelRefresh.setIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/orange_bdot.gif")); // NOI18N
    toolBar.add(labelRefresh);

    labelSysdate.setText("SYSDATE");
    labelSysdate.setToolTipText("<html><font color=gray>SELECT</font>&nbsp;<b>SYSDATE</b>&nbsp;<font color=gray>FROM DUAL</font>");
    toolBar.add(labelSysdate);

    jPanel2.add(toolBar);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableJobs.getQuery().isActive()) {
    refreshListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void comboRefreshItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboRefreshItemStateChanged
  if (comboRefresh.getSelectedItem() != null) {
    if (comboRefresh.getSelectedIndex() > 0) {
      timer.setInterval((int)getRefreshTime());
      timer.setEnabled(true);
    }
    else {
      timer.setEnabled(false);
    }
  }
}//GEN-LAST:event_comboRefreshItemStateChanged

private void cmSubmitJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSubmitJobActionPerformed
  SqlCodeWizardDialog.show(new JobEditWizard(getDatabase(), null), true); 
}//GEN-LAST:event_cmSubmitJobActionPerformed

private void cmEditJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditJobActionPerformed
  if (tableJobs.getSelectedRow() >= 0) {
    try {
      tableJobs.getQuery().getRecord(tableJobs.getSelectedRow());
      if (SqlCodeWizardDialog.show(new JobEditWizard(getDatabase(), tableJobs.getQuery().fieldByName("JOB").getLong()), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmEditJobActionPerformed

private void cmRemoveJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRemoveJobActionPerformed
  if (tableJobs.getSelectedRow() >= 0) {
    try {
      tableJobs.getQuery().getRecord(tableJobs.getSelectedRow());
      if (SqlCodeWizardDialog.show(new JobRemoveWizard(getDatabase(), tableJobs.getQuery().fieldByName("JOB").getLong()), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmRemoveJobActionPerformed

private void cmRunJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRunJobActionPerformed
  if (tableJobs.getSelectedRow() >= 0) {
    try {
      tableJobs.getQuery().getRecord(tableJobs.getSelectedRow());
      if (SqlCodeWizardDialog.show(new JobRunWizard(getDatabase(), tableJobs.getQuery().fieldByName("JOB").getLong()), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmRunJobActionPerformed

private void cmBrokenJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmBrokenJobActionPerformed
  if (tableJobs.getSelectedRow() >= 0) {
    try {
      tableJobs.getQuery().getRecord(tableJobs.getSelectedRow());
      Command command = getDatabase().createCommand();
      if ("true".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role"))) {
        command.setSqlText(
          "BEGIN\n" +
          "  DBMS_IJOB.BROKEN(:JOB, &STATE);\n" +
          "  COMMIT;\n" +
          "END;");
      }
      else {
        command.setSqlText(
          "BEGIN\n" +
          "  DBMS_JOB.BROKEN(:JOB, &STATE);\n" +
          "  COMMIT;\n" +
          "END;");
      }
      command.paramByName("JOB").setLong(tableJobs.getQuery().fieldByName("JOB").getLong());
      command.paramByName("&STATE").setString("Y".equalsIgnoreCase(tableJobs.getQuery().fieldByName("BROKEN").getString()) ? "FALSE" : "TRUE");
      command.execute();
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmBrokenJobActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonBroken;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonEditJob;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRemoveJob;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRunJob;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSubmitJob;
  private pl.mpak.sky.gui.swing.Action cmBrokenJob;
  private pl.mpak.sky.gui.swing.Action cmEditJob;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRemoveJob;
  private pl.mpak.sky.gui.swing.Action cmRunJob;
  private pl.mpak.sky.gui.swing.Action cmSubmitJob;
  private javax.swing.JComboBox comboRefresh;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JLabel labelRefresh;
  private javax.swing.JLabel labelSysdate;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableJobs;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

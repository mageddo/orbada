package pl.mpak.orbada.reports.gui;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.reports.Sql;
import pl.mpak.orbada.reports.db.ReportRecord;
import pl.mpak.orbada.reports.util.Applyable;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.TableColumn;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.RecordLink;
import pl.mpak.usedb.gui.linkreq.FieldRequeiredNotNull;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ReportEditDialog extends javax.swing.JDialog {

  private Database database;
  private Database forDatabase;
  private String orep_id;
  private String orepg_id;
  private boolean shared;
  private boolean rootReport;
  private String orep_owner_id;
  private int modalResult = ModalResult.NONE;
  private ReportRecord report;
  private RecordLink dataLink;
  private ArrayList<ReportRecord> detailList;
  private ArrayList<ReportRecord> deletedList;
  private ISettings settings;
  
  /** Creates new form ProjectEditDialog */
  public ReportEditDialog(Database forDatabase, Database database, String orep_id, String orepg_id, boolean shared) throws IntrospectionException, UseDBException {
    super(SwingUtil.getRootFrame());
    this.database = database;
    this.forDatabase = forDatabase;
    this.orep_id = orep_id;
    this.orepg_id = orepg_id;
    this.shared = shared;
    this.rootReport = true;
    initComponents();
    init();
  }

  public ReportEditDialog(Database forDatabase, Database database, String orep_id, ReportRecord report) throws IntrospectionException, UseDBException {
    super();
    this.database = database;
    this.forDatabase = forDatabase;
    this.orep_owner_id = orep_id;
    this.report = report;
    initComponents();
    init();
  }

  public static String showDialog(Database forDatabase, Database database, String orep_id, String orepg_id, boolean shared) throws IntrospectionException, UseDBException {
    ReportEditDialog dialog = new ReportEditDialog(forDatabase, database, orep_id, orepg_id, shared);
    dialog.setVisible(true);
    return dialog.getModalResult() == ModalResult.OK ? dialog.orep_id : null;
  }
  
  public static ReportRecord showDialog(Database forDatabase, Database database, String orep_id, ReportRecord report) throws IntrospectionException, UseDBException {
    ReportEditDialog dialog = new ReportEditDialog(forDatabase, database, orep_id, report);
    dialog.setVisible(true);
    return dialog.getModalResult() == ModalResult.OK ? dialog.report : null;
  }
  
  public int getModalResult() {
    return modalResult;
  }

  private void init() throws IntrospectionException, UseDBException {
    comboType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("tabelarical") }));

    if (!rootReport) {
      tabbedReport.remove(panelDetailReports);
    }
    else {
      detailList = new ArrayList<ReportRecord>();
      deletedList = new ArrayList<ReportRecord>();
    }
    textSql.setDatabase(forDatabase);
    dataLink = new RecordLink();
    dataLink.add("OREP_ID", textId);
    dataLink.add("OREP_NAME", textName, new FieldRequeiredNotNull(java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("report_name")));
    dataLink.add("OREP_TOOLTIP", textTooltip);
    dataLink.add("OREP_DESCRIPTION", textDescription);
    dataLink.add("OREP_SQL", textSql);
    
    if (report == null) {
      if (orep_id != null) {
        report = new ReportRecord(database, orep_id);
        if (rootReport) {
          loadDetails();
        }
      } else {
        report = new ReportRecord(database);
        report.setOrepgId(orepg_id);
        report.setOrepId(orep_owner_id);
      }
    }
    dataLink.updateComponents(report);
    
    if ("T".equals(report.getType())) {
      comboType.setSelectedIndex(0);
    }

    if (shared) {
      tabbedReport.addTab(java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("users"), new ReportUserListPanel(database, report.getId()));
    }
    
    if (rootReport) {
      cmEditDetail.setEnabled(false);
      cmDeleteDetail.setEnabled(false);
      tableDetails.setModel(getDetailsTableModel());
      tableDetails.addColumn(new TableColumn(0, 300, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("name")));
      tableDetails.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tableDetails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          cmEditDetail.setEnabled(tableDetails.getSelectedRow() >= 0);
          cmDeleteDetail.setEnabled(tableDetails.getSelectedRow() >= 0);
        }
      });
      if (tableDetails.getRowCount() > 0) {
        tableDetails.changeSelection(0, 0, false, false);
      }
    }
    
    settings = Application.get().getSettings("orbada-reports-edit-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", (long)getWidth()).intValue(), settings.getValue("height", (long)getHeight()).intValue());
    } catch (Exception ex) {
    }

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    
    SwingUtil.centerWithinScreen(this);
    if (!rootReport) {
      setLocation(getX() +30, getY() +30);
    }
  }
  
  private void loadDetails() {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getDetailReportList(null));
      query.paramByName("OREP_ID").setString(report.getId());
      query.open();
      while (!query.eof()) {
        ReportRecord r = new ReportRecord(database);
        r.updateFrom(query);
        r.setExists(true);
        detailList.add(r);
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
  private TableModel getDetailsTableModel() {
    AbstractTableModel model = new AbstractTableModel() {
      public int getRowCount() {
        return detailList.size();
      }
      public int getColumnCount() {
        return 0;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
          case 0: return detailList.get(rowIndex).getName();
          default:
            return null;
        }
      }
    };
    return model;
  }
  
  private void updateProject() throws Exception {
    dataLink.updateRecord(report);
    if (comboType.getSelectedIndex() <= 0) {
      report.setType("T");
    }
    if (report.isChanged()) {
      if (orep_id == null) {
        if (rootReport) {
          report.applyInsert();
        }
        orep_id = report.getId();
      } else if (rootReport) {
        report.applyUpdate();
      }
    }
    if (rootReport && shared) {
      for (int i=0; i<tabbedReport.getComponentCount(); i++) {
        Component c = tabbedReport.getComponentAt(i);
        if (c instanceof Applyable) {
          ((Applyable)c).apply();
        }
      }
    }
    if (rootReport) {
      for (ReportRecord r : deletedList) {
        r.applyDelete();
      }
      for (ReportRecord r : detailList) {
        if (r.isChanged()) {
          if (r.isExists()) {
            r.applyUpdate();
          }
          else {
            r.applyInsert();
          }
        }
      }
    }
  }
  
  @Override
  public void dispose() {
    settings.setValue("width", (long)getWidth());
    settings.setValue("height", (long)getHeight());
    settings.store();
    super.dispose();
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    cmNewDetail = new pl.mpak.sky.gui.swing.Action();
    cmEditDetail = new pl.mpak.sky.gui.swing.Action();
    cmDeleteDetail = new pl.mpak.sky.gui.swing.Action();
    tabbedReport = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textId = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel2 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    comboType = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel7 = new javax.swing.JLabel();
    textTooltip = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textDescription = new pl.mpak.sky.gui.swing.comp.TextArea();
    jPanel3 = new javax.swing.JPanel();
    textSql = new pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea();
    panelDetailReports = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableDetails = new pl.mpak.orbada.gui.comps.table.Table();
    buttonNewDetail = new javax.swing.JButton();
    buttonEditDetail = new javax.swing.JButton();
    buttonDeleteDetail = new javax.swing.JButton();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();

    cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports"); // NOI18N
    cmOk.setText(bundle.getString("ok")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(bundle.getString("cancel")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmNewDetail.setActionCommandKey("cmNewDetail");
    cmNewDetail.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_report.gif")); // NOI18N
    cmNewDetail.setText(bundle.getString("add")); // NOI18N
    cmNewDetail.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewDetailActionPerformed(evt);
      }
    });

    cmEditDetail.setActionCommandKey("cmEditDetail");
    cmEditDetail.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEditDetail.setText(bundle.getString("edit")); // NOI18N
    cmEditDetail.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditDetailActionPerformed(evt);
      }
    });

    cmDeleteDetail.setActionCommandKey("cmDeleteDetail");
    cmDeleteDetail.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDeleteDetail.setText(bundle.getString("delete")); // NOI18N
    cmDeleteDetail.setTooltip(bundle.getString("deleting_detail_report")); // NOI18N
    cmDeleteDetail.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteDetailActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(bundle.getString("report")); // NOI18N
    setModal(true);

    tabbedReport.setFocusable(false);

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(bundle.getString("unique_id")); // NOI18N

    textId.setEditable(false);
    textId.setFocusable(false);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(bundle.getString("report_name_collon")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(bundle.getString("type")); // NOI18N

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(bundle.getString("tooltip_collon")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(bundle.getString("details_colon")); // NOI18N

    textDescription.setColumns(20);
    textDescription.setRows(5);
    textDescription.setFont(new java.awt.Font("Monospaced", 0, 14));
    jScrollPane1.setViewportView(textDescription);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textId, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
              .addComponent(textTooltip, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(textTooltip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel5)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
        .addContainerGap())
    );

    tabbedReport.addTab(bundle.getString("report"), jPanel1); // NOI18N

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(textSql, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(textSql, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbedReport.addTab(bundle.getString("sql_command"), jPanel3); // NOI18N

    tableDetails.setAutoCreateRowSorter(true);
    tableDetails.setRowHeight(18);
    jScrollPane2.setViewportView(tableDetails);

    buttonNewDetail.setAction(cmNewDetail);
    buttonNewDetail.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNewDetail.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonEditDetail.setAction(cmEditDetail);
    buttonEditDetail.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEditDetail.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDeleteDetail.setAction(cmDeleteDetail);
    buttonDeleteDetail.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDeleteDetail.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout panelDetailReportsLayout = new javax.swing.GroupLayout(panelDetailReports);
    panelDetailReports.setLayout(panelDetailReportsLayout);
    panelDetailReportsLayout.setHorizontalGroup(
      panelDetailReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelDetailReportsLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(panelDetailReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
          .addGroup(panelDetailReportsLayout.createSequentialGroup()
            .addComponent(buttonNewDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEditDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDeleteDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    panelDetailReportsLayout.setVerticalGroup(
      panelDetailReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDetailReportsLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panelDetailReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNewDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEditDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDeleteDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    tabbedReport.addTab(bundle.getString("detail_reports"), panelDetailReports); // NOI18N

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(tabbedReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tabbedReport, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  try {
    updateProject();
    modalResult = ModalResult.OK;
    dispose();
  } catch (Exception ex) {
    MessageBox.show(this, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
  }
}//GEN-LAST:event_cmOkActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmNewDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewDetailActionPerformed
  try {
    ReportRecord r = ReportEditDialog.showDialog(forDatabase, database, report.getId(), null);
    if (r != null) {
      detailList.add(r);
      tableDetails.revalidate();
      tableDetails.changeSelection(tableDetails.getRowCount() -1, 0, false, false);
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewDetailActionPerformed

private void cmEditDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditDetailActionPerformed
  try {
    if (tableDetails.getSelectedRow() >= 0) {
      ReportRecord r = ReportEditDialog.showDialog(forDatabase, database, report.fieldByName("orep_id").getString(), detailList.get(tableDetails.getSelectedRow()));
      if (r != null) {
        tableDetails.revalidate();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditDetailActionPerformed

private void cmDeleteDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteDetailActionPerformed
  if (tableDetails.getSelectedRow() >= 0) {
    int index = tableDetails.getSelectedRow();
    ReportRecord r = detailList.get(tableDetails.getSelectedRow());
    if (MessageBox.show(this, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("deleting"), java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("delete_selected_report") +r.getName(), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
      if (r.isExists()) {
        deletedList.add(r);
      }
      detailList.remove(tableDetails.getSelectedRow());
      tableDetails.revalidate();
      if (tableDetails.getRowCount() >= 0) {
        if (tableDetails.getRowCount() == index) {
          tableDetails.changeSelection(index -1, 0, false, false);
        }
        else {
          tableDetails.changeSelection(index, 0, false, false);
        }
      }
    }
  }
}//GEN-LAST:event_cmDeleteDetailActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonDeleteDetail;
  private javax.swing.JButton buttonEditDetail;
  private javax.swing.JButton buttonNewDetail;
  private javax.swing.JButton buttonOk;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmDeleteDetail;
  private pl.mpak.sky.gui.swing.Action cmEditDetail;
  private pl.mpak.sky.gui.swing.Action cmNewDetail;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboType;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JPanel panelDetailReports;
  private javax.swing.JTabbedPane tabbedReport;
  private pl.mpak.orbada.gui.comps.table.Table tableDetails;
  private pl.mpak.sky.gui.swing.comp.TextArea textDescription;
  private pl.mpak.sky.gui.swing.comp.TextField textId;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  private pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea textSql;
  private pl.mpak.sky.gui.swing.comp.TextField textTooltip;
  // End of variables declaration//GEN-END:variables

}

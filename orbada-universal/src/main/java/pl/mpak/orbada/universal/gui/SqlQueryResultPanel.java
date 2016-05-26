/*
 * SqlQueryResultPanel.java
 *
 * Created on 19 paüdziernik 2007, 21:12
 */
package pl.mpak.orbada.universal.gui;

import java.awt.FlowLayout;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import orbada.gui.comps.table.DataTable;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.services.UniversalSettingsProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.br.QueryBufferedRecord;
import pl.mpak.usedb.core.CacheRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableColumnModel;
import pl.mpak.usedb.gui.swing.QueryTableModel;
import pl.mpak.usedb.gui.swing.QueryTableStatusBar;
import pl.mpak.usedb.util.JdbcUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.usedb.util.SqlTableName;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringTokenizer;

/**
 *
 * @author  akaluza
 */
public class SqlQueryResultPanel extends javax.swing.JPanel implements Closeable {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  private final static ArrayList<ArrayList<String>> storedColumnList = new ArrayList<ArrayList<String>>();

  private SqlQueryPanelView ownerPanel;
  private String catalogName;
  private String schemaName;
  private String tableName;
  private ArrayList<String> primaryKeyColumns;
  private int lastRowIndex = -1;
  private CacheRecord lastRecord = null;
  private QueryBufferedRecord updateRecord;
  private boolean insertRecord = false;
  private DatabaseInfoProvider infoService;
  private JPanel editPanel;
  private ISettings settings;

  /** Creates new form SqlQueryResultPanel
   * @param ownerPanel
   */
  public SqlQueryResultPanel(SqlQueryPanelView ownerPanel) {
    this.ownerPanel = ownerPanel;
    primaryKeyColumns = new ArrayList<String>();
    settings = ownerPanel.getApplication().getSettings(UniversalSettingsProvider.settingsName);
    initComponents();
    init();
  }

  private void init() {
    dataTable.setDefaultEditor(Boolean.class, new DefaultCellEditor(new JTextField()));
    dataTable.setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()));
    dataTable.setDefaultEditor(Number.class, new DefaultCellEditor(new JTextField()));
    dataTable.setDefaultEditor(Date.class, new DefaultCellEditor(new JTextField()));
    dataTable.setDefaultEditor(Time.class, new DefaultCellEditor(new JTextField()));
    dataTable.setDefaultEditor(Timestamp.class, new DefaultCellEditor(new JTextField()));
    getQuery().setDatabase(ownerPanel.getDatabase());
    getQuery().addQueryListener(new DefaultQueryListener() {
      @Override
      public void beforeOpen(EventObject e) {
        cmStopExecuting.setEnabled(true);
      }
      @Override
      public void afterOpen(EventObject e) {
        cmStopExecuting.setEnabled(false);
        cmEditData.setEnabled(true);
        cmRecordCount.setEnabled(true);
      }
      @Override
      public void beforeClose(EventObject e) {
        cmEditData.setEnabled(false);
        cmRecordCount.setEnabled(false);
      }
      @Override
      public void errorPerformed(EventObject e) {
        cmStopExecuting.setEnabled(false);
        cmRecordCount.setEnabled(false);
      }
    });
    dataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (getQuery().isActive() && dataTable.getSelectedRow() >= 0 && ((QueryTableModel) dataTable.getModel()).isEditable()) {
          cmApplyRecord.performe();
          try {
            lastRowIndex = dataTable.getSelectedRow();
            lastRecord = getQuery().getRecord(lastRowIndex);
            updateRecord.updateFrom(getQuery());
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    initEditPanel();

    SwingUtil.addAction(dataTable, cmInsertRecord);
    SwingUtil.addAction(dataTable, cmDeleteRecord);
    new ComponentActionsAction(getDatabase(), dataTable, buttonActions, menuActions, "orbada-universal-sql-result-content-actions");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        buttonActions.setHideActionText(true);
        buttonActions.setPreferredSize(null);
      }
    });
  }

  private void initEditPanel() {
    editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
    //editPanel.setBorder(statusBar.getPanel(QueryTableStatusBar.PANEL_REC_NO).getBorder());
    statusBar.addBefore(statusBar.getPanel(QueryTableStatusBar.PANEL_REC_NO), editPanel);
    editPanel.add(buttonInsertRecord);
    editPanel.add(buttonApplyRecord);
    editPanel.add(buttonRollbackRecord);
    editPanel.add(buttonDeleteRecord);
    editPanel.setVisible(false);
  }

  public Query getQuery() {
    return dataTable.getQuery();
  }

  public Database getDatabase() {
    return dataTable.getQuery().getDatabase();
  }

  private boolean showParameters(ParametrizedCommand command) {
    if (command.getParameterList().parameterCount() > 0) {
      if (!CommandParametersDialog.showDialog(command)) {
        return false;
      }
    }
    return true;
  }

  private int findColumnList() {
    final Object PRESENT = new Object();
    TableColumnModel model = dataTable.getColumnModel();
    HashMap<String, Object> columnSet = new HashMap<String, Object>();
    for (int c = 0; c<model.getColumnCount(); c++) {
      columnSet.put(model.getColumn(c).getHeaderValue().toString(), PRESENT);
    }
    for (int i = 0; i<storedColumnList.size(); i++) {
      ArrayList<String> columnList = storedColumnList.get(i);
      if (columnList.size() == model.getColumnCount()) {
        boolean found = true;
        for (String s : columnList) {
          if (!columnSet.containsKey(s)) {
            found = false;
            break;
          }
        }
        if (found) {
          return i;
        }
      }
    }
    return -1;
  }

  private void storeColumnPositions() {
    final int maxColumnListCount = settings.getValue(UniversalSettingsProvider.setMaxColumnListCount, (long)UniversalSettingsProvider.default_setMaxColumnListCount).intValue();
    SwingUtil.invokeAndWait(new Callable<Object>() {
      public Object call() throws Exception {
        synchronized (storedColumnList) {
          int index = findColumnList();
          if (index >= 0) {
            storedColumnList.remove(index);
          }
          ArrayList<String> columnList = new ArrayList<String>();
          TableColumnModel model = dataTable.getColumnModel();
          for (int c = 0; c<model.getColumnCount(); c++) {
            columnList.add(model.getColumn(c).getHeaderValue().toString());
          }
          storedColumnList.add(0, columnList);
          while (storedColumnList.size() > maxColumnListCount) {
            storedColumnList.remove(storedColumnList.size() -1);
          }
        }
        return null;
      }
    });
  }

  private void restoreColumnPositions() {
    SwingUtil.invokeAndWait(new Callable<Object>() {
      public Object call() throws Exception {
        synchronized (storedColumnList) {
          int index = findColumnList();
          if (index >= 0) {
            ArrayList<String> columnList = storedColumnList.get(index);
            QueryTableColumnModel model = (QueryTableColumnModel)dataTable.getColumnModel();
            HashMap<String, TableColumn> columnSet = new HashMap<String, TableColumn>();
            while (model.getColumnCount() > 0) {
              TableColumn tc = model.remove(0);
              columnSet.put(tc.getHeaderValue().toString(), tc);
            }
            for (int i=0; i<columnList.size(); i++) {
              model.add(columnSet.get(columnList.get(i)));
            }
            storedColumnList.remove(index);
            storedColumnList.add(0, columnList);
          }
        }
        return null;
      }
    });
  }

  public boolean openQuery(String sqlText) throws Exception {
    final boolean storingColumnListPosition = settings.getValue(UniversalSettingsProvider.setStoringColumnListPosition, UniversalSettingsProvider.default_setStoringColumnListPosition);
    ownerPanel.setTabTooltip(this, null);
    ((QueryTableModel) dataTable.getModel()).setEditable(false);
    lastRecord = null;
    lastRowIndex = -1;
    buttonEditData.setSelected(false);
    cmEditData.setEnabled(false);
    cmDeleteRecord.setEnabled(false);
    cmInsertRecord.setEnabled(false);
    cmApplyRecord.setEnabled(false);
    cmRollbackRecord.setEnabled(false);
    editPanel.setVisible(false);
    if (getQuery().isActive() && storingColumnListPosition) {
      storeColumnPositions();
    }
    if (getQuery().isActive()) {
      ownerPanel.fireEventPanel(SqlQueryPanelView.EventPanel.BEFORE_CLOSE_QUERY, getQuery(), null, null);
      getQuery().close();
      ownerPanel.fireEventPanel(SqlQueryPanelView.EventPanel.AFTER_CLOSE_QUERY, getQuery(), null, null);
    }
    if (!getQuery().getDatabase().equals(ownerPanel.getDatabase())) {
      getQuery().setDatabase(ownerPanel.getDatabase());
    }
    getQuery().setSqlText(sqlText);
    if (!showParameters(getQuery())) {
      return false;
    }
    ownerPanel.fireEventPanel(SqlQueryPanelView.EventPanel.BEFORE_OPEN_QUERY, getQuery(), null, null);
    getQuery().open();
    ownerPanel.fireEventPanel(SqlQueryPanelView.EventPanel.AFTER_OPEN_QUERY, getQuery(), null, null);
    if (!getQuery().eof()) {
      SwingUtil.invokeAndWait(new Callable<Object>() {
        public Object call() throws Exception {
          dataTable.changeSelection(0, 0, false, false);
          lastRowIndex = dataTable.getSelectedRow();
          return null;
        }
      });
      lastRecord = getQuery().getRecord(lastRowIndex);
    }
    if (storingColumnListPosition) {
      restoreColumnPositions();
    }
    revalidate();

    ownerPanel.setTabTooltip(this, SQLUtil.createTooltipFromSql(sqlText));
    return true;
  }

  public void closeQuery() {
    updateRecord = null;
    buttonEditData.setSelected(false);
    cmEditData.setEnabled(false);
    cmDeleteRecord.setEnabled(false);
    cmInsertRecord.setEnabled(false);
    cmApplyRecord.setEnabled(false);
    cmRollbackRecord.setEnabled(false);
    editPanel.setVisible(false);
    ((QueryTableModel) dataTable.getModel()).setEditable(false);
    getQuery().close();
    cmEditData.setTooltip(stringManager.getString("cmEditData-hint-on"));
    lastRecord = null;
    lastRowIndex = -1;
  }

  public void close() throws IOException {
    setVisible(false);
    getQuery().close();
    ownerPanel = null;
  }

  private void checkSqlForEdit(String sqlCode) {
    infoService = null;
    DatabaseInfoProvider[] dips = ownerPanel.getApplication().getServiceArray(DatabaseInfoProvider.class);
    if (dips != null && dips.length > 0) {
      for (DatabaseInfoProvider dip : dips) {
        if (dip.isForDatabase(getDatabase())) {
          infoService = dip;
          break;
        }
      }
    }
    
    String error = "";
    boolean okay = false;

    StringTokenizer st = new StringTokenizer(sqlCode, ",", " \n\r\t");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if ("from".equalsIgnoreCase(token)) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          if (token.length() > 0 && token.charAt(0) != '(') {
            SqlTableName name = SQLUtil.parseSqlTableName(token);
            catalogName = name.getCatalogName();
            schemaName = name.getSchemaName();
            tableName = name.getTableName();
            okay = true;
          } else {
            error = error + stringManager.getString("cmEditData-hint-error-no-subq");
            break;
          }
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            if ("as".equalsIgnoreCase(token)) {
              token = st.nextToken();
              if (st.hasMoreTokens()) {
                token = st.nextToken();
              }
            } else if (!",".equals(token) && !"left".equalsIgnoreCase(token) && !"right".equalsIgnoreCase(token) && !"outer".equalsIgnoreCase(token) && !"inner".equalsIgnoreCase(token) && st.hasMoreTokens()) {
              token = st.nextToken();
            }
            if (",".equals(token) || "left".equalsIgnoreCase(token) || "right".equalsIgnoreCase(token) || "outer".equalsIgnoreCase(token) || "inner".equalsIgnoreCase(token)) {
              error = error + stringManager.getString("cmEditData-hint-error-one-table");
              okay = false;
            }
            else {
              okay = true;
            }
          }
        }
        break;
      }
    }

    if (!okay) {
      cmEditData.setEnabled(false);
      cmEditData.setTooltip("<html>" +stringManager.getString("cmEditData-hint-error") +"<br>" + error);
    } else {
      if (setTableEditable()) {
        cmEditData.setTooltip("<html>" +String.format(stringManager.getString("cmEditData-hint-on-edit-data"), new Object[] {getDatabase().quoteName(catalogName, schemaName, tableName)}));
        cmEditData.setEnabled(true);
      }
      else {
        cmEditData.setEnabled(false);
      }
    }
  }

  private boolean setTableEditable() {
    primaryKeyColumns.clear();
    if (infoService != null) {
      String[] fields = infoService.getUniqueIdentFields(getDatabase(), catalogName, schemaName, tableName);
      if (fields != null) {
        primaryKeyColumns.addAll(Arrays.asList(fields));
      }
    }
    else {
      String[] fields = JdbcUtil.getPrimaryKeyFields(getDatabase(), catalogName, schemaName, tableName);
      if (fields != null) {
        primaryKeyColumns.addAll(Arrays.asList(fields));
      }
    }
    if (primaryKeyColumns.isEmpty()) {
      cmEditData.setTooltip(
        stringManager.getString("cmEditData-hint-primary-key") +"<br>" +
        getDatabase().quoteName(catalogName, schemaName, tableName) + " " + Arrays.toString(primaryKeyColumns.toArray()));
      return false;
    }
    else {
      for (String field : primaryKeyColumns) {
        if (getQuery().findFieldByName(field) == null) {
          cmEditData.setTooltip(
            stringManager.getString("cmEditData-hint-pk-column") +"<br>" +
            getDatabase().quoteName(catalogName, schemaName, tableName) + " " + Arrays.toString(primaryKeyColumns.toArray()));
          return false;
        }
      }
    }
    return true;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonRollbackRecord = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonApplyRecord = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonInsertRecord = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonDeleteRecord = new pl.mpak.sky.gui.swing.comp.ToolButton();
    cmStopExecuting = new pl.mpak.sky.gui.swing.Action();
    cmNewResultPanel = new pl.mpak.sky.gui.swing.Action();
    cmEditData = new pl.mpak.sky.gui.swing.Action();
    cmDeleteRecord = new pl.mpak.sky.gui.swing.Action();
    cmInsertRecord = new pl.mpak.sky.gui.swing.Action();
    cmApplyRecord = new pl.mpak.sky.gui.swing.Action();
    cmRollbackRecord = new pl.mpak.sky.gui.swing.Action();
    cmRecordCount = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuEditData = new javax.swing.JMenuItem();
    menuRecordCount = new javax.swing.JMenuItem();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    dataTable = new DataTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonStopExecuting = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonEditData = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

    buttonRollbackRecord.setAction(cmRollbackRecord);
    buttonRollbackRecord.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRollbackRecord.setPreferredSize(new java.awt.Dimension(20, 16));
    buttonRollbackRecord.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

    buttonApplyRecord.setAction(cmApplyRecord);
    buttonApplyRecord.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonApplyRecord.setPreferredSize(new java.awt.Dimension(20, 16));
    buttonApplyRecord.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

    buttonInsertRecord.setAction(cmInsertRecord);
    buttonInsertRecord.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonInsertRecord.setPreferredSize(new java.awt.Dimension(20, 16));
    buttonInsertRecord.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

    buttonDeleteRecord.setAction(cmDeleteRecord);
    buttonDeleteRecord.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDeleteRecord.setPreferredSize(new java.awt.Dimension(20, 16));
    buttonDeleteRecord.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

    cmStopExecuting.setEnabled(false);
    cmStopExecuting.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/cancel.gif")); // NOI18N
    cmStopExecuting.setTooltip(stringManager.getString("cmStopExecuting-hint")); // NOI18N
    cmStopExecuting.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmStopExecutingActionPerformed(evt);
      }
    });

    cmNewResultPanel.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add_table16.gif")); // NOI18N
    cmNewResultPanel.setText(stringManager.getString("cmNewResultPanel-text")); // NOI18N
    cmNewResultPanel.setTooltip(stringManager.getString("cmNewResultPanel-hint")); // NOI18N
    cmNewResultPanel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewResultPanelActionPerformed(evt);
      }
    });

    cmEditData.setActionCommandKey("cmEditData");
    cmEditData.setEnabled(false);
    cmEditData.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit_table.gif")); // NOI18N
    cmEditData.setText(stringManager.getString("cmEditData-text")); // NOI18N
    cmEditData.setTooltip(stringManager.getString("cmEditData-hint")); // NOI18N
    cmEditData.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditDataActionPerformed(evt);
      }
    });

    cmDeleteRecord.setActionCommandKey("cmDeleteRecord");
    cmDeleteRecord.setEnabled(false);
    cmDeleteRecord.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
    cmDeleteRecord.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/minus9.gif")); // NOI18N
    cmDeleteRecord.setText(stringManager.getString("cmDeleteRecord-text")); // NOI18N
    cmDeleteRecord.setTooltip(stringManager.getString("cmDeleteRecord-hint")); // NOI18N
    cmDeleteRecord.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteRecordActionPerformed(evt);
      }
    });

    cmInsertRecord.setActionCommandKey("cmInsertRecord");
    cmInsertRecord.setEnabled(false);
    cmInsertRecord.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
    cmInsertRecord.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add8.gif")); // NOI18N
    cmInsertRecord.setText(stringManager.getString("cmInsertRecord-text")); // NOI18N
    cmInsertRecord.setTooltip(stringManager.getString("cmInsertRecord-hint")); // NOI18N
    cmInsertRecord.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmInsertRecordActionPerformed(evt);
      }
    });

    cmApplyRecord.setActionCommandKey("cmApplyRecord");
    cmApplyRecord.setEnabled(false);
    cmApplyRecord.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/commit10.gif")); // NOI18N
    cmApplyRecord.setText(stringManager.getString("cmApplyRecord-text")); // NOI18N
    cmApplyRecord.setTooltip(stringManager.getString("cmApplyRecord-hint")); // NOI18N
    cmApplyRecord.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmApplyRecordActionPerformed(evt);
      }
    });

    cmRollbackRecord.setActionCommandKey("cmRollbackRecord");
    cmRollbackRecord.setEnabled(false);
    cmRollbackRecord.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/rollback10.gif")); // NOI18N
    cmRollbackRecord.setText(stringManager.getString("cmRollbackRecord-text")); // NOI18N
    cmRollbackRecord.setTooltip(stringManager.getString("cmRollbackRecord-hint")); // NOI18N
    cmRollbackRecord.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRollbackRecordActionPerformed(evt);
      }
    });

    cmRecordCount.setActionCommandKey("cmRecordCount");
    cmRecordCount.setEnabled(false);
    cmRecordCount.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/counts.gif")); // NOI18N
    cmRecordCount.setText(stringManager.getString("cmRecordCount-text")); // NOI18N
    cmRecordCount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRecordCountActionPerformed(evt);
      }
    });

    menuEditData.setAction(cmEditData);
    menuActions.add(menuEditData);

    menuRecordCount.setAction(cmRecordCount);
    menuActions.add(menuRecordCount);

    setLayout(new java.awt.BorderLayout());

    jPanel2.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(dataTable);

    jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setTable(dataTable);
    jPanel2.add(statusBar, java.awt.BorderLayout.SOUTH);

    add(jPanel2, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(new java.awt.BorderLayout());

    toolBar.setFloatable(false);
    toolBar.setOrientation(1);
    toolBar.setRollover(true);

    buttonStopExecuting.setAction(cmStopExecuting);
    buttonStopExecuting.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonStopExecuting.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonStopExecuting);
    toolBar.add(jSeparator1);

    buttonEditData.setAction(cmEditData);
    buttonEditData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonEditData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonEditData);
    toolBar.add(buttonActions);

    jPanel1.add(toolBar, java.awt.BorderLayout.WEST);

    add(jPanel1, java.awt.BorderLayout.WEST);
  }// </editor-fold>//GEN-END:initComponents

private void cmNewResultPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewResultPanelActionPerformed
  ownerPanel.addResultTab();
}//GEN-LAST:event_cmNewResultPanelActionPerformed
  
private void cmStopExecutingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmStopExecutingActionPerformed
  try {
    cmStopExecuting.setEnabled(false);
    getQuery().cancel();
  } catch (SQLException ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmStopExecutingActionPerformed

private void cmEditDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditDataActionPerformed
  if (!buttonEditData.isSelected()) {
    checkSqlForEdit(getQuery().getSqlText());
    if (!cmEditData.isEnabled()) {
      return;
    }
  }
  try {
    updateRecord = new QueryBufferedRecord(
      getDatabase().quoteName(catalogName, schemaName, tableName),
      primaryKeyColumns.toArray(new String[primaryKeyColumns.size()]),
      getQuery());
    lastRowIndex = dataTable.getSelectedRow();
    if (lastRowIndex != -1) {
      lastRecord = getQuery().getRecord(lastRowIndex);
      updateRecord.updateFrom(getQuery());
    }
    buttonEditData.setSelected(!buttonEditData.isSelected());
    ((QueryTableModel)dataTable.getModel()).setEditable(buttonEditData.isSelected());
    cmDeleteRecord.setEnabled(buttonEditData.isSelected());
    cmInsertRecord.setEnabled(buttonEditData.isSelected());
    cmApplyRecord.setEnabled(buttonEditData.isSelected());
    cmRollbackRecord.setEnabled(buttonEditData.isSelected());
    editPanel.setVisible(buttonEditData.isSelected());
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmEditDataActionPerformed

private void cmApplyRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmApplyRecordActionPerformed
  try {
    if (lastRecord != null && lastRecord.isChanged()) {
      getQuery().getRecord(lastRecord.getIndex());
      updateRecord.updateValues(getQuery());
      if (insertRecord) {
        updateRecord.applyInsert();
      }
      else {
        updateRecord.applyUpdate();
      }
      getQuery().update(lastRecord);
      insertRecord = false;
    }

  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(SqlQueryResultPanel.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmApplyRecordActionPerformed

private void cmDeleteRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteRecordActionPerformed
  if (dataTable.getSelectedRow() != -1) {
    try {
      CacheRecord record = getQuery().getRecord(dataTable.getSelectedRow());
      updateRecord.updateFrom(getQuery());
      updateRecord.applyDelete();
      getQuery().delete(record);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          lastRowIndex = dataTable.getSelectedRow();
          if (lastRowIndex != -1) {
            try {
              lastRecord = getQuery().getRecord(lastRowIndex);
              updateRecord.updateFrom(getQuery());
            } catch (Exception ex) {
              ExceptionUtil.processException(ex);
            }
          }
        }
      });
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(SqlQueryResultPanel.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteRecordActionPerformed

private void cmRollbackRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRollbackRecordActionPerformed
  try {
    if (lastRecord != null && lastRecord.isChanged()) {
      getQuery().getRecord(lastRecord.getIndex());
      updateRecord.cancelUpdates();
      lastRecord.cancelUpdates();
      dataTable.repaint();
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(SqlQueryResultPanel.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmRollbackRecordActionPerformed

private void cmInsertRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmInsertRecordActionPerformed
  try {
    lastRecord = new CacheRecord();
    for (int i=0; i<getQuery().getFieldCount(); i++) {
      lastRecord.add();
    }
    getQuery().append(lastRecord);
    lastRowIndex = lastRecord.getIndex();
    dataTable.changeSelection(lastRowIndex, dataTable.getSelectedColumn(), false, false);
    updateRecord.updateFrom(getQuery());
    insertRecord = true;
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(SqlQueryResultPanel.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmInsertRecordActionPerformed

private void cmRecordCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRecordCountActionPerformed
  Query query = getDatabase().createQuery();
  try {
    query.setSqlText("select count( 0 ) cnt from (" +getQuery().getSqlText() +") x");
    for (int i=0; i<getQuery().getParameterCount(); i++) {
      query.paramByName(getQuery().getParameter(i).getParamName()).setValue(getQuery().getParameter(i).getValue(), getQuery().getParameter(i).getParamDataType());
    }
    query.open();
    String sqlText = getQuery().getSqlText();
    if (sqlText.length() > 200) {
      sqlText = sqlText.substring(0, 200) +"...";
    }
    MessageBox.show(this, stringManager.getString("record-count"), String.format(stringManager.getString("SqlQueryResultPanel-record-count-info"), new Object[] {sqlText, query.fieldByName("cnt").getLong()}), ModalResult.OK, MessageBox.INFORMATION);
  }
  catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
  finally {
    query.close();
  }
}//GEN-LAST:event_cmRecordCountActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonApplyRecord;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDeleteRecord;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonEditData;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonInsertRecord;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRollbackRecord;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonStopExecuting;
  private pl.mpak.sky.gui.swing.Action cmApplyRecord;
  private pl.mpak.sky.gui.swing.Action cmDeleteRecord;
  private pl.mpak.sky.gui.swing.Action cmEditData;
  private pl.mpak.sky.gui.swing.Action cmInsertRecord;
  private pl.mpak.sky.gui.swing.Action cmNewResultPanel;
  private pl.mpak.sky.gui.swing.Action cmRecordCount;
  private pl.mpak.sky.gui.swing.Action cmRollbackRecord;
  private pl.mpak.sky.gui.swing.Action cmStopExecuting;
  private DataTable dataTable;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuEditData;
  private javax.swing.JMenuItem menuRecordCount;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables
  
}

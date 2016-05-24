package pl.mpak.orbada.oracle.gui.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.gui.freezing.ViewFreezeViewService;
import pl.mpak.orbada.oracle.gui.wizards.view.CommentViewWizard;
import pl.mpak.orbada.oracle.gui.wizards.view.CopyViewAsTableWizard;
import pl.mpak.orbada.oracle.gui.wizards.view.DropViewWizard;
import pl.mpak.orbada.oracle.gui.wizards.RenameViewWizard;
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
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ViewsPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private ISettings settings;
  private Timer timer;
  private OrbadaTabbedPane tabbedPane;
  private boolean refreshing = false;
  
  /** Creates new form DerbyDbObjectPanelView
   * @param accesibilities
   */
  public ViewsPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
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
        refreshTabbedPanes();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);

    tabbedPane = new ViewTabbedPane(accesibilities);
    split.setRightComponent(tabbedPane);
    
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-views-panel");
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    
    tableViews.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          ViewSourcePanel panel = (ViewSourcePanel)tabbedPane.getComponent(ViewSourcePanel.class);
          if (panel != null && lastIndex != tableViews.getSelectedRow() && !panel.canClose()) {
            tableViews.changeSelection(lastIndex, tableViews.getSelectedColumn());
            return;
          }
          lastIndex = tableViews.getSelectedRow();
          timer.restart();
        }
      }
    });
    
    tableViews.getQuery().setDatabase(getDatabase());
    tableViews.addColumn(new QueryTableColumn("view_name", stringManager.getString("view-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableViews.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 50, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
      public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
        if (StringUtil.nvl((String)value, "").equals("VALID")) {
          ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
        }
        else if (StringUtil.nvl((String)value, "").equals("INVALID")) {
          ((JLabel)renderer).setForeground(Color.RED);
        }
      }
    })));
    tableViews.addColumn(new QueryTableColumn("remarks", stringManager.getString("comment"), 200));
    tableViews.addColumn(new QueryTableColumn("created", stringManager.getString("created"), 110));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("o.object_name", stringManager.getString("view-name"), (String[])null));
    def.add(new SqlFilterDefComponent("c.comments", stringManager.getString("comment"), (String[])null));
    def.add(new SqlFilterDefComponent("o.status = 'INVALID'", stringManager.getString("invalid")));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-views-filter"),
      cmFilter, buttonFilter, 
      def);
    
    SwingUtil.addAction(tableViews, cmCompile);
    new ComponentActionsAction(getDatabase(), tableViews, buttonActions, menuActions, "oracle-views-actions");

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableViews.requestFocusInWindow();
      }
    });
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
  
  private void refreshTabbedPanes() {
    String viewName = "";
    int rowIndex = tableViews.getSelectedRow();
    if (rowIndex >= 0 && tableViews.getQuery().isActive()) {
      try {
        tableViews.getQuery().getRecord(rowIndex);
        viewName = tableViews.getQuery().fieldByName("view_name").getString();
        final String remarks = tableViews.getQuery().fieldByName("remarks").getString();
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            textRemarks.setText(remarks);
          }
        });
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else {
      textRemarks.setText("");
    }
    tabbedPane.refresh(null, currentSchemaName, viewName);
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
      String viewName = null;
      if (tableViews.getQuery().isActive() && tableViews.getSelectedRow() >= 0) {
        tableViews.getQuery().getRecord(tableViews.getSelectedRow());
        viewName = tableViews.getQuery().fieldByName("view_name").getString();
      }
      refresh(viewName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    refreshing = true;
    try {
      int column = tableViews.getSelectedColumn();
      int index = Math.max(0, tableViews.getSelectedRow());
      tableViews.getQuery().close();
      tableViews.getQuery().setSqlText(Sql.getViewList(filter.getSqlText()));
      tableViews.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableViews.getQuery().open();
      if (objectName != null && tableViews.getQuery().locate("view_name", new Variant(objectName))) {
        tableViews.changeSelection(tableViews.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableViews.getQuery().isEmpty()) {
        tableViews.changeSelection(Math.min(index, tableViews.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      refreshing = false;
      refreshTabbedPanes();
    }
  }
  
  public boolean canClose() {
    return tabbedPane.canClose();
  }

  public void close() throws IOException {
    timer.cancel();
    settings.setValue("split-location", (long)split.getDividerLocation());
    viewClosing = true;
    tabbedPane.close();
    tableViews.getQuery().close();
    accesibilities = null;
    settings.store();
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    cmFilter = new pl.mpak.sky.gui.swing.Action();
    cmDropView = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCommentView = new javax.swing.JMenuItem();
    menuRecordCount = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuRename = new javax.swing.JMenuItem();
    menuCopyView = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropView = new javax.swing.JMenuItem();
    cmComment = new pl.mpak.sky.gui.swing.Action();
    cmRecordCount = new pl.mpak.sky.gui.swing.Action();
    cmRename = new pl.mpak.sky.gui.swing.Action();
    cmCopyViewAsTable = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmCompile = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    panelViews = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableViews = new pl.mpak.orbada.gui.comps.table.ViewTable();
    jPanel1 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFreezeObject = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonCompile = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel2 = new javax.swing.JPanel();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jScrollPane2 = new javax.swing.JScrollPane();
    textRemarks = new pl.mpak.sky.gui.swing.comp.TextArea();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
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

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/filter16.gif")); // NOI18N
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmDropView.setActionCommandKey("cmDropView");
    cmDropView.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropView.setText(stringManager.getString("cmDropView-text")); // NOI18N
    cmDropView.setTooltip(stringManager.getString("cmDropView-hint")); // NOI18N
    cmDropView.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropViewActionPerformed(evt);
      }
    });

    menuCommentView.setAction(cmComment);
    menuActions.add(menuCommentView);

    menuRecordCount.setAction(cmRecordCount);
    menuActions.add(menuRecordCount);
    menuActions.add(jSeparator3);

    menuRename.setAction(cmRename);
    menuActions.add(menuRename);

    menuCopyView.setAction(cmCopyViewAsTable);
    menuActions.add(menuCopyView);
    menuActions.add(jSeparator2);

    menuDropView.setAction(cmDropView);
    menuActions.add(menuDropView);

    cmComment.setActionCommandKey("cmComment");
    cmComment.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif")); // NOI18N
    cmComment.setText(stringManager.getString("cmComment-text")); // NOI18N
    cmComment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentActionPerformed(evt);
      }
    });

    cmRecordCount.setActionCommandKey("cmRecordCount");
    cmRecordCount.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/counts.gif")); // NOI18N
    cmRecordCount.setText(stringManager.getString("cmRecordCount-text")); // NOI18N
    cmRecordCount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRecordCountActionPerformed(evt);
      }
    });

    cmRename.setActionCommandKey("cmRename");
    cmRename.setText(stringManager.getString("cmRename-text")); // NOI18N
    cmRename.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRenameActionPerformed(evt);
      }
    });

    cmCopyViewAsTable.setActionCommandKey("cmCopyViewAsTable");
    cmCopyViewAsTable.setText(stringManager.getString("cmCopyViewAsTable-text")); // NOI18N
    cmCopyViewAsTable.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCopyViewAsTableActionPerformed(evt);
      }
    });

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    cmCompile.setActionCommandKey("cmCompile");
    cmCompile.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmCompile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute16.gif")); // NOI18N
    cmCompile.setText(stringManager.getString("cmCompile-text")); // NOI18N
    cmCompile.setTooltip(stringManager.getString("cmCompile-hint")); // NOI18N
    cmCompile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCompileActionPerformed(evt);
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

    panelViews.setPreferredSize(new java.awt.Dimension(250, 100));
    panelViews.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableViews);

    panelViews.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

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

    buttonFreezeObject.setAction(cmFreezeObject);
    buttonFreezeObject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFreezeObject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonFreezeObject);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBar.add(jSeparator1);

    buttonCompile.setAction(cmCompile);
    buttonCompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonCompile);
    toolBar.add(buttonActions);

    jPanel1.add(toolBar);

    panelViews.add(jPanel1, java.awt.BorderLayout.NORTH);

    jPanel2.setLayout(new java.awt.BorderLayout());

    statusBar.setShowFieldType(false);
    statusBar.setShowFieldValue(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableViews);
    jPanel2.add(statusBar, java.awt.BorderLayout.SOUTH);

    jScrollPane2.setPreferredSize(new java.awt.Dimension(166, 50));

    textRemarks.setColumns(20);
    textRemarks.setEditable(false);
    textRemarks.setLineWrap(true);
    textRemarks.setRows(5);
    textRemarks.setFocusable(false);
    textRemarks.setFont(new java.awt.Font("Courier New", 0, 11));
    jScrollPane2.setViewportView(textRemarks);

    jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    panelViews.add(jPanel2, java.awt.BorderLayout.SOUTH);

    split.setLeftComponent(panelViews);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmDropViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropViewActionPerformed
  if (tableViews.getSelectedRow() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectedRow());
      if (SqlCodeWizardDialog.show(new DropViewWizard(getDatabase(), currentSchemaName, tableViews.getQuery().fieldByName("view_name").getString()), true) != null) {
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDropViewActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableViews.getQuery().isActive()) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
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
  }
  catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
  finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentActionPerformed
  if (tableViews.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectionModel().getLeadSelectionIndex());
      String viewName = tableViews.getQuery().fieldByName("view_name").getString();
      if (SqlCodeWizardDialog.show(new CommentViewWizard(getDatabase(), currentSchemaName, viewName), true) != null) {
        refresh(viewName);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCommentActionPerformed

private void cmRecordCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRecordCountActionPerformed
  if (tableViews.getSelectedRow() >= 0) {
    Query query = getDatabase().createQuery();
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectedRow());
      String schemaName = tableViews.getQuery().fieldByName("schema_name").getString();
      String view_name = tableViews.getQuery().fieldByName("view_name").getString();
      query.open("select count( 0 ) cnt from " +SQLUtil.createSqlName(schemaName, view_name));
      MessageBox.show(this, stringManager.getString("record-count"), String.format(stringManager.getString("ViewsPanelView-record-count-info"), new Object[] {SQLUtil.createSqlName(schemaName, view_name), query.fieldByName("cnt").getString()}), ModalResult.OK, MessageBox.INFORMATION);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    } finally {
      query.close();
    }
  }
}//GEN-LAST:event_cmRecordCountActionPerformed

private void cmRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRenameActionPerformed
  if (tableViews.getSelectedRow() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectedRow());
      String viewName = tableViews.getQuery().fieldByName("view_name").getString();
      if (SqlCodeWizardDialog.show(new RenameViewWizard(getDatabase(), currentSchemaName, viewName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmRenameActionPerformed

private void cmCopyViewAsTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCopyViewAsTableActionPerformed
  if (tableViews.getSelectedRow() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectedRow());
      String viewName = tableViews.getQuery().fieldByName("view_name").getString();
      if (SqlCodeWizardDialog.show(new CopyViewAsTableWizard(getDatabase(), currentSchemaName, viewName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmCopyViewAsTableActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableViews.getSelectedRow() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectedRow());
      final String viewName = tableViews.getQuery().fieldByName("view_name").getString();
      accesibilities.createView(new ViewFreezeViewService(accesibilities, currentSchemaName, viewName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileActionPerformed
  if (tableViews.getSelectedRow() >= 0) {
    try {
      tableViews.getQuery().getRecord(tableViews.getSelectedRow());
      String viewName = tableViews.getQuery().fieldByName("view_name").getString();
      getDatabase().executeCommand("ALTER VIEW " +SQLUtil.createSqlName(currentSchemaName, viewName) +" COMPILE");
      refresh();
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCompile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeObject;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmComment;
  private pl.mpak.sky.gui.swing.Action cmCompile;
  private pl.mpak.sky.gui.swing.Action cmCopyViewAsTable;
  private pl.mpak.sky.gui.swing.Action cmDropView;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRecordCount;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmRename;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCommentView;
  private javax.swing.JMenuItem menuCopyView;
  private javax.swing.JMenuItem menuDropView;
  private javax.swing.JMenuItem menuRecordCount;
  private javax.swing.JMenuItem menuRename;
  private javax.swing.JPanel panelViews;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableViews;
  private pl.mpak.sky.gui.swing.comp.TextArea textRemarks;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables
  
}

package pl.mpak.orbada.universal.gui.tabbed;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectUserData;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.CloseAbilitable;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 * universal panel view with right tabs of object properties
 * database.userProperties[dict-persistent-query] set to true if you want database cursor auto close
 * @author  akaluza
 */
public abstract class UniversalViewTabs extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  protected IViewAccesibilities accesibilities;
  protected String currentSchemaName;
  protected SqlFilter filter;
  protected String tabTitle;
  protected boolean viewClosing = false;
  protected ISettings settings;
  protected Timer timer;
  protected Component tabbedPane;
  protected boolean refreshing = false;
  protected ComponentActionsAction componentActions;
  private int tabNumber;
  
  public UniversalViewTabs(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        initComponents();
        init();
      }
    });
  }
  
  private int findTabNumber() {
    Component[] tabs = accesibilities.getViewComponentList(accesibilities.getViewProvider());
    if (tabs.length > 0) {
      for (int i=0; i<100; i++) {
        boolean found = false;
        for (int t=0; t<tabs.length; t++) {
          UniversalViewTabs tab = (UniversalViewTabs)tabs[t];
          if (tab.getTabNumber() == i) {
            found = true;
            break;
          }
        }
        if (!found) {
          return i;
        }
      }
    }
    return 0;
  }
  
  public int getTabNumber() {
    return tabNumber;
  }
  
  protected void init() {
    tabNumber = findTabNumber();
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refreshTabbedPanes();
      }
    };
    OrbadaUniversalPlugin.refreshQueue.add(timer);

    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), getPanelName() +"-settings");
    
    tabbedPane = getTabbedPane();
    if (tabbedPane != null) {
      splitObjects.setRightComponent(tabbedPane);
      if (!isHorizontal()) {
        splitObjects.setOrientation(JSplitPane.VERTICAL_SPLIT);
      }
      splitObjects.setDividerLocation(settings.getValue("split-location", (long)splitObjects.getDividerLocation()).intValue());
    }
    else {
      Component c = splitObjects.getLeftComponent();
      remove(splitObjects);
      add(c);
      statusBarObjects.setShowFieldValue(true);
    }
    
    currentSchemaName = getCurrentSchemaName();
    if (getSchemaColumnName() == null) {
      cmSelectSchema.setEnabled(false);
      buttonSelectSchema.setVisible(false);
    }
    else if (getTabNumber() > 1) {
      setCurrentSchemaName(settings.getValue("current-schema-" +getTabNumber(), (String)null));
    }
    
    tableObjects.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int lastIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (!refreshing) {
          if (lastIndex != tableObjects.getSelectedRow() && tabbedPane instanceof OrbadaTabbedPane) {
            if (lastIndex == -1) {
              lastIndex = tableObjects.getSelectedRow();
            }
            if (!((OrbadaTabbedPane)tabbedPane).canClose()) {
              tableObjects.changeSelection(lastIndex, tableObjects.getSelectedColumn());
              return;
            }
          }
          lastIndex = tableObjects.getSelectedRow();
          timer.restart();
        }
      }
    });
    
    tableObjects.getQuery().setDatabase(getDatabase());
    try {
      if (StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("dict-persistent-query", "false"))) {
        tableObjects.getQuery().setCloseResultAfterOpen(true);
      }
      QueryTableColumn[] qtcs = getTableColumns();
      if (qtcs != null) {
        for (QueryTableColumn qtc : qtcs) {
          tableObjects.addColumn(qtc);
        }
      }
    
      SqlFilterDefComponent[] fdcs = getFilterDefComponent();
      if (fdcs != null) {
        SqlFilterDef def = new SqlFilterDef();
        for (SqlFilterDefComponent fdc : fdcs) {
          def.add(fdc);
        }
        filter = new SqlFilter(
          accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), getPanelName() +"-filter"),
          cmFilter, buttonFilter,
          def);
      }
      else {
        cmFilter.setEnabled(false);
        buttonFilter.setVisible(false);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    
    scrollDescription.setVisible(getDescriptionColumnName() != null);

    componentActions = new ComponentActionsAction(getDatabase(), tableObjects, buttonActions, menuActions, getPanelName() +"-actions");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableObjects.requestFocusInWindow();
      }
    });
  }
  
  /**
   * <p>if you want vertical orientation then override this function and return false
   * @return 
   */
  public boolean isHorizontal() {
    return true;
  }
  
  /**
   * <p>return TabbedPane or null if view is simple list without right tabs
   * @return 
   */
  abstract public Component getTabbedPane();
  
  /**
   * current selected database schema/catalog
   * @return 
   */
  abstract public String getCurrentSchemaName();
  
  /**
   * panel name used for setting name, filter, actions, etc
   * like "database-panel"
   * @return 
   */
  abstract public String getPanelName();
  
  /**
   * <p>column name for query schema name or null if no schema selection
   * @return 
   */
  abstract public String getSchemaColumnName();
  
  /**
   * <p>column name for query object name
   * @return 
   */
  abstract public String getObjectColumnName();
  
  /**
   * column name of object description
   * if null then text of description is hidden
   * @return 
   */
  abstract public String getDescriptionColumnName();
  
  abstract public QueryTableColumn[] getTableColumns();
  
  /**
   * get sql code for query data list - check filter if null
   * @param filter
   * @return 
   */
  abstract public String getSql(SqlFilter filter);
  
  protected void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    
  }
  
  /**
   * filter definition, can be null
   * @return 
   */
  abstract public SqlFilterDefComponent[] getFilterDefComponent();
  
  /**
   * schema/catalog list, can be null if getSchemaColumnName() return null
   * @return 
   */
  abstract public String[] getSchemaList();
  
  protected boolean executeSql() {
    return false;
  }
  
  public boolean canOpen(ParametrizedCommand qc) {
    return true;
  }
  
  public void afterOpen(Query query) {
    
  }
  
  protected HashMap<String, Variant> getUserData() {
    return null;
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!StringUtil.isEmpty(schemaName) && !currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(getCurrentSchemaName())) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
        buttonSelectSchema.setSelected(true);
      }
      else {
        buttonSelectSchema.setSelected(false);
        accesibilities.setTabTitle(tabTitle);
      }
    }
  }
  
  protected void refreshTabbedPanes() {
    String objectName = "";
    String schemaName = "";
    int rowIndex = tableObjects.getSelectedRow();
    if (rowIndex >= 0) {
      try {
        tableObjects.getQuery().getRecord(rowIndex);
        objectName = tableObjects.getQuery().fieldByName(getObjectColumnName()).getString();
        if (getSchemaColumnName() != null && tableObjects.getQuery().findFieldByName(getSchemaColumnName()) != null) {
          schemaName = tableObjects.getQuery().fieldByName(getSchemaColumnName()).getString();
        }
        String dcn = getDescriptionColumnName();
        if (dcn != null) {
          final String description = tableObjects.getQuery().fieldByName(dcn).getString();
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              textDescription.setText(description);
            }
          });
        }
        if (tabbedPane instanceof ITabObjectUserData) {
          HashMap<String, Variant> map = getUserData();
          if (map != null) {
            ((ITabObjectUserData)tabbedPane).userData(map);
          }
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else {
      if (tabbedPane instanceof ITabObjectUserData) {
        ((ITabObjectUserData)tabbedPane).userData(null);
      }
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          textDescription.setText("");
        }
      });
    }
    if (tabbedPane instanceof ITabObjectInfo) {
      ((ITabObjectInfo)tabbedPane).refresh(null, StringUtil.isEmpty(schemaName) ? currentSchemaName : schemaName, objectName);
    }
  }
  
  protected void refreshTableListTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    if (!isVisible() || !SwingUtil.isVisible(this) || viewClosing) {
      return;
    }
    try {
      String objectName = null;
      if (tableObjects.getQuery().isActive() && tableObjects.getSelectedRow() >= 0) {
        tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
        objectName = tableObjects.getQuery().fieldByName(getObjectColumnName()).getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    refreshing = true;
    try {
      int column = Math.max(0, tableObjects.getSelectedColumn());
      int index = Math.max(0, tableObjects.getSelectedRow());
      tableObjects.getQuery().close();
      if (executeSql()) {
        Command command = getDatabase().createCommand(getSql(filter), false);
        if (canOpen(command)) {
          if (getSchemaColumnName() != null) {
            command.paramByName(getSchemaColumnName()).setString(currentSchemaName);
          }
          extraSqlParameters(command);
          command.execute();
          ResultSet rs;
          if (command.getStatement() != null && (rs = command.getStatement().getResultSet()) != null) {
            tableObjects.getQuery().setResultSet(rs);
          }
        }
      }
      else {
        tableObjects.getQuery().setSqlText(getSql(filter));
        if (canOpen(tableObjects.getQuery())) {
          if (getSchemaColumnName() != null) {
            tableObjects.getQuery().paramByName(getSchemaColumnName()).setString(currentSchemaName);
          }
          extraSqlParameters(tableObjects.getQuery());
          tableObjects.getQuery().open();
        }
      }
      if (!tableObjects.getQuery().isEmpty()) {
        if (objectName != null && tableObjects.getQuery().locate(getObjectColumnName(), new Variant(objectName))) {
          tableObjects.changeSelection(tableObjects.getQuery().getCurrentRecord().getIndex(), column);
        }
        else {
          tableObjects.changeSelection(Math.min(index, tableObjects.getRowCount() -1), column);
        }
      }
      afterOpen(tableObjects.getQuery());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      refreshing = false;
      refreshTabbedPanes();
    }
  }

  public boolean canClose() {
    if (tabbedPane instanceof CloseAbilitable) {
      return ((CloseAbilitable)tabbedPane).canClose();
    }
    return true;
  }

  public void close() throws IOException {
    timer.cancel();
    viewClosing = true;
    if (cmSelectSchema.isEnabled()) {
      if (!currentSchemaName.equalsIgnoreCase(getCurrentSchemaName())) {
        settings.setValue("current-schema-" +getTabNumber(), currentSchemaName);
      }
      else {
        settings.setValue("current-schema-" +getTabNumber(), (String)null);
      }
    }
    if (tabbedPane != null) {
      settings.setValue("split-location", (long)splitObjects.getDividerLocation());
      if (tabbedPane instanceof Closeable) {
        ((Closeable)tabbedPane).close();
      }
    }
    tableObjects.getQuery().close();
    accesibilities = null;
    settings.store();
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public JToolBar getToolBar() {
    return toolBar;
  }
  
  public Query getQuery() {
    return tableObjects.getQuery();
  }
  
  public JTable getTable() {
    return tableObjects;
  }
  
  public IViewAccesibilities getAccesibilities() {
    return accesibilities;
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
        menuActions = new javax.swing.JPopupMenu();
        cmFilter = new pl.mpak.sky.gui.swing.Action();
        splitObjects = new javax.swing.JSplitPane();
        panelObjects = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JSeparator();
        buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableObjects = new pl.mpak.orbada.gui.comps.table.ViewTable();
        scrollDescription = new javax.swing.JScrollPane();
        textDescription = new pl.mpak.sky.gui.swing.comp.TextArea();
        jPanel2 = new javax.swing.JPanel();
        statusBarObjects = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
        temp = new javax.swing.JPanel();

        cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
        cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
        cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
        cmRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmRefreshActionPerformed(evt);
            }
        });

        cmSelectSchema.setActionCommandKey("cmSelectDatabase");
        cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/request.gif")); // NOI18N
        cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
        cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
        cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectSchemaActionPerformed(evt);
            }
        });

        cmFilter.setActionCommandKey("cmFilter");
        cmFilter.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/filter16.gif")); // NOI18N
        cmFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmFilterActionPerformed(evt);
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        splitObjects.setBorder(null);
        splitObjects.setDividerLocation(200);
        splitObjects.setContinuousLayout(true);
        splitObjects.setOneTouchExpandable(true);

        panelObjects.setPreferredSize(new java.awt.Dimension(250, 100));
        panelObjects.setLayout(new java.awt.BorderLayout());

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

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.add(jSeparator1);
        toolBar.add(buttonActions);

        jPanel1.add(toolBar);

        panelObjects.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(tableObjects);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        scrollDescription.setPreferredSize(new java.awt.Dimension(166, 50));

        textDescription.setColumns(20);
        textDescription.setEditable(false);
        textDescription.setLineWrap(true);
        textDescription.setRows(5);
        textDescription.setFocusable(false);
        textDescription.setFont(new java.awt.Font("Courier New", 0, 11));
        scrollDescription.setViewportView(textDescription);

        jPanel3.add(scrollDescription, java.awt.BorderLayout.SOUTH);

        panelObjects.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        statusBarObjects.setShowFieldType(false);
        statusBarObjects.setShowFieldValue(false);
        statusBarObjects.setShowOpenTime(false);
        statusBarObjects.setTable(tableObjects);
        jPanel2.add(statusBarObjects, java.awt.BorderLayout.SOUTH);

        panelObjects.add(jPanel2, java.awt.BorderLayout.SOUTH);

        splitObjects.setLeftComponent(panelObjects);
        splitObjects.setRightComponent(temp);

        add(splitObjects, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableObjects.getQuery().isActive()) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown
  
private void cmSelectSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectSchemaActionPerformed
  ArrayList<String> vl = new ArrayList<String>(Arrays.asList(getSchemaList()));
  Point point = buttonSelectSchema.getLocationOnScreen();
  point.y+= buttonSelectSchema.getHeight();
  SimpleSelectDialog.select((Window)SwingUtil.getWindowComponent(this), point.x, point.y, vl, vl.indexOf(currentSchemaName), new SimpleSelectDialog.CallBack() {
    public void selected(Object o) {
      setCurrentSchemaName(o.toString());
      refreshTableListTask();
    }
  });
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableListTask();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
    private pl.mpak.sky.gui.swing.Action cmFilter;
    private pl.mpak.sky.gui.swing.Action cmRefresh;
    private pl.mpak.sky.gui.swing.Action cmSelectSchema;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu menuActions;
    private javax.swing.JPanel panelObjects;
    private javax.swing.JScrollPane scrollDescription;
    private javax.swing.JSplitPane splitObjects;
    private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarObjects;
    private pl.mpak.orbada.gui.comps.table.ViewTable tableObjects;
    private javax.swing.JPanel temp;
    private pl.mpak.sky.gui.swing.comp.TextArea textDescription;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

}

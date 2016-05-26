package pl.mpak.orbada.universal.gui.tabbed;

import java.io.IOException;
import java.sql.ResultSet;
import javax.swing.JToolBar;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.ISettings;
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
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public abstract class UniversalPropTab extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  protected IViewAccesibilities accesibilities;
  protected ISettings settings;
  protected String currentSchemaName = "";
  protected String currentObjectName = "";
  protected boolean requestRefresh = false;
  protected boolean closing = false;
  protected SqlFilter filter;
  protected ComponentActionsAction componentActions;
  
  public UniversalPropTab(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        init();
      }
    });
  }
  
  protected void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), getPanelName() +"-settings");
    tableProps.getQuery().setDatabase(getDatabase());
    try {
      if (StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("dict-persistent-query", "false"))) {
        tableProps.getQuery().setCloseResultAfterOpen(true);
      }
      QueryTableColumn[] qtcs = getTableColumns();
      if (qtcs != null) {
        for (QueryTableColumn qtc : qtcs) {
          tableProps.addColumn(qtc);
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
    
    componentActions = new ComponentActionsAction(getDatabase(), tableProps, buttonActions, menuActions, getPanelName() +"-actions");
  }
  
  /**
   * panel name used for setting name, filter, actions, etc
   * like "database-panel"
   * @return 
   */
  abstract public String getPanelName();
  
  /**
   * column name for query schema name
   * @return 
   */
  abstract public String getSchemaColumnName();
  
  /**
   * column name for query object name
   * @return 
   */
  abstract public String getObjectColumnName();
  
  /**
   * column name if query property name
   * @return 
   */
  abstract public String getPropColumnName();
  
  abstract public QueryTableColumn[] getTableColumns();
  
  /**
   * get sql code for query data list - check filter if null
   * @param filter
   * @return 
   */
  abstract public String getSql(SqlFilter filter);
  
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    
  }
  
  public void afterOpen(Query query) {
    
  }
  
  /**
   * filter definition, can be null
   * @return 
   */
  abstract public SqlFilterDefComponent[] getFilterDefComponent();
  
  protected boolean executeSql() {
    return false;
  }
  
  public boolean canClose() {
    return true;
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  protected void refreshTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      int column = Math.max(0, tableProps.getSelectedColumn());
      int index = Math.max(0, tableProps.getSelectedRow());
      String propName = null;
      if (tableProps.getQuery().isActive() && tableProps.getSelectedRow() >= 0 && getPropColumnName() != null) {
        tableProps.getQuery().getRecord(tableProps.getSelectedRow());
        propName = tableProps.getQuery().fieldByName(getPropColumnName()).getString();
      }
      tableProps.getQuery().close();
      if (!StringUtil.isEmpty(currentObjectName)) {
        String sql = getSql(filter);
        if (sql != null) {
          if (executeSql()) {
            Command command = getDatabase().createCommand(sql, false);
            if (getSchemaColumnName() != null) {
              command.paramByName(getSchemaColumnName()).setString(currentSchemaName);
            }
            if (getObjectColumnName() != null) {
              command.paramByName(getObjectColumnName()).setString(currentObjectName);
            }
            extraSqlParameters(command);
            command.execute();
            ResultSet rs;
            if (command.getStatement() != null && (rs = command.getStatement().getResultSet()) != null) {
              tableProps.getQuery().setResultSet(rs);
            }
          }
          else {
            tableProps.getQuery().setSqlText(sql);
            if (getSchemaColumnName() != null) {
              tableProps.getQuery().paramByName(getSchemaColumnName()).setString(currentSchemaName);
            }
            if (getObjectColumnName() != null) {
              tableProps.getQuery().paramByName(getObjectColumnName()).setString(currentObjectName);
            }
            extraSqlParameters(tableProps.getQuery());
            tableProps.getQuery().open();
          }
        }
        if (!tableProps.getQuery().isEmpty()) {
          if (propName != null && getPropColumnName() != null && tableProps.getQuery().locate(getPropColumnName(), new Variant(propName))) {
            tableProps.changeSelection(tableProps.getQuery().getCurrentRecord().getIndex(), column);
          }
          else {
            tableProps.changeSelection(Math.min(index, tableProps.getRowCount() -1), column);
          }
        }
        afterOpen(tableProps.getQuery());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!StringUtil.equals(currentSchemaName, schemaName) || !StringUtil.equals(currentObjectName, objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentObjectName = objectName;
      if (SwingUtil.isVisible(this)) {
        refresh();
      }
      else {
        requestRefresh = true;
      }
    }
  }
  
  public String getCurrentSchemaName() {
    return currentSchemaName;
  }

  public String getCurrentObjectName() {
    return currentObjectName;
  }

  public Query getQuery() {
    return tableProps.getQuery();
  }
  
  public JToolBar getToolBar() {
    return toolBar;
  }
  
  public void close() throws IOException {
    closing = true;
    tableProps.getQuery().close();
    settings.store();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProps = new ViewTable();
        statusBarProps = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
        jPanel1 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
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

        jScrollPane1.setViewportView(tableProps);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        statusBarProps.setShowFieldType(false);
        statusBarProps.setShowOpenTime(false);
        statusBarProps.setTable(tableProps);
        add(statusBarProps, java.awt.BorderLayout.PAGE_END);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

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
        toolBar.add(jSeparator1);
        toolBar.add(buttonActions);

        jPanel1.add(toolBar);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed

  private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
    refresh();
}//GEN-LAST:event_cmRefreshActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
    private pl.mpak.sky.gui.swing.Action cmFilter;
    private pl.mpak.sky.gui.swing.Action cmRefresh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu menuActions;
    private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarProps;
    private ViewTable tableProps;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
  
}

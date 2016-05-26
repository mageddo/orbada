package pl.mpak.orbada.universal.gui.tabbed;

import java.io.IOException;
import java.sql.ResultSet;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ITabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
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
public abstract class UniversalPropSourceTab extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  protected IViewAccesibilities accesibilities;
  protected String currentSchemaName = "";
  protected String currentObjectName = "";
  protected String currentPropName;
  protected boolean requestRefresh = false;
  protected boolean closing = false;
  protected SqlFilter filter;
  protected ISettings settings;
  protected ComponentActionsAction componentActions;
  
  /** 
   * @param accesibilities
   */
  public UniversalPropSourceTab(IViewAccesibilities accesibilities) {
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
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());
    
    textProp.setDatabase(getDatabase());
    textProp.setEditable(false);
    tableProps.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableProps.getSelectedRow();
        if (rowIndex >= 0 && tableProps.getQuery().isActive()) {
          try {
            tableProps.getQuery().getRecord(rowIndex);
            if (currentPropName == null || !currentPropName.equals(tableProps.getQuery().fieldByName(getPropColumnName()).getString())) {
              currentPropName = tableProps.getQuery().fieldByName(getPropColumnName()).getString();
              updateBody(textProp);
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          currentPropName = null;
          textProp.setDatabaseObject(null, null, null, "");
        }
      }
    });
    
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
   * column name for query object name
   * @return 
   */
  abstract public String getObjectColumnName();
  
  /**
   * column name for query schema name
   * @return 
   */
  abstract public String getSchemaColumnName();
  
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
  
  /**
   * filter definition, can be null
   * @return 
   */
  abstract public SqlFilterDefComponent[] getFilterDefComponent();
  
  abstract public void updateBody(OrbadaSyntaxTextArea textArea);
  
  protected boolean executeSql() {
    return false;
  }
  
  @Override
  public boolean canClose() {
    return true;
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
      if (tableProps.getQuery().isActive() && tableProps.getSelectedRow() >= 0) {
        tableProps.getQuery().getRecord(tableProps.getSelectedRow());
        propName = tableProps.getQuery().fieldByName(getPropColumnName()).getString();
      }
      tableProps.getQuery().close();
      if (!StringUtil.isEmpty(currentObjectName)) {
        if (executeSql()) {
          Command command = getDatabase().createCommand(getSql(filter), false);
          if (getSchemaColumnName() != null) {
            command.paramByName(getSchemaColumnName()).setString(currentSchemaName);
          }
          if (getObjectColumnName() != null) {
            command.paramByName(getObjectColumnName()).setString(currentObjectName);
          }
          command.execute();
          ResultSet rs;
          if (command.getStatement() != null && (rs = command.getStatement().getResultSet()) != null) {
            tableProps.getQuery().setResultSet(rs);
          }
        }
        else {
          tableProps.getQuery().setSqlText(getSql(filter));
          if (getSchemaColumnName() != null) {
            tableProps.getQuery().paramByName(getSchemaColumnName()).setString(currentSchemaName);
          }
          if (getObjectColumnName() != null) {
            tableProps.getQuery().paramByName(getObjectColumnName()).setString(currentObjectName);
          }
          tableProps.getQuery().open();
        }
        if (!tableProps.getQuery().isEmpty()) {
          if (propName != null && tableProps.getQuery().locate(getPropColumnName(), new Variant(propName))) {
            tableProps.changeSelection(tableProps.getQuery().getCurrentRecord().getIndex(), column);
            //updateBody(textProp);
          }
          else {
            tableProps.changeSelection(Math.min(index, tableProps.getRowCount() -1), column);
            //updateBody(textProp);
          }
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentObjectName.equals(objectName) || requestRefresh) {
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

  public String getCurrentObjectName() {
    return currentObjectName;
  }

  public String getCurrentPropName() {
    return currentPropName;
  }

  public String getCurrentSchemaName() {
    return currentSchemaName;
  }
  
  public Query getQuery() {
    return tableProps.getQuery();
  }
  
  public AbsOrbadaSyntaxTextArea getTextArea() {
    return textProp;
  }
  
  public void close() throws IOException {
    closing = true;
    settings.setValue("split-location", (long)split.getDividerLocation());
    settings.store();
    tableProps.getQuery().close();
    textProp.setDatabase(null);
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
        jPanel2 = new javax.swing.JPanel();
        toolBarTriggers = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
        split = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProps = new ViewTable();
        statusBarProps = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
        textProp = new OrbadaSyntaxTextArea();

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

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        toolBarTriggers.setFloatable(false);
        toolBarTriggers.setRollover(true);

        buttonRefresh.setAction(cmRefresh);
        buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarTriggers.add(buttonRefresh);

        buttonFilter.setAction(cmFilter);
        buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarTriggers.add(buttonFilter);
        toolBarTriggers.add(jSeparator1);
        toolBarTriggers.add(buttonActions);

        jPanel2.add(toolBarTriggers);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        split.setBorder(null);
        split.setDividerLocation(350);
        split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(tableProps);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        statusBarProps.setShowFieldType(false);
        statusBarProps.setShowOpenTime(false);
        statusBarProps.setTable(tableProps);
        jPanel1.add(statusBarProps, java.awt.BorderLayout.SOUTH);

        split.setLeftComponent(jPanel1);

        textProp.setPreferredSize(new java.awt.Dimension(81, 150));
        split.setRightComponent(textProp);

        add(split, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed


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
    private javax.swing.JPopupMenu menuActions;
    private javax.swing.JSplitPane split;
    private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarProps;
    private ViewTable tableProps;
    private OrbadaSyntaxTextArea textProp;
    private javax.swing.JToolBar toolBarTriggers;
    // End of variables declaration//GEN-END:variables
  
}

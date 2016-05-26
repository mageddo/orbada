package pl.mpak.mpak.oracle.tune.gui;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.freezing.FreezeFactory;
import pl.mpak.orbada.oracle.gui.freezing.FreezeViewService;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.ISettings;
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
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
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
public class SqlPlanPanelView extends javax.swing.JPanel implements Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleTunePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private FreezeFactory freezeFactory;
  private ISettings settings;
  private Timer timer;
  private boolean ora10plus;
  private boolean schemaChanged;
  private boolean col_last_active_time;
  private boolean col_parsing_schema_name;
  
  /** 
   * @param accesibilities
   */
  public SqlPlanPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    freezeFactory = new FreezeFactory(accesibilities);
    ora10plus = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("Ora10+"));
    col_last_active_time = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("V%SQL.LAST_ACTIVE_TIME"));
    col_parsing_schema_name = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("V%SQL.PARSING_SCHEMA_NAME"));
    initComponents();
    init();
  }
  
  private void init() {
    syntaxSql.setDatabase(getDatabase());
    
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        refreshPlan();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tune-sql-plan-panel");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        splitPlan.setDividerLocation(settings.getValue("split-plan", (long)splitPlan.getDividerLocation()).intValue());
        splitObjects.setDividerLocation(settings.getValue("split-objects", (long)splitObjects.getWidth() -250).intValue());
        splitSql.setDividerLocation(settings.getValue("split-sql", (long)(splitSql.getWidth() *0.4f)).intValue());
      }
    });

    cmSelectSchema.setEnabled(ora10plus);

    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    tableObjects.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableObjects.getSelectedRow() >= 0) {
          try {
            tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
            cmFreezeObject.setEnabled(freezeFactory.canCreate(tableObjects.getQuery().fieldByName("object_type").getString()));
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    tableSql.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        timer.restart();
      }
    });

    tableSql.getQuery().setDatabase(getDatabase());
    try {
      if (ora10plus) {
        tableSql.addColumn(new QueryTableColumn("optimizer_mode", stringManager.getString("optimizer-mode"), 100));
        tableSql.addColumn(new QueryTableColumn("optimizer_cost", stringManager.getString("optimizer-cost"), 70));
        tableSql.addColumn(new QueryTableColumn("sql_fulltext", stringManager.getString("sql-fulltext"), 400));
        tableSql.addColumn(new QueryTableColumn("cpu_time", stringManager.getString("cpu-time"), 70));
        tableSql.addColumn(new QueryTableColumn("elapsed_time", stringManager.getString("elapsed-time"), 70));
        tableSql.addColumn(new QueryTableColumn("sharable_mem", stringManager.getString("sharable-mem"), 70));
        tableSql.addColumn(new QueryTableColumn("persistent_mem", stringManager.getString("persistent-mem"), 70));
        tableSql.addColumn(new QueryTableColumn("runtime_mem", stringManager.getString("runtime-mem"), 70));
        tableSql.addColumn(new QueryTableColumn("fetches", stringManager.getString("fetches"), 70));
        tableSql.addColumn(new QueryTableColumn("executions", stringManager.getString("executions"), 70));
        tableSql.addColumn(new QueryTableColumn("parse_calls", stringManager.getString("parse-calls"), 70));
        tableSql.addColumn(new QueryTableColumn("disk_reads", stringManager.getString("disk-reads"), 70));
        tableSql.addColumn(new QueryTableColumn("direct_writes", stringManager.getString("direct-writes"), 70));
        tableSql.addColumn(new QueryTableColumn("buffer_gets", stringManager.getString("buffer-gets"), 70));
        tableSql.addColumn(new QueryTableColumn("concurrency_wait_time", stringManager.getString("concurrency-wait-time"), 70));
        tableSql.addColumn(new QueryTableColumn("user_io_wait_time", stringManager.getString("user-io-wait-time"), 70));
        tableSql.addColumn(new QueryTableColumn("plsql_exec_time", stringManager.getString("plsql-exec-time"), 70));
        tableSql.addColumn(new QueryTableColumn("rows_processed", stringManager.getString("rows-processed"), 70));
        if (col_last_active_time) {
          tableSql.addColumn(new QueryTableColumn("last_active_time", stringManager.getString("last-active-time"), 120));
        }
      }
      else {
        tableSql.addColumn(new QueryTableColumn("optimizer_mode", stringManager.getString("optimizer-mode"), 100));
        tableSql.addColumn(new QueryTableColumn("optimizer_cost", stringManager.getString("optimizer-cost"), 70));
        tableSql.addColumn(new QueryTableColumn("sql_text", stringManager.getString("sql-fulltext"), 400));
        tableSql.addColumn(new QueryTableColumn("cpu_time", stringManager.getString("cpu-time"), 70));
        tableSql.addColumn(new QueryTableColumn("elapsed_time", stringManager.getString("elapsed-time"), 70));
        tableSql.addColumn(new QueryTableColumn("sharable_mem", stringManager.getString("sharable-mem"), 70));
        tableSql.addColumn(new QueryTableColumn("persistent_mem", stringManager.getString("persistent-mem"), 70));
        tableSql.addColumn(new QueryTableColumn("runtime_mem", stringManager.getString("runtime-mem"), 70));
        tableSql.addColumn(new QueryTableColumn("fetches", stringManager.getString("fetches"), 70));
        tableSql.addColumn(new QueryTableColumn("executions", stringManager.getString("executions"), 70));
        tableSql.addColumn(new QueryTableColumn("parse_calls", stringManager.getString("parse-calls"), 70));
        tableSql.addColumn(new QueryTableColumn("disk_reads", stringManager.getString("disk-reads"), 70));
        tableSql.addColumn(new QueryTableColumn("buffer_gets", stringManager.getString("buffer-gets"), 70));
        tableSql.addColumn(new QueryTableColumn("rows_processed", stringManager.getString("rows-processed"), 70));
        tableSql.addColumn(new QueryTableColumn("last_load_time", stringManager.getString("last-active-time"), 120));
      }
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("s.optimizer_mode", stringManager.getString("optimizer-mode"), new String[] {"'CHOOSE'", "'RULE'", "'ALL_ROWS'", "'FIRST_ROWS'"}));
      def.add(new SqlFilterDefComponent("s.optimizer_cost", stringManager.getString("optimizer-cost"), (String[])null));
      def.add(new SqlFilterDefComponent("s.cpu_time", stringManager.getString("cpu-time"), (String[])null));
      def.add(new SqlFilterDefComponent("s.executions", stringManager.getString("executions"), (String[])null));
      def.add(new SqlFilterDefComponent("s.rows_processed", stringManager.getString("rows-processed"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-tune-sqls-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    tableObjects.getQuery().setDatabase(getDatabase());
    try {
      tableObjects.addColumn(new QueryTableColumn("object_owner", stringManager.getString("schema-name"), 100));
      tableObjects.addColumn(new QueryTableColumn("object_name", stringManager.getString("object-name"), 150));
      tableObjects.addColumn(new QueryTableColumn("object_type", stringManager.getString("object-type"), 100));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    textSearch.addKeyListener(new TableRowChangeKeyListener(tableSql));
    
    SwingUtil.addAction(textSearch, cmSearch);
    SwingUtil.addAction(tableObjects, cmFreezeObject);
    new ComponentActionsAction(getDatabase(), tableSql, buttonActionsSql, menuActionsSql, "oracle-tune-sql-plan-actions");
    new ComponentActionsAction(getDatabase(), tableObjects, buttonActionsObjects, menuActionsObjects, "oracle-tune-sql-plan-objects-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textSearch.requestFocusInWindow();
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
      schemaChanged = true;
    }
  }
  
  private void expandAll(JTree tree, TreePath parent, boolean expand) {
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path, expand);
      }
    }
    if (expand) {
      tree.expandPath(parent);
    } else {
      tree.collapsePath(parent);
    }
  }

  private void fillTree(Query query, DefaultMutableTreeNode node) {
    try {
      if (query.eof()) {
        return;
      }
      long firstLevel = query.fieldByName("level").getLong();
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(query.fieldByName("plan_output").getString());
      node.add(newNode);
      query.next();
      while (!query.eof()) {
        if (query.fieldByName("level").getLong() > firstLevel) {
          fillTree(query, newNode);
        } else if (query.fieldByName("level").getLong() < firstLevel) {
          break;
        } else {
          newNode = new DefaultMutableTreeNode(query.fieldByName("plan_output").getString());
          node.add(newNode);
          query.next();
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private void refreshPlan() {
    int index = tableSql.getSelectedRow();
    if (index >= 0) {
      try {
        tableSql.getQuery().getRecord(index);
        java.awt.EventQueue.invokeLater(new Runnable() {
          String text = tableSql.getQuery().fieldByName(ora10plus ? "sql_fulltext" : "sql_text").getString();
          public void run() {
            syntaxSql.setText(text);
            syntaxSql.setChanged(false);
            syntaxSql.getEditorArea().setCaretPosition(0, true);
          }
        });
        int column = tableObjects.getSelectedColumn();
        tableObjects.getQuery().close();
        tableObjects.getQuery().setSqlText(Sql.getSqlObjectsList());
        tableObjects.getQuery().paramByName("ADDRESS").setString(tableSql.getQuery().fieldByName("ADDRESS").getString());
        tableObjects.getQuery().paramByName("HASH_VALUE").setString(tableSql.getQuery().fieldByName("HASH_VALUE").getString());
        tableObjects.getQuery().paramByName("CHILD_NUMBER").setString(tableSql.getQuery().fieldByName("CHILD_NUMBER").getString());
        tableObjects.getQuery().open();
        if (!tableObjects.getQuery().isEmpty()) {
          tableObjects.changeSelection(Math.min(index, tableObjects.getRowCount() -1), column, false, false);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            Query query = getDatabase().createQuery();
            try {
              query.setSqlText(Sql.getSqlPlanList(ora10plus));
              query.paramByName("ADDRESS").setString(tableSql.getQuery().fieldByName("ADDRESS").getString());
              query.paramByName("HASH_VALUE").setString(tableSql.getQuery().fieldByName("HASH_VALUE").getString());
              query.paramByName("CHILD_NUMBER").setString(tableSql.getQuery().fieldByName("CHILD_NUMBER").getString());
              query.open();
              DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
              fillTree(query, root);
              treePlan.setModel(new DefaultTreeModel(root));
              if (root.getChildCount() > 0) {
                treePlan.setSelectionInterval(0, 0);
              }
              expandAll(treePlan, new TreePath(root), true);
            } catch (Exception ex) {
              ExceptionUtil.processException(ex);
            } finally {
              query.close();
            }
          }
        });
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else {
      syntaxSql.setText("");
      syntaxSql.setChanged(false);
      treePlan.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
    }
  }

  private void refreshTableListTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refreshTableList();
      }
    });
  }
  
  private void refreshTableList() {
    if (!isVisible() || viewClosing) {
      return;
    }
    try {
      String objectName = null;
      if (tableSql.getQuery().isActive() && tableSql.getSelectedRow() >= 0) {
        tableSql.getQuery().getRecord(tableSql.getSelectedRow());
        objectName = tableSql.getQuery().fieldByName("address").getString();
      }
      refreshTableList(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refreshTableList(String objectName) {
    try {
      int column = tableSql.getSelectedColumn();
      int index = Math.max(0, tableSql.getSelectedRow());
      tableSql.getQuery().close();
      tableSql.getQuery().setSqlText(Sql.getSqlList(
              filter.getSqlText(), 
              ora10plus,
              col_last_active_time,
              col_parsing_schema_name));
      if (ora10plus) {
        tableSql.getQuery().paramByName("SCHEMA_NAME").setString(currentSchemaName);
      }
      tableSql.getQuery().paramByName("SEARCH").setString(textSearch.getText());
      tableSql.getQuery().open();
      if (!schemaChanged) {
        if (objectName != null && tableSql.getQuery().locate("address", new Variant(objectName))) {
          tableSql.changeSelection(tableSql.getQuery().getCurrentRecord().getIndex(), column);
        } else if (!tableSql.getQuery().isEmpty()) {
          tableSql.changeSelection(Math.min(index, tableSql.getRowCount() -1), column);
        }
      }
      else if (!tableSql.getQuery().isEmpty()) {
        tableSql.changeSelection(0, column);
      }
      schemaChanged = false;
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public void close() throws IOException {
    viewClosing = true;
    timer.cancel();
    settings.setValue("split-plan", (long)splitPlan.getDividerLocation());
    settings.setValue("split-objects", (long)splitObjects.getDividerLocation());
    settings.setValue("split-sql", (long)splitSql.getDividerLocation());
    tableSql.getQuery().close();
    tableObjects.getQuery().close();
    syntaxSql.setDatabase(null);
    syntaxSql = null;
    accesibilities = null;
    settings.store();
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
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    menuActionsSql = new javax.swing.JPopupMenu();
    cmSearch = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    menuActionsObjects = new javax.swing.JPopupMenu();
    splitPlan = new javax.swing.JSplitPane();
    splitObjects = new javax.swing.JSplitPane();
    jPanel3 = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSql = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActionsSql = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    panelSearch = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textSearch = new pl.mpak.sky.gui.swing.comp.TextField();
    buttonSearch = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel4 = new javax.swing.JPanel();
    jPanel5 = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    buttonActionsObjects = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
    buttonFreeze = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel6 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableObjects = new ViewTable();
    statusBarObjects = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel7 = new javax.swing.JPanel();
    splitSql = new javax.swing.JSplitPane();
    syntaxSql = new OrbadaSyntaxTextArea();
    jScrollPane3 = new javax.swing.JScrollPane();
    treePlan = new javax.swing.JTree();

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

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    cmSearch.setActionCommandKey("cmSearch");
    cmSearch.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmSearch.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_object16.gif")); // NOI18N
    cmSearch.setText(stringManager.getString("cmSearch-text")); // NOI18N
    cmSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSearchActionPerformed(evt);
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

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    splitPlan.setBorder(null);
    splitPlan.setDividerLocation(300);
    splitPlan.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splitPlan.setContinuousLayout(true);
    splitPlan.setOneTouchExpandable(true);

    splitObjects.setBorder(null);
    splitObjects.setDividerLocation(500);
    splitObjects.setContinuousLayout(true);
    splitObjects.setOneTouchExpandable(true);

    jPanel3.setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableSql);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableSql);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

    jPanel3.add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

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
    toolBar.add(jSeparator1);
    toolBar.add(buttonActionsSql);
    toolBar.add(jSeparator2);

    panelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 3));

    jLabel1.setDisplayedMnemonic('S');
    jLabel1.setLabelFor(textSearch);
    jLabel1.setText(stringManager.getString("search-dd")); // NOI18N
    panelSearch.add(jLabel1);

    textSearch.setPreferredSize(new java.awt.Dimension(120, 20));
    panelSearch.add(textSearch);

    toolBar.add(panelSearch);

    buttonSearch.setAction(cmSearch);
    buttonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonSearch);

    jPanel2.add(toolBar);

    jPanel3.add(jPanel2, java.awt.BorderLayout.NORTH);

    splitObjects.setLeftComponent(jPanel3);

    jPanel4.setLayout(new java.awt.BorderLayout());

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    buttonActionsObjects.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActionsObjects.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonActionsObjects);
    jToolBar1.add(jSeparator3);

    buttonFreeze.setAction(cmFreezeObject);
    buttonFreeze.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFreeze.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonFreeze);
    buttonFreeze.getAccessibleContext().setAccessibleParent(toolBar);

    jPanel5.add(jToolBar1);

    jPanel4.add(jPanel5, java.awt.BorderLayout.NORTH);

    jPanel6.setLayout(new java.awt.BorderLayout());

    tableObjects.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        tableObjectsMouseClicked(evt);
      }
    });
    jScrollPane2.setViewportView(tableObjects);

    jPanel6.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    statusBarObjects.setShowFieldType(false);
    statusBarObjects.setShowOpenTime(false);
    statusBarObjects.setTable(tableObjects);
    jPanel6.add(statusBarObjects, java.awt.BorderLayout.SOUTH);

    jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

    splitObjects.setRightComponent(jPanel4);

    splitPlan.setLeftComponent(splitObjects);

    jPanel7.setLayout(new java.awt.BorderLayout());

    splitSql.setBorder(null);
    splitSql.setDividerLocation(400);
    splitSql.setContinuousLayout(true);
    splitSql.setOneTouchExpandable(true);

    syntaxSql.setEditable(false);
    splitSql.setLeftComponent(syntaxSql);

    treePlan.setModel(null);
    treePlan.setRootVisible(false);
    treePlan.setRowHeight(18);
    treePlan.setShowsRootHandles(true);
    jScrollPane3.setViewportView(treePlan);

    splitSql.setRightComponent(jScrollPane3);

    jPanel7.add(splitSql, java.awt.BorderLayout.CENTER);

    splitPlan.setRightComponent(jPanel7);

    add(splitPlan, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refreshTableList();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableSql.getQuery().isActive()) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableList();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmSelectSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectSchemaActionPerformed
  Query query = getDatabase().createQuery();
  try {
    query.open(pl.mpak.orbada.oracle.Sql.getSchemaList());
    Vector<String> vl = QueryUtil.staticData("{schema_name}", query);
    Point point = buttonSelectSchema.getLocationOnScreen();
    point.y+= buttonSelectSchema.getHeight();
    SimpleSelectDialog.select((Window)SwingUtil.getWindowComponent(this), point.x, point.y, vl, vl.indexOf(currentSchemaName), new SimpleSelectDialog.CallBack() {
      public void selected(Object o) {
        setCurrentSchemaName(o.toString());
        refreshTableList();
      }
    });
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  } finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSearchActionPerformed
  refreshTableList();
}//GEN-LAST:event_cmSearchActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableObjects.getSelectedRow() >= 0) {
    try {
      tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
      String objectName = tableObjects.getQuery().fieldByName("object_name").getString();
      String objectType = tableObjects.getQuery().fieldByName("object_type").getString();
      FreezeViewService service = freezeFactory.createInstance(objectType, currentSchemaName, objectName);
      if (service != null) {
        accesibilities.createView(service);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void tableObjectsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableObjectsMouseClicked
  if (tableObjects.getSelectedRow() >= 0 && evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
    cmFreezeObject.performe();
  }
}//GEN-LAST:event_tableObjectsMouseClicked


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActionsObjects;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActionsSql;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreeze;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSearch;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSearch;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JPopupMenu menuActionsObjects;
  private javax.swing.JPopupMenu menuActionsSql;
  private javax.swing.JPanel panelSearch;
  private javax.swing.JSplitPane splitObjects;
  private javax.swing.JSplitPane splitPlan;
  private javax.swing.JSplitPane splitSql;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarObjects;
  private OrbadaSyntaxTextArea syntaxSql;
  private ViewTable tableObjects;
  private ViewTable tableSql;
  private pl.mpak.sky.gui.swing.comp.TextField textSearch;
  private javax.swing.JToolBar toolBar;
  private javax.swing.JTree treePlan;
  // End of variables declaration//GEN-END:variables
  
}

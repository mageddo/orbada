package pl.mpak.orbada.oracle.gui.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.freezing.FreezeFactory;
import pl.mpak.orbada.oracle.gui.freezing.FreezeViewService;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.oracle.util.SourceCreator;
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
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.LineMark;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class SearchSourcePanelView extends javax.swing.JPanel implements Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String currentObjectName;
  private String currentObjectType;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  private FreezeFactory freezeFactory;
  private ISettings settings;
  private HashMap<String, String> cacheSource;
  private LineMark lineMark;
  
  /** 
   * @param accesibilities
   */
  public SearchSourcePanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    freezeFactory = new FreezeFactory(accesibilities);
    cacheSource = new HashMap<String, String>();
    initComponents();
    init();
  }
  
  private void init() {
    syntaxSource.setDatabase(getDatabase());
    lineMark = new LineMark(-1, Color.getColor("activeCaption", new Color(204, 204, 255)), pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/line_point.gif"));

    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-search-source-panel");
    split.setDividerLocation(settings.getValue("split-location", (long)split.getDividerLocation()).intValue());

    checkRegExpr.setEnabled(StringUtil.toBoolean(accesibilities.getDatabase().getUserProperties().getProperty("Ora10+", "false")));
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    tableObjects.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableObjects.getSelectedRow() >= 0) {
          try {
            tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
            cmFreezeObject.setEnabled(freezeFactory.canCreate(tableObjects.getQuery().fieldByName("object_type").getString()));
            refreshSource();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableObjects.getQuery().setDatabase(getDatabase());
    tableObjects.getQuery().addQueryListener(getQueryListener());
    try {
      tableObjects.addColumn(new QueryTableColumn("schema_name", "Schemat", 100));
      tableObjects.addColumn(new QueryTableColumn("object_type", stringManager.getString("object-type"), 120));
      tableObjects.addColumn(new QueryTableColumn("object_name", stringManager.getString("object-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableObjects.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 70, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("VALID")) {
            ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
          }
          else if (StringUtil.nvl((String)value, "").equals("INVALID")) {
            ((JLabel)renderer).setForeground(Color.RED);
          }
        }
      })));
      tableObjects.addColumn(new QueryTableColumn("line", stringManager.getString("line"), 40));
      tableObjects.addColumn(new QueryTableColumn("text", stringManager.getString("source-part"), 1000));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("s.name", stringManager.getString("object-name"), (String[])null));
      def.add(new SqlFilterDefComponent("s.type", stringManager.getString("object-type"), new String[] {"('FUNCTION', 'PROCEDURE', 'TABLE', 'VIEW', 'PACKAGE', 'TYPE')"}));
      def.add(new SqlFilterDefComponent("o.status", stringManager.getString("status"), new String[] {"VALID", "INVALID"}));
      def.add(new SqlFilterDefComponent("o.status <> 'VALID'", stringManager.getString("only-invalid")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-search-sources-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    textSearch.addKeyListener(new TableRowChangeKeyListener(tableObjects));
    
    SwingUtil.addAction(textSearch, cmSearchObject);
    SwingUtil.addAction(textSearch, cmFreezeObject);
    SwingUtil.addAction(tableObjects, cmFreezeObject);
    SwingUtil.addAction(textSearch, cmCompile);
    SwingUtil.addAction(tableObjects, cmCompile);
    new ComponentActionsAction(getDatabase(), tableObjects, buttonActions, menuActions, "oracle-search-sources-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textSearch.requestFocusInWindow();
      }
    });
  }

  private QueryListener getQueryListener() {
    return new QueryListener() {
      public void beforeScroll(EventObject e) {
      }
      public void afterScroll(EventObject e) {
      }
      public void beforeOpen(EventObject e) {
        cacheSource.clear();
      }
      public void afterOpen(EventObject e) {
      }
      public void beforeClose(EventObject e) {
      }
      public void afterClose(EventObject e) {
        cacheSource.clear();
      }
      public void flushedPerformed(EventObject e) {
      }
      public void errorPerformed(EventObject e) {
      }
    };
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

  private void gotoPoint(final int line, final int column) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int offset = syntaxSource.getEditorArea().getLineStartOffset(line -1) +column -1;
        syntaxSource.getEditorArea().setCaretPosition(offset);
        syntaxSource.updateCaretPositionList();
        syntaxSource.removeLineMark(lineMark);
        lineMark.setLine(line);
        syntaxSource.setLineMark(lineMark);
      }
    });
  }

  private void refreshSource() {
    try {
      tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
      final String objectName = tableObjects.getQuery().fieldByName("object_name").getString();
      final String objectType = tableObjects.getQuery().fieldByName("object_type").getString();
      final int lineNo = tableObjects.getQuery().fieldByName("line").getInteger();
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            final String object = objectType +":" +objectName;
            if (!StringUtil.equals(currentObjectType +":" +currentObjectName, object)) {
              if (cacheSource.containsKey(object)) {
                syntaxSource.setDatabaseObject(currentSchemaName, objectType, objectName, cacheSource.get(object));
              }
              else {
                new SourceCreator(getDatabase(), syntaxSource).getSource(currentSchemaName, objectType, objectName);
                java.awt.EventQueue.invokeLater(new Runnable() {
                  public void run() {
                    cacheSource.put(object, syntaxSource.getText());
                    syntaxSource.setChanged(false);
                  }
                });
              }
              currentObjectType = objectType;
              currentObjectName = objectName;
            }
            gotoPoint(lineNo, 1);
          }
          catch (Exception ex) {
            ExceptionUtil.processException(ex);
            syntaxSource.setDatabaseObject("", "", "", "");
          }
        }
      });
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      syntaxSource.setDatabaseObject("", "", "", "");
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
      if (tableObjects.getQuery().isActive() && tableObjects.getSelectedRow() >= 0) {
        tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
        objectName = tableObjects.getQuery().fieldByName("object_name").getString();
      }
      refreshTableList(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refreshTableList(String objectName) {
    try {
      int column = tableObjects.getSelectedColumn();
      int index = Math.max(0, tableObjects.getSelectedRow());
      tableObjects.getQuery().close();
      if (checkRegExpr.isSelected()) {
        tableObjects.getQuery().setSqlText(Sql.getSourceSearchRegExpr(filter.getSqlText()));
      }
      else {
        tableObjects.getQuery().setSqlText(Sql.getSourceSearch(filter.getSqlText()));
      }
      if (!checkAllSchemas.isSelected()) {
        tableObjects.getQuery().paramByName("schema_name").setString(currentSchemaName);
      }
      else {
        tableObjects.getQuery().paramByName("schema_name").clearValue();
      }
      tableObjects.getQuery().paramByName("user_text").setString(textSearch.getText());
      if (!StringUtil.isEmpty(textSearch.getText())) {
        tableObjects.getQuery().open();
        if (objectName != null && tableObjects.getQuery().locate("object_name", new Variant(objectName))) {
          tableObjects.changeSelection(tableObjects.getQuery().getCurrentRecord().getIndex(), column);
        } else if (!tableObjects.getQuery().isEmpty()) {
          tableObjects.changeSelection(Math.min(index, tableObjects.getRowCount() -1), column);
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public void close() throws IOException {
    settings.setValue("split-location", (long)split.getDividerLocation());
    syntaxSource.setDatabase(null);
    viewClosing = true;
    tableObjects.getQuery().close();
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
        menuActions = new javax.swing.JPopupMenu();
        cmSearchObject = new pl.mpak.sky.gui.swing.Action();
        cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
        cmCompile = new pl.mpak.sky.gui.swing.Action();
        jPanel2 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        panelSearch = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textSearch = new pl.mpak.sky.gui.swing.comp.TextField();
        checkAllSchemas = new javax.swing.JCheckBox();
        checkRegExpr = new javax.swing.JCheckBox();
        buttonSearch = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonFreeze = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonCompile = new pl.mpak.sky.gui.swing.comp.ToolButton();
        split = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableObjects = new pl.mpak.orbada.gui.comps.table.ViewTable();
        statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
        syntaxSource = new pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea();

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

        cmSearchObject.setActionCommandKey("cmSearchObject");
        cmSearchObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        cmSearchObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_source.gif")); // NOI18N
        cmSearchObject.setText(stringManager.getString("cmSearchObject-text")); // NOI18N
        cmSearchObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSearchObjectActionPerformed(evt);
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
        toolBar.add(buttonActions);
        toolBar.add(jSeparator2);

        panelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 3));

        jLabel1.setDisplayedMnemonic('S');
        jLabel1.setLabelFor(textSearch);
        jLabel1.setText(stringManager.getString("search-dd")); // NOI18N
        panelSearch.add(jLabel1);

        textSearch.setPreferredSize(new java.awt.Dimension(120, 20));
        panelSearch.add(textSearch);

        checkAllSchemas.setText(stringManager.getString("checkAllSchemas-text")); // NOI18N
        panelSearch.add(checkAllSchemas);

        checkRegExpr.setText(stringManager.getString("checkRegExpr-text")); // NOI18N
        panelSearch.add(checkRegExpr);

        toolBar.add(panelSearch);

        buttonSearch.setAction(cmSearchObject);
        buttonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonSearch);

        buttonFreeze.setAction(cmFreezeObject);
        buttonFreeze.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonFreeze.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonFreeze);
        buttonFreeze.getAccessibleContext().setAccessibleParent(toolBar);

        buttonCompile.setAction(cmCompile);
        buttonCompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonCompile);

        jPanel2.add(toolBar);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        split.setBorder(null);
        split.setDividerLocation(300);
        split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);

        jPanel1.setLayout(new java.awt.BorderLayout());

        tableObjects.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableObjectsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableObjects);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        statusBar.setShowFieldType(false);
        statusBar.setShowOpenTime(false);
        statusBar.setTable(tableObjects);
        jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

        split.setTopComponent(jPanel1);

        syntaxSource.setEditable(false);
        split.setBottomComponent(syntaxSource);

        add(split, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refreshTableList();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableObjects.getQuery().isActive()) {
    refreshTableList();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableList();
}//GEN-LAST:event_cmRefreshActionPerformed

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
        refreshTableList();
      }
    });
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  } finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmSearchObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSearchObjectActionPerformed
  refreshTableList(null);
}//GEN-LAST:event_cmSearchObjectActionPerformed

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

private void cmCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCompileActionPerformed
  if (tableObjects.getSelectedRow() >= 0) {
    try {
      tableObjects.getQuery().getRecord(tableObjects.getSelectedRow());
      String objectName = tableObjects.getQuery().fieldByName("object_name").getString();
      String objectType = tableObjects.getQuery().fieldByName("object_type").getString();
      if (StringUtil.anyOfString(objectType, new String[] {"PROCEDURE", "FUNCTION", "PACKAGE", "PACKAGE BODY", "VIEW", "TYPE", "TYPE BODY", "MATERIALIZED VIEW", "JAVA SOURCE"}) >= 0) {
        if ("PACKAGE BODY".equals(objectType)) {
          objectType = "PACKAGE";
        }
        getDatabase().executeCommand(
          "ALTER " +objectType +" " +SQLUtil.createSqlName(currentSchemaName, objectName) +" COMPILE" +
          (OracleDbInfoProvider.instance.isDebugClauseNeeded(getDatabase()) ? " DEBUG" : "")
          );
        refreshTableList();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCompileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonCompile;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreeze;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSearch;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
    private javax.swing.JCheckBox checkAllSchemas;
    private javax.swing.JCheckBox checkRegExpr;
    private pl.mpak.sky.gui.swing.Action cmCompile;
    private pl.mpak.sky.gui.swing.Action cmFilter;
    private pl.mpak.sky.gui.swing.Action cmFreezeObject;
    private pl.mpak.sky.gui.swing.Action cmRefresh;
    private pl.mpak.sky.gui.swing.Action cmSearchObject;
    private pl.mpak.sky.gui.swing.Action cmSelectSchema;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu menuActions;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JSplitPane split;
    private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
    private pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea syntaxSource;
    private pl.mpak.orbada.gui.comps.table.ViewTable tableObjects;
    private pl.mpak.sky.gui.swing.comp.TextField textSearch;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
  
}

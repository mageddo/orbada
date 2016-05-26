/*
 * TableConstraintsPanel.java
 *
 * Created on 29 paŸdziernik 2007, 19:26
 */

package pl.mpak.orbada.oracle.gui.tables;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.DataTable;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.DropConstraintWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.EnableAllTableConstraintWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.EnableTableConstraintWizard;
import pl.mpak.orbada.oracle.gui.wizards.table.EnableTableRelatedConstraintWizard;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ITabObjectInfo;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dbinfo.OracleExceptionTableInfo;
import pl.mpak.orbada.oracle.gui.util.ExceptionTableComboBoxModel;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateIndexesFromConstraintsWizard;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.AlterTableNullWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintCheckWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintForeignKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintPrimaryKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class TableConstraintsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private boolean exceptInited = false;
  
  /** Creates new form TableConstraintsPanel 
   * @param accesibilities 
   */
  public TableConstraintsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableConstraints.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableConstraints.getSelectedRow();
        if (rowIndex >= 0 && tableConstraints.getQuery().isActive()) {
          try {
            tableConstraints.getQuery().getRecord(rowIndex);
            if (checkExceptView.isSelected()) {
              checkExceptView.setSelected(false);
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    comboExceptionTables.setModel(new ExceptionTableComboBoxModel(getDatabase()));
    comboExceptionTables.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
        if (value instanceof OracleExceptionTableInfo) {
          OracleExceptionTableInfo info = (OracleExceptionTableInfo)value;
          setText(SQLUtil.createSqlName(info.getTableOwner(), info.getName()));
        }
        return this;
      }
    });

    tableConstraints.getQuery().setDatabase(getDatabase());
    try {
      tableConstraints.addColumn(new QueryTableColumn("constraint_name", stringManager.getString("constraint-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableConstraints.addColumn(new QueryTableColumn("constraint_type", stringManager.getString("constraint-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableConstraints.addColumn(new QueryTableColumn("column_name", stringManager.getString("column-name"), 120));
      tableConstraints.addColumn(new QueryTableColumn("position", stringManager.getString("position"), 25));
      tableConstraints.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
      public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("DISABLED")) {
            ((JLabel)renderer).setForeground(Color.DARK_GRAY);
          }
        }
      })));
      tableConstraints.addColumn(new QueryTableColumn("validated", stringManager.getString("validated"), 70, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("VALIDATED")) {
            ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
          }
          else if (StringUtil.nvl((String)value, "").equals("NOT VALIDATED")) {
            ((JLabel)renderer).setForeground(Color.RED);
          }
        }
      })));
      tableConstraints.addColumn(new QueryTableColumn("r_schema_name", stringManager.getString("ref-schema-name"), 100));
      tableConstraints.addColumn(new QueryTableColumn("r_constraint_name", stringManager.getString("ref-constraint-name"), 150));
      tableConstraints.addColumn(new QueryTableColumn("delete_rule", stringManager.getString("delete-rule"), 70));
      tableConstraints.addColumn(new QueryTableColumn("search_condition", stringManager.getString("search-condition"), 150));
      tableConstraints.addColumn(new QueryTableColumn("generated", stringManager.getString("generated"), 100));
      tableConstraints.addColumn(new QueryTableColumn("deferrable", stringManager.getString("deferrable"), 80));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("cons.constraint_name", stringManager.getString("constraint-name"), (String[])null));
      def.add(new SqlFilterDefComponent("decode( cons.constraint_type, 'C', 'Check', 'P', 'Primary Key', 'R', 'Foreign Key', 'U', 'Unique Key', 'V', 'Check on view')", stringManager.getString("constraint-type"), new String[] {"", "'Check'", "'Primary Key'", "'Foreign Key'", "'Unique Key'", "'Check on view'"}));
      def.add(new SqlFilterDefComponent("cons.r_owner", stringManager.getString("ref-schema-name"), (String[])null));
      def.add(new SqlFilterDefComponent("cons.status = 'DISABLED'", stringManager.getString("disabled")));
      def.add(new SqlFilterDefComponent("cons.validated <> 'VALIDATED'", stringManager.getString("invalid")));
      def.add(new SqlFilterDefComponent("cons.generated = 'USER NAME'", stringManager.getString("only-named")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-table-constraints-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    
    tableExceptions.getQuery().setDatabase(getDatabase());
    statusBarExceptions.setTable(tableExceptions);
    
    new ComponentActionsAction(getDatabase(), tableConstraints, buttonActions, menuActions, "oracle-table-constraints-actions");
    new ComponentActionsAction(getDatabase(), tableExceptions, buttonExceptActions, menuExceptActions, "oracle-table-constraint-exceptions-actions");
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }

  private void initExceptionsView() {
    exceptInited = true;
    ((ExceptionTableComboBoxModel)comboExceptionTables.getModel()).change();
    comboExceptionTables.setEnabled(((ExceptionTableComboBoxModel)comboExceptionTables.getModel()).getSize() > 0);
    cmExceptRefresh.setEnabled(((ExceptionTableComboBoxModel)comboExceptionTables.getModel()).getSize() > 0);
    checkShowData.setEnabled(((ExceptionTableComboBoxModel)comboExceptionTables.getModel()).getSize() > 0);
    cmDeleteExceptedRecords.setEnabled(((ExceptionTableComboBoxModel)comboExceptionTables.getModel()).getSize() > 0);
  }
  
  private void refreshTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableConstraints.getQuery().close();
      tableConstraints.getQuery().setSqlText(Sql.getConstraintList(filter.getSqlText()));
      tableConstraints.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableConstraints.getQuery().paramByName("table_name").setString(currentTableName);
      tableConstraints.getQuery().open();
      if (!tableConstraints.getQuery().isEmpty()) {
        tableConstraints.changeSelection(0, 0);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentTableName = objectName;
      if (isVisible()) {
        refresh();
      }
      else {
        requestRefresh = true;
      }
    }
  }
  
  private void refreshExceptionsTask() {
    if (cmExceptRefresh.isEnabled()) {
      cmExceptRefresh.setEnabled(false);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          refreshExceptions();
        }
      });
    }
  }
  
  public void refreshExceptions() {
    if (comboExceptionTables.getSelectedItem() instanceof OracleExceptionTableInfo) {
      try {
        tableExceptions.getQuery().close();
        if (tableConstraints.getSelectedRow() >= 0) {
          tableConstraints.getQuery().getRecord(tableConstraints.getSelectedRow());
          if (checkExceptView.isSelected()) {
            OracleExceptionTableInfo info = (OracleExceptionTableInfo)comboExceptionTables.getSelectedItem();
            if (checkShowData.isSelected()) {
              tableExceptions.getQuery().setSqlText(
                "select *\n" +
                "  from " +SQLUtil.createSqlName(currentSchemaName, currentTableName) +"\n" +
                " where rowid in (\n" +
                "       select row_id\n" +
                "         from " +SQLUtil.createSqlName(info.getTableOwner(), info.getName()) +"\n" +
                "        where owner = :SCHEMA_NAME\n" +
                "          and table_name = :TABLE_NAME\n" +
                "          and constraint = :CONSTRAINT_NAME)");
              tableExceptions.getQuery().paramByName("schema_name").setString(currentSchemaName);
              tableExceptions.getQuery().paramByName("table_name").setString(currentTableName);
              tableExceptions.getQuery().paramByName("constraint_name").setString(tableConstraints.getQuery().fieldByName("constraint_name").getString());
            }
            else {
              tableExceptions.getQuery().setSqlText(
                "select *\n" +
                "  from " +SQLUtil.createSqlName(info.getTableOwner(), info.getName()) +"\n" +
                " where owner = :SCHEMA_NAME\n" +
                "   and table_name = :TABLE_NAME\n" +
                "   and constraint = :CONSTRAINT_NAME");
              tableExceptions.getQuery().paramByName("schema_name").setString(currentSchemaName);
              tableExceptions.getQuery().paramByName("table_name").setString(currentTableName);
              tableExceptions.getQuery().paramByName("constraint_name").setString(tableConstraints.getQuery().fieldByName("constraint_name").getString());
            }
            tableExceptions.getQuery().open();
            if (!tableExceptions.getQuery().isEmpty()) {
              tableExceptions.changeSelection(0, 0);
            }
          }
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    cmExceptRefresh.setEnabled(true);
  }
  
  @Override
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    closing = true;
    tableConstraints.getQuery().close();
    tableExceptions.getQuery().close();
    accesibilities = null;
  }
  
  public String getTitle() {
    return stringManager.getString("TableConstraintsPanel-title");
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
    cmDropConstraint = new pl.mpak.sky.gui.swing.Action();
    cmColumnNotNull = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintCheck = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintFK = new pl.mpak.sky.gui.swing.Action();
    cmCreateConstraintPK = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuColumnNotNull = new javax.swing.JMenuItem();
    menuCreateConstraintCheck = new javax.swing.JMenuItem();
    menuCreateConstraintFK = new javax.swing.JMenuItem();
    menuCreateConstraintPK = new javax.swing.JMenuItem();
    menuCreateIndexesFromConstraints = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuEnableDisable = new javax.swing.JMenuItem();
    menuEnableDisableAll = new javax.swing.JMenuItem();
    menuEnableDisableRelated = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    menuDropConstraint = new javax.swing.JMenuItem();
    cmEnableDisableAll = new pl.mpak.sky.gui.swing.Action();
    cmEnableDisableRelated = new pl.mpak.sky.gui.swing.Action();
    cmEnableDisableThis = new pl.mpak.sky.gui.swing.Action();
    cmExceptRefresh = new pl.mpak.sky.gui.swing.Action();
    menuExceptActions = new javax.swing.JPopupMenu();
    cmDeleteExceptedRecords = new pl.mpak.sky.gui.swing.Action();
    cmCreateIndexesFromConstraints = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    jPanel5 = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    toolBarConstraints = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator7 = new javax.swing.JToolBar.Separator();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton7 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator8 = new javax.swing.JToolBar.Separator();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton4 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton5 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator9 = new javax.swing.JToolBar.Separator();
    toolButton6 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableConstraints = new ViewTable();
    statusBarConstraints = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    panelExceptions = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    toolBarConstraints1 = new javax.swing.JToolBar();
    jPanel2 = new javax.swing.JPanel();
    checkExceptView = new javax.swing.JCheckBox();
    jSeparator6 = new javax.swing.JToolBar.Separator();
    buttonExceptRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonExceptActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    jPanel4 = new javax.swing.JPanel();
    comboExceptionTables = new javax.swing.JComboBox();
    checkShowData = new javax.swing.JCheckBox();
    jSeparator5 = new javax.swing.JToolBar.Separator();
    buttonDeleteExceptedRecords = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableExceptions = new DataTable();
    statusBarExceptions = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();

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

    cmDropConstraint.setActionCommandKey("cmDropConstraint");
    cmDropConstraint.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropConstraint.setText(stringManager.getString("cmDropConstraint-text")); // NOI18N
    cmDropConstraint.setTooltip(stringManager.getString("cmDropConstraint-hint")); // NOI18N
    cmDropConstraint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropConstraintActionPerformed(evt);
      }
    });

    cmColumnNotNull.setActionCommandKey("cmColumnNotNull");
    cmColumnNotNull.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/lock16.gif")); // NOI18N
    cmColumnNotNull.setText(stringManager.getString("cmColumnNotNull-text")); // NOI18N
    cmColumnNotNull.setTooltip(stringManager.getString("cmColumnNotNull-hint")); // NOI18N
    cmColumnNotNull.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColumnNotNullActionPerformed(evt);
      }
    });

    cmCreateConstraintCheck.setActionCommandKey("cmCreateConstraintCheck");
    cmCreateConstraintCheck.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/verify_document_16.gif")); // NOI18N
    cmCreateConstraintCheck.setText(stringManager.getString("cmCreateConstraintCheck-text")); // NOI18N
    cmCreateConstraintCheck.setTooltip(stringManager.getString("cmCreateConstraintCheck-hint")); // NOI18N
    cmCreateConstraintCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintCheckActionPerformed(evt);
      }
    });

    cmCreateConstraintFK.setActionCommandKey("cmCreateConstraintFK");
    cmCreateConstraintFK.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/foreign_key16.gif")); // NOI18N
    cmCreateConstraintFK.setText(stringManager.getString("cmCreateConstraintFK-text")); // NOI18N
    cmCreateConstraintFK.setTooltip(stringManager.getString("cmCreateConstraintFK-hint")); // NOI18N
    cmCreateConstraintFK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintFKActionPerformed(evt);
      }
    });

    cmCreateConstraintPK.setActionCommandKey("cmCreateConstraintPK");
    cmCreateConstraintPK.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/primary_key16.gif")); // NOI18N
    cmCreateConstraintPK.setText(stringManager.getString("cmCreateConstraintPK-text")); // NOI18N
    cmCreateConstraintPK.setTooltip(stringManager.getString("cmCreateConstraintPK-hint")); // NOI18N
    cmCreateConstraintPK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateConstraintPKActionPerformed(evt);
      }
    });

    menuColumnNotNull.setAction(cmColumnNotNull);
    menuActions.add(menuColumnNotNull);

    menuCreateConstraintCheck.setAction(cmCreateConstraintCheck);
    menuActions.add(menuCreateConstraintCheck);

    menuCreateConstraintFK.setAction(cmCreateConstraintFK);
    menuActions.add(menuCreateConstraintFK);

    menuCreateConstraintPK.setAction(cmCreateConstraintPK);
    menuActions.add(menuCreateConstraintPK);

    menuCreateIndexesFromConstraints.setAction(cmCreateIndexesFromConstraints);
    menuActions.add(menuCreateIndexesFromConstraints);
    menuActions.add(jSeparator2);

    menuEnableDisable.setAction(cmEnableDisableThis);
    menuActions.add(menuEnableDisable);

    menuEnableDisableAll.setAction(cmEnableDisableAll);
    menuActions.add(menuEnableDisableAll);

    menuEnableDisableRelated.setAction(cmEnableDisableRelated);
    menuActions.add(menuEnableDisableRelated);
    menuActions.add(jSeparator3);

    menuDropConstraint.setAction(cmDropConstraint);
    menuActions.add(menuDropConstraint);

    cmEnableDisableAll.setActionCommandKey("cmEnableDisableAll");
    cmEnableDisableAll.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enable_all.gif")); // NOI18N
    cmEnableDisableAll.setText(stringManager.getString("cmEnableDisableAll-text")); // NOI18N
    cmEnableDisableAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEnableDisableAllActionPerformed(evt);
      }
    });

    cmEnableDisableRelated.setActionCommandKey("cmEnableDisableRelated");
    cmEnableDisableRelated.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enable_all.gif")); // NOI18N
    cmEnableDisableRelated.setText(stringManager.getString("cmEnableDisableRelated-text")); // NOI18N
    cmEnableDisableRelated.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEnableDisableRelatedActionPerformed(evt);
      }
    });

    cmEnableDisableThis.setActionCommandKey("cmEnableDisableThis");
    cmEnableDisableThis.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmEnableDisableThis.setText(stringManager.getString("cmEnableDisableThis-text")); // NOI18N
    cmEnableDisableThis.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEnableDisableThisActionPerformed(evt);
      }
    });

    cmExceptRefresh.setActionCommandKey("cmExceptRefresh");
    cmExceptRefresh.setEnabled(false);
    cmExceptRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmExceptRefresh.setText(stringManager.getString("cmExceptRefresh-text")); // NOI18N
    cmExceptRefresh.setTooltip(stringManager.getString("cmExceptRefresh-hint")); // NOI18N
    cmExceptRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmExceptRefreshActionPerformed(evt);
      }
    });

    cmDeleteExceptedRecords.setActionCommandKey("cmDeleteExceptedRecords");
    cmDeleteExceptedRecords.setEnabled(false);
    cmDeleteExceptedRecords.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDeleteExceptedRecords.setText(stringManager.getString("cmDeleteExceptedRecords-text")); // NOI18N
    cmDeleteExceptedRecords.setTooltip(stringManager.getString("cmDeleteExceptedRecords-hint")); // NOI18N
    cmDeleteExceptedRecords.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteExceptedRecordsActionPerformed(evt);
      }
    });

    cmCreateIndexesFromConstraints.setText(stringManager.getString("cmCreateIndexesFromConstraints-text")); // NOI18N
    cmCreateIndexesFromConstraints.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateIndexesFromConstraintsActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    split.setBorder(null);
    split.setDividerLocation(300);
    split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel5.setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarConstraints.setFloatable(false);
    toolBarConstraints.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(buttonFilter);
    toolBarConstraints.add(jSeparator7);

    toolButton1.setAction(cmEnableDisableThis);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton1);

    toolButton7.setAction(cmEnableDisableAll);
    toolButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton7);
    toolBarConstraints.add(jSeparator8);

    toolButton2.setAction(cmColumnNotNull);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton2);

    toolButton3.setAction(cmCreateConstraintCheck);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton3);

    toolButton4.setAction(cmCreateConstraintFK);
    toolButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton4);

    toolButton5.setAction(cmCreateConstraintPK);
    toolButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton5);
    toolBarConstraints.add(jSeparator9);

    toolButton6.setAction(cmDropConstraint);
    toolButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints.add(toolButton6);
    toolBarConstraints.add(jSeparator1);
    toolBarConstraints.add(buttonActions);

    jPanel1.add(toolBarConstraints);

    jPanel5.add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableConstraints);

    jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarConstraints.setShowFieldType(false);
    statusBarConstraints.setShowOpenTime(false);
    statusBarConstraints.setTable(tableConstraints);
    jPanel5.add(statusBarConstraints, java.awt.BorderLayout.PAGE_END);

    split.setTopComponent(jPanel5);

    panelExceptions.setLayout(new java.awt.BorderLayout());

    jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarConstraints1.setFloatable(false);
    toolBarConstraints1.setRollover(true);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 3));

    checkExceptView.setText(stringManager.getString("checkExceptView-text")); // NOI18N
    checkExceptView.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        checkExceptViewItemStateChanged(evt);
      }
    });
    jPanel2.add(checkExceptView);

    toolBarConstraints1.add(jPanel2);
    toolBarConstraints1.add(jSeparator6);

    buttonExceptRefresh.setAction(cmExceptRefresh);
    buttonExceptRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonExceptRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints1.add(buttonExceptRefresh);
    toolBarConstraints1.add(buttonExceptActions);
    toolBarConstraints1.add(jSeparator4);

    jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 3));

    comboExceptionTables.setPreferredSize(new java.awt.Dimension(150, 22));
    jPanel4.add(comboExceptionTables);

    checkShowData.setSelected(true);
    checkShowData.setText(stringManager.getString("checkShowData-text")); // NOI18N
    checkShowData.setEnabled(false);
    checkShowData.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        checkShowDataItemStateChanged(evt);
      }
    });
    jPanel4.add(checkShowData);

    toolBarConstraints1.add(jPanel4);
    toolBarConstraints1.add(jSeparator5);

    buttonDeleteExceptedRecords.setAction(cmDeleteExceptedRecords);
    buttonDeleteExceptedRecords.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDeleteExceptedRecords.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarConstraints1.add(buttonDeleteExceptedRecords);

    jPanel3.add(toolBarConstraints1);

    panelExceptions.add(jPanel3, java.awt.BorderLayout.NORTH);

    jScrollPane2.setViewportView(tableExceptions);

    panelExceptions.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    statusBarExceptions.setShowFieldType(false);
    statusBarExceptions.setShowOpenTime(false);
    panelExceptions.add(statusBarExceptions, java.awt.BorderLayout.PAGE_END);

    split.setRightComponent(panelExceptions);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmCreateConstraintPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintPKActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintPrimaryKeyWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
  }
}//GEN-LAST:event_cmCreateConstraintPKActionPerformed

private void cmCreateConstraintFKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintFKActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintForeignKeyWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
  }
}//GEN-LAST:event_cmCreateConstraintFKActionPerformed

private void cmCreateConstraintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateConstraintCheckActionPerformed
  try {
    SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(getDatabase(), currentSchemaName, currentTableName), true);
    refresh();
  } catch (Exception ex) {
    MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
  }
}//GEN-LAST:event_cmCreateConstraintCheckActionPerformed

private void cmColumnNotNullActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColumnNotNullActionPerformed
  try {
    SqlCodeWizardDialog.show(new AlterTableNullWizardPanel(getDatabase(), currentSchemaName, currentTableName, null, "MODIFY", null), true);
    refresh();
  } catch (Exception ex) {
    MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
  }
}//GEN-LAST:event_cmColumnNotNullActionPerformed

  private void cmDropConstraintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropConstraintActionPerformed
  if (tableConstraints.getSelectedRow() >= 0) {
    try {
      tableConstraints.getQuery().getRecord(tableConstraints.getSelectedRow());
      String constraintName = tableConstraints.getQuery().fieldByName("constraint_name").getString();
      if (SqlCodeWizardDialog.show(new DropConstraintWizard(getDatabase(), currentSchemaName, currentTableName, constraintName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropConstraintActionPerformed

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

private void cmEnableDisableAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEnableDisableAllActionPerformed
  if (SqlCodeWizardDialog.show(new EnableAllTableConstraintWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmEnableDisableAllActionPerformed

private void cmEnableDisableRelatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEnableDisableRelatedActionPerformed
  if (SqlCodeWizardDialog.show(new EnableTableRelatedConstraintWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmEnableDisableRelatedActionPerformed

private void cmEnableDisableThisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEnableDisableThisActionPerformed
  if (tableConstraints.getSelectedRow() >= 0) {
    try {
      tableConstraints.getQuery().getRecord(tableConstraints.getSelectedRow());
      String constraintName = tableConstraints.getQuery().fieldByName("constraint_name").getString();
      if (SqlCodeWizardDialog.show(new EnableTableConstraintWizard(getDatabase(), currentSchemaName, currentTableName, constraintName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmEnableDisableThisActionPerformed

private void cmExceptRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExceptRefreshActionPerformed
  refreshExceptions();
}//GEN-LAST:event_cmExceptRefreshActionPerformed

private void checkShowDataItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkShowDataItemStateChanged
  refreshExceptions();
}//GEN-LAST:event_checkShowDataItemStateChanged

private void checkExceptViewItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkExceptViewItemStateChanged
  if (!exceptInited) {
    initExceptionsView();
  }
  if (cmExceptRefresh.isEnabled()) {
    refreshExceptionsTask();
  }
}//GEN-LAST:event_checkExceptViewItemStateChanged

private void cmDeleteExceptedRecordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteExceptedRecordsActionPerformed
  if (comboExceptionTables.getSelectedItem() instanceof OracleExceptionTableInfo) {
    try {
      if (tableConstraints.getSelectedRow() >= 0 && checkExceptView.isSelected()) {
        if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("TableConstraintsPanel-delete-excepted-records-q"), ModalResult.YESNO) == ModalResult.YES) {
          tableConstraints.getQuery().getRecord(tableConstraints.getSelectedRow());
          OracleExceptionTableInfo info = (OracleExceptionTableInfo)comboExceptionTables.getSelectedItem();
          Command command = getDatabase().createCommand();
          command.setSqlText(
            "delete\n" +
            "  from " +SQLUtil.createSqlName(currentSchemaName, currentTableName) +"\n" +
            " where rowid in (\n" +
            "       select row_id\n" +
            "         from " +SQLUtil.createSqlName(info.getTableOwner(), info.getName()) +"\n" +
            "        where owner = :SCHEMA_NAME\n" +
            "          and table_name = :TABLE_NAME\n" +
            "          and constraint = :CONSTRAINT_NAME)");
          command.paramByName("schema_name").setString(currentSchemaName);
          command.paramByName("table_name").setString(currentTableName);
          command.paramByName("constraint_name").setString(tableConstraints.getQuery().fieldByName("constraint_name").getString());
          command.execute();
          refreshExceptions();
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteExceptedRecordsActionPerformed

private void cmCreateIndexesFromConstraintsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateIndexesFromConstraintsActionPerformed
  if (tableConstraints.getSelectedRow() >= 0) {
    try {
      if (SqlCodeWizardDialog.show(new CreateIndexesFromConstraintsWizard(getDatabase(), currentSchemaName, currentTableName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCreateIndexesFromConstraintsActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDeleteExceptedRecords;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonExceptActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonExceptRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private javax.swing.JCheckBox checkExceptView;
  private javax.swing.JCheckBox checkShowData;
  private pl.mpak.sky.gui.swing.Action cmColumnNotNull;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintCheck;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintFK;
  private pl.mpak.sky.gui.swing.Action cmCreateConstraintPK;
  private pl.mpak.sky.gui.swing.Action cmCreateIndexesFromConstraints;
  private pl.mpak.sky.gui.swing.Action cmDeleteExceptedRecords;
  private pl.mpak.sky.gui.swing.Action cmDropConstraint;
  private pl.mpak.sky.gui.swing.Action cmEnableDisableAll;
  private pl.mpak.sky.gui.swing.Action cmEnableDisableRelated;
  private pl.mpak.sky.gui.swing.Action cmEnableDisableThis;
  private pl.mpak.sky.gui.swing.Action cmExceptRefresh;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JComboBox comboExceptionTables;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JToolBar.Separator jSeparator5;
  private javax.swing.JToolBar.Separator jSeparator6;
  private javax.swing.JToolBar.Separator jSeparator7;
  private javax.swing.JToolBar.Separator jSeparator8;
  private javax.swing.JToolBar.Separator jSeparator9;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuColumnNotNull;
  private javax.swing.JMenuItem menuCreateConstraintCheck;
  private javax.swing.JMenuItem menuCreateConstraintFK;
  private javax.swing.JMenuItem menuCreateConstraintPK;
  private javax.swing.JMenuItem menuCreateIndexesFromConstraints;
  private javax.swing.JMenuItem menuDropConstraint;
  private javax.swing.JMenuItem menuEnableDisable;
  private javax.swing.JMenuItem menuEnableDisableAll;
  private javax.swing.JMenuItem menuEnableDisableRelated;
  private javax.swing.JPopupMenu menuExceptActions;
  private javax.swing.JPanel panelExceptions;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarConstraints;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarExceptions;
  private ViewTable tableConstraints;
  private DataTable tableExceptions;
  private javax.swing.JToolBar toolBarConstraints;
  private javax.swing.JToolBar toolBarConstraints1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton4;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton5;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton6;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton7;
  // End of variables declaration//GEN-END:variables
  
}

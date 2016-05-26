package pl.mpak.orbada.oracle.gui.packages;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.packages.cm.PageDownAction;
import pl.mpak.orbada.oracle.gui.packages.cm.PageUpAction;
import pl.mpak.orbada.oracle.gui.wizards.CallObjectWizard;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.SwingUtil.Color;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class PackageMethodsPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentPackageName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  
  /** Creates new form TableColumns
   * @param accesibilities
   */
  public PackageMethodsPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    tableMethods.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      private int lastRowIndex = -1;
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableMethods.getSelectedRow();
        if (lastRowIndex != rowIndex && rowIndex >= 0 && tableMethods.getQuery().isActive()) {
          lastRowIndex = rowIndex;
          try {
            tableMethods.getQuery().getRecord(rowIndex);
            tableArguments.getQuery().close();
            tableArguments.getQuery().paramByName("method_name").setString(tableMethods.getQuery().fieldByName("method_name").getString());
            tableArguments.getQuery().paramByName("overload").setString(tableMethods.getQuery().fieldByName("overload").getString());
            tableArguments.getQuery().open();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableMethods.getQuery().setDatabase(getDatabase());
    try {
      tableMethods.addColumn(new QueryTableColumn("method_type", stringManager.getString("method-type"), 100));
      tableMethods.addColumn(new QueryTableColumn("method_name", stringManager.getString("method-name"), 180, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableMethods.addColumn(new QueryTableColumn("overload", stringManager.getString("overload"), 60));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("attr_name", stringManager.getString("attribute-name"), (String[])null));
      def.add(new SqlFilterDefComponent("attr_type_name", stringManager.getString("attribute-type"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-package-methods-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    tableArguments.getQuery().setDatabase(getDatabase());
    try {
      tableArguments.getQuery().setSqlText(Sql.getPackageMethodArgumentList(null));
      tableArguments.addColumn(new QueryTableColumn("overload", stringManager.getString("overload"), 30));
      tableArguments.addColumn(new QueryTableColumn("position", stringManager.getString("pos"), 30));
      tableArguments.addColumn(new QueryTableColumn("in_out", stringManager.getString("in-out"), 70));
      tableArguments.addColumn(new QueryTableColumn("argument_name", stringManager.getString("argument-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableArguments.addColumn(new QueryTableColumn("data_type", stringManager.getString("data-type"), 150, new QueryTableCellRenderer(Color.GREEN)));
      tableArguments.addColumn(new QueryTableColumn("default_value", stringManager.getString("default-value"), 200));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    SwingUtil.addAction(tableMethods, cmGotoSource);
    SwingUtil.addAction(tableMethods, cmCallPackageMethod);
    new ComponentActionsAction(getDatabase(), tableMethods, buttonActions, menuActions, "oracle-package-methods-actions");
    SwingUtil.addAction(tableMethods, new PageUpAction(this));
    SwingUtil.addAction(tableMethods, new PageDownAction(this));
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("PackageMethodsPanel-title");
  }
  
  @Override
  public boolean requestFocusInWindow() {
    return tableMethods.requestFocusInWindow();
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
      String attrName = null;
      requestRefresh = false;
      if (tableMethods.getQuery().isActive() && tableMethods.getSelectedRow() >= 0) {
        tableMethods.getQuery().getRecord(tableMethods.getSelectedRow());
        attrName = tableMethods.getQuery().fieldByName("method_name").getString();
      }
      tableArguments.getQuery().close();
      tableMethods.getQuery().close();
      tableMethods.getQuery().setSqlText(Sql.getPackageMethodList(filter.getSqlText()));
      tableMethods.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableMethods.getQuery().paramByName("package_name").setString(currentPackageName);
      tableMethods.getQuery().open();
      tableArguments.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableArguments.getQuery().paramByName("package_name").setString(currentPackageName);
      if (!tableMethods.getQuery().isEmpty()) {
        if (attrName != null && tableMethods.getQuery().locate("method_name", new Variant(attrName))) {
          tableMethods.changeSelection(tableMethods.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableMethods.changeSelection(0, 0);
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentPackageName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentPackageName = objectName;
      if (isVisible()) {
        refresh();
      }
      else {
        requestRefresh = true;
      }
    }
  }
  
  @Override
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    closing = true;
    tableMethods.getQuery().close();
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
    cmGotoSource = new pl.mpak.sky.gui.swing.Action();
    cmCallPackageMethod = new pl.mpak.sky.gui.swing.Action();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarColumns = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSplitPane1 = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableMethods = new ViewTable();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableArguments = new ViewTable();

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

    cmGotoSource.setActionCommandKey("cmGotoSource");
    cmGotoSource.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmGotoSource.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/descending.gif")); // NOI18N
    cmGotoSource.setText(stringManager.getString("cmGotoSource-text")); // NOI18N
    cmGotoSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmGotoSourceActionPerformed(evt);
      }
    });

    cmCallPackageMethod.setActionCommandKey("cmCallPackageMethod");
    cmCallPackageMethod.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/call.gif")); // NOI18N
    cmCallPackageMethod.setText(stringManager.getString("cmCallPackageMethod-text")); // NOI18N
    cmCallPackageMethod.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCallPackageMethodActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableMethods);
    add(statusBar, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarColumns.setFloatable(false);
    toolBarColumns.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(buttonFilter);
    toolBarColumns.add(jSeparator1);

    toolButton1.setAction(cmGotoSource);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton1);

    toolButton2.setAction(cmCallPackageMethod);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarColumns.add(toolButton2);
    toolBarColumns.add(buttonActions);

    jPanel1.add(toolBarColumns);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jSplitPane1.setBorder(null);
    jSplitPane1.setDividerLocation(300);
    jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setContinuousLayout(true);

    tableMethods.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        tableMethodsMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(tableMethods);

    jSplitPane1.setLeftComponent(jScrollPane1);

    jScrollPane2.setViewportView(tableArguments);

    jSplitPane1.setBottomComponent(jScrollPane2);

    add(jSplitPane1, java.awt.BorderLayout.CENTER);
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

private void cmGotoSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGotoSourceActionPerformed
  if (tableMethods.getSelectedRow() >= 0) {
    try {
      tableMethods.getQuery().getRecord(tableMethods.getSelectedRow());
      PackagePartTabbedPane pptp = null;
      PackageSourcePanel source = null;
      PackageTabbedPane tp = (PackageTabbedPane)SwingUtil.getOwnerComponent(PackageTabbedPane.class, this);
      for (int i=0; i<tp.getTabCount(); i++) {
        Component c = tp.getComponentAt(i);
        if (c instanceof PackagePartTabbedPane) {
          pptp = (PackagePartTabbedPane)c;
          tp.setSelectedComponent(pptp);
          if ("PACKAGE BODY".equalsIgnoreCase(pptp.getObjectType())) {
            for (int j=0; j<pptp.getTabCount(); j++) {
              Component tps = pptp.getComponentAt(j);
              if (tps instanceof PackageSourcePanel) {
                source = (PackageSourcePanel)tps;
                break;
              }
            }
          }
        }
        if (source != null) {
          break;
        }
      }
      if (source != null) {
        tp.setSelectedComponent(pptp);
        pptp.setSelectedComponent(source);
        source.gotoMethod(
          tableMethods.getQuery().fieldByName("method_name").getString(),
          tableMethods.getQuery().fieldByName("method_type").getString());
        final PackageSourcePanel focus = source;
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            focus.requestFocusInWindow();
          }
        });
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmGotoSourceActionPerformed

private void tableMethodsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMethodsMouseClicked
  if (tableMethods.getSelectedRow() >= 0 && evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
    cmGotoSource.performe();
  }
}//GEN-LAST:event_tableMethodsMouseClicked

private void cmCallPackageMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCallPackageMethodActionPerformed
  if (tableMethods.getSelectedRow() >= 0) {
    try {
      tableMethods.getQuery().getRecord(tableMethods.getSelectedRow());
      SqlCodeWizardDialog.show(new CallObjectWizard(getDatabase(), currentSchemaName, currentPackageName +"." +tableMethods.getQuery().fieldByName("method_name").getString(), tableMethods.getQuery().fieldByName("overload").getString()), true);
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmCallPackageMethodActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmCallPackageMethod;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmGotoSource;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JPopupMenu menuActions;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableArguments;
  private ViewTable tableMethods;
  private javax.swing.JToolBar toolBarColumns;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  // End of variables declaration//GEN-END:variables
  
}

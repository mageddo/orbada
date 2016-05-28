package pl.mpak.orbada.oracle.gui.directories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.CreateDirectoryWizard;
import pl.mpak.orbada.oracle.gui.wizards.DropDirectoryWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
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
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class DirectoriesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  public DirectoriesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = OracleDbInfoProvider.getCurrentSchema(getDatabase());
    tableDirs.getQuery().setDatabase(getDatabase());
    try {
      tableDirs.addColumn(new QueryTableColumn("directory_name", stringManager.getString("directory-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableDirs.addColumn(new QueryTableColumn("created", stringManager.getString("created"), 120));
      tableDirs.addColumn(new QueryTableColumn("status", stringManager.getString("status"), 60, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (StringUtil.nvl((String)value, "").equals("VALID")) {
            ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
          }
          else if (StringUtil.nvl((String)value, "").equals("INVALID")) {
            ((JLabel)renderer).setForeground(Color.RED);
          }
        }
      })));
      tableDirs.addColumn(new QueryTableColumn("read_for", stringManager.getString("read-for"), 100));
      tableDirs.addColumn(new QueryTableColumn("read", stringManager.getString("read"), 40));
      tableDirs.addColumn(new QueryTableColumn("write_for", stringManager.getString("write-for"), 100));
      tableDirs.addColumn(new QueryTableColumn("write", stringManager.getString("write"), 40));
      tableDirs.addColumn(new QueryTableColumn("directory_path", stringManager.getString("directory-path"), 350));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("directory_name", stringManager.getString("directory-name"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "oracle-directories-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableDirs, buttonActions, menuActions, "oracle-directories-actions");
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
      String objectName = null;
      if (tableDirs.getQuery().isActive() && tableDirs.getSelectedRow() >= 0) {
        tableDirs.getQuery().getRecord(tableDirs.getSelectedRow());
        objectName = tableDirs.getQuery().fieldByName("directory_name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = tableDirs.getSelectedColumn();
      int index = Math.max(0, tableDirs.getSelectedRow());
      tableDirs.getQuery().close();
      tableDirs.getQuery().setSqlText(Sql.getDirectoryList(filter.getSqlText()));
      tableDirs.getQuery().paramByName("schema_name").setString(currentSchemaName);
      tableDirs.getQuery().open();
      if (objectName != null && tableDirs.getQuery().locate("directory_name", new Variant(objectName))) {
        tableDirs.changeSelection(tableDirs.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableDirs.getQuery().isEmpty()) {
        tableDirs.changeSelection(Math.min(index, tableDirs.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    viewClosing = true;
    tableDirs.getQuery().close();
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
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    menuCreateDirectory = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    menuDropDirectory = new javax.swing.JMenuItem();
    cmCreateDirectory = new pl.mpak.sky.gui.swing.Action();
    cmDropDirectory = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableDirs = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
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

    cmSelectSchema.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/users16.gif")); // NOI18N
    cmSelectSchema.setText(stringManager.getString("cmSelectSchema-text")); // NOI18N
    cmSelectSchema.setTooltip(stringManager.getString("cmSelectSchema-hint")); // NOI18N
    cmSelectSchema.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectSchemaActionPerformed(evt);
      }
    });

    menuCreateDirectory.setAction(cmCreateDirectory);
    menuActions.add(menuCreateDirectory);
    menuActions.add(jSeparator2);

    menuDropDirectory.setAction(cmDropDirectory);
    menuActions.add(menuDropDirectory);

    cmCreateDirectory.setActionCommandKey("cmCreateDirectory");
    cmCreateDirectory.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/directory.gif")); // NOI18N
    cmCreateDirectory.setText(stringManager.getString("cmCreateDirectory-text")); // NOI18N
    cmCreateDirectory.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateDirectoryActionPerformed(evt);
      }
    });

    cmDropDirectory.setActionCommandKey("cmDropDirectory");
    cmDropDirectory.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif")); // NOI18N
    cmDropDirectory.setText(stringManager.getString("cmDropDirectory-text")); // NOI18N
    cmDropDirectory.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropDirectoryActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableDirs);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableDirs);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

    add(jPanel1, java.awt.BorderLayout.CENTER);

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

    jPanel2.add(toolBar);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableDirs.getQuery().isActive()) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
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
        refresh();
      }
    });
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  } finally {
    query.close();
  }
}//GEN-LAST:event_cmSelectSchemaActionPerformed

private void cmCreateDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateDirectoryActionPerformed
  if (SqlCodeWizardDialog.show(new CreateDirectoryWizard(getDatabase()), true) != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateDirectoryActionPerformed

private void cmDropDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropDirectoryActionPerformed
  if (tableDirs.getSelectedRow() >= 0) {
    try {
      tableDirs.getQuery().getRecord(tableDirs.getSelectedRow());
      String directoryName = tableDirs.getQuery().fieldByName("directory_name").getString();
      if (SqlCodeWizardDialog.show(new DropDirectoryWizard(getDatabase(), directoryName), true) != null) {
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropDirectoryActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmCreateDirectory;
  private pl.mpak.sky.gui.swing.Action cmDropDirectory;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuCreateDirectory;
  private javax.swing.JMenuItem menuDropDirectory;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableDirs;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

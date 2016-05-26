/*
 * DerbyDbObjectPanelView.java
 *
 * Created on 28 paŸdziernik 2007, 16:45
 */

package pl.mpak.orbada.derbydb.files;

import java.awt.Point;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.util.SimpleSelectDialog;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class JarFilesPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaDerbyDbPlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName;
  private String tabTitle;
  private SqlFilter filter;
  private boolean viewClosing = false;
  
  /** Creates new form DerbyDbObjectPanelView
   * @param accesibilities
   */
  public JarFilesPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    tabTitle = this.accesibilities.getTabTitle();
    initComponents();
    init();
  }
  
  private void init() {
    currentSchemaName = getDatabase().getUserName().toUpperCase();
    
    tableFiles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        String fileName = "";
        int rowIndex = tableFiles.getSelectedRow();
        if (rowIndex >= 0 && tableFiles.getQuery().isActive()) {
          try {
            tableFiles.getQuery().getRecord(rowIndex);
            fileName = tableFiles.getQuery().fieldByName("filename").getString();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableFiles.getQuery().setDatabase(getDatabase());
    tableFiles.addColumn(new QueryTableColumn("filename", stringManager.getString("JarFilesPanelView-file-name"), 250));
    SqlFilterDef def = new SqlFilterDef();
    def.add(new SqlFilterDefComponent("f.filename", stringManager.getString("JarFilesPanelView-file-name"), (String[])null));
    filter = new SqlFilter(
      accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "derbydb-files-filter"),
      cmFilter, buttonFilter, 
      def);
    new ComponentActionsAction(getDatabase(), tableFiles, buttonActions, menuActions, "derbydb-jarfile-actions");
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableFiles.requestFocusInWindow();
      }
    });
  }
  
  public void setCurrentSchemaName(String schemaName) {
    if (!currentSchemaName.equals(schemaName)) {
      currentSchemaName = schemaName;
      if (!currentSchemaName.equalsIgnoreCase(getDatabase().getUserName())) {
        accesibilities.setTabTitle(tabTitle +" (" +currentSchemaName +")");
      }
      else {
        accesibilities.setTabTitle(tabTitle);
      }
    }
  }
  
  public void refresh() {
    if (!isVisible() || viewClosing) {
      return;
    }
    try {
      String fileName = null;
      if (tableFiles.getQuery().isActive() && tableFiles.getSelectedRow() >= 0) {
        tableFiles.getQuery().getRecord(tableFiles.getSelectedRow());
        fileName = tableFiles.getQuery().fieldByName("FILENAME").getString();
      }
      refresh(fileName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int index = Math.max(0, tableFiles.getSelectedRow());
      tableFiles.getQuery().close();
      tableFiles.getQuery().setSqlText(DerbyDbSql.getFileList(filter.getSqlText()));
      tableFiles.getQuery().paramByName("schemaname").setString(currentSchemaName);
      tableFiles.getQuery().open();
      if (objectName != null && tableFiles.getQuery().locate("FILENAME", new Variant(objectName))) {
        tableFiles.changeSelection(tableFiles.getQuery().getCurrentRecord().getIndex(), tableFiles.getSelectedColumn());
      } else if (!tableFiles.getQuery().isEmpty()) {
        tableFiles.changeSelection(Math.min(index, tableFiles.getRowCount() -1), tableFiles.getSelectedColumn());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    viewClosing = true;
    tableFiles.getQuery().close();
    accesibilities = null;
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

    cmRefreshFiles = new pl.mpak.sky.gui.swing.Action();
    cmSelectSchema = new pl.mpak.sky.gui.swing.Action();
    cmFilter = new pl.mpak.sky.gui.swing.Action();
    cmRemoveFile = new pl.mpak.sky.gui.swing.Action();
    cmAddFile = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    panelTables = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableFiles = new ViewTable();
    statusBarTables = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel1 = new javax.swing.JPanel();
    toolBarTables = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSelectSchema = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonAddFile = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonRemoveFile = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRefreshFiles.setActionCommandKey("cmRefreshFiles");
    cmRefreshFiles.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefreshFiles.setText(stringManager.getString("JarFilesPanelView-cmRefreshFiles-text")); // NOI18N
    cmRefreshFiles.setTooltip(stringManager.getString("JarFilesPanelView-cmRefreshFiles-hint")); // NOI18N
    cmRefreshFiles.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshFilesActionPerformed(evt);
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

    cmRemoveFile.setActionCommandKey("cmRemoveFile");
    cmRemoveFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmRemoveFile.setText(stringManager.getString("JarFilesPanelView-cmRemoveFile-text")); // NOI18N
    cmRemoveFile.setTooltip(stringManager.getString("JarFilesPanelView-cmRemoveFile-hint")); // NOI18N
    cmRemoveFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRemoveFileActionPerformed(evt);
      }
    });

    cmAddFile.setActionCommandKey("cmAddFile");
    cmAddFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_package.gif")); // NOI18N
    cmAddFile.setText(stringManager.getString("JarFilesPanelView-cmAddFile-text")); // NOI18N
    cmAddFile.setTooltip(stringManager.getString("JarFilesPanelView-cmAddFile-hint")); // NOI18N
    cmAddFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddFileActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    panelTables.setPreferredSize(new java.awt.Dimension(250, 100));
    panelTables.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableFiles);

    panelTables.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarTables.setShowFieldType(false);
    statusBarTables.setShowFieldValue(false);
    statusBarTables.setShowOpenTime(false);
    statusBarTables.setTable(tableFiles);
    panelTables.add(statusBarTables, java.awt.BorderLayout.PAGE_END);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarTables.setFloatable(false);
    toolBarTables.setRollover(true);

    buttonRefresh.setAction(cmRefreshFiles);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonRefresh);

    buttonSelectSchema.setAction(cmSelectSchema);
    buttonSelectSchema.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSelectSchema.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonSelectSchema);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonFilter);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBarTables.add(jSeparator1);

    buttonAddFile.setAction(cmAddFile);
    buttonAddFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonAddFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonAddFile);

    buttonRemoveFile.setAction(cmRemoveFile);
    buttonRemoveFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRemoveFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonRemoveFile);
    toolBarTables.add(jSeparator2);

    buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTables.add(buttonActions);

    jPanel1.add(toolBarTables);

    panelTables.add(jPanel1, java.awt.BorderLayout.NORTH);

    add(panelTables, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void cmRemoveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRemoveFileActionPerformed
  if (tableFiles.getSelectionModel().getLeadSelectionIndex() >= 0) {
    try {
      tableFiles.getQuery().getRecord(tableFiles.getSelectionModel().getLeadSelectionIndex());
      String objectName = SQLUtil.createSqlName(currentSchemaName, tableFiles.getQuery().fieldByName("filename").getString(), getDatabase());
      if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("JarFilesPanelView-delete-jar-file-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        getDatabase().createCommand("call sqlj.remove_jar('" +objectName +"', 1)", true);
        refresh(null);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmRemoveFileActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableFiles.getQuery().isActive()) {
    refresh();
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
    query.open("select schemaname from sys.sysschemas order by schemaname");
    Vector<String> vl = QueryUtil.staticData("{schemaname}", query);
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

private void cmRefreshFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshFilesActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshFilesActionPerformed

private void cmAddFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddFileActionPerformed
  File file = FileUtil.selectFileToOpen(this, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("JarFilesPanelView-jar-library"), new String[] { ".jar" })});
  if (file != null) {
    String name = JOptionPane.showInputDialog(this, stringManager.getString("JarFilesPanelView-input-jar-name-dd"), file.getName().replace('.', '_'));
    if (name != null) {
      try {
        getDatabase().createCommand("call sqlj.install_jar('" +file.getPath() +"', '" + name+"', 1)", true);
        refresh(null);
      } catch (Exception ex) {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
        ExceptionUtil.processException(ex);
      }
    }
  }
}//GEN-LAST:event_cmAddFileActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonAddFile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRemoveFile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectSchema;
  private pl.mpak.sky.gui.swing.Action cmAddFile;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmRefreshFiles;
  private pl.mpak.sky.gui.swing.Action cmRemoveFile;
  private pl.mpak.sky.gui.swing.Action cmSelectSchema;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JPanel panelTables;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTables;
  private ViewTable tableFiles;
  private javax.swing.JToolBar toolBarTables;
  // End of variables declaration//GEN-END:variables

}

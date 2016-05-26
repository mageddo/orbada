package pl.mpak.orbada.gui;

import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.comps.table.DataTable;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class ContentPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  protected IViewAccesibilities accesibilities;
  protected String currentCatalogName = "";
  protected String currentSchemaName = "";
  protected String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  
  /** Creates new form TableContentPanel 
   * @param accesibilities 
   */
  public ContentPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    textFilter.setModel(new DefaultComboBoxModel());
    tableContent.getQuery().setDatabase(getDatabase());
    SwingUtil.addAction((JTextField)textFilter.getEditor().getEditorComponent(), cmSearch);
    ((JTextField)textFilter.getEditor().getEditorComponent()).getComponentPopupMenu().addSeparator();
    ((JTextField)textFilter.getEditor().getEditorComponent()).getComponentPopupMenu().add(cmSearch);
    new ComponentActionsAction(getDatabase(), tableContent, buttonActions, menuActions, "orbada-content-actions");
  }

  protected Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  private String getFilterText() {
    return textFilter.getText();
  }
  
  private void setFilterText(String text) {
    textFilter.setText(text);
  }
  
  private void addTextToList(String text) {
    DefaultComboBoxModel model = (DefaultComboBoxModel)textFilter.getModel();
    int index = model.getIndexOf(text);
    if (index != -1) {
      model.removeElementAt(index);
    }
    model.insertElementAt(text, 0);
    if (model.getSize() > 10) {
      model.removeElementAt(model.getSize() -1);
    }
    setFilterText(text);
  }
  
  @Override
  public String getTitle() {
    return stringManager.getString("ContentPanel-content");
  }
  
  public String getSqlText(String filter) {
    return null;
  }
  
  private void refreshTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        refresh();
      }
    });
  }
  
  @Override
  public void refresh() {
    try {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          cmRefresh.setEnabled(false);
        }
      });
      try {
        requestRefresh = false;
        tableContent.getQuery().close();
        if (StringUtil.equals(currentTableName, "")) {
          return;
        }
        String sql = getSqlText(getFilterText());
        if (sql == null) {
          tableContent.getQuery().setSqlText(
            "select * from " +getDatabase().quoteName(currentCatalogName, currentSchemaName, currentTableName) +
            ("".equals(getFilterText()) ? "" : " WHERE " +getFilterText()));
        }
        else {
          tableContent.getQuery().setSqlText(sql);
        }
        tableContent.getQuery().open();
        if (!tableContent.getQuery().isEmpty()) {
          tableContent.changeSelection(0, 0);
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    finally {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          cmRefresh.setEnabled(true);
        }
      });
    }
  }
  
  @Override
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!StringUtil.equals(catalogName, currentCatalogName) || !StringUtil.equals(schemaName, currentSchemaName) || !currentTableName.equals(objectName) || requestRefresh) {
      currentCatalogName = catalogName;
      currentSchemaName = schemaName;
      currentTableName = objectName.trim();
      textFilter.setText("");
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

  @Override
  public void close() throws IOException {
    closing = true;
    tableContent.getQuery().close();
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
    menuActions = new javax.swing.JPopupMenu();
    menuRecordCount = new javax.swing.JMenuItem();
    menuDeleteRecords = new javax.swing.JMenuItem();
    cmRecordCount = new pl.mpak.sky.gui.swing.Action();
    cmDeleteRecords = new pl.mpak.sky.gui.swing.Action();
    cmSearch = new pl.mpak.sky.gui.swing.Action();
    statusBarContent = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableContent = new DataTable();
    jPanel1 = new javax.swing.JPanel();
    toolBarContent = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonRecordCount = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textFilter = new pl.mpak.sky.gui.swing.comp.ComboBox();
    buttonSearch = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    menuRecordCount.setAction(cmRecordCount);
    menuActions.add(menuRecordCount);

    menuDeleteRecords.setAction(cmDeleteRecords);
    menuActions.add(menuDeleteRecords);

    cmRecordCount.setActionCommandKey("cmRecordCount");
    cmRecordCount.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/counts.gif")); // NOI18N
    cmRecordCount.setText(stringManager.getString("ContentPanel-cmRecordCount-text")); // NOI18N
    cmRecordCount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRecordCountActionPerformed(evt);
      }
    });

    cmDeleteRecords.setActionCommandKey("cmDeleteRecords");
    cmDeleteRecords.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDeleteRecords.setText(stringManager.getString("ContentPanel-cmDeleteRecords")); // NOI18N
    cmDeleteRecords.setTooltip(stringManager.getString("ContentPanel-cmDeleteRecords-hint")); // NOI18N
    cmDeleteRecords.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteRecordsActionPerformed(evt);
      }
    });

    cmSearch.setActionCommandKey("cmSearch");
    cmSearch.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmSearch.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_source.gif")); // NOI18N
    cmSearch.setText(stringManager.getString("cmSearch-text")); // NOI18N
    cmSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSearchActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    statusBarContent.setTable(tableContent);
    add(statusBarContent, java.awt.BorderLayout.PAGE_END);

    jScrollPane1.setViewportView(tableContent);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarContent.setFloatable(false);
    toolBarContent.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarContent.add(buttonRefresh);

    buttonRecordCount.setAction(cmRecordCount);
    buttonRecordCount.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRecordCount.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarContent.add(buttonRecordCount);
    toolBarContent.add(jSeparator1);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 3));

    jLabel1.setText("WHERE ");
    jPanel2.add(jLabel1);

    textFilter.setEditable(true);
    textFilter.setPreferredSize(new java.awt.Dimension(150, 22));
    jPanel2.add(textFilter);

    toolBarContent.add(jPanel2);

    buttonSearch.setAction(cmSearch);
    buttonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarContent.add(buttonSearch);
    toolBarContent.add(buttonActions);

    jPanel1.add(toolBarContent);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRecordCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRecordCountActionPerformed
  Query query = getDatabase().createQuery();
  try {
    query.open("select count( 0 ) cnt from (" +tableContent.getQuery().getSqlText() +") x");
    String sqlText = tableContent.getQuery().getSqlText();
    if (sqlText.length() > 200) {
      sqlText = sqlText.substring(0, 200) +"...";
    }
    MessageBox.show(this, stringManager.getString("ContentPanel-record-count"), String.format(stringManager.getString("ContentPanel-record-count-info"), new Object[] {sqlText, query.fieldByName("cnt").getLong()}), ModalResult.OK, MessageBox.INFORMATION);
  }
  catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
  finally {
    query.close();
  }
}//GEN-LAST:event_cmRecordCountActionPerformed

private void cmDeleteRecordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteRecordsActionPerformed
  if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("ContentPanel-delete-records-q"), ModalResult.YESNO) == ModalResult.YES) {
    Command command = getDatabase().createCommand();
    try {
      command.setSqlText("delete from " + getDatabase().quoteName(currentCatalogName, currentSchemaName, currentTableName) + ("".equals(getFilterText()) ? "" : " WHERE " + getFilterText()));
      command.execute();
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteRecordsActionPerformed

private void cmSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSearchActionPerformed
  if (!"".equals(getFilterText())) {
    addTextToList(getFilterText());
  }
  refreshTask();
}//GEN-LAST:event_cmSearchActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRecordCount;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSearch;
  private pl.mpak.sky.gui.swing.Action cmDeleteRecords;
  private pl.mpak.sky.gui.swing.Action cmRecordCount;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSearch;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JMenuItem menuDeleteRecords;
  private javax.swing.JMenuItem menuRecordCount;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarContent;
  private DataTable tableContent;
  private pl.mpak.sky.gui.swing.comp.ComboBox textFilter;
  private javax.swing.JToolBar toolBarContent;
  // End of variables declaration//GEN-END:variables

}

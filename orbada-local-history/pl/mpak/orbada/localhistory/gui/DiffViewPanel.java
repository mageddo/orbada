/*
 * DiffViewPanel.java
 *
 * Created on 28 listopad 2008, 11:56
 */

package pl.mpak.orbada.localhistory.gui;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jmeld.ui.JMeldPanel;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.Sql;
import pl.mpak.orbada.localhistory.core.SchemaObjects;
import pl.mpak.orbada.localhistory.db.OlhObjectRecord;
import pl.mpak.orbada.localhistory.services.LocalHistoryDatabaseService;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.patt.Resolvers;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class DiffViewPanel extends javax.swing.JPanel implements Closeable {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  private OlhObjectRecord olho;
  private SyntaxTextArea syntaxTextArea;
  private IViewAccesibilities accesibilities;
  private boolean viewClosing = false;
  private Timer timer;
  private JMeldPanel meldPanel;
  private ArrayList<File> fileList;
  private String tempDir;
  private File oryginalFile;
  private boolean reloading;
  
  /** Creates new form DiffViewPanel */
  public DiffViewPanel(IViewAccesibilities accesibilities, OlhObjectRecord olho, SyntaxTextArea syntaxTextArea) {
    this.accesibilities = accesibilities;
    this.olho = olho;
    this.syntaxTextArea = syntaxTextArea;
    tempDir = Resolvers.expand("$(java.io.tmpdir)");
    fileList = new ArrayList<File>();
    initComponents();
    init();
  }
  
  private void init() {
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        reloadDiff();
      }
    };
    OrbadaLocalHistoryPlugin.getRefreshQueue().add(timer);

    tableHistory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!reloading) {
          timer.restart();
        }
      }
    });
    
    tableHistory.getQuery().setDatabase(accesibilities.getApplication().getOrbadaDatabase());
    tableHistory.addColumn(new QueryTableColumn("olho_created", stringManager.getString("date"), 140, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableHistory.addColumn(new QueryTableColumn("source_size", stringManager.getString("size"), 80));
    tableHistory.addColumn(new QueryTableColumn("olho_description", stringManager.getString("comment"), 700));

    new ComponentActionsAction(accesibilities.getDatabase(), tableHistory, buttonActions, menuActions, "local-history-object-actions");
    
    meldPanel = new JMeldPanel();
    split.setBottomComponent(meldPanel);
    
    oryginalFile = new File(tempDir +"/" +olho.getObjectType() +" " +olho.getObjectSchema() +" " +olho.getObjectName());
    try {
      syntaxTextArea.saveToFile(oryginalFile);
    } catch (IOException ex) {
      ExceptionUtil.processException(ex);
    }

    fileList.add(oryginalFile);
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
      String olho_id = null;
      if (tableHistory.getQuery().isActive() && tableHistory.getSelectedRow() >= 0) {
        tableHistory.getQuery().getRecord(tableHistory.getSelectedRow());
        olho_id = tableHistory.getQuery().fieldByName("olho_id").getString();
      }
      refreshTableList(olho_id);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refreshTableList(String olho_id) {
    try {
      int column = tableHistory.getSelectedColumn();
      int index = Math.max(0, tableHistory.getSelectedRow());
      tableHistory.getQuery().close();
      tableHistory.getQuery().setSqlText(Sql.getObjectHistoryList());
      tableHistory.getQuery().paramByName("SCH_ID").setString(accesibilities.getDatabase().getUserProperties().getProperty("schemaId"));
      tableHistory.getQuery().paramByName("SCHEMA_NAME").setString(olho.getObjectSchema());
      tableHistory.getQuery().paramByName("OBJECT_TYPE").setString(olho.getObjectType());
      tableHistory.getQuery().paramByName("OBJECT_NAME").setString(olho.getObjectName());
      tableHistory.getQuery().open();
      if (olho_id != null && tableHistory.getQuery().locate("olho_id", new Variant(olho_id))) {
        tableHistory.changeSelection(tableHistory.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!tableHistory.getQuery().isEmpty()) {
        tableHistory.changeSelection(Math.min(index, tableHistory.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void close() throws IOException {
    viewClosing = true;
    timer.cancel();
    tableHistory.getQuery().close();
    for (File file : fileList) {
      file.delete();
    }
    accesibilities = null;
  }
  
  private void reloadDiff() {
    if (tableHistory.getQuery().isActive() && tableHistory.getSelectedRow() >= 0) {
      try {
        tableHistory.getQuery().getRecord(tableHistory.getSelectedRow());
        File file = new File(tempDir +"/" +tableHistory.getQuery().fieldByName("olho_id").getString());
        if (fileList.indexOf(file) == -1) {
          OlhObjectRecord record = new OlhObjectRecord(accesibilities.getApplication().getOrbadaDatabase(), tableHistory.getQuery().fieldByName("olho_id").getString());
          OutputStream os = new FileOutputStream(file);
          try {
            os.write(record.getSource().getBytes());
          }
          finally {
            os.close();
          }
          fileList.add(file);
        }
        meldPanel.openFileComparison(oryginalFile, file, false);
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    menuActions = new javax.swing.JPopupMenu();
    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmCommentChange = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonCommentChange = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableHistory = new pl.mpak.orbada.gui.comps.table.ViewTable();

    cmRefresh.setActionCommandKey("cmRefresh");
    cmRefresh.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/refresh16.gif"))); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmCommentChange.setActionCommandKey("cmCommentChange");
    cmCommentChange.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/comment_edit.gif"))); // NOI18N
    cmCommentChange.setText(stringManager.getString("cmCommentChange-text")); // NOI18N
    cmCommentChange.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCommentChangeActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    split.setBorder(null);
    split.setDividerLocation(110);
    split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);
    toolBar.add(jSeparator1);

    buttonCommentChange.setAction(cmCommentChange);
    buttonCommentChange.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonCommentChange.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonCommentChange);
    toolBar.add(buttonActions);

    jPanel2.add(toolBar);

    jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableHistory);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    split.setTopComponent(jPanel1);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTableList();
}//GEN-LAST:event_cmRefreshActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableHistory.getQuery().isActive() && !viewClosing) {
    refreshTableListTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmCommentChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCommentChangeActionPerformed
  if (tableHistory.getQuery().isActive() && tableHistory.getSelectedRow() >= 0) {
    try {
      tableHistory.getQuery().getRecord(tableHistory.getSelectedRow());
      SchemaObjects so = LocalHistoryDatabaseService.getSchemaObjects(accesibilities.getDatabase());
      if (so != null) {
        OlhObjectRecord olho_u = new OlhObjectRecord(accesibilities.getApplication().getOrbadaDatabase(), tableHistory.getQuery().fieldByName("olho_id").getString());
        String description = JOptionPane.showInputDialog(stringManager.getString("DiffViewPanel-last-comment"), olho_u.getDescription());
        if (description != null) {
          olho_u.setDescription(description);
          try {
            olho_u.applyUpdate();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
            MessageBox.show(syntaxTextArea, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
          }
          reloading = true;
          try {
            refreshTableList();
          }
          finally {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                reloading = false;
              }
            });
          }
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmCommentChangeActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonCommentChange;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmCommentChange;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JSplitPane split;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableHistory;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}

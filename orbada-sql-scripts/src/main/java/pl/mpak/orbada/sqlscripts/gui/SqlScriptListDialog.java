package pl.mpak.orbada.sqlscripts.gui;

import java.util.EventObject;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.sqlscripts.OrbadaSqlScriptsPlugin;
import pl.mpak.orbada.sqlscripts.db.SqlScriptRecord;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.QueryListener;
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
public class SqlScriptListDialog extends javax.swing.JDialog {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("sql-scripts");

  private ISettings settings;
  private String dtp_id;
  private Database forDatabase;
  
  /** Creates new form SchemaListDialog */
  public SqlScriptListDialog(Database forDatabase) {
    super(SwingUtil.getRootFrame());
    this.forDatabase = forDatabase;
    this.dtp_id = forDatabase.getUserProperties().getProperty("dtp_id");
    initComponents();
    init();
  }
  
  public static void showDialog(Database forDatabase) {
    SqlScriptListDialog dialog = new SqlScriptListDialog(forDatabase);
    dialog.setVisible(true);
  }
  
  private void init() {
    settings = Application.get().getSettings("orbada-sql-script-list-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(getWidth())).getInteger(), settings.getValue("height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }
    SwingUtil.centerWithinScreen(this);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmClose");
    getRootPane().getActionMap().put("cmClose", cmClose);
    
    tableSqlScripts.getQuery().setDatabase(InternalDatabase.get());
    tableSqlScripts.getQuery().addQueryListener(new QueryListener() {
      public void beforeScroll(EventObject e) {
      }
      public void afterScroll(EventObject e) {
      }
      public void beforeOpen(EventObject e) {
      }
      public void afterOpen(EventObject e) {
        cmNew.setEnabled(true);
      }
      public void beforeClose(EventObject e) {
      }
      public void afterClose(EventObject e) {
        cmNew.setEnabled(false);
        cmEdit.setEnabled(false);
        cmDelete.setEnabled(false);
      }
      public void flushedPerformed(EventObject e) {
      }
      public void errorPerformed(EventObject e) {
      }
    });
    
    tableSqlScripts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableSqlScripts.getSelectedRow() >= 0) {
          try {
            tableSqlScripts.getQuery().getRecord(tableSqlScripts.getSelectedRow());
            cmEdit.setEnabled(true);
            cmDelete.setEnabled(true);
          } catch (Exception ex) {
            MessageBox.show(SqlScriptListDialog.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
          }
        }
        else {
          cmEdit.setEnabled(false);
          cmDelete.setEnabled(false);
        }
      }
    });
    
    try {
      tableSqlScripts.addColumn(new QueryTableColumn("oss_name", stringManager.getString("sql-script"), 450, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableSqlScripts.getQuery().setSqlText(
        "select oss_id, oss_name\n" +
        "  from osqlscripts\n" +
        " where oss_usr_id = :USR_ID\n" +
        "   and oss_dtp_id = :DTP_ID\n" +
        " order by oss_name");
      tableSqlScripts.getQuery().paramByName("usr_id").setString(Application.get().getUserId());
      tableSqlScripts.getQuery().paramByName("dtp_id").setString(dtp_id);
      tableSqlScripts.getQuery().open();
      if (!tableSqlScripts.getQuery().isEmpty()) {
        tableSqlScripts.changeSelection(0, 0);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }
  
  @Override
  public void dispose() {
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    tableSqlScripts.getQuery().close();
    super.dispose();
  }
  
  public void refreshQuery() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int lastRow = tableSqlScripts.getSelectedRow();
        try {
          tableSqlScripts.getQuery().refresh();
          if (tableSqlScripts.getRowCount() > lastRow && lastRow >= 0) {
            tableSqlScripts.changeSelection(lastRow, lastRow);
          } else if (tableSqlScripts.getRowCount() > 0) {
            tableSqlScripts.changeSelection(0, 0);
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmClose = new pl.mpak.sky.gui.swing.Action();
    cmNew = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    buttonClose = new javax.swing.JButton();
    buttonNew = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSqlScripts = new ViewTable();

    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("close-action")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmNew.setEnabled(false);
    cmNew.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/new16.gif"))); // NOI18N
    cmNew.setText(stringManager.getString("new-action")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setEnabled(false);
    cmEdit.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/edit16.gif"))); // NOI18N
    cmEdit.setText(stringManager.getString("edit-action")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setEnabled(false);
    cmDelete.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/trash.gif"))); // NOI18N
    cmDelete.setText(stringManager.getString("delete-action")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("define-sql-scripts")); // NOI18N
    setModal(true);

    buttonClose.setAction(cmClose);
    buttonClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonClose.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 23));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 23));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 23));

    jScrollPane1.setViewportView(tableSqlScripts);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
            .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  if (tableSqlScripts.getQuery().isActive() && tableSqlScripts.getSelectedRow() >= 0) {
    try {
      tableSqlScripts.getQuery().getRecord(tableSqlScripts.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("sql-script-deleting-question"), new Object[] {tableSqlScripts.getQuery().fieldByName("OSS_NAME").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        SqlScriptRecord sqlScript = new SqlScriptRecord(InternalDatabase.get());
        sqlScript.getPrimaryKeyField().setValue(new Variant(tableSqlScripts.getQuery().fieldByName("OSS_ID").getString()));
        sqlScript.applyDelete();
        refreshQuery();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableSqlScripts.getQuery().isActive() && tableSqlScripts.getSelectedRow() >= 0) {
      tableSqlScripts.getQuery().getRecord(tableSqlScripts.getSelectedRow());
      if (SqlScriptEditDialog.showDialog(forDatabase, tableSqlScripts.getQuery().fieldByName("oss_id").getString(), dtp_id) == ModalResult.OK) {
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableSqlScripts.getQuery().isActive()) {
      if (SqlScriptEditDialog.showDialog(forDatabase, null, dtp_id) == ModalResult.OK) {
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
  dispose();
}//GEN-LAST:event_cmCloseActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClose;
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonNew;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private javax.swing.JScrollPane jScrollPane1;
  private ViewTable tableSqlScripts;
  // End of variables declaration//GEN-END:variables
  
}

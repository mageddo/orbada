/*
 * GeneralSettingsProvider.java
 *
 * Created on 10 grudzieñ 2007, 20:05
 */

package pl.mpak.orbada.beanshell.gui;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.beanshell.OrbadaBeanshellPlugin;
import pl.mpak.orbada.beanshell.db.BshActionRecord;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class BshActionsSettingsPanel extends javax.swing.JPanel implements ISettingsComponent, Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  private IApplication application;
  
  /** Creates new form GeneralSettingsProvider */
  public BshActionsSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }
  
  private void init() {
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonEdit, buttonDelete, buttonExport, buttonImport});
    tableBshActions.getQuery().setDatabase(application.getOrbadaDatabase());
    tableBshActions.getQuery().addQueryListener(new DefaultQueryListener() {
      @Override
      public void afterOpen(EventObject e) {
        updateActions();
      }
      @Override
      public void afterClose(EventObject e) {
        updateActions();
      }
    });
    
    tableBshActions.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableBshActions.getSelectedRow() >= 0) {
          try {
            tableBshActions.getQuery().getRecord(tableBshActions.getSelectedRow());
            updateActions();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          cmEdit.setEnabled(false);
          cmDelete.setEnabled(false);
        }
      }
    });
    
    tableBshActions.addColumn(new QueryTableColumn("scope", stringManager.getString("scope"), 100));
    tableBshActions.addColumn(new QueryTableColumn("bsha_title", stringManager.getString("title"), 250, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableBshActions.addColumn(new QueryTableColumn("bsha_key", stringManager.getString("key"), 150));
    tableBshActions.addColumn(new QueryTableColumn("dtp_name", stringManager.getString("driver"), 150));
    refresh(null);
  }
  
  private void updateActions() {
    if (tableBshActions.getQuery().isActive() && tableBshActions.getSelectedRow() >= 0) {
      try {
        if (tableBshActions.getQuery().fieldByName("bsha_usr_id").isNull()) {
          cmEdit.setEnabled(application.isUserAdmin());
          cmDelete.setEnabled(application.isUserAdmin());
        } else {
          cmEdit.setEnabled(true);
          cmDelete.setEnabled(true);
        }
      } catch (Exception ex) {
        cmEdit.setEnabled(false);
        cmDelete.setEnabled(false);
      }
    }
    else {
      cmEdit.setEnabled(false);
      cmDelete.setEnabled(false);
    }
    cmNew.setEnabled(tableBshActions.getQuery().isActive());
  }
  
  private void refresh(String gqi_id) {
    try {
      if (gqi_id == null && tableBshActions.getQuery().isActive() && tableBshActions.getSelectedRow() >= 0) {
        tableBshActions.getQuery().getRecord(tableBshActions.getSelectedRow());
        gqi_id = tableBshActions.getQuery().fieldByName("bsha_id").getString();
      }
      tableBshActions.getQuery().close();
      tableBshActions.getQuery().setSqlText(
        "select bsha_id, bsha_usr_id, case when bsha_usr_id is null then 'Globalne' else 'U¿ytkownika' end scope, bsha_title, bsha_key, dtp_name\n" +
        "  from bshactions left outer join driver_types on (bsha_dtp_id = dtp_id)\n" +
        " where (bsha_usr_id = :USR_ID or bsha_usr_id is null)\n" +
        " order by bsha_title");
      tableBshActions.getQuery().paramByName("usr_id").setString(application.getUserId());
      tableBshActions.getQuery().open();
      if (gqi_id != null && tableBshActions.getQuery().locate("bsha_id", new Variant(gqi_id))) {
        tableBshActions.changeSelection(tableBshActions.getQuery().getCurrentRecord().getIndex(), 0, false, false);
      }
      else {
        tableBshActions.changeSelection(0, 0, false, false);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }
  
  public void restoreSettings() {
  }

  public void applySettings() {
    application.postPluginMessage(new PluginMessage(null, OrbadaBeanshellPlugin.bshActionsReloadMessage, null));
  }

  public void cancelSettings() {
  }
  
  public void close() throws IOException {
    tableBshActions.getQuery().close();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmNew = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    cmExport = new pl.mpak.sky.gui.swing.Action();
    cmImport = new pl.mpak.sky.gui.swing.Action();
    buttonNew = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableBshActions = new ViewTable();
    buttonExport = new javax.swing.JButton();
    buttonImport = new javax.swing.JButton();

    cmNew.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmNew.setText(stringManager.getString("cmNew-text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit-text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDelete.setText(stringManager.getString("cmDelete-text")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    cmExport.setActionCommandKey("cmExport");
    cmExport.setText(stringManager.getString("cmExport-text")); // NOI18N
    cmExport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmExportActionPerformed(evt);
      }
    });

    cmImport.setActionCommandKey("cmImport");
    cmImport.setText(stringManager.getString("cmImport-text")); // NOI18N
    cmImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmImportActionPerformed(evt);
      }
    });

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 25));

    jLabel1.setText(stringManager.getString("BshActionsSettingsPanel-action-script-info")); // NOI18N

    jScrollPane1.setViewportView(tableBshActions);

    buttonExport.setAction(cmExport);
    buttonExport.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonExport.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonImport.setAction(cmImport);
    buttonImport.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonImport.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableBshActions.getQuery().isActive()) {
      String gqi_id = BshActionEditDialog.show(application, null);
      if (gqi_id != null) {
        refresh(gqi_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableBshActions.getQuery().isActive() && tableBshActions.getSelectedRow() >= 0) {
      tableBshActions.getQuery().getRecord(tableBshActions.getSelectedRow());
      String bsha_id = BshActionEditDialog.show(application, tableBshActions.getQuery().fieldByName("bsha_id").getString());
      if (bsha_id != null) {
        refresh(bsha_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  try {
    if (tableBshActions.getQuery().isActive() && tableBshActions.getSelectedRow() >= 0) {
      tableBshActions.getQuery().getRecord(tableBshActions.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("BshActionsSettingsPanel-del-action-q"), new Object[] {tableBshActions.getQuery().fieldByName("bsha_title").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        BshActionRecord action = new BshActionRecord(application.getOrbadaDatabase());
        action.setPrimaryKeyValue(new Variant(tableBshActions.getQuery().fieldByName("bsha_id").getString()));
        action.applyDelete();
        refresh(null);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExportActionPerformed
  try {
    if (tableBshActions.getQuery().isActive() && tableBshActions.getSelectedRow() >= 0) {
      tableBshActions.getQuery().getRecord(tableBshActions.getSelectedRow());
      File file = FileUtil.selectFileToSave(this, stringManager.getString("save-action-script"), new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("bshaction-files"), new String[] {".xml.bshaction"})});
      if (file != null) {
        BshActionRecord action = new BshActionRecord(application.getOrbadaDatabase(), tableBshActions.getQuery().fieldByName("bsha_id").getString());
        action.storeToXML(new FileOutputStream(file), null, "utf-8");
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmExportActionPerformed

private void cmImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmImportActionPerformed
  try {
    if (tableBshActions.getQuery().isActive() && tableBshActions.getSelectedRow() >= 0) {
      tableBshActions.getQuery().getRecord(tableBshActions.getSelectedRow());
      File file = FileUtil.selectFileToOpen(this, stringManager.getString("open-action-script"), (File)null, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("bshaction-files"), new String[] {".xml.bshaction"})});
      if (file != null) {
        BshActionRecord action = new BshActionRecord(application.getOrbadaDatabase());
        action.loadFromXML(new FileInputStream(file));
        action.setId(new UniqueID().toString());
        if (!application.isUserAdmin() || action.getUsrId() != null) {
          action.setUsrId(application.getUserId());
        }
        String bsha_id = BshActionEditDialog.show(application, null, action);
        if (bsha_id != null) {
          refresh(bsha_id);
        }
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmImportActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonExport;
  private javax.swing.JButton buttonImport;
  private javax.swing.JButton buttonNew;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmExport;
  private pl.mpak.sky.gui.swing.Action cmImport;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private ViewTable tableBshActions;
  // End of variables declaration//GEN-END:variables

}

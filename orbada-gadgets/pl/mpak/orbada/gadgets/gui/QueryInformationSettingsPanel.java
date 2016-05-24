/*
 * GeneralSettingsProvider.java
 *
 * Created on 10 grudzieñ 2007, 20:05
 */

package pl.mpak.orbada.gadgets.gui;

import java.io.Closeable;
import java.io.IOException;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gadgets.Consts;
import pl.mpak.orbada.gadgets.OrbadaGadgetsPlugin;
import pl.mpak.orbada.gadgets.db.OgQueryInfo;
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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class QueryInformationSettingsPanel extends javax.swing.JPanel implements ISettingsComponent, Closeable {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaGadgetsPlugin.class);

  private IApplication application;
  
  /** Creates new form GeneralSettingsProvider */
  public QueryInformationSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }
  
  private void init() {
    tableQueryInfos.getQuery().setDatabase(application.getOrbadaDatabase());
    tableQueryInfos.getQuery().addQueryListener(new DefaultQueryListener() {
      @Override
      public void afterOpen(EventObject e) {
        updateActions();
      }
      @Override
      public void afterClose(EventObject e) {
        updateActions();
      }
    });
    
    tableQueryInfos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableQueryInfos.getSelectedRow() >= 0) {
          try {
            tableQueryInfos.getQuery().getRecord(tableQueryInfos.getSelectedRow());
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
    
    tableQueryInfos.addColumn(new QueryTableColumn("scope", stringManager.getString("scope"), 100));
    tableQueryInfos.addColumn(new QueryTableColumn("gqi_name", stringManager.getString("name"), 250, new QueryTableCellRenderer(java.awt.Font.BOLD)));
    tableQueryInfos.addColumn(new QueryTableColumn("dtp_name", stringManager.getString("driver"), 150));
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonEdit, buttonDelete});
    refresh(null);
  }
  
  private void updateActions() {
    if (tableQueryInfos.getQuery().isActive() && tableQueryInfos.getSelectedRow() >= 0) {
      try {
        if (tableQueryInfos.getQuery().fieldByName("gqi_usr_id").isNull()) {
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
    cmNew.setEnabled(tableQueryInfos.getQuery().isActive());
  }
  
  private void refresh(String gqi_id) {
    try {
      if (gqi_id == null && tableQueryInfos.getQuery().isActive() && tableQueryInfos.getSelectedRow() >= 0) {
        tableQueryInfos.getQuery().getRecord(tableQueryInfos.getSelectedRow());
        gqi_id = tableQueryInfos.getQuery().fieldByName("gqi_id").getString();
      }
      tableQueryInfos.getQuery().close();
      tableQueryInfos.getQuery().setSqlText(
        "select gqi_id, gqi_usr_id, case when gqi_usr_id is null then 'Globalne' else 'U¿ytkownika' end scope, gqi_name, dtp_name\n" +
        "  from og_queryinfos left outer join driver_types on (gqi_dtp_id = dtp_id)\n" +
        " where (gqi_usr_id = :USR_ID or gqi_usr_id is null)\n" +
        " order by gqi_name");
      tableQueryInfos.getQuery().paramByName("usr_id").setString(application.getUserId());
      tableQueryInfos.getQuery().open();
      if (gqi_id != null && tableQueryInfos.getQuery().locate("gqi_id", new Variant(gqi_id))) {
        tableQueryInfos.changeSelection(tableQueryInfos.getQuery().getCurrentRecord().getIndex(), 0, false, false);
      }
      else {
        tableQueryInfos.changeSelection(0, 0, false, false);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }
  
  public void restoreSettings() {
  }

  public void applySettings() {
  }

  public void cancelSettings() {
  }
  
  public void close() throws IOException {
    tableQueryInfos.getQuery().close();
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
    buttonNew = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableQueryInfos = new pl.mpak.orbada.gui.comps.table.ViewTable();

    cmNew.setActionCommandKey("cmNew");
    cmNew.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmNew.setText(stringManager.getString("cmNew-text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setActionCommandKey("cmEdit");
    cmEdit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit-text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setActionCommandKey("cmDelete");
    cmDelete.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDelete.setText(stringManager.getString("cmDelete-text")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 23));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 23));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 23));

    jLabel1.setText(stringManager.getString("QueryInformationSettingsPanel-def-sql-command-into")); // NOI18N

    jScrollPane1.setViewportView(tableQueryInfos);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableQueryInfos.getQuery().isActive()) {
      String gqi_id = OgQueryInfoDialog.show(application, null);
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
    if (tableQueryInfos.getQuery().isActive() && tableQueryInfos.getSelectedRow() >= 0) {
      tableQueryInfos.getQuery().getRecord(tableQueryInfos.getSelectedRow());
      String gqi_id = OgQueryInfoDialog.show(application, tableQueryInfos.getQuery().fieldByName("gqi_id").getString());
      if (gqi_id != null) {
        refresh(gqi_id);
        application.postPluginMessage(new PluginMessage(null, Consts.QueryInfoPanelReload, null));
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  try {
    if (tableQueryInfos.getQuery().isActive() && tableQueryInfos.getSelectedRow() >= 0) {
      tableQueryInfos.getQuery().getRecord(tableQueryInfos.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("QueryInformationSettingsPanel-delete-sql-command-q"), new Object[] {tableQueryInfos.getQuery().fieldByName("gqi_name").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        OgQueryInfo info = new OgQueryInfo(application.getOrbadaDatabase());
        info.setPrimaryKeyValue(new Variant(tableQueryInfos.getQuery().fieldByName("gqi_id").getString()));
        info.applyDelete();
        refresh(null);
        application.postPluginMessage(new PluginMessage(null, Consts.QueryInfoPanelReload, null));
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }

}//GEN-LAST:event_cmDeleteActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonNew;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableQueryInfos;
  // End of variables declaration//GEN-END:variables

}

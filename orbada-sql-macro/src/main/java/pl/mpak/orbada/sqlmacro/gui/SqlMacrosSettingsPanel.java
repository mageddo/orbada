package pl.mpak.orbada.sqlmacro.gui;

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
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.sqlmacro.Consts;
import pl.mpak.orbada.sqlmacro.OrbadaSqlMacrosPlugin;
import pl.mpak.orbada.sqlmacro.db.SqlMacroRecord;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.DefaultQueryListener;
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
public class SqlMacrosSettingsPanel extends javax.swing.JPanel implements ISettingsComponent, Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSqlMacrosPlugin.class);

  private IApplication application;
  
  /** Creates new form GeneralSettingsProvider */
  public SqlMacrosSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }
  
  private void init() {
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonEdit, buttonDelete, buttonExport, buttonImport});
    tableMacros.getQuery().setDatabase(application.getOrbadaDatabase());
    tableMacros.getQuery().addQueryListener(new DefaultQueryListener() {
      @Override
      public void afterOpen(EventObject e) {
        updateActions();
      }
      @Override
      public void afterClose(EventObject e) {
        updateActions();
      }
    });
    
    tableMacros.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableMacros.getSelectedRow() >= 0) {
          try {
            tableMacros.getQuery().getRecord(tableMacros.getSelectedRow());
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
    
    tableMacros.addColumn(new QueryTableColumn("scope", stringManager.getString("scope"), 70));
    tableMacros.addColumn(new QueryTableColumn("osm_name", stringManager.getString("name"), 370));
    tableMacros.addColumn(new QueryTableColumn("dtp_name", stringManager.getString("driver"), 100));
    tableMacros.addColumn(new QueryTableColumn("osm_order", stringManager.getString("order"), 30));
    refresh(null);
  }
  
  private void updateActions() {
    if (tableMacros.getQuery().isActive() && tableMacros.getSelectedRow() >= 0) {
      try {
        if (tableMacros.getQuery().fieldByName("osm_usr_id").isNull()) {
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
    cmNew.setEnabled(tableMacros.getQuery().isActive());
  }
  
  private void refresh(String osm_id) {
    try {
      if (osm_id == null && tableMacros.getQuery().isActive() && tableMacros.getSelectedRow() >= 0) {
        tableMacros.getQuery().getRecord(tableMacros.getSelectedRow());
        osm_id = tableMacros.getQuery().fieldByName("osm_id").getString();
      }
      tableMacros.getQuery().close();
      tableMacros.getQuery().setSqlText(
        "select osm_id, osm_usr_id, case when osm_usr_id is null then 'Global' else 'User' end scope, osm_name, osm_order, dtp_id, dtp_name\n" +
        "  from osqlmacros left outer join driver_types on (dtp_id = osm_dtp_id)\n" +
        " where (osm_usr_id = :USR_ID or osm_usr_id is null)\n" +
        " order by osm_order, dtp_id");
      tableMacros.getQuery().paramByName("usr_id").setString(application.getUserId());
      tableMacros.getQuery().open();
      if (osm_id != null && tableMacros.getQuery().locate("osm_id", new Variant(osm_id))) {
        tableMacros.changeSelection(tableMacros.getQuery().getCurrentRecord().getIndex(), 0, false, false);
      }
      else {
        tableMacros.changeSelection(0, 0, false, false);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }
  
  public void restoreSettings() {
  }

  public void applySettings() {
    application.postPluginMessage(new PluginMessage(null, Consts.sqlMacrosReloadMsg, null));
  }

  public void cancelSettings() {
  }
  
  public void close() throws IOException {
    tableMacros.getQuery().close();
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
    tableMacros = new ViewTable();
    buttonExport = new javax.swing.JButton();
    buttonImport = new javax.swing.JButton();

    cmNew.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/new16.gif"))); // NOI18N
    cmNew.setText(stringManager.getString("cmNew-text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/edit16.gif"))); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit-text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/trash.gif"))); // NOI18N
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

    jLabel1.setText(stringManager.getString("SqlMacrosSettingsPanel-defined-macro-info")); // NOI18N

    jScrollPane1.setViewportView(tableMacros);

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
          .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
            .addContainerGap())
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .addGap(47, 47, 47))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(buttonExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableMacros.getQuery().isActive()) {
      String osm_id = SqlMacroEditDialog.show(application, null);
      if (osm_id != null) {
        refresh(osm_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableMacros.getQuery().isActive() && tableMacros.getSelectedRow() >= 0) {
      tableMacros.getQuery().getRecord(tableMacros.getSelectedRow());
      String osm_id = SqlMacroEditDialog.show(application, tableMacros.getQuery().fieldByName("osm_id").getString());
      if (osm_id != null) {
        refresh(osm_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  try {
    if (tableMacros.getQuery().isActive() && tableMacros.getSelectedRow() >= 0) {
      tableMacros.getQuery().getRecord(tableMacros.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("SqlMacrosSettingsPanel-del-macro-q"), new Object[] {tableMacros.getQuery().fieldByName("osm_name").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        SqlMacroRecord macro = new SqlMacroRecord(application.getOrbadaDatabase());
        macro.setPrimaryKeyValue(new Variant(tableMacros.getQuery().fieldByName("osm_id").getString()));
        macro.applyDelete();
        refresh(null);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }

}//GEN-LAST:event_cmDeleteActionPerformed

private void cmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExportActionPerformed
   try {
    if (tableMacros.getQuery().isActive() && tableMacros.getSelectedRow() >= 0) {
      tableMacros.getQuery().getRecord(tableMacros.getSelectedRow());
      File file = FileUtil.selectFileToSave(this, stringManager.getString("save-sql-macro"), new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("sql-macro-files"), new String[] {".xml.sqlmacro"})});
      if (file != null) {
        SqlMacroRecord macro = new SqlMacroRecord(application.getOrbadaDatabase(), tableMacros.getQuery().fieldByName("osm_id").getString());
        macro.storeToXML(new FileOutputStream(file), null, "utf-8");
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmExportActionPerformed

private void cmImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmImportActionPerformed
try {
    if (tableMacros.getQuery().isActive() && tableMacros.getSelectedRow() >= 0) {
      tableMacros.getQuery().getRecord(tableMacros.getSelectedRow());
      File file = FileUtil.selectFileToOpen(this, stringManager.getString("open-sql-macro"), (File)null, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("sql-macro-files"), new String[] {".xml.sqlmacro"})});
      if (file != null) {
        SqlMacroRecord macro = new SqlMacroRecord(application.getOrbadaDatabase());
        macro.loadFromXML(new FileInputStream(file));
        macro.setId(new UniqueID().toString());
        if (!application.isUserAdmin() || macro.getUsrId() != null) {
          macro.setUsrId(application.getUserId());
        }
        String osm_id = SqlMacroEditDialog.show(application, null, macro);
        if (osm_id != null) {
          refresh(osm_id);
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
  private ViewTable tableMacros;
  // End of variables declaration//GEN-END:variables

}

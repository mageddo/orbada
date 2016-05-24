package pl.mpak.orbada.snippets.gui;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.snippets.OrbadaSnippetsPlugin;
import pl.mpak.orbada.snippets.SnippetsManager;
import pl.mpak.orbada.snippets.db.SnippetRecord;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.YesNoCellRendererFilter;
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
public class SnippetsSettingsPanel extends javax.swing.JPanel implements ISettingsComponent, Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSnippetsPlugin.class);

  private IApplication application;
  private ISettings settings;
  private boolean changed;
  
  /** Creates new form GeneralSettingsProvider */
  public SnippetsSettingsPanel(IApplication application) {
    this.application = application;
    changed = false;
    initComponents();
    init();
  }
  
  private void init() {
    settings = application.getSettings(Consts.orbadaSnippetsPluginId);
    
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonEdit, buttonDelete, buttonExport, buttonImport});
    tableSnippets.getQuery().setDatabase(application.getOrbadaDatabase());
    tableSnippets.getQuery().addQueryListener(new DefaultQueryListener() {
      @Override
      public void afterOpen(EventObject e) {
        updateActions();
      }
      @Override
      public void afterClose(EventObject e) {
        updateActions();
      }
    });
    
    tableSnippets.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (tableSnippets.getSelectedRow() >= 0) {
          try {
            tableSnippets.getQuery().getRecord(tableSnippets.getSelectedRow());
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
    
    tableSnippets.addColumn(new QueryTableColumn("scope", stringManager.getString("scope"), 70));
    tableSnippets.addColumn(new QueryTableColumn("snp_name", stringManager.getString("snippet-name"), 150));
    tableSnippets.addColumn(new QueryTableColumn("snp_editor", stringManager.getString("snippet-editor"), 50));
    tableSnippets.addColumn(new QueryTableColumn("snp_active", stringManager.getString("snippet-active"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter())));
    tableSnippets.addColumn(new QueryTableColumn("snp_immediate", stringManager.getString("snippet-immediate"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, tableSnippets.getForeground()))));
    tableSnippets.addColumn(new QueryTableColumn("driver_type", stringManager.getString("driver-type"), 100));
    
    restoreSettings();
    refresh(null);
  }
  
  private void updateActions() {
    if (tableSnippets.getQuery().isActive() && tableSnippets.getSelectedRow() >= 0) {
      try {
        if (tableSnippets.getQuery().fieldByName("snp_usr_id").isNull()) {
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
    cmNew.setEnabled(tableSnippets.getQuery().isActive());
  }
  
  private void refresh(String snp_id) {
    try {
      if (snp_id == null && tableSnippets.getQuery().isActive() && tableSnippets.getSelectedRow() >= 0) {
        tableSnippets.getQuery().getRecord(tableSnippets.getSelectedRow());
        snp_id = tableSnippets.getQuery().fieldByName("snp_id").getString();
      }
      tableSnippets.getQuery().close();
      tableSnippets.getQuery().setSqlText(
        "select snp_id, snp_usr_id, snp_name, snp_editor, snp_active, snp_immediate, case when snp_usr_id is null then 'Global' else 'User' end scope, dtp_name driver_type \n" +
        "  from snippets left join driver_types on snp_dtp_id = dtp_id\n" +
        " where (snp_usr_id is null or snp_usr_id = :USR_ID)\n" +
        " order by snp_name");
      tableSnippets.getQuery().paramByName("usr_id").setString(application.getUserId());
      tableSnippets.getQuery().open();
      if (snp_id != null && tableSnippets.getQuery().locate("snp_id", new Variant(snp_id))) {
        tableSnippets.changeSelection(tableSnippets.getQuery().getCurrentRecord().getIndex(), 0, false, false);
      }
      else {
        tableSnippets.changeSelection(0, 0, false, false);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }
  
  @Override
  public void restoreSettings() {
    checkSnippetsEnabled.setSelected(settings.getValue(SnippetsManager.SET_SNIPPETS_ENABLED, Boolean.TRUE));
  }

  @Override
  public void applySettings() {
    if (checkSnippetsEnabled.isSelected() != settings.getValue(SnippetsManager.SET_SNIPPETS_ENABLED, Boolean.TRUE) || changed) {
      settings.setValue(SnippetsManager.SET_SNIPPETS_ENABLED, checkSnippetsEnabled.isSelected());
      settings.store();
      OrbadaSnippetsPlugin.getSnippetsManager().reloadSnippets(true, true);
    }
  }

  @Override
  public void cancelSettings() {
    restoreSettings();
  }
  
  @Override
  public void close() throws IOException {
    tableSnippets.getQuery().close();
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
        tableSnippets = new pl.mpak.orbada.gui.comps.table.ViewTable();
        buttonExport = new javax.swing.JButton();
        buttonImport = new javax.swing.JButton();
        checkSnippetsEnabled = new javax.swing.JCheckBox();

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

        jLabel1.setText(stringManager.getString("SnippetsSettingsPanel-defined-macro-info")); // NOI18N

        jScrollPane1.setViewportView(tableSnippets);

        buttonExport.setAction(cmExport);
        buttonExport.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonExport.setPreferredSize(new java.awt.Dimension(85, 25));

        buttonImport.setAction(cmImport);
        buttonImport.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonImport.setPreferredSize(new java.awt.Dimension(85, 25));

        checkSnippetsEnabled.setText(stringManager.getString("checkSnippetsEnabled-text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkSnippetsEnabled)
                        .addContainerGap(449, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkSnippetsEnabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
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
    if (tableSnippets.getQuery().isActive()) {
      String snp_id = SnippetEditDialog.show(application, null);
      if (snp_id != null) {
        changed = true;
        refresh(snp_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableSnippets.getQuery().isActive() && tableSnippets.getSelectedRow() >= 0) {
      tableSnippets.getQuery().getRecord(tableSnippets.getSelectedRow());
      String snp_id = SnippetEditDialog.show(application, tableSnippets.getQuery().fieldByName("snp_id").getString());
      if (snp_id != null) {
        changed = true;
        refresh(snp_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  try {
    if (tableSnippets.getQuery().isActive() && tableSnippets.getSelectedRow() >= 0) {
      tableSnippets.getQuery().getRecord(tableSnippets.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("SnippetsSettingsPanel-del-snippet-q"), new Object[] {tableSnippets.getQuery().fieldByName("snp_name").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        SnippetRecord macro = new SnippetRecord(application.getOrbadaDatabase());
        macro.setPrimaryKeyValue(new Variant(tableSnippets.getQuery().fieldByName("snp_id").getString()));
        macro.applyDelete();
        changed = true;
        refresh(null);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }

}//GEN-LAST:event_cmDeleteActionPerformed

private void cmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExportActionPerformed
   try {
    if (tableSnippets.getQuery().isActive() && tableSnippets.getSelectedRow() >= 0) {
      tableSnippets.getQuery().getRecord(tableSnippets.getSelectedRow());
      File file = FileUtil.selectFileToSave(this, stringManager.getString("save-snippet"), new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("snippet-files"), new String[] {".xml.snippet"})});
      if (file != null) {
        SnippetRecord macro = new SnippetRecord(application.getOrbadaDatabase(), tableSnippets.getQuery().fieldByName("snp_id").getString());
        macro.storeToXML(new FileOutputStream(file), null, "utf-8");
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmExportActionPerformed

private void cmImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmImportActionPerformed
try {
    if (tableSnippets.getQuery().isActive() && tableSnippets.getSelectedRow() >= 0) {
      tableSnippets.getQuery().getRecord(tableSnippets.getSelectedRow());
      File file = FileUtil.selectFileToOpen(this, stringManager.getString("open-snippet"), (File)null, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("snippet-files"), new String[] {".xml.snippet"})});
      if (file != null) {
        SnippetRecord macro = new SnippetRecord(application.getOrbadaDatabase());
        macro.loadFromXML(new FileInputStream(file));
        macro.setId(new UniqueID().toString());
        if (!application.isUserAdmin() || macro.getUsrId() != null) {
          macro.setUsrId(application.getUserId());
        }
        String snp_id = SnippetEditDialog.show(application, null, macro);
        if (snp_id != null) {
          changed = true;
          refresh(snp_id);
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
    private javax.swing.JCheckBox checkSnippetsEnabled;
    private pl.mpak.sky.gui.swing.Action cmDelete;
    private pl.mpak.sky.gui.swing.Action cmEdit;
    private pl.mpak.sky.gui.swing.Action cmExport;
    private pl.mpak.sky.gui.swing.Action cmImport;
    private pl.mpak.sky.gui.swing.Action cmNew;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private pl.mpak.orbada.gui.comps.table.ViewTable tableSnippets;
    // End of variables declaration//GEN-END:variables

}

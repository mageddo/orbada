package pl.mpak.orbada.gui.schemas;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.Driver;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.ISettings;
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
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class DriverListDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private ISettings settings;

  /** Creates new form DriverListDialog */
  public DriverListDialog() {
    super(SwingUtil.getRootFrame());
    initComponents();
    init();
  }
  
  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        DriverListDialog dialog = new DriverListDialog();
        dialog.setVisible(true);
      }
    });
  }
  
  private void init() {
    settings = Application.get().getSettings("orbada-driver-list-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(getWidth())).getInteger(), settings.getValue("height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmClose");
    getRootPane().getActionMap().put("cmClose", cmClose);
    
    tableDrivers.getQuery().setDatabase(InternalDatabase.get());
    tableDrivers.getQuery().addQueryListener(new DefaultQueryListener() {
      public void afterOpen(EventObject e) {
        cmNew.setEnabled(true);
        cmImport.setEnabled(true);
      }
      public void afterClose(EventObject e) {
        cmNew.setEnabled(false);
        cmEdit.setEnabled(false);
        cmDelete.setEnabled(false);
        cmImport.setEnabled(false);
        cmExport.setEnabled(false);
      }
    });
    
    tableDrivers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableDrivers.getSelectedRow() >= 0) {
          try {
            tableDrivers.getQuery().getRecord(tableDrivers.getSelectedRow());
            cmEdit.setEnabled(e.getFirstIndex() >= 0 && (Application.get().isUserAdmin() || !tableDrivers.getQuery().fieldByName("drv_usr_id").isNull()));
            cmDelete.setEnabled(e.getFirstIndex() >= 0 && (Application.get().isUserAdmin() || !tableDrivers.getQuery().fieldByName("drv_usr_id").isNull()));
            cmExport.setEnabled(e.getFirstIndex() >= 0 && (Application.get().isUserAdmin() || !tableDrivers.getQuery().fieldByName("drv_usr_id").isNull()));
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }

        }
        else {
          cmEdit.setEnabled(false);
          cmDelete.setEnabled(false);
          cmExport.setEnabled(false);
        }
      }
    });
    
    try {
      tableDrivers.addColumn(new QueryTableColumn("drv_name", stringManager.getString("DriverListDialog-driver-name"), 150, new QueryTableCellRenderer(Font.BOLD)));
      tableDrivers.addColumn(new QueryTableColumn("drv_type_name", stringManager.getString("DriverListDialog-kind"), 100, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableDrivers.addColumn(new QueryTableColumn("drv_class_name", stringManager.getString("DriverListDialog-class"), 200));
      tableDrivers.addColumn(new QueryTableColumn("drv_url_template", stringManager.getString("DriverListDialog-url-pattern"), 250));
      tableDrivers.getQuery().setSqlText("select * from drivers where drv_usr_id = :drv_usr_id or drv_usr_id is null order by drv_name");
      tableDrivers.getQuery().paramByName("drv_usr_id").setString(Application.get().getUserId());
      tableDrivers.getQuery().open();
      if (!tableDrivers.getQuery().isEmpty()) {
        tableDrivers.setRowSelectionInterval(0, 0);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }

    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonEdit, buttonDelete, buttonClose, buttonFunctions});
    SwingUtil.centerWithinScreen(this);
  }
  
  @Override
  public void dispose() {
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    tableDrivers.getQuery().close();
    super.dispose();
  }
  
  public void refreshQuery(final String drv_id) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int lastRow = tableDrivers.getSelectedRow();
        try {
          String id = drv_id;
          if (tableDrivers.getQuery().isActive() && tableDrivers.getSelectedRow() >= 0 && id == null) {
            tableDrivers.getQuery().getRecord(lastRow);
            id = tableDrivers.getQuery().fieldByName("drv_id").getString();
          }
          tableDrivers.getQuery().refresh();
          if (id != null && tableDrivers.getQuery().locate("drv_id", new Variant(id))) {
            tableDrivers.changeSelection(tableDrivers.getQuery().getCurrentRecord().getIndex(), 0);
          }
          else if (tableDrivers.getRowCount() > lastRow && lastRow >= 0) {
            tableDrivers.changeSelection(lastRow, 0);
          }
          else if (tableDrivers.getRowCount() > 0) {
            tableDrivers.changeSelection(0, 0);
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
    cmFunctions = new pl.mpak.sky.gui.swing.Action();
    popupFunctions = new javax.swing.JPopupMenu();
    menuExport = new javax.swing.JMenuItem();
    menuImport = new javax.swing.JMenuItem();
    cmExport = new pl.mpak.sky.gui.swing.Action();
    cmImport = new pl.mpak.sky.gui.swing.Action();
    buttonClose = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableDrivers = new pl.mpak.orbada.gui.comps.table.ViewTable();
    buttonNew = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    buttonFunctions = new javax.swing.JButton();

    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("cmClose-text")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmNew.setEnabled(false);
    cmNew.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmNew.setText(stringManager.getString("cmNew-text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setEnabled(false);
    cmEdit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit-text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setEnabled(false);
    cmDelete.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDelete.setText(stringManager.getString("cmDelete-text")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    cmFunctions.setActionCommandKey("cmFunctions");
    cmFunctions.setText(stringManager.getString("DriverListDialog-cmFunctions-text")); // NOI18N
    cmFunctions.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFunctionsActionPerformed(evt);
      }
    });

    menuExport.setAction(cmExport);
    popupFunctions.add(menuExport);

    menuImport.setAction(cmImport);
    popupFunctions.add(menuImport);

    cmExport.setActionCommandKey("cmExport");
    cmExport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/export.gif")); // NOI18N
    cmExport.setText(stringManager.getString("DriverListDialog-cmExport-text")); // NOI18N
    cmExport.setTooltip(stringManager.getString("DriverListDialog-cmExport-hint")); // NOI18N
    cmExport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmExportActionPerformed(evt);
      }
    });

    cmImport.setActionCommandKey("cmImport");
    cmImport.setEnabled(false);
    cmImport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/import.gif")); // NOI18N
    cmImport.setText(stringManager.getString("DriverListDialog-cmImport-text")); // NOI18N
    cmImport.setTooltip(stringManager.getString("DriverListDialog-cmImport-hint")); // NOI18N
    cmImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmImportActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("DriverListDialog-title")); // NOI18N
    setModal(true);

    buttonClose.setAction(cmClose);
    buttonClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonClose.setPreferredSize(new java.awt.Dimension(85, 25));

    jScrollPane1.setViewportView(tableDrivers);

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonFunctions.setAction(cmFunctions);
    buttonFunctions.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonFunctions.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonFunctions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
            .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(buttonFunctions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  if (tableDrivers.getQuery().isActive() && tableDrivers.getSelectedRow() >= 0) {
    try {
      tableDrivers.getQuery().getRecord(tableDrivers.getSelectedRow());
      String drv_id = tableDrivers.getQuery().fieldByName("DRV_ID").getString();
      String drv_name = tableDrivers.getQuery().fieldByName("DRV_NAME").getString();
      if (MessageBox.show(this,
            stringManager.getString("deleting"),
            String.format(
              stringManager.getString("DriverListDialog-delete-q"),
              new Object[] {drv_id, drv_name}),
            ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        Driver driver = new Driver(Application.get().getOrbadaDatabase());
        driver.getPrimaryKeyField().setValue(new Variant(drv_id));
        driver.applyDelete();
        refreshQuery(null);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableDrivers.getQuery().isActive() && tableDrivers.getSelectedRow() >= 0) {
      tableDrivers.getQuery().getRecord(tableDrivers.getSelectedRow());
      String drv_id = DriverEditDialog.showDialog(tableDrivers.getQuery().fieldByName("drv_id").getString());
      if (drv_id != null) {
        refreshQuery(drv_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableDrivers.getQuery().isActive()) {
      String drv_id = DriverEditDialog.showDialog((String)null);
      if (drv_id != null) {
        refreshQuery(drv_id);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
  dispose();
}//GEN-LAST:event_cmCloseActionPerformed

private void cmFunctionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFunctionsActionPerformed
  popupFunctions.show(buttonFunctions, 0, buttonFunctions.getHeight());
}//GEN-LAST:event_cmFunctionsActionPerformed

private void cmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExportActionPerformed
  try {
    if (tableDrivers.getQuery().isActive() && tableDrivers.getSelectedRow() >= 0) {
      tableDrivers.getQuery().getRecord(tableDrivers.getSelectedRow());
      File file = FileUtil.selectFileToSave(this, stringManager.getString("DriverListDialog-save-driver"), new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("DriverListDialog-driver-file"), new String[] {".xml.ojdbcdriver"})});
      if (file != null) {
        Driver driver = new Driver(InternalDatabase.get(), tableDrivers.getQuery().fieldByName("drv_id").getString());
        driver.storeToXML(new FileOutputStream(file), null, "utf-8");
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmExportActionPerformed

private void cmImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmImportActionPerformed
  try {
    if (tableDrivers.getQuery().isActive()) {
      File file = FileUtil.selectFileToOpen(this, stringManager.getString("DriverListDialog-open-driver"), (File)null, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("DriverListDialog-driver-file"), new String[] {".xml.ojdbcdriver"})});
      if (file != null) {
        Driver driver = new Driver(InternalDatabase.get());
        driver.loadFromXML(new FileInputStream(file));
        String drv_id = DriverEditDialog.showDialog(driver);
        if (drv_id != null) {
          refreshQuery(drv_id);
        }
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmImportActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClose;
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonFunctions;
  private javax.swing.JButton buttonNew;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmExport;
  private pl.mpak.sky.gui.swing.Action cmFunctions;
  private pl.mpak.sky.gui.swing.Action cmImport;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JMenuItem menuExport;
  private javax.swing.JMenuItem menuImport;
  private javax.swing.JPopupMenu popupFunctions;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableDrivers;
  // End of variables declaration//GEN-END:variables
  
}

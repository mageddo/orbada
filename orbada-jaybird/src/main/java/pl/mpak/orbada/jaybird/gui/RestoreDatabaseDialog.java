/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RestoreDatabaseDialog.java
 *
 * Created on 2009-05-23, 22:37:18
 */

package pl.mpak.orbada.jaybird.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import org.firebirdsql.management.FBBackupManager;
import pl.mpak.orbada.db.Schema;
import pl.mpak.orbada.gui.comps.util.SchemasComboBoxModel;
import pl.mpak.orbada.gui.comps.util.SchemasListCellRenderer;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RestoreDatabaseDialog extends javax.swing.JDialog {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);
  private Thread worker;
  private OutputStream logger;

  /** Creates new form RestoreDatabaseDialog */
  public RestoreDatabaseDialog() {
    super(SwingUtil.getRootFrame(), true);
    initComponents();
    init();
  }

  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        RestoreDatabaseDialog dialog = new RestoreDatabaseDialog();
        dialog.setVisible(true);
      }
    });
  }

  private void init() {
    comboSchemas.setModel(new SchemasComboBoxModel(OrbadaJaybirdPlugin.firebirdDriverType));
    comboSchemas.setRenderer(new SchemasListCellRenderer());
    schemaChanged();

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmClose);
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel});
    SwingUtil.centerWithinScreen(this);
  }

  private void enableControls(final boolean value) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        comboSchemas.setEnabled(value);
        textHost.setEnabled(value);
        textUser.setEnabled(value);
        textPassword.setEnabled(value);
        textDatabase.setEnabled(value);
        textPort.setEnabled(value);
        textBackupDatabase.setEnabled(value);
        cmRestore.setEnabled(value);
        cmClose.setEnabled(value);
        comboPageSize.setEnabled(value);
        checkNoIndex.setEnabled(value);
        checkNoShadow.setEnabled(value);
        checkNoValid.setEnabled(value);
      }
    });
  }

  private OutputStream getLogger() {
    if (logger == null) {
      logger = new OutputStream() {
        final StringBuffer sb = new StringBuffer();
        @Override
        public void write(int b) throws IOException {
          synchronized (sb) {
            sb.append((char)b);
          }
          if (sb.length() > 10000) {
            flush();
          }
        }
        @Override
        public void flush() throws IOException {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              synchronized (sb) {
                textLog.append(sb.toString());
                sb.setLength(0);
              }
            }
          });
        }
      };
    }
    return logger;
  }

  private void createBackupDatabase() {
    final FBBackupManager manager = new FBBackupManager();
    try {
      enableControls(false);
      textLog.setText("");
      manager.setHost(textHost.getText());
      manager.setUser(textUser.getText());
      manager.setPassword(new String(textPassword.getPassword()));
      manager.setPort(Integer.valueOf(textPort.getText()));
      manager.setDatabase(textDatabase.getText());
      manager.setBackupPath(textBackupDatabase.getText());
      manager.setVerbose(true);
      manager.setRestorePageSize(Integer.valueOf(comboPageSize.getText()));
      manager.setRestoreReplace(checkRestoreReplace.isSelected());
      int checks = 0;
      if (checkNoIndex.isSelected()) {
        checks |= FBBackupManager.RESTORE_DEACTIVATE_INDEX;
      }
      if (checkNoShadow.isSelected()) {
        checks |= FBBackupManager.RESTORE_NO_SHADOW;
      }
      if (checkNoValid.isSelected()) {
        checks |= FBBackupManager.RESTORE_NO_VALIDITY;
      }
      final int options = checks;
      manager.setLogger(getLogger());
      worker = new Thread("Restore Firbird Database") {
        @Override
        public void run() {
          try {
            manager.restoreDatabase(options);
          } catch (final SQLException ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                textLog.append("\n" +ex.getMessage());
              }
            });
          }
          finally {
            try {
              getLogger().flush();
            } catch (IOException ex) {
            }
            enableControls(true);
          }
        }
      };
      worker.start();
      tabbed.setSelectedComponent(panelLog);
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
      enableControls(true);
    }
  }

  private void schemaChanged() {
    if (comboSchemas.getSelectedItem() instanceof Schema) {
      Schema schema = (Schema)comboSchemas.getSelectedItem();
      textHost.setText(schema.getHost());
      textUser.setText(schema.getUser());
      textPassword.setText(schema.getPassword());
      textDatabase.setText(schema.getDatabaseName());
      if (schema.getPort() == null) {
        textPort.setText("3050");
      }
      else {
        textPort.setText(schema.getPort().toString());
      }
    }
    else {
      textHost.setText("");
      textUser.setText("SYSDBA");
      textPassword.setText("masterkey");
      textDatabase.setText("");
      textPort.setText("3050");
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

    cmRestore = new pl.mpak.sky.gui.swing.Action();
    cmClose = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    tabbed = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    comboSchemas = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel2 = new javax.swing.JLabel();
    textHost = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    textUser = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    textPassword = new javax.swing.JPasswordField();
    jLabel5 = new javax.swing.JLabel();
    textDatabase = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel6 = new javax.swing.JLabel();
    textPort = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel7 = new javax.swing.JLabel();
    textBackupDatabase = new pl.mpak.sky.gui.swing.comp.TextField();
    checkNoIndex = new javax.swing.JCheckBox();
    checkNoValid = new javax.swing.JCheckBox();
    checkNoShadow = new javax.swing.JCheckBox();
    checkRestoreReplace = new javax.swing.JCheckBox();
    jLabel8 = new javax.swing.JLabel();
    comboPageSize = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel9 = new javax.swing.JLabel();
    panelLog = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textLog = new pl.mpak.sky.gui.swing.comp.TextArea();

    cmRestore.setActionCommandKey("cmRestore");
    cmRestore.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmRestore.setText(stringManager.getString("cmRestore.text")); // NOI18N
    cmRestore.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRestoreActionPerformed(evt);
      }
    });

    cmClose.setActionCommandKey("cmClose");
    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("cmClose.text")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("RestoreDatabaseDialog.title")); // NOI18N

    buttonOk.setAction(cmRestore);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmClose);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    tabbed.setFocusable(false);

    jLabel1.setText(stringManager.getString("SelectSchemaForRestore")); // NOI18N

    comboSchemas.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboSchemasItemStateChanged(evt);
      }
    });

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("Host-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("User-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("Password-dd")); // NOI18N

    textPassword.setText("jPasswordField1");

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("Database-dd")); // NOI18N

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("Port-dd")); // NOI18N

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(stringManager.getString("ArchiveName-dd")); // NOI18N

    checkNoIndex.setText(stringManager.getString("DeactivateIndexes")); // NOI18N

    checkNoValid.setText(stringManager.getString("NoValidateDatabase")); // NOI18N

    checkNoShadow.setText(stringManager.getString("NoShadow")); // NOI18N

    checkRestoreReplace.setSelected(true);
    checkRestoreReplace.setText(stringManager.getString("CreateNewDatabase")); // NOI18N
    checkRestoreReplace.setEnabled(false);

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText(stringManager.getString("PageSize-dd")); // NOI18N

    comboPageSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1024", "2048", "4096", "8192" }));

    jLabel9.setText(stringManager.getString("bytes")); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(comboSchemas, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
              .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
              .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
              .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
              .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(checkNoIndex)
              .addComponent(checkNoValid)
              .addComponent(checkNoShadow)
              .addComponent(checkRestoreReplace)
              .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(textPassword, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(textUser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textHost, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
              .addComponent(textDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
              .addComponent(textBackupDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(comboPageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(comboSchemas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(textDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(textBackupDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(comboPageSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel9))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkNoIndex)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkNoValid)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkNoShadow)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkRestoreReplace)
        .addContainerGap(34, Short.MAX_VALUE))
    );

    tabbed.addTab(stringManager.getString("Settings"), jPanel1); // NOI18N

    textLog.setColumns(20);
    textLog.setEditable(false);
    textLog.setRows(5);
    textLog.setFont(new java.awt.Font("Monospaced", 0, 12));
    jScrollPane1.setViewportView(textLog);

    javax.swing.GroupLayout panelLogLayout = new javax.swing.GroupLayout(panelLog);
    panelLog.setLayout(panelLogLayout);
    panelLogLayout.setHorizontalGroup(
      panelLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelLogLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
        .addContainerGap())
    );
    panelLogLayout.setVerticalGroup(
      panelLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelLogLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbed.addTab(stringManager.getString("Log"), panelLog); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(tabbed, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tabbed, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void cmRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRestoreActionPerformed
      createBackupDatabase();
}//GEN-LAST:event_cmRestoreActionPerformed

    private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
      dispose();
}//GEN-LAST:event_cmCloseActionPerformed

    private void comboSchemasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboSchemasItemStateChanged
      if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        schemaChanged();
      }
    }//GEN-LAST:event_comboSchemasItemStateChanged

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JCheckBox checkNoIndex;
  private javax.swing.JCheckBox checkNoShadow;
  private javax.swing.JCheckBox checkNoValid;
  private javax.swing.JCheckBox checkRestoreReplace;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmRestore;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboPageSize;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboSchemas;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel panelLog;
  private javax.swing.JTabbedPane tabbed;
  private pl.mpak.sky.gui.swing.comp.TextField textBackupDatabase;
  private pl.mpak.sky.gui.swing.comp.TextField textDatabase;
  private pl.mpak.sky.gui.swing.comp.TextField textHost;
  private pl.mpak.sky.gui.swing.comp.TextArea textLog;
  private javax.swing.JPasswordField textPassword;
  private pl.mpak.sky.gui.swing.comp.TextField textPort;
  private pl.mpak.sky.gui.swing.comp.TextField textUser;
  // End of variables declaration//GEN-END:variables

}

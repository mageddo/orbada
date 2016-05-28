/*
 * DatabaseSettingsPanel.java
 *
 * Created on 10 grudzie� 2007, 21:16
 */

package pl.mpak.orbada.universal.gui;

import java.io.File;
import java.io.IOException;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.services.UniversalDatabaseProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class DatabaseSettingsPanel extends javax.swing.JPanel implements ISettingsComponent {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  private IApplication application;
  private Database database;

  /** Creates new form DatabaseSettingsPanel */
  public DatabaseSettingsPanel(IApplication application, Database database) {
    this.application = application;
    this.database = database;
    initComponents();
    init();
  }
  
  private void init() {
    textAfterConnect.setDatabase(database);
    textBeforeDisconnect.setDatabase(database);
    restoreSettings();
  }
  
  public void restoreSettings() {
    if (UniversalDatabaseProvider.instance != null) {
      String script = UniversalDatabaseProvider.instance.readScript(database, true);
      textAfterConnect.setText(script != null ? script : "");
      script = UniversalDatabaseProvider.instance.readScript(database, false);
      textBeforeDisconnect.setText(script != null ? script : "");
    }
  }

  public void applySettings() {
    File file = new File(UniversalDatabaseProvider.instance.getFilePath());
    file.mkdirs();
    
    String script = textAfterConnect.getText();
    String fileName = UniversalDatabaseProvider.instance.getScriptFile(database, true);
    file = new File(fileName);
    if (!StringUtil.isEmpty(script)) {
      try {
        textAfterConnect.saveToFile(file);
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
    else if (file.exists()) {
      file.delete();
    }
    script = textBeforeDisconnect.getText();
    fileName = UniversalDatabaseProvider.instance.getScriptFile(database, false);
    file = new File(fileName);
    if (!StringUtil.isEmpty(script)) {
      try {
        textBeforeDisconnect.saveToFile(file);
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
    else if (file.exists()) {
      file.delete();
    }
  }

  public void cancelSettings() {
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    textAfterConnect = new OrbadaSyntaxTextArea();
    jLabel2 = new javax.swing.JLabel();
    textBeforeDisconnect = new OrbadaSyntaxTextArea();

    jLabel1.setText(stringManager.getString("DatabaseSettingsPanel-after-connect-dd")); // NOI18N

    jLabel2.setText(stringManager.getString("DatabaseSettingsPanel-before-disconnect-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(textBeforeDisconnect, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
          .addComponent(textAfterConnect, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textAfterConnect, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textBeforeDisconnect, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private OrbadaSyntaxTextArea textAfterConnect;
  private OrbadaSyntaxTextArea textBeforeDisconnect;
  // End of variables declaration//GEN-END:variables
  
}
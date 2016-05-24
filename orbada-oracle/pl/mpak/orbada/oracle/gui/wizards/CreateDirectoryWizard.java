package pl.mpak.orbada.oracle.gui.wizards;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class CreateDirectoryWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  
  public CreateDirectoryWizard(Database database) {
    this.database = database;
    initComponents();
    init();
  }
  
  private void init() {
  }
  
  public void wizardShow() {
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateDirectoryWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateDirectoryWizard-tab-title");
  }
  
  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    return "CREATE OR REPLACE DIRECTORY " +textName.getText() +" AS '" +textPath.getText() +"'";
  }
  
  public boolean execute() {
    try {
      database.executeCommand(getSqlCode());
      return true;
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    textPath = new pl.mpak.sky.gui.swing.comp.TextField();

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("catalog-name-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("path-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textPath, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel4;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  private pl.mpak.sky.gui.swing.comp.TextField textPath;
  // End of variables declaration//GEN-END:variables
  
}

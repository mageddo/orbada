/*
 * CreateIndexWizardPanel.java
 *
 * Created on 22 listopad 2007, 22:42
 */

package pl.mpak.orbada.hsqldb.gui.wizards;

import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.orbada.universal.gui.util.SchemaComboBoxModel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class AlterUserSchemaWizardPanel extends SqlCodeWizardPanel {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaHSqlDbPlugin.class);

  private Database database;
  private String schemaName;
  private String userName;
  
  /** Creates new form CreateIndexWizardPanel
   * @param database
   * @param schemaName
   */
  public AlterUserSchemaWizardPanel(Database database, String schemaName) {
    this.database = database;
    this.schemaName = schemaName;
    initComponents();
    init();
  }
  
  private void init() {
    Query query = database.createQuery();
    try {
      query.open("select user() user_name from dual");
      userName = query.fieldByName("user_name").getString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }

    comboSchemas.setModel(new SchemaComboBoxModel(database));
  }
  
  public void wizardShow() {
    ((SchemaComboBoxModel)comboSchemas.getModel()).change();
    ((SchemaComboBoxModel)comboSchemas.getModel()).select(schemaName, comboSchemas);
  }
  
  public String getDialogTitle() {
    return stringManager.getString("AlterUserSchemaWizardPanel-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("AlterUserSchemaWizardPanel-tab-title");
  }
  
  public String getSqlCode() {
    return "ALTER USER \"" +userName +"\" SET INITIAL SCHEMA " +SQLUtil.createSqlName(comboSchemas.getSelectedItem().toString());
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

    jLabel2 = new javax.swing.JLabel();
    comboSchemas = new javax.swing.JComboBox();

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("schema-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(comboSchemas, 0, 246, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboSchemas, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboSchemas;
  private javax.swing.JLabel jLabel2;
  // End of variables declaration//GEN-END:variables
  
}

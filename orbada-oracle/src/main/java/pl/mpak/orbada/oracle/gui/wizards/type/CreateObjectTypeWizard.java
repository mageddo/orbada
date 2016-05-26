package pl.mpak.orbada.oracle.gui.wizards.type;

import java.util.HashMap;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.Template;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.services.OracleTemplatesSettingsProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class CreateObjectTypeWizard extends SqlCodeWizardPanel {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  private String schemaName;
  private Template template;
  private Template templateBody;
  
  /** 
   * @param database
   * @param schemaName
   */
  public CreateObjectTypeWizard(Database database, String schemaName) {
    this.database = database;
    this.schemaName = schemaName;
    initComponents();
    init();
  }

  private void init() {
  }

  public void wizardShow() {
    ISettings oracle = Application.get().getSettings(OracleTemplatesSettingsProvider.settingsName);
    template = new Template(Application.get().getOrbadaDatabase()).loadByName(oracle.getValue(OracleTemplatesSettingsProvider.setObjectType, "oracle-object-type"));
    templateBody = new Template(Application.get().getOrbadaDatabase()).loadByName(oracle.getValue(OracleTemplatesSettingsProvider.setObjectTypeBody, "oracle-object-type-body"));
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateObjectTypeWizard-dialog-title");
  }

  public String getTabTitle() {
    return stringManager.getString("CreateObjectTypeWizard-tab-title");
  }

  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    String body = 
      "\nCREATE TYPE BODY " +textName.getText() +" IS\n" +
      "-- " +textDescription.getText() +"\n" +
      "END;\n/";
    if (template == null) {
      return
        "CREATE TYPE " +textName.getText() +" AS OBJECT (\n" +
        "-- " +textDescription.getText() +"\n" +
        ");\n/" +
        (checkBody.isSelected() ? body : "");
    }
    else {
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("&name", textName.getText());
      map.put("&description", textDescription.getText());
      return 
        template.expand(map) +"\n/" +
        (checkBody.isSelected() ? (templateBody != null ? "\n" +templateBody.expand(map) +"\n/" : body) : "");
    }
  }

  public boolean execute() {
    SimpleSQLScript script = new SimpleSQLScript(database);
    if (!script.executeScript(getSqlCode())) {
      MessageBox.show(this, stringManager.getString("error"), script.getErrors(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
    return true;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel3 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    textDescription = new pl.mpak.sky.gui.swing.comp.TextField();
    checkBody = new javax.swing.JCheckBox();

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("type-name-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("description-dd")); // NOI18N

    checkBody.setText(stringManager.getString("create-a-body-type")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(checkBody)
              .addComponent(textDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkBody)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkBody;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private pl.mpak.sky.gui.swing.comp.TextField textDescription;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables
}

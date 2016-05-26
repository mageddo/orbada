package pl.mpak.orbada.oracle.gui.wizards;

import java.util.HashMap;
import javax.swing.JTextField;
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
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class CreateFunctionWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  private String schemaName;
  private FunctionParameterDefinitionWizard parametrDefinition;
  private Template template;
  
  public CreateFunctionWizard(Database database, String schemaName) {
    this.database = database;
    this.schemaName = schemaName;
    initComponents();
    init();
  }
  
  private void init() {
    parametrDefinition = new FunctionParameterDefinitionWizard(database);
    tab.addTab(parametrDefinition.getTabTitle(), parametrDefinition);
  }
  
  public void wizardShow() {
    ISettings oracle = Application.get().getSettings(OracleTemplatesSettingsProvider.settingsName);
    template = new Template(Application.get().getOrbadaDatabase()).loadByName(oracle.getValue(OracleTemplatesSettingsProvider.setFunction, "oracle-function"));
    parametrDefinition.wizardShow();
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateFunctionWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateFunctionWizard-tab-title");
  }
  
  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    if (template == null) {
      return
        "CREATE FUNCTION " +SQLUtil.createSqlName(schemaName) +"." +textName.getText() +" " +
        parametrDefinition.getSqlCode() +
        " RETURN " +((JTextField)comboReturnType.getEditor().getEditorComponent()).getText() +" IS \n" +
        "-- " +textDescription.getText() +"\n" +
        "BEGIN\n" +
        "  NULL;\n" +
        "END;\n" +
        "/";
    }
    else {
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("&name", textName.getText());
      map.put("&parameters", parametrDefinition.getSqlCode());
      map.put("&description", textDescription.getText());
      map.put("&return", ((JTextField)comboReturnType.getEditor().getEditorComponent()).getText());
      map.put("&body", "  NULL;");
      return template.expand(map) +"\n/";
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

    tab = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    textDescription = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    comboReturnType = new javax.swing.JComboBox();

    tab.setFocusable(false);

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("procedure-name-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("description-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("return-type-dd")); // NOI18N

    comboReturnType.setEditable(true);
    comboReturnType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BFILE", "BINARY_DOUBLE", "BINARY_FLOAT", "BLOB", "CHAR", "CHAR VARYING", "CLOB", "DATE", "DECIMAL", "DOUBLE PRECISION", "FLOAT", "INTEGER", "LONG", "LONG RAW", "LONG VARCHAR", "NATIONAL CHAR", "NATIONAL CHAR VARYING", "NUMBER", "NVARCHAR2", "RAW", "REAL", "ROWID", "UROWID", "SMALLINT", "TIMESTAMP", "VARCHAR", "VARCHAR2" }));
    comboReturnType.setSelectedIndex(26);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
          .addComponent(comboReturnType, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(comboReturnType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(168, Short.MAX_VALUE))
    );

    tab.addTab(stringManager.getString("basic"), jPanel1); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboReturnType;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JTabbedPane tab;
  private pl.mpak.sky.gui.swing.comp.TextField textDescription;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables
  
}

package pl.mpak.orbada.firebird.gui.wizards;

import javax.swing.DefaultComboBoxModel;
import pl.mpak.orbada.firebird.Consts;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
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
public class CreateDomainWizard extends SqlCodeWizardPanel {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private Database database;
  
  public CreateDomainWizard(Database database) {
    this.database = database;
    initComponents();
    init();
  }
  
  private void init() {
    comboDataType.setModel(new DefaultComboBoxModel(Consts.COMBO_TYPS));
  }
  
  public void wizardShow() {
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateDomainWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateDomainWizard-tab-title");
  }
  
  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    return 
      "CREATE DOMAIN " +textName.getText() +"\n" +
      "  AS " +comboDataType.getText() +"\n" +
      ("".equals(comboDefault.getText()) ? "" : "  DEFAULT " +comboDefault.getText() +"\n") +
      (checkNotNull.isSelected() ? "  NOT NULL\n" : "") +
      ("".equals(textCheck.getText()) ? "" : "  CHECK (" +textCheck.getText() +")\n") +
      ("".equals(textCollate.getText()) ? "" : "  COLLATE " +textCollate.getText() +"\n") +
      "/\n";
  }
  
  public boolean execute() {
    try {
      database.executeScript(getSqlCode());
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
    jLabel2 = new javax.swing.JLabel();
    comboDataType = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel3 = new javax.swing.JLabel();
    comboDefault = new pl.mpak.sky.gui.swing.comp.ComboBox();
    checkNotNull = new javax.swing.JCheckBox();
    jLabel4 = new javax.swing.JLabel();
    textCheck = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    textCollate = new pl.mpak.sky.gui.swing.comp.TextField();

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("domain-name-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("value-type-dd")); // NOI18N

    comboDataType.setEditable(true);

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("default-value-dd")); // NOI18N

    comboDefault.setEditable(true);
    comboDefault.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "NULL", "USER" }));

    checkNotNull.setText("NOT NULL");

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("CreateDomainWizard-check-dd")); // NOI18N

    textCheck.setText("VALUE ");

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("CreateDomainWizard-collate-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
          .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
          .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
          .addComponent(comboDataType, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
          .addComponent(checkNotNull)
          .addComponent(comboDefault, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
          .addComponent(textCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
          .addComponent(textCollate, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(comboDataType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(comboDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkNotNull)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel4))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textCollate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkNotNull;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboDataType;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboDefault;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private pl.mpak.sky.gui.swing.comp.TextField textCheck;
  private pl.mpak.sky.gui.swing.comp.TextField textCollate;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables
  
}
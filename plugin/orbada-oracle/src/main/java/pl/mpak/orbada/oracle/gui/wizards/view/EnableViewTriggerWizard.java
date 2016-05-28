/*
 * AlterTableNullWizardPanel.java
 *
 * Created on 27 listopad 2007, 19:39
 */

package pl.mpak.orbada.oracle.gui.wizards.view;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.orbada.oracle.dbinfo.OracleViewInfo;
import pl.mpak.orbada.oracle.gui.util.TriggerComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.ViewComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.ViewItemListener;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class EnableViewTriggerWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  private String schemaName;
  private String objectName;
  private String triggerName;
  
  public EnableViewTriggerWizard(Database database, String schemaName, String objectName, String triggerName) {
    this.database = database;
    this.schemaName = schemaName;
    this.objectName = objectName;
    this.triggerName = triggerName;
    initComponents();
    init();
  }
  
  private void init() {
    comboTriggers.setModel(new TriggerComboBoxModel(database));
    if (objectName != null) {
      comboTables.setModel(new ViewComboBoxModel(database));

      comboTables.addItemListener(new ViewItemListener() {
        public void itemChanged(OracleViewInfo info) {
          ((TriggerComboBoxModel)comboTriggers.getModel()).change(info.getSchema().getName(), info.getName(), "VIEW");
        }
      });
    }
    else {
      comboTables.setEnabled(false);
    }
  }
  
  public void wizardShow() {
    if (objectName != null) {
      ((ViewComboBoxModel)comboTables.getModel()).change(schemaName);
      ((ViewComboBoxModel)comboTables.getModel()).select(objectName, comboTables);
    }
    else {
      ((TriggerComboBoxModel)comboTriggers.getModel()).change(schemaName, null, null);
    }
    ((TriggerComboBoxModel)comboTriggers.getModel()).select(triggerName, comboTriggers);
  }
  
  public String getDialogTitle() {
    return stringManager.getString("EnableViewTriggerWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("EnableViewTriggerWizard-tab-title");
  }
  
  public String getSqlCode() {
    return "ALTER TRIGGER " +SQLUtil.createSqlName(schemaName, comboTriggers.getSelectedItem().toString()) +" " +comboState.getSelectedItem().toString();
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
    comboTables = new javax.swing.JComboBox();
    jLabel3 = new javax.swing.JLabel();
    comboTriggers = new javax.swing.JComboBox();
    jLabel4 = new javax.swing.JLabel();
    comboState = new javax.swing.JComboBox();

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("view-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("trigger-list-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("state-dd")); // NOI18N

    comboState.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ENABLE", "DISABLE" }));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTables, 0, 196, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTriggers, 0, 196, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboState, 0, 196, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTables, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(comboTriggers, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(comboState, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(25, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboState;
  private javax.swing.JComboBox comboTables;
  private javax.swing.JComboBox comboTriggers;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  // End of variables declaration//GEN-END:variables
  
}
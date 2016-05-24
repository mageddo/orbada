package pl.mpak.orbada.oracle.gui.wizards.mview;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dbinfo.OracleMViewInfo;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.orbada.oracle.gui.util.MViewComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.MViewIndexComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.MViewItemListener;
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
public class RebuildMViewIndexWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private String schemaName;
  private String tableName;
  private String indexName;
  
  public RebuildMViewIndexWizard(Database database, String schemaName, String tableName, String indexName) {
    this.database = database;
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.indexName = indexName;
    initComponents();
    init();
  }
  
  private void init() {
    comboIndexes.setModel(new MViewIndexComboBoxModel(database));
    comboTables.setModel(new MViewComboBoxModel(database));
    
    comboTables.addItemListener(new MViewItemListener() {
      public void itemChanged(OracleMViewInfo info) {
        ((MViewIndexComboBoxModel)comboIndexes.getModel()).change(info.getSchema().getName(), info.getName());
      }
    });
  }
  
  public void wizardShow() {
    ((MViewComboBoxModel)comboTables.getModel()).change(schemaName);
    ((MViewComboBoxModel)comboTables.getModel()).select(tableName, comboTables);
    ((MViewIndexComboBoxModel)comboIndexes.getModel()).select(indexName, comboIndexes);
  }
  
  public String getDialogTitle() {
    return stringManager.getString("RebuildMViewIndexWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("RebuildMViewIndexWizard-tab-title");
  }
  
  public String getSqlCode() {
    return "ALTER INDEX " +SQLUtil.createSqlName(schemaName, comboIndexes.getSelectedItem().toString()) +" REBUILD";
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
    comboIndexes = new javax.swing.JComboBox();

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("view-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("index-list-dd")); // NOI18N

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
            .addComponent(comboIndexes, 0, 196, Short.MAX_VALUE)))
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
          .addComponent(comboIndexes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboIndexes;
  private javax.swing.JComboBox comboTables;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  // End of variables declaration//GEN-END:variables
  
}

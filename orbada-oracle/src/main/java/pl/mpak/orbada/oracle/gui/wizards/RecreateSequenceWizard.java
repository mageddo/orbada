package pl.mpak.orbada.oracle.gui.wizards;

import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dbinfo.OracleSequenceInfo;
import pl.mpak.orbada.oracle.gui.util.SequenceComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.SequenceItemListener;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class RecreateSequenceWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  private String schemaName;
  private String sequenceName;
  
  public RecreateSequenceWizard(Database database, String schemaName, String sequenceName) {
    this.database = database;
    this.schemaName = schemaName;
    this.sequenceName = sequenceName;
    initComponents();
    init();
  }
  
  private void init() {
    schemaName = schemaName == null ? OracleDbInfoProvider.getCurrentSchema(database) : schemaName;
    comboSequences.setModel(new SequenceComboBoxModel(database));
    comboSequences.addItemListener(new SequenceItemListener() {
      public void itemChanged(OracleSequenceInfo info) {
        textStart.setText(info.getLastValue().toString());
        textMin.setText(info.getMinimumValue().toString());
        textMax.setText("999999999999999999999999999".equals(info.getMaximumValue().toString()) ? "" : info.getMaximumValue().toString());
        textInc.setText(info.getIncrement().toString());
        textCacheSize.setText("0".equals(info.getBufferSize().toString()) ? "" : info.getBufferSize().toString());
        checkCycle.setSelected("YES".equals(info.getCycle()));
        checkOrder.setSelected("YES".equals(info.getOrdered()));
      }
    });

  }
  
  public void wizardShow() {
    ((SequenceComboBoxModel)comboSequences.getModel()).change(schemaName);
    ((SequenceComboBoxModel)comboSequences.getModel()).select(sequenceName, comboSequences);
  }
  
  public String getDialogTitle() {
    return stringManager.getString("RecreateSequenceWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("RecreateSequenceWizard-tab-title");
  }
  
  public String getSqlCode() {
    return 
      "DROP SEQUENCE " +SQLUtil.createSqlName(schemaName, comboSequences.getSelectedItem().toString()) +"\n/\n" +
      "CREATE SEQUENCE " +SQLUtil.createSqlName(schemaName, comboSequences.getSelectedItem().toString()) +"\n" +
      "  START WITH " +textStart.getText() +"\n" +
      "  INCREMENT BY " +textInc.getText() +"\n" +
      "  " +(StringUtil.isEmpty(textMin.getText()) ? "NOMINVALUE" : "MINVALUE " +textMin.getText()) +"\n" +
      "  " +(StringUtil.isEmpty(textMax.getText()) ? "NOMAXVALUE" : "MAXVALUE " +textMax.getText()) +"\n" +
      "  " +(StringUtil.isEmpty(textCacheSize.getText()) ? "NOCACHE" : "CACHE " +textCacheSize.getText()) +"\n" +
      "  " +(checkCycle.isSelected() ? "CYCLE" : "NOCYCLE") +"\n" +
      "  " +(checkOrder.isSelected() ? "ORDER" : "NOORDER") +"\n/";
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

    jLabel8 = new javax.swing.JLabel();
    comboSequences = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    textStart = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    textMin = new pl.mpak.sky.gui.swing.comp.TextField();
    textMax = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    textCacheSize = new pl.mpak.sky.gui.swing.comp.TextField();
    checkCycle = new javax.swing.JCheckBox();
    checkOrder = new javax.swing.JCheckBox();
    jLabel6 = new javax.swing.JLabel();
    textInc = new pl.mpak.sky.gui.swing.comp.TextField();

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText(stringManager.getString("sequence-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("start-at-dd")); // NOI18N

    textStart.setText("1");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("minimum-value-dd")); // NOI18N

    textMin.setText("1");

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("maximum-value-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("buffer-size-dd")); // NOI18N

    checkCycle.setText(stringManager.getString("cycle")); // NOI18N

    checkOrder.setText(stringManager.getString("keep-order")); // NOI18N

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("change-by-dd")); // NOI18N

    textInc.setText("1");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboSequences, 0, 233, Short.MAX_VALUE))
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
              .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(textInc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
              .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(textMax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
              .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(textMin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
              .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(textStart, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(checkCycle)
              .addComponent(checkOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(textCacheSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(comboSequences, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(textInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(textCacheSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkCycle)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkOrder)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkCycle;
  private javax.swing.JCheckBox checkOrder;
  private javax.swing.JComboBox comboSequences;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel8;
  private pl.mpak.sky.gui.swing.comp.TextField textCacheSize;
  private pl.mpak.sky.gui.swing.comp.TextField textInc;
  private pl.mpak.sky.gui.swing.comp.TextField textMax;
  private pl.mpak.sky.gui.swing.comp.TextField textMin;
  private pl.mpak.sky.gui.swing.comp.TextField textStart;
  // End of variables declaration//GEN-END:variables
  
}

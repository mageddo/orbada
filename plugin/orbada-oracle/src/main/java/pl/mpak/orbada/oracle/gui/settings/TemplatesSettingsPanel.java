package pl.mpak.orbada.oracle.gui.settings;

import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.services.OracleTemplatesSettingsProvider;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TemplatesSettingsPanel extends javax.swing.JPanel implements ISettingsComponent {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IApplication application;
  private ISettings settings;

  /** Creates new form DatabaseSettingsPanel */
  public TemplatesSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }
  
  private void init() {
    Query query = InternalDatabase.get().createQuery();
    try {
      ArrayList<String> teplateList = new ArrayList<String>();
      query.setSqlText(
        "select distinct tpl_name\n" +
        "  from templates\n" +
        " where (tpl_usr_id = :usr_id or tpl_usr_id is null)\n" +
        " order by tpl_name");
      query.paramByName("usr_id").setString(application.getUserId());
      query.open();
      while (!query.eof()) {
        teplateList.add(query.fieldByName("tpl_name").getString());
        query.next();
      }
      comboFunction.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboJavaSource.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboObjectType.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboObjectTypeBody.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboPackage.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboPackageBody.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboProcedure.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboTableType.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboTrigger.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboVarrayType.setModel(new DefaultComboBoxModel(teplateList.toArray()));
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }

    settings = application.getSettings(OracleTemplatesSettingsProvider.settingsName);
    restoreSettings();
  }
  
  public void restoreSettings() {
    comboFunction.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setFunction, "oracle-function"));
    comboJavaSource.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setJavaSource, "oracle-java-source"));
    comboObjectType.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setObjectType, "oracle-object-type"));
    comboObjectTypeBody.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setObjectTypeBody, "oracle-object-type-body"));
    comboPackage.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setPackage, "oracle-package"));
    comboPackageBody.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setPackageBody, "oracle-package-body"));
    comboProcedure.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setProcedure, "oracle-procedure"));
    comboTableType.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setTableType, "oracle-table-type"));
    comboTrigger.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setTrigger, "oracle-trigger"));
    comboVarrayType.setSelectedItem(settings.getValue(OracleTemplatesSettingsProvider.setVarrayType, "oracle-varray-type"));
  }

  public void applySettings() {
    settings.setValue(OracleTemplatesSettingsProvider.setFunction, comboFunction.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setJavaSource, comboJavaSource.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setObjectType, comboObjectType.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setObjectTypeBody, comboObjectTypeBody.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setPackage, comboPackage.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setPackageBody, comboPackageBody.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setProcedure, comboProcedure.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setTableType, comboTableType.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setTrigger, comboTrigger.getSelectedItem().toString());
    settings.setValue(OracleTemplatesSettingsProvider.setVarrayType, comboVarrayType.getSelectedItem().toString());
    settings.store();
  }

  public void cancelSettings() {
    restoreSettings();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    comboTrigger = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    comboFunction = new javax.swing.JComboBox();
    comboProcedure = new javax.swing.JComboBox();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    comboPackage = new javax.swing.JComboBox();
    jLabel5 = new javax.swing.JLabel();
    comboPackageBody = new javax.swing.JComboBox();
    jLabel6 = new javax.swing.JLabel();
    comboObjectType = new javax.swing.JComboBox();
    jLabel7 = new javax.swing.JLabel();
    comboObjectTypeBody = new javax.swing.JComboBox();
    jLabel8 = new javax.swing.JLabel();
    comboTableType = new javax.swing.JComboBox();
    jLabel9 = new javax.swing.JLabel();
    comboVarrayType = new javax.swing.JComboBox();
    jLabel10 = new javax.swing.JLabel();
    comboJavaSource = new javax.swing.JComboBox();

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("trigger-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("function-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("procedure-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("package-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("package-body-dd")); // NOI18N

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("object-type-dd")); // NOI18N

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(stringManager.getString("object-body-type-dd")); // NOI18N

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText(stringManager.getString("table-type-dd")); // NOI18N

    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText(stringManager.getString("varray-type-dd")); // NOI18N

    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel10.setText(stringManager.getString("java-source-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTrigger, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboFunction, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboProcedure, 0, 285, Short.MAX_VALUE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboPackage, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboPackageBody, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboObjectType, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboObjectTypeBody, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTableType, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboVarrayType, 0, 285, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboJavaSource, 0, 285, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(comboTrigger, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(comboProcedure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(comboPackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(comboPackageBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(comboObjectType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(comboObjectTypeBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(comboTableType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel9)
          .addComponent(comboVarrayType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel10)
          .addComponent(comboJavaSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(69, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboFunction;
  private javax.swing.JComboBox comboJavaSource;
  private javax.swing.JComboBox comboObjectType;
  private javax.swing.JComboBox comboObjectTypeBody;
  private javax.swing.JComboBox comboPackage;
  private javax.swing.JComboBox comboPackageBody;
  private javax.swing.JComboBox comboProcedure;
  private javax.swing.JComboBox comboTableType;
  private javax.swing.JComboBox comboTrigger;
  private javax.swing.JComboBox comboVarrayType;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  // End of variables declaration//GEN-END:variables
  
}

package pl.mpak.orbada.mysql.gui.settings;

import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.services.MySQLTemplatesSettingsProvider;
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
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

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
      comboProcedure.setModel(new DefaultComboBoxModel(teplateList.toArray()));
      comboTrigger.setModel(new DefaultComboBoxModel(teplateList.toArray()));
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }

    settings = application.getSettings(MySQLTemplatesSettingsProvider.settingsName);
    restoreSettings();
  }
  
  public void restoreSettings() {
    comboFunction.setSelectedItem(settings.getValue(MySQLTemplatesSettingsProvider.setFunction, "mysql-function"));
    comboProcedure.setSelectedItem(settings.getValue(MySQLTemplatesSettingsProvider.setProcedure, "mysql-procedure"));
    comboTrigger.setSelectedItem(settings.getValue(MySQLTemplatesSettingsProvider.setTrigger, "mysql-trigger"));
  }

  public void applySettings() {
    settings.setValue(MySQLTemplatesSettingsProvider.setFunction, comboFunction.getSelectedItem().toString());
    settings.setValue(MySQLTemplatesSettingsProvider.setProcedure, comboProcedure.getSelectedItem().toString());
    settings.setValue(MySQLTemplatesSettingsProvider.setTrigger, comboTrigger.getSelectedItem().toString());
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

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("trigger-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("function-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("procedure-dd")); // NOI18N

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
            .addComponent(comboProcedure, 0, 285, Short.MAX_VALUE)))
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
        .addContainerGap(265, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboFunction;
  private javax.swing.JComboBox comboProcedure;
  private javax.swing.JComboBox comboTrigger;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  // End of variables declaration//GEN-END:variables
  
}

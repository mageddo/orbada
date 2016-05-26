package pl.mpak.orbada.oracle.gui.settings;

import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.services.OracleCompileErrorSettingsProvider;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class CompileErrorSettingsPanel extends javax.swing.JPanel implements ISettingsComponent {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IApplication application;
  private ISettings settings;

  /** Creates new form DatabaseSettingsPanel */
  public CompileErrorSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }
  
  private void init() {
    settings = application.getSettings(OracleCompileErrorSettingsProvider.settingsName);
    restoreSettings();
  }
  
  public void restoreSettings() {
    checkErrorGoToTab.setSelected(settings.getValue(OracleCompileErrorSettingsProvider.setOnErrorGoToTab, true));
    panelColorErrorLine.setBackground(settings.getValue(OracleCompileErrorSettingsProvider.setErrorLineColor, SwingUtil.Color.DARKORANGE));
  }

  public void applySettings() {
    settings.setValue(OracleCompileErrorSettingsProvider.setOnErrorGoToTab, checkErrorGoToTab.isSelected());
    settings.setValue(OracleCompileErrorSettingsProvider.setErrorLineColor, panelColorErrorLine.getBackground());
    settings.store();
  }

  public void cancelSettings() {
    restoreSettings();
  }
  
  private void changeDataColor(JPanel panel) {
    Color color = JColorChooser.showDialog(this, stringManager.getString("select-color"), panel.getBackground());
    if (color != null) {
      panel.setBackground(color);
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmColorErrorLine = new pl.mpak.sky.gui.swing.Action();
    checkErrorGoToTab = new javax.swing.JCheckBox();
    panelColorErrorLine = new javax.swing.JPanel();
    jButton1 = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();

    cmColorErrorLine.setActionCommandKey("cmColorErrorLine");
    cmColorErrorLine.setText(stringManager.getString("cmColorErrorLine-text")); // NOI18N
    cmColorErrorLine.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmColorErrorLineActionPerformed(evt);
      }
    });

    checkErrorGoToTab.setSelected(true);
    checkErrorGoToTab.setText(stringManager.getString("CompileErrorSettingsPanel-checkErrorGotoTab-text")); // NOI18N

    panelColorErrorLine.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

    javax.swing.GroupLayout panelColorErrorLineLayout = new javax.swing.GroupLayout(panelColorErrorLine);
    panelColorErrorLine.setLayout(panelColorErrorLineLayout);
    panelColorErrorLineLayout.setHorizontalGroup(
      panelColorErrorLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 104, Short.MAX_VALUE)
    );
    panelColorErrorLineLayout.setVerticalGroup(
      panelColorErrorLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 19, Short.MAX_VALUE)
    );

    jButton1.setAction(cmColorErrorLine);
    jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jButton1.setPreferredSize(new java.awt.Dimension(75, 23));

    jLabel1.setText(stringManager.getString("CompileErrorSettingsPanel-select-color-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(checkErrorGoToTab)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(panelColorErrorLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(54, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkErrorGoToTab)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(panelColorErrorLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap(276, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmColorErrorLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorErrorLineActionPerformed
  changeDataColor(panelColorErrorLine);
}//GEN-LAST:event_cmColorErrorLineActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkErrorGoToTab;
  private pl.mpak.sky.gui.swing.Action cmColorErrorLine;
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel panelColorErrorLine;
  // End of variables declaration//GEN-END:variables
  
}
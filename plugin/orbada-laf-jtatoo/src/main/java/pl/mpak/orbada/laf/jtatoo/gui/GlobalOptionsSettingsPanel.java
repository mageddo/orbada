/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.OrbadaLafJTatooPlugin;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAcrylLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAeroLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAluminiumLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooBernsteinLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooFastLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooGraphiteLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooHiFiLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooLunaLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooMcWinLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooMintLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooNoireLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooSmartLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.starters.abs.JTatooLookAndFeelStarter;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class GlobalOptionsSettingsPanel extends javax.swing.JPanel implements ISettingsComponent {

  private StringManager i18n = StringManagerFactory.getStringManager("laf-jtatoo");

  private IApplication application;
  private File propFile;
  private Properties props;
  private int selectedTheme = -1;
  private String lafId;
  
  private static final HashMap<String, String[]> themes;
  static {
    themes = new HashMap<String, String[]>();
    themes.put(JTatooAcrylLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font", "Green", "Green-Small-Font", 
      "Green-Large-Font", "Green-Giant-Font", "Lemmon", "Lemmon-Small-Font", "Lemmon-Large-Font",
      "Lemmon-Giant-Font", "Red", "Red-Small-Font", "Red-Large-Font", "Red-Giant-Font"});
    themes.put(JTatooAeroLookAndFeelService.lookAndFeelId, new String[] { 
      "Default", "Small-Font", "Large-Font", "Giant-Font", "Gold", "Gold-Small-Font", "Gold-Large-Font",
      "Gold-Giant-Font", "Green", "Green-Small-Font", "Green-Large-Font", "Green-Giant-Font"});
    themes.put(JTatooAluminiumLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooBernsteinLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooFastLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font", "Blue", "Blue-Small-Font", 
      "Blue-Large-Font", "Blue-Giant-Font", "Green", "Green-Small-Font", "Green-Large-Font", "Green-Giant-Font"});
    themes.put(JTatooGraphiteLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font", "Green-Small-Font", "Green-Medium-Font", 
      "Green-Large-Font", "Blue-Small-Font", "Blue-Medium-Font", "Blue-Large-Font"});
    themes.put(JTatooHiFiLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooLunaLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooMcWinLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooMintLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooNoireLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font"});
    themes.put(JTatooSmartLookAndFeelService.lookAndFeelId, new String[] {
      "Default", "Small-Font", "Large-Font", "Giant-Font", "Gold", "Gold-Small-Font", 
      "Gold-Large-Font", "Gold-Giant-Font", "Green", "Green-Small-Font", "Green-Large-Font", 
      "Green-Giant-Font", "Brown", "Brown-Small-Font", "Brown-Large-Font", "Brown-Giant-Font", 
      "Lemmon", "Lemmon-Small-Font", "Lemmon-Large-Font", "Lemmon-Giant-Font", "Gray", 
      "Gray-Small-Font", "Gray-Large-Font", "Gray-Giant-Font"});
  }

  /** Creates new form GlobalOptionsSettingsPanel */
  public GlobalOptionsSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }

  private void init() {
    propFile = new File(application.getConfigPath() + "/" +JTatooLookAndFeelStarter.configFileName);
    props = new Properties();
    if (propFile.exists()) {
      try {
        props.load(new FileInputStream(propFile));
      } catch (Exception ex) {
        MessageBox.show(this, i18n.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
        ExceptionUtil.processException(ex);
      }
    }
    lafId = application.getSettings(Consts.orbadaSettings).getValue(Consts.lookAndFeelSetting, "");

    restoreSettings();
  }

  @Override
  public void restoreSettings() {
    textLicenseKey.setText(props.getProperty(JTatooLookAndFeelStarter.set_LicenseKey));
    textCompanyName.setText(props.getProperty(JTatooLookAndFeelStarter.set_CompanyName));
    
    String[] themeList = themes.get(lafId);
    if (themeList != null) {
      comboThemes.setModel(new DefaultComboBoxModel(themeList));
      String currentTheme = props.getProperty(lafId +JTatooLookAndFeelStarter.set_CurrentTheme, themeList[0]);
      for (int i=0; i<themeList.length; i++) {
        if (themeList[i].equals(currentTheme)) {
          comboThemes.setSelectedIndex(i);
          selectedTheme = i;
          break;
        }
      }
      if (comboThemes.getSelectedIndex() == -1) {
        comboThemes.setSelectedIndex(0);
        selectedTheme = 0;
      }
      labelThemes.setEnabled(true);
      comboThemes.setEnabled(true);
    }
    else {
      comboThemes.setModel(new DefaultComboBoxModel());
      labelThemes.setEnabled(false);
      comboThemes.setEnabled(false);
    }
  }

  @Override
  public void applySettings() {
    props.setProperty(JTatooLookAndFeelStarter.set_LicenseKey, textLicenseKey.getText());
    props.setProperty(JTatooLookAndFeelStarter.set_CompanyName, textCompanyName.getText());
    if (comboThemes.isEnabled()) {
      props.setProperty(lafId +JTatooLookAndFeelStarter.set_CurrentTheme, comboThemes.getText());
    }
    
    try {
      props.store(new FileOutputStream(propFile), "");
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    if (selectedTheme != comboThemes.getSelectedIndex()) {
      application.updateLAF();
    }
  }

  @Override
  public void cancelSettings() {
    restoreSettings();
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    labelThemes = new javax.swing.JLabel();
    comboThemes = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel2 = new javax.swing.JLabel();
    textLicenseKey = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    textCompanyName = new pl.mpak.sky.gui.swing.comp.TextField();

    labelThemes.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    labelThemes.setText(i18n.getString("jtatoo-current-theme-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel2.setText(i18n.getString("jtatoo-license-key-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel3.setText(i18n.getString("jtatoo-company-name-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(labelThemes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(textCompanyName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(textLicenseKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(comboThemes, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
        .addContainerGap(221, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelThemes)
          .addComponent(comboThemes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textLicenseKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textCompanyName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(256, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ComboBox comboThemes;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel labelThemes;
  private pl.mpak.sky.gui.swing.comp.TextField textCompanyName;
  private pl.mpak.sky.gui.swing.comp.TextField textLicenseKey;
  // End of variables declaration//GEN-END:variables

}

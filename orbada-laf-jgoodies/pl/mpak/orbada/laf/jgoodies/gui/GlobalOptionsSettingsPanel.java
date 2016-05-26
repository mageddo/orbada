/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GlobalOptionsSettingsPanel.java
 *
 * Created on 2010-10-31, 21:01:53
 */

package pl.mpak.orbada.laf.jgoodies.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.swing.plaf.metal.MetalTheme;
import orbada.Consts;
import pl.mpak.orbada.laf.jgoodies.OrbadaLafJGoodiesPlugin;
import pl.mpak.orbada.laf.jgoodies.services.Plastic3DLookAndFeelService;
import pl.mpak.orbada.laf.jgoodies.services.PlasticLookAndFeelService;
import pl.mpak.orbada.laf.jgoodies.services.PlasticXPLookAndFeelService;
import pl.mpak.orbada.laf.jgoodies.services.WindowsLookAndFeelService;
import pl.mpak.orbada.laf.jgoodies.starters.abs.JGoodiesLookAndFeelStarter;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class GlobalOptionsSettingsPanel extends javax.swing.JPanel implements ISettingsComponent {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLafJGoodiesPlugin.class);

  private IApplication application;
  private File propFile;
  private Properties props;
  private String[] themeNames;
  private Class[] themeClasses = {
    com.jgoodies.looks.plastic.theme.BrownSugar.class,
    com.jgoodies.looks.plastic.theme.DarkStar.class,
    com.jgoodies.looks.plastic.theme.DesertBlue.class,
    com.jgoodies.looks.plastic.theme.DesertGreen.class,
    com.jgoodies.looks.plastic.theme.DesertRed.class,
    com.jgoodies.looks.plastic.theme.DesertYellow.class,
    com.jgoodies.looks.plastic.theme.ExperienceBlue.class,
    com.jgoodies.looks.plastic.theme.ExperienceGreen.class,
    com.jgoodies.looks.plastic.theme.ExperienceRoyale.class,
    com.jgoodies.looks.plastic.theme.LightGray.class,
    com.jgoodies.looks.plastic.theme.Silver.class,
    com.jgoodies.looks.plastic.theme.SkyBlue.class,
    com.jgoodies.looks.plastic.theme.SkyBluer.class,
    com.jgoodies.looks.plastic.theme.SkyGreen.class,
    com.jgoodies.looks.plastic.theme.SkyKrupp.class,
    com.jgoodies.looks.plastic.theme.SkyPink.class,
    com.jgoodies.looks.plastic.theme.SkyRed.class,
    com.jgoodies.looks.plastic.theme.SkyYellow.class
  };
  private int selectedTheme = -1;

  /** Creates new form GlobalOptionsSettingsPanel */
  public GlobalOptionsSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }

  private void init() {
    themeNames = new String[themeClasses.length];
    for (int i=0; i<themeClasses.length; i++) {
      try {
        themeNames[i] = ((MetalTheme) themeClasses[i].newInstance()).getName();
      } catch (Exception ex) {
        themeNames[i] = "???";
        ExceptionUtil.processException(ex);
      }
    }
    comboTheme.setModel(new javax.swing.DefaultComboBoxModel(themeNames));

    propFile = new File(application.getConfigPath() + "/" +JGoodiesLookAndFeelStarter.jgoodiesConfigFileName);
    props = new Properties();
    if (propFile.exists()) {
      try {
        props.load(new FileInputStream(propFile));
      } catch (Exception ex) {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
        ExceptionUtil.processException(ex);
      }
    }

    restoreSettings();
  }

  @Override
  public void restoreSettings() {
    checkPopupDropShadowEnabled.setSelected(StringUtil.toBoolean(props.getProperty(JGoodiesLookAndFeelStarter.set_PopupDropShadowEnabled, "true")));
    checkTabIconsEnabled.setSelected(StringUtil.toBoolean(props.getProperty(JGoodiesLookAndFeelStarter.set_TabIconsEnabled, "true")));
    checkUseNarrowButtons.setSelected(StringUtil.toBoolean(props.getProperty(JGoodiesLookAndFeelStarter.set_UseNarrowButtons, "true")));
    checkUseSystemFonts.setSelected(StringUtil.toBoolean(props.getProperty(JGoodiesLookAndFeelStarter.set_UseSystemFonts, "true")));

    String currentTheme = props.getProperty(JGoodiesLookAndFeelStarter.set_CurrentTheme, themeClasses[0].getName());
    for (int i=0; i<themeClasses.length; i++) {
      if (themeClasses[i].getName().equals(currentTheme)) {
        comboTheme.setSelectedIndex(i);
        break;
      }
    }
    if (comboTheme.getSelectedIndex() == -1) {
      comboTheme.setSelectedIndex(0);
    }

    comboTabStyle.setSelectedItem(props.getProperty(JGoodiesLookAndFeelStarter.set_TabStyle, "Metal"));
  }

  @Override
  public void applySettings() {
    props.setProperty(JGoodiesLookAndFeelStarter.set_PopupDropShadowEnabled, checkPopupDropShadowEnabled.isSelected() ? "true" : "false");
    props.setProperty(JGoodiesLookAndFeelStarter.set_TabIconsEnabled, checkTabIconsEnabled.isSelected() ? "true" : "false");
    props.setProperty(JGoodiesLookAndFeelStarter.set_UseNarrowButtons, checkUseNarrowButtons.isSelected() ? "true" : "false");
    props.setProperty(JGoodiesLookAndFeelStarter.set_UseSystemFonts, checkUseSystemFonts.isSelected() ? "true" : "false");
    props.setProperty(JGoodiesLookAndFeelStarter.set_CurrentTheme, themeClasses[comboTheme.getSelectedIndex()].getName());
    props.setProperty(JGoodiesLookAndFeelStarter.set_TabStyle, comboTabStyle.getText());
    
    try {
      props.store(new FileOutputStream(propFile), "");
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    if (selectedTheme != comboTheme.getSelectedIndex() &&
        StringUtil.equalAnyOfString(
          application.getSettings(Consts.orbadaSettings).getValue(Consts.lookAndFeelSetting, ""),
          new String[] {
            WindowsLookAndFeelService.lookAndFeelId,
            PlasticLookAndFeelService.lookAndFeelId,
            Plastic3DLookAndFeelService.lookAndFeelId,
            PlasticXPLookAndFeelService.lookAndFeelId
          }
        )) {
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

    checkPopupDropShadowEnabled = new javax.swing.JCheckBox();
    checkTabIconsEnabled = new javax.swing.JCheckBox();
    checkUseNarrowButtons = new javax.swing.JCheckBox();
    checkUseSystemFonts = new javax.swing.JCheckBox();
    jPanel1 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    comboTheme = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel1 = new javax.swing.JLabel();
    comboTabStyle = new pl.mpak.sky.gui.swing.comp.ComboBox();

    checkPopupDropShadowEnabled.setText(stringManager.getString("checkPopupDropShadowEnabled-text")); // NOI18N

    checkTabIconsEnabled.setText(stringManager.getString("checkTabIconsEnabled-text")); // NOI18N

    checkUseNarrowButtons.setText(stringManager.getString("checkUseNarrowButtons-text")); // NOI18N

    checkUseSystemFonts.setText(stringManager.getString("checkUseSystemFonts-text")); // NOI18N

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("settings-for-plastic-laf"))); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("theme-dd")); // NOI18N

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("tab-style-dd")); // NOI18N

    comboTabStyle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Metal" }));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTabStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(219, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTheme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(comboTabStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(checkPopupDropShadowEnabled)
          .addComponent(checkTabIconsEnabled)
          .addComponent(checkUseNarrowButtons)
          .addComponent(checkUseSystemFonts))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkPopupDropShadowEnabled)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkTabIconsEnabled)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkUseNarrowButtons)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkUseSystemFonts)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(154, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkPopupDropShadowEnabled;
  private javax.swing.JCheckBox checkTabIconsEnabled;
  private javax.swing.JCheckBox checkUseNarrowButtons;
  private javax.swing.JCheckBox checkUseSystemFonts;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboTabStyle;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboTheme;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  // End of variables declaration//GEN-END:variables

}

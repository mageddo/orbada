/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GlobalOptionsSettingsPanel.java
 *
 * Created on 2010-10-31, 21:01:53
 */

package pl.mpak.orbada.laf.tinylaf.gui;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.ThemeDescription;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.tinylaf.OrbadaLafTinyLaFPlugin;
import pl.mpak.orbada.laf.tinylaf.services.TinyLookAndFeelService;
import pl.mpak.orbada.laf.tinylaf.starters.TinyLookAndFeelStarter;
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

  private StringManager stringManager = StringManagerFactory.getStringManager("laf-tinylaf");

  private IApplication application;
  private File propFile;
  private Properties props;
  private int selectedTheme = -1;
  private ThemeDescription[] themes;

  /** Creates new form GlobalOptionsSettingsPanel */
  public GlobalOptionsSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }

  private void init() {
    themes = Theme.getAvailableThemes();
    comboTheme.setModel(new javax.swing.DefaultComboBoxModel(themes));

    propFile = new File(application.getConfigPath() + "/" +TinyLookAndFeelStarter.tinyLaFConfigFileName);
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
    String currentTheme = props.getProperty(TinyLookAndFeelStarter.set_CurrentTheme, themes[0].getName());
    for (int i=0; i<themes.length; i++) {
      if (themes[i].getName().equals(currentTheme)) {
        comboTheme.setSelectedIndex(i);
        break;
      }
    }
    if (comboTheme.getSelectedIndex() == -1) {
      comboTheme.setSelectedIndex(0);
    }
  }

  @Override
  public void applySettings() {
    props.setProperty(TinyLookAndFeelStarter.set_CurrentTheme, themes[comboTheme.getSelectedIndex()].getName());
    
    try {
      props.store(new FileOutputStream(propFile), "");
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    if (selectedTheme != comboTheme.getSelectedIndex() &&
        TinyLookAndFeelService.lookAndFeelId.equals(application.getSettings(Consts.orbadaSettings).getValue(Consts.lookAndFeelSetting, ""))) {
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

    jLabel2 = new javax.swing.JLabel();
    comboTheme = new pl.mpak.sky.gui.swing.comp.ComboBox();

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("theme-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(comboTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(279, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTheme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(309, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ComboBox comboTheme;
  private javax.swing.JLabel jLabel2;
  // End of variables declaration//GEN-END:variables

}

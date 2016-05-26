/*
 * GeneralSettingsProvider.java
 *
 * Created on 10 grudzieñ 2007, 20:05
 */

package pl.mpak.orbada.universal.gui;

import javax.swing.SpinnerNumberModel;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.execmd.ExecutedSqlManager;
import pl.mpak.orbada.universal.services.UniversalSettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class GeneralSettingsPanel extends javax.swing.JPanel implements ISettingsComponent {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  private IApplication application;
  private ISettings settings;
  
  /** Creates new form GeneralSettingsProvider */
  public GeneralSettingsPanel(IApplication application) {
    this.application = application;
    initComponents();
    init();
  }
  
  private void init() {
    comboAutoSaveDitor.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
      stringManager.getString("for-driver"),
      stringManager.getString("for-database-server"),
      stringManager.getString("for-connection-schema") }));
    spinDeleteAfterDays.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
    spinMaxColumnListCount.setModel(new SpinnerNumberModel(UniversalSettingsProvider.default_setMaxColumnListCount, 2, 100, 1));
    spinAutoSaveEditorContentIntervalSecond.setModel(new SpinnerNumberModel(60, 10, 3600, 10));
    settings = application.getSettings(UniversalSettingsProvider.settingsName);
    restoreSettings();
  }
  
  public void restoreSettings() {
    spinDeleteAfterDays.setValue(settings.getValue(UniversalSettingsProvider.setDeleteAfterDays, (long)ExecutedSqlManager.deleteAfterDays).intValue());
    comboAutoSaveDitor.setSelectedIndex(settings.getValue(UniversalSettingsProvider.setAutoSaveEditor, (long)UniversalSettingsProvider.AUTO_SAVE_EDITOR_SCHEMA).intValue());
    spinAutoSaveEditorContentIntervalSecond.setValue(settings.getValue(UniversalSettingsProvider.setAutoSaveEditorContentIntervalSeconds, 60L).intValue());
    checkAutoSaveEditorContent.setSelected(settings.getValue(UniversalSettingsProvider.setAutoSaveEditorContent, checkAutoSaveEditorContent.isSelected()));
    checkOnErrorShowMessageBox.setSelected(settings.getValue(UniversalSettingsProvider.setOnErrorShowMessageBox, checkOnErrorShowMessageBox.isSelected()));
    checkAutoExpandSqlText.setSelected(settings.getValue(UniversalSettingsProvider.setAutoExpandSqlText, checkAutoExpandSqlText.isSelected()));
    checkNewEditorContent.setSelected(settings.getValue(UniversalSettingsProvider.setNewEditorContent, checkNewEditorContent.isSelected()));
    checkStoringColumnListPosition.setSelected(settings.getValue(UniversalSettingsProvider.setStoringColumnListPosition, UniversalSettingsProvider.default_setStoringColumnListPosition));
    spinMaxColumnListCount.setValue(settings.getValue(UniversalSettingsProvider.setMaxColumnListCount, (long)UniversalSettingsProvider.default_setMaxColumnListCount).intValue());
    checkSplitPanelVertical.setSelected(settings.getValue(UniversalSettingsProvider.setSplitPanelVertical, UniversalSettingsProvider.default_setSplitPanelVertical));
    checkAutoCloneConnetion.setSelected(settings.getValue(UniversalSettingsProvider.setAutoCloneConnection, UniversalSettingsProvider.default_setAutoCloneConnection));
    checkCommentAsTabTitle.setSelected(settings.getValue(UniversalSettingsProvider.setCommentAtFirstLineTitle, checkCommentAsTabTitle.isSelected()));
  }

  public void applySettings() {
    settings.setValue(UniversalSettingsProvider.setDeleteAfterDays, (long)(Integer)spinDeleteAfterDays.getValue());
    settings.setValue(UniversalSettingsProvider.setAutoSaveEditor, (long)comboAutoSaveDitor.getSelectedIndex());
    settings.setValue(UniversalSettingsProvider.setAutoSaveEditorContentIntervalSeconds, (long)(Integer)spinAutoSaveEditorContentIntervalSecond.getValue());
    settings.setValue(UniversalSettingsProvider.setAutoSaveEditorContent, checkAutoSaveEditorContent.isSelected());
    settings.setValue(UniversalSettingsProvider.setOnErrorShowMessageBox, checkOnErrorShowMessageBox.isSelected());
    settings.setValue(UniversalSettingsProvider.setAutoExpandSqlText, checkAutoExpandSqlText.isSelected());
    settings.setValue(UniversalSettingsProvider.setNewEditorContent, checkNewEditorContent.isSelected());
    settings.setValue(UniversalSettingsProvider.setStoringColumnListPosition, checkStoringColumnListPosition.isSelected());
    settings.setValue(UniversalSettingsProvider.setMaxColumnListCount, (long)(Integer)spinMaxColumnListCount.getValue());
    settings.setValue(UniversalSettingsProvider.setSplitPanelVertical, checkSplitPanelVertical.isSelected());
    settings.setValue(UniversalSettingsProvider.setAutoCloneConnection, checkAutoCloneConnetion.isSelected());
    settings.setValue(UniversalSettingsProvider.setCommentAtFirstLineTitle, checkCommentAsTabTitle.isSelected());
    settings.store();
    application.postPluginMessage(new PluginMessage(null, OrbadaUniversalPlugin.universalSettingsRefresh, null));
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
        spinDeleteAfterDays = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        comboAutoSaveDitor = new javax.swing.JComboBox();
        checkAutoSaveEditorContent = new javax.swing.JCheckBox();
        spinAutoSaveEditorContentIntervalSecond = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        checkOnErrorShowMessageBox = new javax.swing.JCheckBox();
        checkAutoExpandSqlText = new javax.swing.JCheckBox();
        checkNewEditorContent = new javax.swing.JCheckBox();
        checkStoringColumnListPosition = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        spinMaxColumnListCount = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        checkSplitPanelVertical = new javax.swing.JCheckBox();
        checkAutoCloneConnetion = new javax.swing.JCheckBox();
        checkCommentAsTabTitle = new javax.swing.JCheckBox();

        jLabel1.setText(stringManager.getString("GeneralSettingsPanel-exec-sql-remember-dd")); // NOI18N

        jLabel2.setText(stringManager.getString("days")); // NOI18N

        jLabel3.setText(stringManager.getString("GeneralSettingsPanel-content-remember-for-dd")); // NOI18N

        checkAutoSaveEditorContent.setSelected(true);
        checkAutoSaveEditorContent.setText(stringManager.getString("GeneralSettingsPanel-auto-save-editor-content")); // NOI18N

        jLabel5.setText(stringManager.getString("seconds")); // NOI18N

        checkOnErrorShowMessageBox.setText(stringManager.getString("GeneralSettingsPanel-on-error-show-message")); // NOI18N

        checkAutoExpandSqlText.setText(stringManager.getString("GeneralSettingsPanel-auto-expand-sql")); // NOI18N

        checkNewEditorContent.setText(stringManager.getString("GeneralSettingsPanel-new-editor-content")); // NOI18N

        checkStoringColumnListPosition.setText(stringManager.getString("GeneralSettingsPanel-storing-column-list-pos")); // NOI18N

        jLabel6.setText(stringManager.getString("GeneralSettingsPanel-column-list-coun-before")); // NOI18N

        jLabel7.setText(stringManager.getString("GeneralSettingsPanel-column-list-coun-after")); // NOI18N

        checkSplitPanelVertical.setText(stringManager.getString("GeneralSettingsPanel-split-panel-vert")); // NOI18N

        checkAutoCloneConnetion.setText(stringManager.getString("GeneralSettingsPanel-auto-clone-connection")); // NOI18N

        checkCommentAsTabTitle.setText(stringManager.getString("checkCommentAsTabTitle-text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkCommentAsTabTitle)
                    .addComponent(checkAutoCloneConnetion)
                    .addComponent(checkSplitPanelVertical)
                    .addComponent(checkStoringColumnListPosition)
                    .addComponent(checkNewEditorContent)
                    .addComponent(checkAutoExpandSqlText)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinDeleteAfterDays, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboAutoSaveDitor, 0, 254, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkAutoSaveEditorContent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinAutoSaveEditorContentIntervalSecond, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addComponent(checkOnErrorShowMessageBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinMaxColumnListCount, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(spinDeleteAfterDays, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboAutoSaveDitor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAutoSaveEditorContent)
                    .addComponent(spinAutoSaveEditorContentIntervalSecond, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkOnErrorShowMessageBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoExpandSqlText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkNewEditorContent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkStoringColumnListPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(spinMaxColumnListCount, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSplitPanelVertical, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoCloneConnetion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkCommentAsTabTitle)
                .addContainerGap(73, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkAutoCloneConnetion;
    private javax.swing.JCheckBox checkAutoExpandSqlText;
    private javax.swing.JCheckBox checkAutoSaveEditorContent;
    private javax.swing.JCheckBox checkCommentAsTabTitle;
    private javax.swing.JCheckBox checkNewEditorContent;
    private javax.swing.JCheckBox checkOnErrorShowMessageBox;
    private javax.swing.JCheckBox checkSplitPanelVertical;
    private javax.swing.JCheckBox checkStoringColumnListPosition;
    private javax.swing.JComboBox comboAutoSaveDitor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSpinner spinAutoSaveEditorContentIntervalSecond;
    private javax.swing.JSpinner spinDeleteAfterDays;
    private javax.swing.JSpinner spinMaxColumnListCount;
    // End of variables declaration//GEN-END:variables
  
}

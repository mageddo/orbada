package pl.mpak.orbada.oracle.gui.dbmsoutput;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ParameterMetaData;
import java.sql.Types;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.services.OracleDbmsOutputSettingsProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.timer.Timer;

/**
 *
 * @author  akaluza
 */
public class DbmsOutputPanelView extends javax.swing.JPanel implements Closeable {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private IViewAccesibilities accesibilities;
  private boolean viewClosing = false;
  private ISettings settings;
  private boolean outputEnable = false;
  private Icon iconEnabled;
  private Icon iconDisabled;
  private Timer timer;

  /** Creates new form TableTriggersPanel
   * @param accesibilities
   */
  public DbmsOutputPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    iconEnabled = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/service_start.gif");
    iconDisabled = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/service_stop.gif");
    initComponents();
    init();
  }

  private void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), OracleDbmsOutputSettingsProvider.settingsName);
    if (settings.getValue(OracleDbmsOutputSettingsProvider.setUseGlobalSettings, true)) {
      settings = accesibilities.getApplication().getSettings(OracleDbmsOutputSettingsProvider.settingsName);
    }
    timer = new Timer(1000) {
      public void run() {
        refreshOutput();
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timer);
    if (settings.getValue(OracleDbmsOutputSettingsProvider.setOnStartupViewEnable, true)) {
      cmSwitchEnableDbmsOutput.performe();
    }

    updateEnable();

    comboRefresh.setSelectedItem(settings.getValue(OracleDbmsOutputSettingsProvider.setRefreshInterval, "5"));
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }

  public void close() throws IOException {
    viewClosing = true;
    if (outputEnable) {
      cmSwitchEnableDbmsOutput.performe();
    }
    timer.cancel();
    timer = null;
    accesibilities = null;
  }

  private void refreshOutput() {
    if (outputEnable && SwingUtil.isVisible(this)) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          labelRefresh.setEnabled(true);
        }
      });
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            Command command = getDatabase().createCommand();
            command.setSqlText(Sql.getDbmsOutputLine());
            command.paramByName("TEXT").setParamMode(ParameterMetaData.parameterModeOut, Types.VARCHAR);
            command.paramByName("STATUS").setParamMode(ParameterMetaData.parameterModeOut, Types.INTEGER);
            boolean hasMore = true;
            while (hasMore && !viewClosing) {
              command.execute();
              hasMore = (command.paramByName("STATUS").getInteger() == 0);
              if (hasMore) {
                textOutput.append(command.paramByName("TEXT").getString() +"\n");
              }
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
            textOutput.append(ex.getMessage() +"\n");
          }
          finally {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                labelRefresh.setEnabled(false);
              }
            });
          }
        }
      });
    }
  }

  private void updateEnable() {
    cmRefreshDbmsOutput.setEnabled(outputEnable);
    if (outputEnable) {
      cmSwitchEnableDbmsOutput.setSmallIcon(iconDisabled);
      cmSwitchEnableDbmsOutput.setTooltip(stringManager.getString("cmSwitchEnableDbmsOutput-hint-enabled"));
    } else {
      cmSwitchEnableDbmsOutput.setSmallIcon(iconEnabled);
      cmSwitchEnableDbmsOutput.setTooltip(stringManager.getString("cmSwitchEnableDbmsOutput-hint-disabled"));
    }
    if (timer != null) {
      timer.setEnabled(outputEnable && isVisible());
    }
  }

  private long getRefreshTime() {
    if (comboRefresh.getSelectedIndex() == 0) {
      return 1000 *1000;
    }
    return (long)(Double.parseDouble(comboRefresh.getSelectedItem().toString()) *1000);
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefreshDbmsOutput = new pl.mpak.sky.gui.swing.Action();
    cmSwitchEnableDbmsOutput = new pl.mpak.sky.gui.swing.Action();
    cmClearDbmsOutput = new pl.mpak.sky.gui.swing.Action();
    cmSaveToFile = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textOutput = new pl.mpak.sky.gui.swing.comp.TextArea();
    jPanel2 = new javax.swing.JPanel();
    toolBarTriggers = new javax.swing.JToolBar();
    buttonSwitchEnable = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonClear = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSave = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    jLabel1 = new javax.swing.JLabel();
    comboRefresh = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    labelRefresh = new javax.swing.JLabel();

    cmRefreshDbmsOutput.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefreshDbmsOutput.setText(stringManager.getString("cmRefreshDbmsOutput-text")); // NOI18N
    cmRefreshDbmsOutput.setTooltip(stringManager.getString("cmRefreshDbmsOutput-hint")); // NOI18N
    cmRefreshDbmsOutput.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshDbmsOutputActionPerformed(evt);
      }
    });

    cmSwitchEnableDbmsOutput.setActionCommandKey("cmSwitchEnableDbmsOutput");
    cmSwitchEnableDbmsOutput.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/service_start.gif")); // NOI18N
    cmSwitchEnableDbmsOutput.setText(stringManager.getString("cmSwitchEnableDbmsOutput-text")); // NOI18N
    cmSwitchEnableDbmsOutput.setTooltip(stringManager.getString("cmSwitchEnableDbmsOutput-hint")); // NOI18N
    cmSwitchEnableDbmsOutput.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSwitchEnableDbmsOutputActionPerformed(evt);
      }
    });

    cmClearDbmsOutput.setActionCommandKey("cmClearDbmsOutput");
    cmClearDbmsOutput.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmClearDbmsOutput.setText(stringManager.getString("cmClearDbmsOutput-text")); // NOI18N
    cmClearDbmsOutput.setTooltip(stringManager.getString("cmClearDbmsOutput-hint")); // NOI18N
    cmClearDbmsOutput.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmClearDbmsOutputActionPerformed(evt);
      }
    });

    cmSaveToFile.setActionCommandKey("cmSaveToFile");
    cmSaveToFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
    cmSaveToFile.setText(stringManager.getString("cmSaveToFile-text")); // NOI18N
    cmSaveToFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSaveToFileActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentHidden(java.awt.event.ComponentEvent evt) {
        formComponentHidden(evt);
      }
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    textOutput.setColumns(20);
    textOutput.setEditable(false);
    textOutput.setRows(5);
    textOutput.setFont(new java.awt.Font("Courier New", 0, 11));
    jScrollPane1.setViewportView(textOutput);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarTriggers.setFloatable(false);
    toolBarTriggers.setRollover(true);

    buttonSwitchEnable.setAction(cmSwitchEnableDbmsOutput);
    buttonSwitchEnable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSwitchEnable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonSwitchEnable);

    buttonRefresh.setAction(cmRefreshDbmsOutput);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonRefresh);
    toolBarTriggers.add(jSeparator1);

    buttonClear.setAction(cmClearDbmsOutput);
    buttonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonClear);

    buttonSave.setAction(cmSaveToFile);
    buttonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonSave);
    toolBarTriggers.add(jSeparator2);

    jLabel1.setText(stringManager.getString("refresh-dd")); // NOI18N
    toolBarTriggers.add(jLabel1);

    comboRefresh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "off", "0.5", "1", "5", "10", "30", "60", "120" }));
    comboRefresh.setPreferredSize(new java.awt.Dimension(60, 22));
    comboRefresh.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboRefreshItemStateChanged(evt);
      }
    });
    toolBarTriggers.add(comboRefresh);

    jLabel2.setText(" s ");
    toolBarTriggers.add(jLabel2);

    labelRefresh.setIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/orange_bdot.gif")); // NOI18N
    labelRefresh.setEnabled(false);
    toolBarTriggers.add(labelRefresh);

    jPanel2.add(toolBarTriggers);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  refreshOutput();
  updateEnable();
}//GEN-LAST:event_formComponentShown

private void cmRefreshDbmsOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshDbmsOutputActionPerformed
  refreshOutput();
}//GEN-LAST:event_cmRefreshDbmsOutputActionPerformed

private void cmSwitchEnableDbmsOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSwitchEnableDbmsOutputActionPerformed
  try {
    if (!outputEnable) {
      Command command = getDatabase().createCommand();
      command.setSqlText(Sql.getDbmsOutputEnable());
      command.paramByName("BYTES").setLong(settings.getValue(OracleDbmsOutputSettingsProvider.setBufferSize, 1000L) *1000L);
      command.execute();
      outputEnable = true;
    }
    else {
      Command command = getDatabase().createCommand();
      command.setSqlText(Sql.getDbmsOutputDisable());
      command.execute();
      outputEnable = false;
    }
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
  updateEnable();
}//GEN-LAST:event_cmSwitchEnableDbmsOutputActionPerformed

private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
  updateEnable();
}//GEN-LAST:event_formComponentHidden

private void cmClearDbmsOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmClearDbmsOutputActionPerformed
  textOutput.setText("");
}//GEN-LAST:event_cmClearDbmsOutputActionPerformed

private void cmSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveToFileActionPerformed
  File file = FileUtil.selectFileToSave(this, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("text-files"), new String[] { ".txt" })});
  if (file != null) {
    try {
      PrintWriter pw = new PrintWriter(new FileOutputStream(file));
      pw.write(textOutput.getText());
      pw.close();
    } catch (FileNotFoundException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    
  }
}//GEN-LAST:event_cmSaveToFileActionPerformed

private void comboRefreshItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboRefreshItemStateChanged
if (comboRefresh.getSelectedItem() != null) {
    if (comboRefresh.getSelectedIndex() > 0) {
      timer.setInterval((int)getRefreshTime());
      timer.setEnabled(true);
    }
    else {
      timer.setEnabled(false);
    }
  }
}//GEN-LAST:event_comboRefreshItemStateChanged


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonClear;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSave;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSwitchEnable;
  private pl.mpak.sky.gui.swing.Action cmClearDbmsOutput;
  private pl.mpak.sky.gui.swing.Action cmRefreshDbmsOutput;
  private pl.mpak.sky.gui.swing.Action cmSaveToFile;
  private pl.mpak.sky.gui.swing.Action cmSwitchEnableDbmsOutput;
  private javax.swing.JComboBox comboRefresh;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JLabel labelRefresh;
  private pl.mpak.sky.gui.swing.comp.TextArea textOutput;
  private javax.swing.JToolBar toolBarTriggers;
  // End of variables declaration//GEN-END:variables

}

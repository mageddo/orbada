package pl.mpak.orbada.oracle.gui.wizards.mview;

import javax.swing.DefaultListModel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dbinfo.OracleMViewInfo;
import pl.mpak.orbada.oracle.gui.util.MViewComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.MViewItemListener;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class CreateMViewTriggerWizard extends SqlCodeWizardPanel {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private String schemaName;
  private String tableName;
  private boolean nameChanged = false;

  public CreateMViewTriggerWizard(Database database, String schemaName, String tableName) {
    this.database = database;
    this.schemaName = schemaName;
    this.tableName = tableName;
    initComponents();
    init();
  }

  private void init() {
    listTableColumns.setModel(new DefaultListModel());
    listSelectedColumns.setModel(new DefaultListModel());
    comboTables.setModel(new MViewComboBoxModel(database));
    comboTables.addItemListener(new MViewItemListener() {
      public void itemChanged(OracleMViewInfo info) {
        if (!nameChanged) {
          updateName();
        }
        updateTableColumns(info);
      }
    });
  }

  public void wizardShow() {
    ((MViewComboBoxModel) comboTables.getModel()).change(schemaName);
    ((MViewComboBoxModel)comboTables.getModel()).select(tableName, comboTables);
  }
  
  private void updateMoveActions() {
    cmMoveRight.setEnabled(checkUpdate.isSelected() && listTableColumns.getSelectedValue() != null);
    cmMoveAllRight.setEnabled(checkUpdate.isSelected() && listTableColumns.getModel().getSize() > 0);
    cmMoveLeft.setEnabled(checkUpdate.isSelected() && listSelectedColumns.getSelectedValue() != null);
    cmMoveAllLeft.setEnabled(checkUpdate.isSelected() && listSelectedColumns.getModel().getSize() > 0);
  }

  private void updateTableColumns(OracleMViewInfo ti) {
    DefaultListModel model = (DefaultListModel)listSelectedColumns.getModel();
    model.removeAllElements();
    model = (DefaultListModel)listTableColumns.getModel();
    model.removeAllElements();
    DbObjectIdentified list = ti.getObjectInfo("/COLUMNS");
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        model.addElement(i);
      }
    }
    updateMoveActions();
  }
      
  private void updateName() {
    String idu = "";
    if (comboType.getSelectedIndex() == 0) {
      idu = idu +"_B";
    }
    else if (comboType.getSelectedIndex() == 1) {
      idu = idu +"_A";
    }
    if (checkInsert.isSelected()) {
      idu = idu +"_NEW";
    }
    if (checkDelete.isSelected()) {
      idu = idu +"_DEL";
    }
    if (checkUpdate.isSelected()) {
      idu = idu +"_UPD";
    }
    textName.setText(comboTables.getSelectedItem().toString() +idu +"_TRG");
  }

  public String getDialogTitle() {
    return stringManager.getString("CreateMViewTriggerWizard-dialog-title");
  }

  public String getTabTitle() {
    return stringManager.getString("CreateMViewTriggerWizard-tab-title");
  }

  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    String idu = "";
    if (checkInsert.isSelected()) {
      idu = idu +" INSERT";
    }
    if (checkDelete.isSelected()) {
      if (!StringUtil.isEmpty(idu)) {
        idu = idu +" OR";
      }
      idu = idu +" DELETE";
    }
    if (checkUpdate.isSelected()) {
      if (!StringUtil.isEmpty(idu)) {
        idu = idu +" OR";
      }
      idu = idu +" UPDATE";
      DefaultListModel sel = (DefaultListModel)listSelectedColumns.getModel();
      if (sel.getSize() > 0) {
        idu = idu +" OF ";
        for (int i=0; i<sel.getSize(); i++) {
          if (i > 0) {
            idu = idu +", ";
          }
          idu = idu +sel.getElementAt(i);
        }
      }
    }
    idu = idu +" ON " +SQLUtil.createSqlName(schemaName, comboTables.getSelectedItem().toString());
    String ref = "";
    if (!StringUtil.isEmpty(textOldRef.getText())) {
      ref = ref +" OLD AS " +textOldRef.getText();
    }
    if (!StringUtil.isEmpty(textNewRef.getText())) {
      ref = ref +" NEW AS " +textNewRef.getText();
    }
    return
      "CREATE TRIGGER " +textName.getText() +"\n" +
      "  " +(comboType.getSelectedIndex() == 0 ? "BEFORE" : "AFTER") +idu +"\n" +
      (!StringUtil.isEmpty(ref) ? "  REFERENCING" +ref +"\n" : "") +
      (comboLevel.getSelectedIndex() == 0 ? "  FOR EACH ROW\n" : "") +
      (!StringUtil.isEmpty(textWhen.getText()) ? "  WHEN (" +textWhen.getText() +")\n" : "") +
      "BEGIN\n" +
      "  NULL;\n" +
      "END;";
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

    cmMoveRight = new pl.mpak.sky.gui.swing.Action();
    cmMoveAllRight = new pl.mpak.sky.gui.swing.Action();
    cmMoveLeft = new pl.mpak.sky.gui.swing.Action();
    cmMoveAllLeft = new pl.mpak.sky.gui.swing.Action();
    jLabel2 = new javax.swing.JLabel();
    comboTables = new javax.swing.JComboBox();
    jLabel3 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    comboLevel = new javax.swing.JComboBox();
    jLabel5 = new javax.swing.JLabel();
    comboType = new javax.swing.JComboBox();
    checkInsert = new javax.swing.JCheckBox();
    checkDelete = new javax.swing.JCheckBox();
    checkUpdate = new javax.swing.JCheckBox();
    jLabel6 = new javax.swing.JLabel();
    textNewRef = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel7 = new javax.swing.JLabel();
    textOldRef = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel8 = new javax.swing.JLabel();
    textWhen = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    listTableColumns = new javax.swing.JList();
    buttonMoveRight = new javax.swing.JButton();
    buttonMoveAllRight = new javax.swing.JButton();
    buttonMoveRight1 = new javax.swing.JButton();
    buttonMoveAllRight1 = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    listSelectedColumns = new javax.swing.JList();
    labelSelectedColumns = new javax.swing.JLabel();

    cmMoveRight.setActionCommandKey("cmMoveRight");
    cmMoveRight.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/move_right.gif")); // NOI18N
    cmMoveRight.setText(stringManager.getString("cmMoveRight-text")); // NOI18N
    cmMoveRight.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMoveRightActionPerformed(evt);
      }
    });

    cmMoveAllRight.setActionCommandKey("cmMoveAllRight");
    cmMoveAllRight.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/move_all_right.gif")); // NOI18N
    cmMoveAllRight.setText(stringManager.getString("cmMoveAllRight-text")); // NOI18N
    cmMoveAllRight.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMoveAllRightActionPerformed(evt);
      }
    });

    cmMoveLeft.setActionCommandKey("cmMoveLeft");
    cmMoveLeft.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/move_left.gif")); // NOI18N
    cmMoveLeft.setText(stringManager.getString("cmMoveLeft-text")); // NOI18N
    cmMoveLeft.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMoveLeftActionPerformed(evt);
      }
    });

    cmMoveAllLeft.setActionCommandKey("cmMoveAllLeft");
    cmMoveAllLeft.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/move_all_left.gif")); // NOI18N
    cmMoveAllLeft.setText(stringManager.getString("cmMoveAllLeft-text")); // NOI18N
    cmMoveAllLeft.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMoveAllLeftActionPerformed(evt);
      }
    });

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("table-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("trigger-name-dd")); // NOI18N

    textName.setText("_TRG");
    textName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        textNameKeyPressed(evt);
      }
    });

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("execution-range-dd")); // NOI18N

    comboLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dla ka¿dego wiersza (ROW)", "Dla polecenia SQL (STATEMENT)" }));
    comboLevel.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboLevelItemStateChanged(evt);
      }
    });

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("trigger-type-dd")); // NOI18N

    comboType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Przed aktualizacj¹ danych (BEFORE)", "Po aktualizacji danych (AFTER)" }));
    comboType.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboTypeItemStateChanged(evt);
      }
    });

    checkInsert.setText(stringManager.getString("inserting")); // NOI18N
    checkInsert.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkInsertStateChanged(evt);
      }
    });

    checkDelete.setText(stringManager.getString("deleting")); // NOI18N
    checkDelete.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkDeleteStateChanged(evt);
      }
    });

    checkUpdate.setText(stringManager.getString("updating")); // NOI18N
    checkUpdate.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkUpdateStateChanged(evt);
      }
    });

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("reference-new-dd")); // NOI18N

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(stringManager.getString("reference-old-dd")); // NOI18N

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText(stringManager.getString("only-when-dd")); // NOI18N

    jLabel1.setText(stringManager.getString("available-columns-dd")); // NOI18N

    listTableColumns.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        listTableColumnsValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(listTableColumns);

    buttonMoveRight.setAction(cmMoveRight);
    buttonMoveRight.setHideActionText(true);
    buttonMoveRight.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonMoveRight.setPreferredSize(new java.awt.Dimension(50, 23));

    buttonMoveAllRight.setAction(cmMoveAllRight);
    buttonMoveAllRight.setHideActionText(true);
    buttonMoveAllRight.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonMoveAllRight.setPreferredSize(new java.awt.Dimension(50, 23));

    buttonMoveRight1.setAction(cmMoveLeft);
    buttonMoveRight1.setHideActionText(true);
    buttonMoveRight1.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonMoveRight1.setPreferredSize(new java.awt.Dimension(50, 23));

    buttonMoveAllRight1.setAction(cmMoveAllLeft);
    buttonMoveAllRight1.setHideActionText(true);
    buttonMoveAllRight1.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonMoveAllRight1.setPreferredSize(new java.awt.Dimension(50, 23));

    listSelectedColumns.setEnabled(false);
    listSelectedColumns.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        listSelectedColumnsValueChanged(evt);
      }
    });
    jScrollPane2.setViewportView(listSelectedColumns);

    labelSelectedColumns.setText(stringManager.getString("selected-columns-dd")); // NOI18N
    labelSelectedColumns.setEnabled(false);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(buttonMoveAllRight1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(buttonMoveRight1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(buttonMoveAllRight, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(buttonMoveRight, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
              .addComponent(jLabel1))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
              .addComponent(labelSelectedColumns)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboLevel, 0, 278, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTables, 0, 278, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textWhen, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textOldRef, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textNewRef, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(checkInsert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkUpdate))
              .addComponent(comboType, 0, 278, Short.MAX_VALUE))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel2)
              .addComponent(comboTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel3)
              .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel4)
              .addComponent(comboLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel5)
              .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(checkInsert)
              .addComponent(checkDelete)
              .addComponent(checkUpdate))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel6)
              .addComponent(textNewRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel7)
              .addComponent(textOldRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel8)
              .addComponent(textWhen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel1)
              .addComponent(labelSelectedColumns))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
              .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
          .addGroup(layout.createSequentialGroup()
            .addGap(240, 240, 240)
            .addComponent(buttonMoveRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonMoveAllRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonMoveRight1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonMoveAllRight1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  private void textNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textNameKeyPressed
    nameChanged = true;
  }//GEN-LAST:event_textNameKeyPressed

  private void checkInsertStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkInsertStateChanged
    if (!nameChanged) {
      updateName();
    }
  }//GEN-LAST:event_checkInsertStateChanged

  private void checkDeleteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkDeleteStateChanged
    if (!nameChanged) {
      updateName();
    }
  }//GEN-LAST:event_checkDeleteStateChanged

  private void checkUpdateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkUpdateStateChanged
    if (!nameChanged) {
      updateName();
    }
    listSelectedColumns.setEnabled(checkUpdate.isSelected());
    labelSelectedColumns.setEnabled(checkUpdate.isSelected());
    updateMoveActions();
  }//GEN-LAST:event_checkUpdateStateChanged

  private void comboLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboLevelItemStateChanged
    textWhen.setEnabled(comboLevel.getSelectedIndex() == 0);
  }//GEN-LAST:event_comboLevelItemStateChanged

  private void comboTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboTypeItemStateChanged
    if (!nameChanged) {
      updateName();
    }
}//GEN-LAST:event_comboTypeItemStateChanged

  private void listTableColumnsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listTableColumnsValueChanged
    updateMoveActions();
  }//GEN-LAST:event_listTableColumnsValueChanged

  private void listSelectedColumnsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listSelectedColumnsValueChanged
    updateMoveActions();
  }//GEN-LAST:event_listSelectedColumnsValueChanged

  private void cmMoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveRightActionPerformed
    DefaultListModel sel = (DefaultListModel)listSelectedColumns.getModel();
    DefaultListModel tab = (DefaultListModel)listTableColumns.getModel();
    sel.addElement(listTableColumns.getSelectedValue());
    tab.removeElement(listTableColumns.getSelectedValue());
    updateMoveActions();
  }//GEN-LAST:event_cmMoveRightActionPerformed

  private void cmMoveLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveLeftActionPerformed
    DefaultListModel sel = (DefaultListModel)listSelectedColumns.getModel();
    DefaultListModel tab = (DefaultListModel)listTableColumns.getModel();
    tab.addElement(listSelectedColumns.getSelectedValue());
    sel.removeElement(listSelectedColumns.getSelectedValue());
    updateMoveActions();
  }//GEN-LAST:event_cmMoveLeftActionPerformed

  private void cmMoveAllRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveAllRightActionPerformed
    DefaultListModel sel = (DefaultListModel)listSelectedColumns.getModel();
    DefaultListModel tab = (DefaultListModel)listTableColumns.getModel();
    for (int i=0; i<tab.getSize(); i++) {
      sel.addElement(tab.get(i));
    }
    tab.removeAllElements();
    updateMoveActions();
  }//GEN-LAST:event_cmMoveAllRightActionPerformed

  private void cmMoveAllLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveAllLeftActionPerformed
    DefaultListModel sel = (DefaultListModel)listSelectedColumns.getModel();
    DefaultListModel tab = (DefaultListModel)listTableColumns.getModel();
    for (int i=0; i<sel.getSize(); i++) {
      tab.addElement(sel.get(i));
    }
    sel.removeAllElements();
    updateMoveActions();
  }//GEN-LAST:event_cmMoveAllLeftActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonMoveAllRight;
  private javax.swing.JButton buttonMoveAllRight1;
  private javax.swing.JButton buttonMoveRight;
  private javax.swing.JButton buttonMoveRight1;
  private javax.swing.JCheckBox checkDelete;
  private javax.swing.JCheckBox checkInsert;
  private javax.swing.JCheckBox checkUpdate;
  private pl.mpak.sky.gui.swing.Action cmMoveAllLeft;
  private pl.mpak.sky.gui.swing.Action cmMoveAllRight;
  private pl.mpak.sky.gui.swing.Action cmMoveLeft;
  private pl.mpak.sky.gui.swing.Action cmMoveRight;
  private javax.swing.JComboBox comboLevel;
  private javax.swing.JComboBox comboTables;
  private javax.swing.JComboBox comboType;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JLabel labelSelectedColumns;
  private javax.swing.JList listSelectedColumns;
  private javax.swing.JList listTableColumns;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  private pl.mpak.sky.gui.swing.comp.TextField textNewRef;
  private pl.mpak.sky.gui.swing.comp.TextField textOldRef;
  private pl.mpak.sky.gui.swing.comp.TextField textWhen;
  // End of variables declaration//GEN-END:variables
}

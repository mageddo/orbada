/*
 * ExportToSqlInsertDialog.java
 *
 * Created on 11 paüdziernik 2008, 15:29
 */

package pl.mpak.orbada.export;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.charset.Charset;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ExportToSqlInsertDialog extends javax.swing.JDialog {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaExportPlugin.class);

  private ISettings config;
  private int modalResult = ModalResult.NONE;

  /** Creates new form ExportToSqlInsertDialog */
  public ExportToSqlInsertDialog(ISettings config) {
    super(SwingUtil.getRootFrame());
    this.config = config;
    initComponents();
    init();
  }

  public static boolean showDialog(ISettings config) {
    ExportToSqlInsertDialog dialog = new ExportToSqlInsertDialog(config);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK;
  }

  private void init() {
    for (Charset charset : Charset.availableCharsets().values()) {
      comboCharset.addItem(charset.displayName());
    }
    comboDefinitions.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof ExportToSqlInsertDefinition) {
          setText(((ExportToSqlInsertDefinition)value).getName());
        }
        return this;
      }
    });

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonOk);
    configToDialog();
    SwingUtil.centerWithinScreen(this);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        editTableName.requestFocus();
      }
    });
  }
  
  private void preDefinitions() {
    comboDefinitions.addItem(new ExportToSqlInsertDefinition(
      "Standard", "INSERT INTO \"$(table-name)\" ($(column-name-list)) VALUES ($(value-list))\\n/", "\"$(column-name)\"",
      "'$(value)'", "'", "'", "'$(value)'", "$(value)", "true", "false", "'$(value)'", "null", true, false
      ));
    comboDefinitions.addItem(new ExportToSqlInsertDefinition(
      "Oracle", "INSERT INTO \"$(table-name)\" ($(column-name-list)) VALUES ($(value-list))\\n/", "\"$(column-name)\"",
      "'$(value)'", "'", "'", "to_date('$(value)', 'rrrr-mm-dd hh24:mi:ss')", "$(value)", "true", "false", 
      "hextoraw('$(value)')", "null", true, false
      ));
  }
  
  private void configToDialog() {
    try {
      checkClipboard.setSelected(config.getValue("to-clipboard", checkClipboard.isSelected()));
      comboCharset.setSelectedItem(config.getValue("charset", new Variant(Charset.defaultCharset().displayName())).getString());
      editTableName.setText(config.getValue("table-name", editTableName.getText()));
      long defCount = config.getValue("def-count", 0L);
      String selected = config.getValue("def-selected", "");
      int selectedIndex = 0;
      for (int i=0; i<defCount; i++) {
        String def = config.getValue("def-" +i, (String)null);
        if (def != null) {
          ExportToSqlInsertDefinition eti = ExportToSqlInsertDefinition.toDefinition(def);
          if (eti.getName().equals(selected)) {
            selectedIndex = i;
          }
          comboDefinitions.addItem(eti);
        }
      }
      if (defCount == 0) {
        preDefinitions();
      }
      if (comboDefinitions.getItemCount() > 0) {
        comboDefinitions.setSelectedIndex(selectedIndex);
      }
      String userDef = config.getValue("def-user", (String)null);
      if (userDef != null && !"".equals(userDef)) {
        ExportToSqlInsertDefinition eti = ExportToSqlInsertDefinition.toDefinition(userDef);
        defToDialog(eti);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void defToDialog(ExportToSqlInsertDefinition eti) {
    editCommand.setText(eti.getCommand());
    editColumnName.setText(eti.getColumnName());
    editVarcharValue.setText(eti.getVarcharValue());
    editVarcharChars.setText(eti.getVarcharChars());
    editCharPrefix.setText(eti.getCharPrefix());
    editTimestampValue.setText(eti.getTimestampValue());
    editNumericValue.setText(eti.getNumericValue());
    editTrueValue.setText(eti.getTrueValue());
    editFalseValue.setText(eti.getFalseValue());
    editBinaryValue.setText(eti.getBinaryValue());
    editNullValue.setText(eti.getNullValue());
    checkBinaryHex.setSelected(eti.isBinaryHex());
    checkDotToComma.setSelected(eti.isDotToComma());
  }
  
  private ExportToSqlInsertDefinition dialogToDef(ExportToSqlInsertDefinition eti) {
    eti.setCommand(editCommand.getText());
    eti.setColumnName(editColumnName.getText());
    eti.setVarcharValue(editVarcharValue.getText());
    eti.setVarcharChars(editVarcharChars.getText());
    eti.setCharPrefix(editCharPrefix.getText());
    eti.setTimestampValue(editTimestampValue.getText());
    eti.setNumericValue(editNumericValue.getText());
    eti.setTrueValue(editTrueValue.getText());
    eti.setFalseValue(editFalseValue.getText());
    eti.setBinaryValue(editBinaryValue.getText());
    eti.setNullValue(editNullValue.getText());
    eti.setBinaryHex(checkBinaryHex.isSelected());
    eti.setDotToComma(checkDotToComma.isSelected());
    return eti;
  }
  
  private void dialogToConfig() {
    ExportToSqlInsertDefinition userEti = new ExportToSqlInsertDefinition("UserCurrentDefinition");
    dialogToDef(userEti);
    
    config.setValue("table-name", editTableName.getText());
    config.setValue("def-selected", ((ExportToSqlInsertDefinition)comboDefinitions.getSelectedItem()).getName());
    config.setValue("def-user", ExportToSqlInsertDefinition.toString(userEti));
    for (int i = 0; i < comboDefinitions.getItemCount(); i++) {
      config.setValue("def-" +i, ExportToSqlInsertDefinition.toString((ExportToSqlInsertDefinition)comboDefinitions.getItemAt(i)));
    }
    config.setValue("def-count", (long) comboDefinitions.getItemCount());
    config.setValue("charset", new Variant(comboCharset.getSelectedItem().toString()));
    config.setValue("to-clipboard", checkClipboard.isSelected());
    config.store();
  }
  
  private boolean selectFile() {
    File lastFile = null;
    try {
      if (!config.getValue("file-name").getString().equals("")) {
        lastFile = new File(config.getValue("file-name").getString());
      }
    } catch (Exception ex) {
    }
    lastFile = FileUtil.selectFileToSave(this, null, null, lastFile, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("ExportToSqlInsertDialog-sql-files"), new String[] { ".sql" })});
    if (lastFile != null) {
      config.setValue("file-name", new Variant(lastFile.getAbsoluteFile()));
      return true;
    }
    return false;
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    cmNewDef = new pl.mpak.sky.gui.swing.Action();
    cmUpdateDef = new pl.mpak.sky.gui.swing.Action();
    cmDeleteDef = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    editTableName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel2 = new javax.swing.JLabel();
    editCommand = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    editColumnName = new pl.mpak.sky.gui.swing.comp.TextField();
    jPanel1 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    editVarcharValue = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    editVarcharChars = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel6 = new javax.swing.JLabel();
    editCharPrefix = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel7 = new javax.swing.JLabel();
    editTimestampValue = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel8 = new javax.swing.JLabel();
    editNumericValue = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel9 = new javax.swing.JLabel();
    editTrueValue = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel10 = new javax.swing.JLabel();
    editNullValue = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel11 = new javax.swing.JLabel();
    editBinaryValue = new pl.mpak.sky.gui.swing.comp.TextField();
    checkBinaryHex = new javax.swing.JCheckBox();
    checkDotToComma = new javax.swing.JCheckBox();
    jLabel14 = new javax.swing.JLabel();
    editFalseValue = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel15 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    comboDefinitions = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    jButton3 = new javax.swing.JButton();
    comboCharset = new javax.swing.JComboBox();
    jLabel13 = new javax.swing.JLabel();
    checkClipboard = new javax.swing.JCheckBox();

    cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmOk.setText(stringManager.getString("cmOk-text")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmNewDef.setActionCommandKey("cmNewDef");
    cmNewDef.setText(stringManager.getString("ExportToSqlInsertDialog-cmNewDef-text")); // NOI18N
    cmNewDef.setTooltip(stringManager.getString("ExportToSqlInsertDialog-cmNewDef-hint")); // NOI18N
    cmNewDef.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewDefActionPerformed(evt);
      }
    });

    cmUpdateDef.setActionCommandKey("cmUpdateDef");
    cmUpdateDef.setText(stringManager.getString("ExportToSqlInsertDialog-cmUpdateDef-text")); // NOI18N
    cmUpdateDef.setTooltip(stringManager.getString("ExportToSqlInsertDialog-cmUpdateDef-hint")); // NOI18N
    cmUpdateDef.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmUpdateDefActionPerformed(evt);
      }
    });

    cmDeleteDef.setActionCommandKey("cmDeleteDef");
    cmDeleteDef.setText(stringManager.getString("ExportToSqlInsertDialog-cmDeleteDef-text")); // NOI18N
    cmDeleteDef.setTooltip(stringManager.getString("ExportToSqlInsertDialog-cmDeleteDef-hint")); // NOI18N
    cmDeleteDef.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteDefActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("ExportToSqlInsertDialog-title")); // NOI18N
    setModal(true);

    buttonOk.setAction(cmOk);
    buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("ExportToSqlInsertDialog-table-name-dd")); // NOI18N

    editTableName.setText("TABLE_NAME");

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("ExportToSqlInsertDialog-command-dd")); // NOI18N

    editCommand.setText("INSERT INTO \"$(table-name)\" ($(column-name-list)) VALUES ($(value-list))\\n/");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("ExportToSqlInsertDialog-column-dd")); // NOI18N

    editColumnName.setText("\"$(column-name)\"");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("ExportToSqlInsertDialog-data-conversion"))); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("ExportToSqlInsertDialog-char-seq-dd")); // NOI18N

    editVarcharValue.setText("'$(value)'");

    jLabel5.setText(stringManager.getString("ExportToSqlInsertDialog-before-char-dd")); // NOI18N

    editVarcharChars.setText("'");

    jLabel6.setText(stringManager.getString("ExportToSqlInsertDialog-char-dd")); // NOI18N

    editCharPrefix.setText("'");

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(stringManager.getString("ExportToSqlInsertDialog-date-time-dd")); // NOI18N

    editTimestampValue.setText("to_date('$(value)', 'rrrr-mm-dd hh24:mi:ss')");

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText(stringManager.getString("ExportToSqlInsertDialog-number-dd")); // NOI18N

    editNumericValue.setText("$(value)");

    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText(stringManager.getString("ExportToSqlInsertDialog-boolean-dd")); // NOI18N

    editTrueValue.setText("true");

    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel10.setText(stringManager.getString("ExportToSqlInsertDialog-null-value-dd")); // NOI18N

    editNullValue.setText(null);

    jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel11.setText(stringManager.getString("ExportToSqlInsertDialog-binary-dd")); // NOI18N

    editBinaryValue.setText("hextoraw('$(value)')");

    checkBinaryHex.setSelected(true);
    checkBinaryHex.setText(stringManager.getString("ExportToSqlInsertDialog-checkBinaryHex-text")); // NOI18N

    checkDotToComma.setText(stringManager.getString("ExportToSqlInsertDialog-checkDotToComma-text")); // NOI18N

    jLabel14.setText(stringManager.getString("ExportToSqlInsertDialog-true-dd")); // NOI18N

    editFalseValue.setText("false");

    jLabel15.setText(stringManager.getString("ExportToSqlInsertDialog-false-dd")); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editVarcharValue, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editVarcharChars, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel6)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editCharPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editTimestampValue, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editNumericValue, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(checkDotToComma))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel14)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editTrueValue, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel15)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editFalseValue, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editBinaryValue, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(checkBinaryHex))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editNullValue, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5)
          .addComponent(editVarcharChars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel6)
          .addComponent(editCharPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editVarcharValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editTimestampValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkDotToComma)
          .addComponent(editNumericValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel14)
          .addComponent(jLabel15)
          .addComponent(editTrueValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editFalseValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkBinaryHex)
          .addComponent(editBinaryValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editNullValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel12.setText(stringManager.getString("ExportToSqlInsertDialog-definition-dd")); // NOI18N

    comboDefinitions.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboDefinitionsItemStateChanged(evt);
      }
    });

    jButton1.setAction(cmNewDef);
    jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));

    jButton2.setAction(cmUpdateDef);
    jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));

    jButton3.setAction(cmDeleteDef);
    jButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));

    comboCharset.setEditable(true);
    comboCharset.setFont(new java.awt.Font("Courier New", 0, 12));

    jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel13.setText(stringManager.getString("ExportToSqlInsertDialog-charcode-dd")); // NOI18N

    checkClipboard.setText(stringManager.getString("ExportToSqlInsertDialog-checkClipboard-text")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(editColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(editCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addComponent(comboCharset, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(editTableName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(comboDefinitions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3))))
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(checkClipboard)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 260, Short.MAX_VALUE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(comboDefinitions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jButton1)
          .addComponent(jButton2)
          .addComponent(jButton3))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(comboCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editTableName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(editColumnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkClipboard))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  if (checkClipboard.isSelected() || selectFile()) {
    modalResult = pl.mpak.sky.gui.mr.ModalResult.OK;
    dialogToConfig();
    dispose();
  }
}//GEN-LAST:event_cmOkActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = pl.mpak.sky.gui.mr.ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void comboDefinitionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboDefinitionsItemStateChanged
  if (evt.getStateChange() == ItemEvent.SELECTED) {
    if (evt.getItem() instanceof ExportToSqlInsertDefinition) {
      defToDialog((ExportToSqlInsertDefinition)evt.getItem());
    }
  }
}//GEN-LAST:event_comboDefinitionsItemStateChanged

private void cmUpdateDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUpdateDefActionPerformed
  if (comboDefinitions.getSelectedItem() instanceof ExportToSqlInsertDefinition) {
    dialogToDef((ExportToSqlInsertDefinition)comboDefinitions.getSelectedItem());
  }
}//GEN-LAST:event_cmUpdateDefActionPerformed

private void cmDeleteDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteDefActionPerformed
  if (comboDefinitions.getSelectedItem() instanceof ExportToSqlInsertDefinition) {
    if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("ExportToSqlInsertDialog-delete-def-q") +((ExportToSqlInsertDefinition)comboDefinitions.getSelectedItem()).getName(), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
      comboDefinitions.removeItem(comboDefinitions.getSelectedItem());
      if (comboDefinitions.getItemCount() == 0) {
        preDefinitions();
      }
    }
  }
}//GEN-LAST:event_cmDeleteDefActionPerformed

private void cmNewDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewDefActionPerformed
  String name = JOptionPane.showInputDialog(this, stringManager.getString("ExportToSqlInsertDialog-input-name-dd"), stringManager.getString("ExportToSqlInsertDialog-def-name"), JOptionPane.QUESTION_MESSAGE);
  if (name != null) {
    ExportToSqlInsertDefinition eti = new ExportToSqlInsertDefinition(name);
    dialogToDef(eti);
    comboDefinitions.addItem(eti);
    comboDefinitions.setSelectedItem(eti);
  }
}//GEN-LAST:event_cmNewDefActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JCheckBox checkBinaryHex;
  private javax.swing.JCheckBox checkClipboard;
  private javax.swing.JCheckBox checkDotToComma;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmDeleteDef;
  private pl.mpak.sky.gui.swing.Action cmNewDef;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private pl.mpak.sky.gui.swing.Action cmUpdateDef;
  private javax.swing.JComboBox comboCharset;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboDefinitions;
  private pl.mpak.sky.gui.swing.comp.TextField editBinaryValue;
  private pl.mpak.sky.gui.swing.comp.TextField editCharPrefix;
  private pl.mpak.sky.gui.swing.comp.TextField editColumnName;
  private pl.mpak.sky.gui.swing.comp.TextField editCommand;
  private pl.mpak.sky.gui.swing.comp.TextField editFalseValue;
  private pl.mpak.sky.gui.swing.comp.TextField editNullValue;
  private pl.mpak.sky.gui.swing.comp.TextField editNumericValue;
  private pl.mpak.sky.gui.swing.comp.TextField editTableName;
  private pl.mpak.sky.gui.swing.comp.TextField editTimestampValue;
  private pl.mpak.sky.gui.swing.comp.TextField editTrueValue;
  private pl.mpak.sky.gui.swing.comp.TextField editVarcharChars;
  private pl.mpak.sky.gui.swing.comp.TextField editVarcharValue;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel13;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  // End of variables declaration//GEN-END:variables

}

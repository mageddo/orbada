/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImportFromCsvDialog.java
 *
 * Created on 2014-07-20, 19:59:59
 */
package pl.mpak.orbada.imports.csv.gui;

import com.csvreader.CsvReader;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.imports.csv.OrbadaImportCsvPlugin;
import pl.mpak.orbada.imports.csv.engine.CsvImport;
import pl.mpak.orbada.imports.csv.engine.CsvImportColumn;
import pl.mpak.orbada.imports.csv.engine.CsvImportConfiguration;
import pl.mpak.orbada.imports.csv.engine.CsvImportEvent;
import pl.mpak.orbada.imports.csv.engine.CsvImportListener;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.files.FileExtensionFilter;

/**
 *
 * @author akaluza
 */
public class ImportFromCsvDialog extends javax.swing.JDialog {

  private StringManager stringManager = StringManagerFactory.getStringManager("import-csv");

  private int modalResult = pl.mpak.sky.gui.mr.ModalResult.NONE;
  private CsvImportConfiguration config;
  private CsvImportColumn[] columns;
  private Database database;
  
  /** Creates new form ImportFromCsvDialog */
  public ImportFromCsvDialog(CsvImportConfiguration config, Database database) {
    super(SwingUtil.getRootFrame(), true);
    this.database = database;
    this.config = config;
    initComponents();
    init();
  }
  
  public static boolean showDialog(CsvImportConfiguration config, Database database) {
    final ImportFromCsvDialog dialog = new ImportFromCsvDialog(config, database);
    if (dialog.selectFile()) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          dialog.analyze();
        }
      });
      dialog.setVisible(true);
      return dialog.modalResult == ModalResult.OK;
    }
    return false;
  }
  
  private void init() {
    comboTable.setModel(new TableComboBoxModel(database));
    SwingUtil.centerWithinScreen(this);
    for (Charset charset : Charset.availableCharsets().values()) {
      comboCharset.addItem(charset.displayName());
    }
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonOk);
    SwingUtil.setButtonSizesTheSame(new JButton[] {buttonOk, buttonCancel, buttonAnalyze, buttonReset});
    configToDialog(config);
  }
  
  private void configToDialog(CsvImportConfiguration config) {
    try {
      comboCharset.setText(config.getEncoding());
      textFileName.setText(config.getFileName());
      if (config.getDelimiter() == '\t') {
        comboDelimiter.setText("TAB");
      }
      else if (config.getDelimiter() == ' ') {
        comboDelimiter.setText("SPACE");
      }
      else {
        comboDelimiter.setText("" +config.getDelimiter());
      }
      comboTextQualifier.setText("" +config.getTextQualifier());
      checkUseTextQualifier.setSelected(config.isUseTextQualifier());
      if (config.getEscapeMode() == CsvReader.ESCAPE_MODE_BACKSLASH) {
        comboEscapeMode.setText("BACKSLASH");
      }
      else if (config.getEscapeMode() == CsvReader.ESCAPE_MODE_DOUBLED) {
        comboEscapeMode.setText("DOUBLED");
      }
      if (config.getRecordDelimiter() == '\n') {
        comboEndOfLineChar.setText("\\n");
      }
      else if (config.getRecordDelimiter() == '\r') {
        comboEndOfLineChar.setText("\\r");
      }
      else if (config.getRecordDelimiter() == '\f') {
        comboEndOfLineChar.setText("\\f");
      }
      checkTrimWhitespace.setSelected(config.isTrimWhitespace());
      textPrecisionRound.setValue(config.getPrecisionRound());
      checkHeaderPresent.setSelected(config.isHeaderPresent());
      checkIgnoreErrors.setSelected(config.isIgnoreErrors());
      switch (config.getImportMode()) {
        case CREATE_TABLE_INSERT: radioCREATE_TABLE_INSERT.setSelected(true); break;
        case DELETE_ONLY: radioDELETE_ONLY.setSelected(true); break;
        case INSERT_ALL: radioINSERT_ALL.setSelected(true); break;
        case INSERT_NEW: radioINSERT_NEW.setSelected(true); break;
        case TRUNCATE_AND_INSERT: radioTRUNCATE_AND_INSERT.setSelected(true); break;
        case UPDATE_ONLY: radioUPDATE_ONLY.setSelected(true); break;
        case UPDATE_OR_INSERT: radioUPDATE_OR_INSERT.setSelected(true); break;
      }
      checkFullAuto.setSelected(config.isTableAutomatic());
      comboTable.setText(config.getTableName());
      textPrimaryKeyColumns.setText(Arrays.asList(config.getTablePrimaryKey()).toString().replaceAll("[\\[|\\]]", ""));
      checkAddPrimaryKey.setSelected(config.isAddPrimaryKey());
      checkAddNullCheck.setSelected(config.isAddNullCheck());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void dialogToConfig(CsvImportConfiguration config) {
    config.setEncoding(comboCharset.getText());
    config.setFileName(textFileName.getText());
    if ("TAB".equals(comboDelimiter.getText())) {
      config.setDelimiter('\t');
    }
    else if ("SPACE".equals(comboDelimiter.getText())) {
      config.setDelimiter(' ');
    }
    else {
      config.setDelimiter(comboDelimiter.getText());
    }
    config.setTextQualifier(comboTextQualifier.getText());
    config.setUseTextQualifier(checkUseTextQualifier.isSelected());
    if ("BACKSLASH".equals(comboEscapeMode.getText())) {
      config.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
    }
    else if ("DOUBLED".equals(comboEscapeMode.getText())) {
      config.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
    }
    if ("\\n".equals(comboEndOfLineChar.getText())) {
      config.setRecordDelimiter('\n');
    }
    else if ("\\r".equals(comboEndOfLineChar.getText())) {
      config.setRecordDelimiter('\r');
    }
    else if ("\\f".equals(comboEndOfLineChar.getText())) {
      config.setRecordDelimiter('\f');
    }
    config.setTrimWhitespace(checkTrimWhitespace.isSelected());
    config.setPrecisionRound((Integer)textPrecisionRound.getValue());
    config.setHeaderPresent(checkHeaderPresent.isSelected());
    config.setIgnoreErrors(checkIgnoreErrors.isSelected());
    if (radioCREATE_TABLE_INSERT.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.CREATE_TABLE_INSERT);
    }
    else if (radioDELETE_ONLY.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.DELETE_ONLY);
    }
    else if (radioINSERT_ALL.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.INSERT_ALL);
    }
    else if (radioINSERT_NEW.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.INSERT_NEW);
    }
    else if (radioTRUNCATE_AND_INSERT.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.TRUNCATE_AND_INSERT);
    }
    else if (radioUPDATE_ONLY.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.UPDATE_ONLY);
    }
    else if (radioUPDATE_OR_INSERT.isSelected()) {
      config.setImportMode(CsvImportConfiguration.ImportMode.UPDATE_OR_INSERT);
    }
    config.setTableAutomatic(checkFullAuto.isSelected());
    config.setTableName(comboTable.getText());
    config.setTablePrimaryKey(textPrimaryKeyColumns.getText().split(","));
    config.setAddPrimaryKey(checkAddPrimaryKey.isSelected());
    config.setAddNullCheck(checkAddNullCheck.isSelected());
    if (!config.isTableAutomatic()) {
      config.setTableCreationCommand(textTableCreatCode.getText());
    }
    config.getColumnList().clear();
    if (columns != null) {
      config.add(columns);
    }
  }
  
  private boolean selectFile() {
    File lastFile = null;
    try {
      if (!StringUtil.isEmpty(textFileName.getText())) {
        lastFile = new File(textFileName.getText());
      }
    } catch (Exception ex) {
    }
    lastFile = FileUtil.selectFileToOpen(this, null, null, lastFile, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("ImportFromCsvDialog-csv-files"), new String[] {".csv"})});
    if (lastFile != null) {
      textFileName.setText(lastFile.getAbsoluteFile().toString());
      return true;
    }
    return false;
  }
  
  private void updateTableCreation() {
    if (columns != null) {
      try {
        CsvImportConfiguration cnf = new CsvImportConfiguration();
        dialogToConfig(cnf);
        CsvImport csv = new CsvImport(cnf);
        textTableCreatCode.setText(csv.createTableScript(database));
      } catch (Exception ex) {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
  }
  
  private void analyze() {
    try {
      CsvImportConfiguration cnf = new CsvImportConfiguration();
      dialogToConfig(cnf);
      CsvImport csv = new CsvImport(cnf);
      csv.addImportListener(new CsvImportListener() {
        @Override
        public void beforeImport(CsvImportEvent event) {
        }
        @Override
        public void afterImport(CsvImportEvent event) {
        }
        @Override
        public void beforeImportRecord(CsvImportEvent event) {
          if (event.isAnalyzing() && event.getRecNo() > 1000) {
            event.setAction(CsvImportEvent.STOP_IMORT);
          }
        }
        @Override
        public void afterImportRecord(CsvImportEvent event) {
        }
      });
      columns = csv.analyze();
      configToDialog(cnf);
      updateTableCreation();
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
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
        cmSelectFile = new pl.mpak.sky.gui.swing.Action();
        groupImportMode = new javax.swing.ButtonGroup();
        cmAnalyze = new pl.mpak.sky.gui.swing.Action();
        cmReset = new pl.mpak.sky.gui.swing.Action();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMode = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        textFileName = new pl.mpak.sky.gui.swing.comp.TextField();
        toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
        checkIgnoreErrors = new javax.swing.JCheckBox();
        radioCREATE_TABLE_INSERT = new javax.swing.JRadioButton();
        radioTRUNCATE_AND_INSERT = new javax.swing.JRadioButton();
        radioINSERT_NEW = new javax.swing.JRadioButton();
        radioUPDATE_OR_INSERT = new javax.swing.JRadioButton();
        radioDELETE_ONLY = new javax.swing.JRadioButton();
        radioUPDATE_ONLY = new javax.swing.JRadioButton();
        radioINSERT_ALL = new javax.swing.JRadioButton();
        panelMain = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        comboCharset = new pl.mpak.sky.gui.swing.comp.ComboBox();
        comboDelimiter = new pl.mpak.sky.gui.swing.comp.ComboBox();
        jLabel18 = new javax.swing.JLabel();
        comboTextQualifier = new pl.mpak.sky.gui.swing.comp.ComboBox();
        jLabel19 = new javax.swing.JLabel();
        checkUseTextQualifier = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        comboEscapeMode = new pl.mpak.sky.gui.swing.comp.ComboBox();
        jLabel21 = new javax.swing.JLabel();
        comboEndOfLineChar = new pl.mpak.sky.gui.swing.comp.ComboBox();
        checkTrimWhitespace = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        textPrecisionRound = new javax.swing.JSpinner();
        checkHeaderPresent = new javax.swing.JCheckBox();
        panelTable = new javax.swing.JPanel();
        checkFullAuto = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        comboTable = new pl.mpak.sky.gui.swing.comp.ComboBox();
        jLabel10 = new javax.swing.JLabel();
        textPrimaryKeyColumns = new pl.mpak.sky.gui.swing.comp.TextField();
        checkAddPrimaryKey = new javax.swing.JCheckBox();
        checkAddNullCheck = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        textTableCreatCode = new OrbadaSyntaxTextArea();
        buttonReset = new javax.swing.JButton();
        buttonAnalyze = new javax.swing.JButton();

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

        cmSelectFile.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/open16.gif"))); // NOI18N
        cmSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectFileActionPerformed(evt);
            }
        });

        cmAnalyze.setText(stringManager.getString("cmAnalyze-text")); // NOI18N
        cmAnalyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmAnalyzeActionPerformed(evt);
            }
        });

        cmReset.setText(stringManager.getString("cmReset-text")); // NOI18N
        cmReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmResetActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        buttonOk.setAction(cmOk);
        buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

        buttonCancel.setAction(cmCancel);
        buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

        jTabbedPane1.setFocusable(false);

        jLabel8.setText(stringManager.getString("ImportFromCsvDialog-file-name-dd")); // NOI18N

        toolButton1.setAction(cmSelectFile);

        checkIgnoreErrors.setText(stringManager.getString("ImportFromCsvDialog-checkIgnoreErrors-text")); // NOI18N

        groupImportMode.add(radioCREATE_TABLE_INSERT);
        radioCREATE_TABLE_INSERT.setText(stringManager.getString("CREATE_TABLE_INSERT")); // NOI18N

        groupImportMode.add(radioTRUNCATE_AND_INSERT);
        radioTRUNCATE_AND_INSERT.setText(stringManager.getString("TRUNCATE_AND_INSERT")); // NOI18N

        groupImportMode.add(radioINSERT_NEW);
        radioINSERT_NEW.setText(stringManager.getString("INSERT_NEW")); // NOI18N

        groupImportMode.add(radioUPDATE_OR_INSERT);
        radioUPDATE_OR_INSERT.setText(stringManager.getString("UPDATE_OR_INSERT")); // NOI18N

        groupImportMode.add(radioDELETE_ONLY);
        radioDELETE_ONLY.setText(stringManager.getString("DELETE_ONLY")); // NOI18N

        groupImportMode.add(radioUPDATE_ONLY);
        radioUPDATE_ONLY.setText(stringManager.getString("UPDATE_ONLY")); // NOI18N

        groupImportMode.add(radioINSERT_ALL);
        radioINSERT_ALL.setText(stringManager.getString("INSERT_ALL")); // NOI18N

        javax.swing.GroupLayout panelModeLayout = new javax.swing.GroupLayout(panelMode);
        panelMode.setLayout(panelModeLayout);
        panelModeLayout.setHorizontalGroup(
            panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioUPDATE_ONLY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioDELETE_ONLY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioUPDATE_OR_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioINSERT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioINSERT_ALL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioTRUNCATE_AND_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioCREATE_TABLE_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkIgnoreErrors)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModeLayout.createSequentialGroup()
                        .addGroup(panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(textFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toolButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelModeLayout.setVerticalGroup(
            panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(toolButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelModeLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkIgnoreErrors)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioCREATE_TABLE_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioTRUNCATE_AND_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioINSERT_ALL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioINSERT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioUPDATE_OR_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioDELETE_ONLY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioUPDATE_ONLY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(stringManager.getString("ImportFromCsvDialog-import-mode-tab"), panelMode); // NOI18N

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(stringManager.getString("ImportFromCsvDialog-charset-dd")); // NOI18N

        comboDelimiter.setEditable(true);
        comboDelimiter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ",", ";", "|", "TAB", "SPACE" }));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText(stringManager.getString("ImportFromCsvDialog-separator-dd")); // NOI18N

        comboTextQualifier.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "\"", "'", "`" }));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText(stringManager.getString("ImportFromCsvDialog-text-qualifier-dd")); // NOI18N

        checkUseTextQualifier.setText(stringManager.getString("ImportFromCsvDialog-checkUseTextQualifier-text")); // NOI18N

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText(stringManager.getString("ImportFromCsvDialog-escape-mode-dd")); // NOI18N

        comboEscapeMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BACKSLASH", "DOUBLED" }));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText(stringManager.getString("ImportFromCsvDialog-end-of-line-char-dd")); // NOI18N

        comboEndOfLineChar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "\\n", "\\r", "\\f" }));

        checkTrimWhitespace.setText(stringManager.getString("ImportFromCsvDialog-checkTrimWhitespace-text")); // NOI18N

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText(stringManager.getString("ImportFromCsvDialog-precision-round-dd")); // NOI18N

        textPrecisionRound.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        checkHeaderPresent.setText(stringManager.getString("ImportFromCsvDialog-checkHeaderPresent-text")); // NOI18N

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkHeaderPresent)
                    .addComponent(checkTrimWhitespace)
                    .addComponent(checkUseTextQualifier)
                    .addComponent(comboCharset, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                    .addComponent(textPrecisionRound, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(comboEndOfLineChar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboEscapeMode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                        .addComponent(comboTextQualifier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboDelimiter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(comboCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(comboDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(comboTextQualifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkUseTextQualifier)
                .addGap(4, 4, 4)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboEscapeMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(comboEndOfLineChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkTrimWhitespace)
                .addGap(4, 4, 4)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(textPrecisionRound, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkHeaderPresent)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(stringManager.getString("ImportFromCsvDialog-main-tab"), panelMain); // NOI18N

        checkFullAuto.setText(stringManager.getString("ImportFromCsvDialog-checkFullAuto-text")); // NOI18N
        checkFullAuto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkFullAutoItemStateChanged(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(stringManager.getString("ImportFromCsvDialog-table-name-dd")); // NOI18N

        comboTable.setEditable(true);
        comboTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                comboTableFocusLost(evt);
            }
        });

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(stringManager.getString("ImportFromCsvDialog-primary-key-columns-dd")); // NOI18N

        textPrimaryKeyColumns.setToolTipText(stringManager.getString("ImportFromCsvDialog-primary-key-columns-tooltip-dd")); // NOI18N
        textPrimaryKeyColumns.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textPrimaryKeyColumnsFocusLost(evt);
            }
        });

        checkAddPrimaryKey.setText(stringManager.getString("ImportFromCsvDialog-checkAddPrimaryKey-text")); // NOI18N
        checkAddPrimaryKey.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkAddPrimaryKeyItemStateChanged(evt);
            }
        });

        checkAddNullCheck.setText(stringManager.getString("ImportFromCsvDialog-checkAddNullCheck-text")); // NOI18N
        checkAddNullCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkAddNullCheckItemStateChanged(evt);
            }
        });

        jLabel1.setText(stringManager.getString("ImportFromCsvDialog-table-create-source-dd")); // NOI18N

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textTableCreatCode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addComponent(checkFullAuto, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTableLayout.createSequentialGroup()
                        .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textPrimaryKeyColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                            .addComponent(comboTable, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                            .addComponent(checkAddPrimaryKey)
                            .addComponent(checkAddNullCheck)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkFullAuto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(comboTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(textPrimaryKeyColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTableLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkAddPrimaryKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkAddNullCheck))
                    .addGroup(panelTableLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textTableCreatCode, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(stringManager.getString("ImportFromCsvDialog-table-tab"), panelTable); // NOI18N

        buttonReset.setAction(cmReset);
        buttonReset.setPreferredSize(new java.awt.Dimension(75, 23));

        buttonAnalyze.setAction(cmAnalyze);
        buttonAnalyze.setPreferredSize(new java.awt.Dimension(75, 23));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                        .addComponent(buttonAnalyze, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonAnalyze, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
    File file = new File(textFileName.getText());
    if (file.exists()) {
      modalResult = ModalResult.OK;       
      dialogToConfig(config);
      dispose();
    }
    else {
      MessageBox.show(this, stringManager.getString("file"), stringManager.getString("file-not-exists-msg"), ModalResult.OK, MessageBox.ERROR);
    }
  }//GEN-LAST:event_cmOkActionPerformed

  private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
    modalResult = ModalResult.CANCEL;
    dispose();   
  }//GEN-LAST:event_cmCancelActionPerformed

  private void cmSelectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectFileActionPerformed
    if (selectFile()) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          analyze();
        }
      });
    }
  }//GEN-LAST:event_cmSelectFileActionPerformed

  private void cmResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmResetActionPerformed
    configToDialog(config);
  }//GEN-LAST:event_cmResetActionPerformed

  private void cmAnalyzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAnalyzeActionPerformed
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        analyze();
      }
    });
  }//GEN-LAST:event_cmAnalyzeActionPerformed

  private void comboTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comboTableFocusLost
    updateTableCreation();
  }//GEN-LAST:event_comboTableFocusLost

  private void textPrimaryKeyColumnsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textPrimaryKeyColumnsFocusLost
    updateTableCreation();
  }//GEN-LAST:event_textPrimaryKeyColumnsFocusLost

  private void checkAddPrimaryKeyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkAddPrimaryKeyItemStateChanged
    updateTableCreation();
  }//GEN-LAST:event_checkAddPrimaryKeyItemStateChanged

  private void checkAddNullCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkAddNullCheckItemStateChanged
    updateTableCreation();
  }//GEN-LAST:event_checkAddNullCheckItemStateChanged

  private void checkFullAutoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkFullAutoItemStateChanged
    updateTableCreation();
  }//GEN-LAST:event_checkFullAutoItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAnalyze;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonReset;
    private javax.swing.JCheckBox checkAddNullCheck;
    private javax.swing.JCheckBox checkAddPrimaryKey;
    private javax.swing.JCheckBox checkFullAuto;
    private javax.swing.JCheckBox checkHeaderPresent;
    private javax.swing.JCheckBox checkIgnoreErrors;
    private javax.swing.JCheckBox checkTrimWhitespace;
    private javax.swing.JCheckBox checkUseTextQualifier;
    private pl.mpak.sky.gui.swing.Action cmAnalyze;
    private pl.mpak.sky.gui.swing.Action cmCancel;
    private pl.mpak.sky.gui.swing.Action cmOk;
    private pl.mpak.sky.gui.swing.Action cmReset;
    private pl.mpak.sky.gui.swing.Action cmSelectFile;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboCharset;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboDelimiter;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboEndOfLineChar;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboEscapeMode;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboTable;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboTextQualifier;
    private javax.swing.ButtonGroup groupImportMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelMode;
    private javax.swing.JPanel panelTable;
    private javax.swing.JRadioButton radioCREATE_TABLE_INSERT;
    private javax.swing.JRadioButton radioDELETE_ONLY;
    private javax.swing.JRadioButton radioINSERT_ALL;
    private javax.swing.JRadioButton radioINSERT_NEW;
    private javax.swing.JRadioButton radioTRUNCATE_AND_INSERT;
    private javax.swing.JRadioButton radioUPDATE_ONLY;
    private javax.swing.JRadioButton radioUPDATE_OR_INSERT;
    private pl.mpak.sky.gui.swing.comp.TextField textFileName;
    private javax.swing.JSpinner textPrecisionRound;
    private pl.mpak.sky.gui.swing.comp.TextField textPrimaryKeyColumns;
    private OrbadaSyntaxTextArea textTableCreatCode;
    private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
    // End of variables declaration//GEN-END:variables
}
